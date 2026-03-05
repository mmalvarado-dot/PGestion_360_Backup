package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.ChangeRequest;
import com.mycompany.myapp.domain.TrackingRecord;
import com.mycompany.myapp.domain.enumeration.TrackingActionType;
import com.mycompany.myapp.repository.ChangeRequestRepository;
import com.mycompany.myapp.repository.DepartmentRepository;
import com.mycompany.myapp.repository.TrackingRecordRepository;
import com.mycompany.myapp.repository.UserRepository;
import com.mycompany.myapp.security.AuthoritiesConstants; // <-- AÑADIDO PARA SEGURIDAD
import com.mycompany.myapp.security.SecurityUtils; // <-- AÑADIDO PARA SEGURIDAD
import com.mycompany.myapp.service.ChangeRequestService;
import com.mycompany.myapp.service.MailService;
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
    private final DepartmentRepository departmentRepository;
    private final MailService mailService;
    private final UserRepository userRepository;

    public ChangeRequestServiceImpl(
        ChangeRequestRepository changeRequestRepository,
        ChangeRequestMapper changeRequestMapper,
        TrackingRecordRepository trackingRecordRepository,
        DepartmentRepository departmentRepository,
        MailService mailService,
        UserRepository userRepository
    ) {
        this.changeRequestRepository = changeRequestRepository;
        this.changeRequestMapper = changeRequestMapper;
        this.trackingRecordRepository = trackingRecordRepository;
        this.departmentRepository = departmentRepository;
        this.mailService = mailService;
        this.userRepository = userRepository;
    }

    @Override
    public Mono<ChangeRequestDTO> save(ChangeRequestDTO changeRequestDTO) {
        LOG.debug("Request to save ChangeRequest : {}", changeRequestDTO);
        return changeRequestRepository
            .save(changeRequestMapper.toEntity(changeRequestDTO))
            .flatMap(savedRequest -> saveTrackingHistory(savedRequest, TrackingActionType.CAMBIO_ESTADO).thenReturn(savedRequest))
            .doOnSuccess(savedRequest -> {
                // MAGIA: Dispara el correo en segundo plano si hay un usuario asignado
                if (savedRequest != null && savedRequest.getUserId() != null) {
                    userRepository
                        .findById(savedRequest.getUserId())
                        .subscribe(user -> mailService.sendChangeRequestAssignmentEmail(user, changeRequestMapper.toDto(savedRequest)));
                }
            })
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

    // ========================================================================
    //  🚀 MAGIA NUEVA: AISLAMIENTO DE DATOS (MÉTODO FIND ALL)
    // ========================================================================
    @Override
    @Transactional(readOnly = true)
    public Flux<ChangeRequestDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all ChangeRequests con Aislamiento de Datos");

        return SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.ADMIN)
            .flatMapMany(isAdmin -> {
                if (Boolean.TRUE.equals(isAdmin)) {
                    // 👑 Si es Admin, le mostramos TODO
                    return changeRequestRepository.findAllBy(pageable);
                } else {
                    // 👤 Si es un usuario normal, le mostramos solo lo suyo
                    return SecurityUtils.getCurrentUserLogin()
                        .flatMap(login -> userRepository.findOneByLogin(login))
                        .flatMapMany(user -> changeRequestRepository.findByUserId(user.getId(), pageable));
                }
            })
            .map(changeRequestMapper::toDto);
    }

    // ========================================================================
    //  🚀 MAGIA NUEVA: AISLAMIENTO DE DATOS (MÉTODO COUNT)
    // ========================================================================
    public Mono<Long> countAll() {
        return SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.ADMIN).flatMap(isAdmin -> {
            if (Boolean.TRUE.equals(isAdmin)) {
                // 👑 Si es Admin, contamos TODO
                return changeRequestRepository.count();
            } else {
                // 👤 Si es un usuario normal, contamos solo lo suyo
                return SecurityUtils.getCurrentUserLogin()
                    .flatMap(login -> userRepository.findOneByLogin(login))
                    .flatMap(user -> changeRequestRepository.countByUserId(user.getId()))
                    .switchIfEmpty(Mono.just(0L));
            }
        });
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
    //  EL CEREBRO: LÓGICA AUTOMÁTICA DEL TRACKING
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

        if (request.getUserId() != null) {
            tracking.setUserId(request.getUserId());
        }

        if (request.getDepartamento() != null && !request.getDepartamento().trim().isEmpty()) {
            return departmentRepository
                .findByDepartmentName(request.getDepartamento())
                .flatMap(department -> {
                    tracking.setDepartmentId(department.getId());
                    return trackingRecordRepository.save(tracking);
                })
                .switchIfEmpty(
                    Mono.defer(() -> {
                        LOG.warn("No se encontró el departamento con nombre: {}", request.getDepartamento());
                        return trackingRecordRepository.save(tracking);
                    })
                );
        } else {
            return trackingRecordRepository.save(tracking);
        }
    }
}
