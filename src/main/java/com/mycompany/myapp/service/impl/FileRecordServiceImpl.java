package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.repository.FileRecordRepository;
import com.mycompany.myapp.repository.UserRepository; // <-- AÑADIDO PARA SEGURIDAD
import com.mycompany.myapp.security.AuthoritiesConstants; // <-- AÑADIDO PARA SEGURIDAD
import com.mycompany.myapp.security.SecurityUtils; // <-- AÑADIDO PARA SEGURIDAD
import com.mycompany.myapp.service.FileRecordService;
import com.mycompany.myapp.service.dto.FileRecordDTO;
import com.mycompany.myapp.service.mapper.FileRecordMapper;
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
    private final UserRepository userRepository; // <-- AÑADIDO PARA SEGURIDAD

    public FileRecordServiceImpl(
        FileRecordRepository fileRecordRepository,
        FileRecordMapper fileRecordMapper,
        UserRepository userRepository // <-- AÑADIDO PARA SEGURIDAD
    ) {
        this.fileRecordRepository = fileRecordRepository;
        this.fileRecordMapper = fileRecordMapper;
        this.userRepository = userRepository; // <-- AÑADIDO PARA SEGURIDAD
    }

    @Override
    public Mono<FileRecordDTO> save(FileRecordDTO fileRecordDTO) {
        LOG.debug("Request to save FileRecord : {}", fileRecordDTO);
        return fileRecordRepository.save(fileRecordMapper.toEntity(fileRecordDTO)).map(fileRecordMapper::toDto);
    }

    @Override
    public Mono<FileRecordDTO> update(FileRecordDTO fileRecordDTO) {
        LOG.debug("Request to update FileRecord : {}", fileRecordDTO);
        return fileRecordRepository.save(fileRecordMapper.toEntity(fileRecordDTO)).map(fileRecordMapper::toDto);
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
            .map(fileRecordMapper::toDto);
    }

    // ========================================================================
    //  🚀 MAGIA NUEVA: AISLAMIENTO DE DATOS EN ARCHIVOS (FIND ALL)
    // ========================================================================
    @Override
    @Transactional(readOnly = true)
    public Flux<FileRecordDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all FileRecords con Aislamiento de Datos");
        return SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.ADMIN)
            .flatMapMany(isAdmin -> {
                if (Boolean.TRUE.equals(isAdmin)) {
                    // 👑 Si es Admin, le mostramos todos los archivos
                    return fileRecordRepository.findAllBy(pageable);
                } else {
                    // 👤 Si es un usuario normal, buscamos a qué ChangeRequests pertenece y mostramos sus archivos
                    return SecurityUtils.getCurrentUserLogin()
                        .flatMap(login -> userRepository.findOneByLogin(login))
                        .flatMapMany(user -> fileRecordRepository.findByChangeRequestUserId(user.getId(), pageable));
                }
            })
            .map(fileRecordMapper::toDto);
    }

    // ========================================================================
    //  🚀 MAGIA NUEVA: AISLAMIENTO DE DATOS EN ARCHIVOS (COUNT ALL)
    // ========================================================================
    public Mono<Long> countAll() {
        return SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.ADMIN).flatMap(isAdmin -> {
            if (Boolean.TRUE.equals(isAdmin)) {
                return fileRecordRepository.count();
            } else {
                return SecurityUtils.getCurrentUserLogin()
                    .flatMap(login -> userRepository.findOneByLogin(login))
                    .flatMap(user -> fileRecordRepository.countByChangeRequestUserId(user.getId()))
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
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete FileRecord : {}", id);
        return fileRecordRepository.deleteById(id);
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
