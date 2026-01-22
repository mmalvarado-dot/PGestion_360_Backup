package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.repository.ResponsibleRepository;
import com.mycompany.myapp.service.ResponsibleService;
import com.mycompany.myapp.service.dto.ResponsibleDTO;
import com.mycompany.myapp.service.mapper.ResponsibleMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.Responsible}.
 */
@Service
@Transactional
public class ResponsibleServiceImpl implements ResponsibleService {

    private static final Logger LOG = LoggerFactory.getLogger(ResponsibleServiceImpl.class);

    private final ResponsibleRepository responsibleRepository;

    private final ResponsibleMapper responsibleMapper;

    public ResponsibleServiceImpl(ResponsibleRepository responsibleRepository, ResponsibleMapper responsibleMapper) {
        this.responsibleRepository = responsibleRepository;
        this.responsibleMapper = responsibleMapper;
    }

    @Override
    public Mono<ResponsibleDTO> save(ResponsibleDTO responsibleDTO) {
        LOG.debug("Request to save Responsible : {}", responsibleDTO);
        return responsibleRepository.save(responsibleMapper.toEntity(responsibleDTO)).map(responsibleMapper::toDto);
    }

    @Override
    public Mono<ResponsibleDTO> update(ResponsibleDTO responsibleDTO) {
        LOG.debug("Request to update Responsible : {}", responsibleDTO);
        return responsibleRepository.save(responsibleMapper.toEntity(responsibleDTO)).map(responsibleMapper::toDto);
    }

    @Override
    public Mono<ResponsibleDTO> partialUpdate(ResponsibleDTO responsibleDTO) {
        LOG.debug("Request to partially update Responsible : {}", responsibleDTO);

        return responsibleRepository
            .findById(responsibleDTO.getId())
            .map(existingResponsible -> {
                responsibleMapper.partialUpdate(existingResponsible, responsibleDTO);

                return existingResponsible;
            })
            .flatMap(responsibleRepository::save)
            .map(responsibleMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<ResponsibleDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Responsibles");
        return responsibleRepository.findAllBy(pageable).map(responsibleMapper::toDto);
    }

    public Mono<Long> countAll() {
        return responsibleRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<ResponsibleDTO> findOne(Long id) {
        LOG.debug("Request to get Responsible : {}", id);
        return responsibleRepository.findById(id).map(responsibleMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete Responsible : {}", id);
        return responsibleRepository.deleteById(id);
    }
}
