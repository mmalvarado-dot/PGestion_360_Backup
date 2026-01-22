package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.repository.TrackingRecordRepository;
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

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.TrackingRecord}.
 */
@Service
@Transactional
public class TrackingRecordServiceImpl implements TrackingRecordService {

    private static final Logger LOG = LoggerFactory.getLogger(TrackingRecordServiceImpl.class);

    private final TrackingRecordRepository trackingRecordRepository;

    private final TrackingRecordMapper trackingRecordMapper;

    public TrackingRecordServiceImpl(TrackingRecordRepository trackingRecordRepository, TrackingRecordMapper trackingRecordMapper) {
        this.trackingRecordRepository = trackingRecordRepository;
        this.trackingRecordMapper = trackingRecordMapper;
    }

    @Override
    public Mono<TrackingRecordDTO> save(TrackingRecordDTO trackingRecordDTO) {
        LOG.debug("Request to save TrackingRecord : {}", trackingRecordDTO);
        return trackingRecordRepository.save(trackingRecordMapper.toEntity(trackingRecordDTO)).map(trackingRecordMapper::toDto);
    }

    @Override
    public Mono<TrackingRecordDTO> update(TrackingRecordDTO trackingRecordDTO) {
        LOG.debug("Request to update TrackingRecord : {}", trackingRecordDTO);
        return trackingRecordRepository.save(trackingRecordMapper.toEntity(trackingRecordDTO)).map(trackingRecordMapper::toDto);
    }

    @Override
    public Mono<TrackingRecordDTO> partialUpdate(TrackingRecordDTO trackingRecordDTO) {
        LOG.debug("Request to partially update TrackingRecord : {}", trackingRecordDTO);

        return trackingRecordRepository
            .findById(trackingRecordDTO.getId())
            .map(existingTrackingRecord -> {
                trackingRecordMapper.partialUpdate(existingTrackingRecord, trackingRecordDTO);

                return existingTrackingRecord;
            })
            .flatMap(trackingRecordRepository::save)
            .map(trackingRecordMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<TrackingRecordDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all TrackingRecords");
        return trackingRecordRepository.findAllBy(pageable).map(trackingRecordMapper::toDto);
    }

    public Mono<Long> countAll() {
        return trackingRecordRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<TrackingRecordDTO> findOne(Long id) {
        LOG.debug("Request to get TrackingRecord : {}", id);
        return trackingRecordRepository.findById(id).map(trackingRecordMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete TrackingRecord : {}", id);
        return trackingRecordRepository.deleteById(id);
    }
}
