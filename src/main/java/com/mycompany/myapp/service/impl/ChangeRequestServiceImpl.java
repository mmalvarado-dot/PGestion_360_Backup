package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.repository.ChangeRequestRepository;
import com.mycompany.myapp.service.ChangeRequestService;
import com.mycompany.myapp.service.dto.ChangeRequestDTO;
import com.mycompany.myapp.service.mapper.ChangeRequestMapper;
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

    public ChangeRequestServiceImpl(ChangeRequestRepository changeRequestRepository, ChangeRequestMapper changeRequestMapper) {
        this.changeRequestRepository = changeRequestRepository;
        this.changeRequestMapper = changeRequestMapper;
    }

    @Override
    public Mono<ChangeRequestDTO> save(ChangeRequestDTO changeRequestDTO) {
        LOG.debug("Request to save ChangeRequest : {}", changeRequestDTO);
        return changeRequestRepository.save(changeRequestMapper.toEntity(changeRequestDTO)).map(changeRequestMapper::toDto);
    }

    @Override
    public Mono<ChangeRequestDTO> update(ChangeRequestDTO changeRequestDTO) {
        LOG.debug("Request to update ChangeRequest : {}", changeRequestDTO);
        return changeRequestRepository.save(changeRequestMapper.toEntity(changeRequestDTO)).map(changeRequestMapper::toDto);
    }

    @Override
    public Mono<ChangeRequestDTO> partialUpdate(ChangeRequestDTO changeRequestDTO) {
        LOG.debug("Request to partially update ChangeRequest : {}", changeRequestDTO);

        return changeRequestRepository
            .findById(changeRequestDTO.getId())
            .map(existingChangeRequest -> {
                changeRequestMapper.partialUpdate(existingChangeRequest, changeRequestDTO);

                return existingChangeRequest;
            })
            .flatMap(changeRequestRepository::save)
            .map(changeRequestMapper::toDto);
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
}
