package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.ChangeRequest;
import com.mycompany.myapp.domain.TrackingRecord;
import com.mycompany.myapp.domain.enumeration.TrackingActionType; // <--- IMPORTANTE
import com.mycompany.myapp.repository.ChangeRequestRepository;
import com.mycompany.myapp.repository.TrackingRecordRepository;
import com.mycompany.myapp.service.ChangeRequestService;
import com.mycompany.myapp.service.dto.ChangeRequestDTO;
import com.mycompany.myapp.service.mapper.ChangeRequestMapper;
import java.time.Instant; // <--- IMPORTANTE: Usamos Instant, no LocalDate
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.ChangeRequest}.
 */
@Service
@Transactional
public class ChangeRequestServiceImpl implements ChangeRequestService {

    private static final Logger LOG = LoggerFactory.getLogger(ChangeRequestServiceImpl.class);

    private final ChangeRequestRepository changeRequestRepository;
    private final ChangeRequestMapper changeRequestMapper;
    private final TrackingRecordRepository trackingRecordRepository;

    public ChangeRequestServiceImpl(
        ChangeRequestRepository changeRequestRepository,
        ChangeRequestMapper changeRequestMapper,
        TrackingRecordRepository trackingRecordRepository
    ) {
        this.changeRequestRepository = changeRequestRepository;
        this.changeRequestMapper = changeRequestMapper;
        this.trackingRecordRepository = trackingRecordRepository;
    }

    @Override
    public Mono<ChangeRequestDTO> save(ChangeRequestDTO changeRequestDTO) {
        LOG.debug("Request to save ChangeRequest : {}", changeRequestDTO);
        return changeRequestRepository
            .save(changeRequestMapper.toEntity(changeRequestDTO))
            .flatMap(savedRequest ->
                // Al crear, SIEMPRE es un Cambio de Estado (de Nada -> Creado)
                saveTrackingHistory(savedRequest, TrackingActionType.CAMBIO_ESTADO).thenReturn(savedRequest)
            )
            .map(changeRequestMapper::toDto);
    }

    @Override
    public Mono<ChangeRequestDTO> update(ChangeRequestDTO changeRequestDTO) {
        LOG.debug("Request to update ChangeRequest : {}", changeRequestDTO);

        return changeRequestRepository
            .findById(changeRequestDTO.getId())
            .flatMap(existingRequest -> {
                // 1. Capturamos el estado ANTES de actualizar
                String oldStatus = existingRequest.getStatus();

                // 2. Actualizamos la entidad con los datos nuevos
                ChangeRequest updatedEntity = changeRequestMapper.toEntity(changeRequestDTO);

                // 3. Detectamos qué tipo de acción es
                TrackingActionType type = detectActionType(oldStatus, updatedEntity.getStatus());

                return changeRequestRepository.save(updatedEntity).flatMap(saved -> saveTrackingHistory(saved, type).thenReturn(saved));
            })
            .map(changeRequestMapper::toDto);
    }

    @Override
    public Mono<ChangeRequestDTO> partialUpdate(ChangeRequestDTO changeRequestDTO) {
        LOG.debug("Request to partially update ChangeRequest : {}", changeRequestDTO);

        return changeRequestRepository
            .findById(changeRequestDTO.getId())
            .map(existingChangeRequest -> {
                // 1. Capturamos el estado ANTES de actualizar
                String oldStatus = existingChangeRequest.getStatus();

                // 2. Aplicamos los cambios
                changeRequestMapper.partialUpdate(existingChangeRequest, changeRequestDTO);

                // 3. Comparamos para saber qué pasó (Detectamos el cambio)
                String newStatus = existingChangeRequest.getStatus();
                TrackingActionType type = detectActionType(oldStatus, newStatus);

                // Guardamos el tipo en un objeto temporal o contexto si fuera necesario,
                // pero aquí lo pasamos directo al saveTrackingHistory abajo
                // (Truco: Usamos un Pair o simplemente pasamos el 'type' al siguiente flatMap si pudiéramos,
                // pero para simplificar R2DBC, llamaremos al saveTracking dentro del flujo).

                return new Wrapper(existingChangeRequest, type);
            })
            .flatMap(wrapper ->
                changeRequestRepository.save(wrapper.request).flatMap(saved -> saveTrackingHistory(saved, wrapper.type).thenReturn(saved))
            )
            .map(changeRequestMapper::toDto);
    }

    // Clase auxiliar pequeña para pasar datos en el flujo reactivo
    private static class Wrapper {

        ChangeRequest request;
        TrackingActionType type;

        Wrapper(ChangeRequest r, TrackingActionType t) {
            this.request = r;
            this.type = t;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<ChangeRequestDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all ChangeRequests");
        return changeRequestRepository.findAllBy(pageable).map(changeRequestMapper::toDto);
    }

    public Mono<Long> countAll() {
        return changeRequestRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<ChangeRequestDTO> findOne(Long id) {
        LOG.debug("Request to get ChangeRequest : {}", id);
        return changeRequestRepository.findById(id).map(changeRequestMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete ChangeRequest : {}", id);
        return changeRequestRepository.deleteById(id);
    }

    // ========================================================================
    //  EL CEREBRO: LÓGICA AUTOMÁTICA
    // ========================================================================

    /**
     * Compara estados y decide si es Edición o Cambio de Estado
     */
    private TrackingActionType detectActionType(String oldStatus, String newStatus) {
        if (oldStatus == null && newStatus != null) return TrackingActionType.CAMBIO_ESTADO;
        if (oldStatus != null && !oldStatus.equals(newStatus)) {
            return TrackingActionType.CAMBIO_ESTADO;
        }
        return TrackingActionType.EDICION;
    }

    /**
     * Guarda el historial en la tabla tracking_record
     */
    private Mono<TrackingRecord> saveTrackingHistory(ChangeRequest request, TrackingActionType actionType) {
        TrackingRecord tracking = new TrackingRecord();

        tracking.setChangeDate(Instant.now()); // <--- Corregido a Instant
        tracking.setStatus(request.getStatus());
        tracking.setActionType(actionType); // <--- Guardamos si fue Edición o Cambio

        // Generamos un comentario automático
        if (actionType == TrackingActionType.CAMBIO_ESTADO) {
            tracking.setComments("El estado cambió a: " + request.getStatus());
        } else {
            tracking.setComments("Se actualizaron detalles de la solicitud.");
        }

        // Conexiones
        tracking.setChangeRequestId(request.getId());
        tracking.setResponsibleId(request.getResponsibleId());

        // Como en ChangeRequest el departamento es un String,
        // lo guardamos en los comentarios del historial por ahora
        // para no perder la información en las estadísticas.
        if (request.getDepartamento() != null) {
            tracking.setComments(tracking.getComments() + " - Dept: " + request.getDepartamento());
        }

        // Para que no de error, dejamos el ID en null o lo comentamos:
        // tracking.setDepartmentId(null);

        return trackingRecordRepository.save(tracking);
    }
}
