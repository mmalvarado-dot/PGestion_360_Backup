package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.repository.FileRecordRepository;
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

    public FileRecordServiceImpl(FileRecordRepository fileRecordRepository, FileRecordMapper fileRecordMapper) {
        this.fileRecordRepository = fileRecordRepository;
        this.fileRecordMapper = fileRecordMapper;
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

    @Override
    @Transactional(readOnly = true)
    public Flux<FileRecordDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all FileRecords");
        return fileRecordRepository.findAllBy(pageable).map(fileRecordMapper::toDto);
    }

    public Mono<Long> countAll() {
        return fileRecordRepository.count();
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
