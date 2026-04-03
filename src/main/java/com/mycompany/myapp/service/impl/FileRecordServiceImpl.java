package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.FileRecord;
import com.mycompany.myapp.domain.TrackingRecord;
import com.mycompany.myapp.domain.enumeration.TrackingActionType;
import com.mycompany.myapp.repository.ChangeRequestRepository;
import com.mycompany.myapp.repository.DepartmentRepository;
import com.mycompany.myapp.repository.FileRecordRepository;
import com.mycompany.myapp.repository.TrackingRecordRepository;
import com.mycompany.myapp.repository.UserRepository;
import com.mycompany.myapp.security.AuthoritiesConstants;
import com.mycompany.myapp.security.SecurityUtils;
import com.mycompany.myapp.service.FileRecordService;
import com.mycompany.myapp.service.dto.FileRecordDTO;
import com.mycompany.myapp.service.mapper.FileRecordMapper;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.FileRecord}.
 */
@Service
@Transactional
public class FileRecordServiceImpl implements FileRecordService {

    private static final Logger LOG = LoggerFactory.getLogger(FileRecordServiceImpl.class);

    private final FileRecordRepository fileRecordRepository;
    private final FileRecordMapper fileRecordMapper;
    private final UserRepository userRepository;

    private final TrackingRecordRepository trackingRecordRepository;
    private final ChangeRequestRepository changeRequestRepository;
    private final DepartmentRepository departmentRepository;

    public FileRecordServiceImpl(
        FileRecordRepository fileRecordRepository,
        FileRecordMapper fileRecordMapper,
        UserRepository userRepository,
        TrackingRecordRepository trackingRecordRepository,
        ChangeRequestRepository changeRequestRepository,
        DepartmentRepository departmentRepository
    ) {
        this.fileRecordRepository = fileRecordRepository;
        this.fileRecordMapper = fileRecordMapper;
        this.userRepository = userRepository;
        this.trackingRecordRepository = trackingRecordRepository;
        this.changeRequestRepository = changeRequestRepository;
        this.departmentRepository = departmentRepository;
    }

    @Override
    public Mono<FileRecordDTO> save(FileRecordDTO fileRecordDTO) {
        LOG.debug("Request to save FileRecord : {}", fileRecordDTO);
        return fileRecordRepository
            .save(fileRecordMapper.toEntity(fileRecordDTO))
            .flatMap(savedFile -> trackFileAction(savedFile, "Se adjuntó un nuevo archivo"))
            .map(fileRecordMapper::toDto);
    }

    @Override
    public Mono<FileRecordDTO> update(FileRecordDTO fileRecordDTO) {
        LOG.debug("Request to update FileRecord : {}", fileRecordDTO);
        return fileRecordRepository
            .save(fileRecordMapper.toEntity(fileRecordDTO))
            .flatMap(savedFile -> trackFileAction(savedFile, "Se actualizó un archivo adjunto"))
            .map(fileRecordMapper::toDto);
    }

    @Override
    public Mono<FileRecordDTO> partialUpdate(FileRecordDTO fileRecordDTO) {
        LOG.debug("Request to partially update FileRecord : {}", fileRecordDTO);

        return fileRecordRepository
            .findById(fileRecordDTO.getId())
            .map(existingFileRecord -> {
                fileRecordMapper.partialUpdate(existingFileRecord, fileRecordDTO);
                return existingFileRecord;
            })
            .flatMap(fileRecordRepository::save)
            .flatMap(savedFile -> trackFileAction(savedFile, "Se modificaron propiedades de un archivo adjunto"))
            .map(fileRecordMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete FileRecord : {}", id);

        return fileRecordRepository
            .findById(id)
            .flatMap(fileRecord -> trackFileAction(fileRecord, "Se eliminó un archivo adjunto").thenReturn(fileRecord))
            .flatMap(file -> fileRecordRepository.deleteById(id));
    }

    private Mono<FileRecord> trackFileAction(FileRecord fileRecord, String actionMessage) {
        if (fileRecord.getChangeRequestId() == null) {
            return Mono.just(fileRecord);
        }

        return changeRequestRepository
            .findById(fileRecord.getChangeRequestId())
            .flatMap(changeRequest -> {
                TrackingRecord tracking = new TrackingRecord();
                tracking.setChangeDate(Instant.now());
                tracking.setStatus(changeRequest.getStatus());
                tracking.setActionType(TrackingActionType.EDICION);

                tracking.setComments(actionMessage + " (ID de Archivo: " + fileRecord.getId() + ")");

                tracking.setChangeRequestId(changeRequest.getId());

                if (changeRequest.getUserId() != null) {
                    tracking.setUserId(changeRequest.getUserId());
                }

                if (changeRequest.getDepartamento() != null && !changeRequest.getDepartamento().trim().isEmpty()) {
                    return departmentRepository
                        .findByDepartmentName(changeRequest.getDepartamento())
                        .flatMap(dept -> {
                            tracking.setDepartmentId(dept.getId());
                            return trackingRecordRepository.save(tracking);
                        })
                        .switchIfEmpty(Mono.defer(() -> trackingRecordRepository.save(tracking)));
                } else {
                    return trackingRecordRepository.save(tracking);
                }
            })
            .thenReturn(fileRecord);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<FileRecordDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all FileRecords con Aislamiento de Datos");
        return SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.ADMIN)
            .flatMapMany(isAdmin -> {
                if (Boolean.TRUE.equals(isAdmin)) {
                    return fileRecordRepository.findAllBy(pageable);
                } else {
                    return SecurityUtils.getCurrentUserLogin()
                        .flatMap(login -> userRepository.findOneByLogin(login))
                        .flatMapMany(user ->
                            fileRecordRepository.findAllByChangeRequestUser(user.getId(), pageable.getPageSize(), pageable.getOffset())
                        )
                        .flatMapSequential(fr -> fileRecordRepository.findById(fr.getId()));
                }
            })
            .map(fileRecordMapper::toDto);
    }

    public Mono<Long> countAll() {
        return SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.ADMIN).flatMap(isAdmin -> {
            if (Boolean.TRUE.equals(isAdmin)) {
                return fileRecordRepository.count();
            } else {
                return SecurityUtils.getCurrentUserLogin()
                    .flatMap(login -> userRepository.findOneByLogin(login))
                    .flatMap(user -> fileRecordRepository.countAllByChangeRequestUser(user.getId()))
                    .switchIfEmpty(Mono.just(0L));
            }
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<FileRecordDTO> findOne(Long id) {
        LOG.debug("Request to get FileRecord : {}", id);
        return fileRecordRepository.findById(id).map(fileRecordMapper::toDto);
    }

    @Override
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public Flux<FileRecordDTO> findByChangeRequestId(Long changeRequestId, Pageable pageable) {
        return fileRecordRepository.findByChangeRequestId(changeRequestId, pageable).map(fileRecordMapper::toDto);
    }

    @Override
    public Mono<Long> countByChangeRequestId(Long changeRequestId) {
        return fileRecordRepository.countByChangeRequestId(changeRequestId);
    }
}
