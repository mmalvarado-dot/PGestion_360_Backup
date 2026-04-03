package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.repository.TrackingRecordRepository;
import com.mycompany.myapp.repository.TrackingStats;
import com.mycompany.myapp.service.TrackingRecordService;
import com.mycompany.myapp.service.dto.TrackingRecordDTO;
import com.mycompany.myapp.service.mapper.TrackingRecordMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Transactional
public class TrackingRecordServiceImpl implements TrackingRecordService {

    private final Logger log = LoggerFactory.getLogger(TrackingRecordServiceImpl.class);
    private final TrackingRecordRepository trackingRecordRepository;
    private final TrackingRecordMapper trackingRecordMapper;

    public TrackingRecordServiceImpl(TrackingRecordRepository trackingRecordRepository, TrackingRecordMapper trackingRecordMapper) {
        this.trackingRecordRepository = trackingRecordRepository;
        this.trackingRecordMapper = trackingRecordMapper;
    }

    @Override
    public Mono<TrackingRecordDTO> save(TrackingRecordDTO trackingRecordDTO) {
        log.debug("Request to save TrackingRecord : {}", trackingRecordDTO);
        return trackingRecordRepository.save(trackingRecordMapper.toEntity(trackingRecordDTO)).map(trackingRecordMapper::toDto);
    }

    @Override
    public Mono<TrackingRecordDTO> update(TrackingRecordDTO trackingRecordDTO) {
        log.debug("Request to update TrackingRecord : {}", trackingRecordDTO);
        return trackingRecordRepository.save(trackingRecordMapper.toEntity(trackingRecordDTO)).map(trackingRecordMapper::toDto);
    }

    @Override
    public Mono<TrackingRecordDTO> partialUpdate(TrackingRecordDTO trackingRecordDTO) {
        log.debug("Request to partially update TrackingRecord : {}", trackingRecordDTO);
        return trackingRecordRepository
            .findById(trackingRecordDTO.getId())
            .map(existingTrackingRecord -> {
                trackingRecordMapper.partialUpdate(existingTrackingRecord, trackingRecordDTO);
                return existingTrackingRecord;
            })
            .flatMap(trackingRecordRepository::save)
            .map(trackingRecordMapper::toDto);
    }

    // CONSULTAS GENERALES (ADMIN)

    @Override
    @Transactional(readOnly = true)
    public Flux<TrackingRecordDTO> findAll(Pageable pageable) {
        log.debug("Request to get all TrackingRecords");
        return trackingRecordRepository.findAllBy(pageable).map(trackingRecordMapper::toDto);
    }

    @Override
    public Mono<Long> countAll() {
        return trackingRecordRepository.count();
    }

    //    SEGURIDAD (USUARIO NORMAL)

    @Override
    @Transactional(readOnly = true)
    public Flux<TrackingRecordDTO> findAllByUser(Pageable pageable, Long userId) {
        log.debug("Request to get TrackingRecords by User ID: {}", userId);
        return trackingRecordRepository
            .findAllByChangeRequestUser(userId, pageable.getPageSize(), pageable.getOffset())
            .flatMapSequential(tr -> trackingRecordRepository.findById(tr.getId()))
            .map(trackingRecordMapper::toDto);
    }

    @Override
    public Mono<Long> countAllByUser(Long userId) {
        log.debug("Request to count TrackingRecords by User ID: {}", userId);
        return trackingRecordRepository.countAllByChangeRequestUser(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<TrackingRecordDTO> findOne(Long id) {
        log.debug("Request to get TrackingRecord : {}", id);
        return trackingRecordRepository.findById(id).map(trackingRecordMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete TrackingRecord : {}", id);
        return trackingRecordRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<TrackingRecordDTO> findAllByRequestId(Long id) {
        log.debug("Request to get history for Request ID : {}", id);
        return trackingRecordRepository.findByChangeRequestId(id).map(trackingRecordMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<TrackingStats> getDepartmentStats(Integer year, Integer month) {
        log.debug("Request to get Department stats for year: {} and month: {}", year, month);
        return trackingRecordRepository.countMovementsByDepartment(year, month);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<TrackingStats> getUserStats(Integer year, Integer month) {
        log.debug("Request to get User stats for year: {} and month: {}", year, month);
        return trackingRecordRepository.countMovementsByUser(year, month);
    }
}
