package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.ChangeRequest;
import com.mycompany.myapp.domain.TrackingRecord;
import com.mycompany.myapp.domain.enumeration.TrackingActionType;
import com.mycompany.myapp.repository.ChangeRequestRepository;
import com.mycompany.myapp.repository.DepartmentRepository;
import com.mycompany.myapp.repository.TrackingRecordRepository;
import com.mycompany.myapp.repository.UserRepository;
import com.mycompany.myapp.security.AuthoritiesConstants;
import com.mycompany.myapp.security.SecurityUtils;
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
            .flatMap(savedRequest ->
                saveTrackingHistory(savedRequest, TrackingActionType.CAMBIO_ESTADO, "Solicitud creada.").thenReturn(savedRequest)
            )
            .doOnSuccess(savedRequest -> {
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
                ChangeRequest updatedEntity = changeRequestMapper.toEntity(changeRequestDTO);
                TrackingActionType type = detectActionType(existingRequest.getStatus(), updatedEntity.getStatus());
                String details = buildChangeMessage(existingRequest, updatedEntity);

                return changeRequestRepository
                    .save(updatedEntity)
                    .flatMap(saved -> saveTrackingHistory(saved, type, details).thenReturn(saved));
            })
            .map(changeRequestMapper::toDto);
    }

    @Override
    public Mono<ChangeRequestDTO> partialUpdate(ChangeRequestDTO changeRequestDTO) {
        LOG.debug("Request to partially update ChangeRequest : {}", changeRequestDTO);

        return changeRequestRepository
            .findById(changeRequestDTO.getId())
            .map(existingChangeRequest -> {
                ChangeRequest oldState = changeRequestMapper.toEntity(changeRequestMapper.toDto(existingChangeRequest));
                changeRequestMapper.partialUpdate(existingChangeRequest, changeRequestDTO);

                TrackingActionType type = detectActionType(oldState.getStatus(), existingChangeRequest.getStatus());
                String details = buildChangeMessage(oldState, existingChangeRequest);

                return new Wrapper(existingChangeRequest, type, details);
            })
            .flatMap(wrapper ->
                changeRequestRepository
                    .save(wrapper.request)
                    .flatMap(saved -> saveTrackingHistory(saved, wrapper.type, wrapper.details).thenReturn(saved))
            )
            .map(changeRequestMapper::toDto);
    }

    private static class Wrapper {

        ChangeRequest request;
        TrackingActionType type;
        String details;

        Wrapper(ChangeRequest r, TrackingActionType t, String d) {
            this.request = r;
            this.type = t;
            this.details = d;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<ChangeRequestDTO> findAll(Pageable pageable) {
        return SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.ADMIN)
            .flatMapMany(isAdmin -> {
                if (Boolean.TRUE.equals(isAdmin)) {
                    return changeRequestRepository.findAllBy(pageable);
                } else {
                    return SecurityUtils.getCurrentUserLogin()
                        .flatMap(login -> userRepository.findOneByLogin(login))
                        .flatMapMany(user -> changeRequestRepository.findByUserId(user.getId(), pageable));
                }
            })
            .map(changeRequestMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Long> countAll() {
        return SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.ADMIN).flatMap(isAdmin -> {
            if (Boolean.TRUE.equals(isAdmin)) {
                return changeRequestRepository.count();
            } else {
                return SecurityUtils.getCurrentUserLogin()
                    .flatMap(login -> userRepository.findOneByLogin(login))
                    .flatMap(user -> changeRequestRepository.countByUserId(user.getId()))
                    .switchIfEmpty(Mono.just(0L));
            }
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<ChangeRequestDTO> findByFilters(String search, String status, Instant startDate, Instant endDate, Pageable pageable) {
        return SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.ADMIN)
            .flatMapMany(isAdmin -> {
                if (Boolean.TRUE.equals(isAdmin)) {
                    return changeRequestRepository.findAllByFilters(
                        search,
                        status,
                        startDate,
                        endDate,
                        pageable.getPageSize(),
                        pageable.getOffset()
                    );
                } else {
                    return SecurityUtils.getCurrentUserLogin()
                        .flatMap(login -> userRepository.findOneByLogin(login))
                        .flatMapMany(user ->
                            changeRequestRepository.findByUserIdAndFilters(
                                search,
                                status,
                                startDate,
                                endDate,
                                user.getId(),
                                pageable.getPageSize(),
                                pageable.getOffset()
                            )
                        );
                }
            })
            .concatMap(partialEntity -> changeRequestRepository.findById(partialEntity.getId()))
            .map(changeRequestMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Long> countByFilters(String search, String status, Instant startDate, Instant endDate) {
        return SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.ADMIN).flatMap(isAdmin -> {
            if (Boolean.TRUE.equals(isAdmin)) {
                return changeRequestRepository.countAllByFilters(search, status, startDate, endDate);
            } else {
                return SecurityUtils.getCurrentUserLogin()
                    .flatMap(login -> userRepository.findOneByLogin(login))
                    .flatMap(user -> changeRequestRepository.countByUserIdAndFilters(search, status, startDate, endDate, user.getId()))
                    .switchIfEmpty(Mono.just(0L));
            }
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<ChangeRequestDTO> findOne(Long id) {
        return changeRequestRepository.findById(id).map(changeRequestMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        return changeRequestRepository.deleteById(id);
    }

    @Override
    public Mono<Void> recordFileTracking(Long id, String fileName) {
        return changeRequestRepository
            .findById(id)
            .flatMap(req -> saveTrackingHistory(req, TrackingActionType.EDICION, "Se adjuntó el archivo: " + fileName))
            .then();
    }

    private TrackingActionType detectActionType(String oldStatus, String newStatus) {
        if (oldStatus != null && !oldStatus.equals(newStatus)) return TrackingActionType.CAMBIO_ESTADO;
        return TrackingActionType.EDICION;
    }

    private String buildChangeMessage(ChangeRequest oldReq, ChangeRequest newReq) {
        if (oldReq == null) return "Solicitud creada.";
        java.util.List<String> changes = new java.util.ArrayList<>();

        if (!java.util.Objects.equals(oldReq.getTitle(), newReq.getTitle())) changes.add("Título: '" + newReq.getTitle() + "'");

        if (!java.util.Objects.equals(oldReq.getDescription(), newReq.getDescription())) {
            String oldDesc = oldReq.getDescription() != null ? oldReq.getDescription() : "vacío";
            String newDesc = newReq.getDescription() != null ? newReq.getDescription() : "vacío";
            changes.add("Descripción cambió de: [" + oldDesc + "] a: [" + newDesc + "]");
        }

        if (!java.util.Objects.equals(oldReq.getStatus(), newReq.getStatus())) changes.add("Estado cambió a: '" + newReq.getStatus() + "'");

        if (!java.util.Objects.equals(oldReq.getObservaciones(), newReq.getObservaciones())) {
            String oldObs = oldReq.getObservaciones() != null ? oldReq.getObservaciones() : "vacío";
            String newObs = newReq.getObservaciones() != null ? newReq.getObservaciones() : "vacío";
            changes.add("Observaciones cambió de: [" + oldObs + "] a: [" + newObs + "]");
        }

        if (changes.isEmpty()) return "Se actualizaron detalles menores.";
        return String.join(" | ", changes);
    }

    private Mono<TrackingRecord> saveTrackingHistory(ChangeRequest request, TrackingActionType actionType, String comments) {
        TrackingRecord tracking = new TrackingRecord();
        tracking.setChangeDate(Instant.now());
        tracking.setStatus(request.getStatus());
        tracking.setActionType(actionType);
        tracking.setComments(comments);
        tracking.setChangeRequestId(request.getId());
        if (request.getUserId() != null) tracking.setUserId(request.getUserId());

        if (request.getDepartamento() != null && !request.getDepartamento().trim().isEmpty()) {
            return departmentRepository
                .findByDepartmentName(request.getDepartamento())
                .flatMap(dept -> {
                    tracking.setDepartmentId(dept.getId());
                    return trackingRecordRepository.save(tracking);
                })
                .switchIfEmpty(Mono.defer(() -> trackingRecordRepository.save(tracking)));
        }
        return trackingRecordRepository.save(tracking);
    }
}
