package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.ChangeRequest;
import com.mycompany.myapp.domain.TrackingRecord;
import com.mycompany.myapp.domain.enumeration.TrackingActionType;
import com.mycompany.myapp.repository.ChangeRequestRepository;
import com.mycompany.myapp.repository.DepartmentRepository; // <--- NUEVO IMPORT
import com.mycompany.myapp.repository.TrackingRecordRepository;
import com.mycompany.myapp.service.ChangeRequestService;
import com.mycompany.myapp.service.dto.ChangeRequestDTO;
import com.mycompany.myapp.service.mapper.ChangeRequestMapper;
import java.time.Instant;
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
    private final DepartmentRepository departmentRepository; // <--- NUEVA DEPENDENCIA

    public ChangeRequestServiceImpl(
        ChangeRequestRepository changeRequestRepository,
        ChangeRequestMapper changeRequestMapper,
        TrackingRecordRepository trackingRecordRepository,
        DepartmentRepository departmentRepository // <--- INYECTAMOS AQUÍ
    ) {
        this.changeRequestRepository = changeRequestRepository;
        this.changeRequestMapper = changeRequestMapper;
        this.trackingRecordRepository = trackingRecordRepository;
        this.departmentRepository = departmentRepository; // <--- ASIGNAMOS AQUÍ
    }

    @Override
    public Mono<ChangeRequestDTO> save(ChangeRequestDTO changeRequestDTO) {
        LOG.debug("Request to save ChangeRequest : {}", changeRequestDTO);
        return changeRequestRepository
            .save(changeRequestMapper.toEntity(changeRequestDTO))
            .flatMap(savedRequest -> saveTrackingHistory(savedRequest, TrackingActionType.CAMBIO_ESTADO).thenReturn(savedRequest))
            .map(changeRequestMapper::toDto);
    }

    @Override
    public Mono<ChangeRequestDTO> update(ChangeRequestDTO changeRequestDTO) {
        LOG.debug("Request to update ChangeRequest : {}", changeRequestDTO);

        return changeRequestRepository
            .findById(changeRequestDTO.getId())
            .flatMap(existingRequest -> {
                String oldStatus = existingRequest.getStatus();
                ChangeRequest updatedEntity = changeRequestMapper.toEntity(changeRequestDTO);
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
                String oldStatus = existingChangeRequest.getStatus();
                changeRequestMapper.partialUpdate(existingChangeRequest, changeRequestDTO);
                String newStatus = existingChangeRequest.getStatus();
                TrackingActionType type = detectActionType(oldStatus, newStatus);
                return new Wrapper(existingChangeRequest, type);
            })
            .flatMap(wrapper ->
                changeRequestRepository.save(wrapper.request).flatMap(saved -> saveTrackingHistory(saved, wrapper.type).thenReturn(saved))
            )
            .map(changeRequestMapper::toDto);
    }

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

        tracking.setChangeDate(Instant.now());
        tracking.setStatus(request.getStatus());
        tracking.setActionType(actionType);

        if (actionType == TrackingActionType.CAMBIO_ESTADO) {
            tracking.setComments("El estado cambió a: " + request.getStatus());
        } else {
            tracking.setComments("Se actualizaron detalles de la solicitud.");
        }

        tracking.setChangeRequestId(request.getId());
        tracking.setResponsibleId(request.getResponsibleId());

        // MAGIA NUEVA: Buscamos el ID del departamento por su nombre
        if (request.getDepartamento() != null && !request.getDepartamento().trim().isEmpty()) {
            return departmentRepository
                .findByDepartmentName(request.getDepartamento())
                .flatMap(department -> {
                    // Si lo encuentra, le asignamos el ID real
                    tracking.setDepartmentId(department.getId());
                    return trackingRecordRepository.save(tracking);
                })
                .switchIfEmpty(
                    Mono.defer(() -> {
                        // Si no lo encuentra (o escribieron mal el nombre), guardamos sin ID
                        LOG.warn("No se encontró el departamento con nombre: {}", request.getDepartamento());
                        return trackingRecordRepository.save(tracking);
                    })
                );
        } else {
            // Si la solicitud no tiene departamento, guardamos normal
            return trackingRecordRepository.save(tracking);
        }
    }
}
