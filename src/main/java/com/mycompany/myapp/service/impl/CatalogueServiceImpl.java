package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.repository.CatalogueRepository;
import com.mycompany.myapp.service.CatalogueService;
import com.mycompany.myapp.service.dto.CatalogueDTO;
import com.mycompany.myapp.service.mapper.CatalogueMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.Catalogue}.
 */
@Service
@Transactional
public class CatalogueServiceImpl implements CatalogueService {

    private static final Logger LOG = LoggerFactory.getLogger(CatalogueServiceImpl.class);

    private final CatalogueRepository catalogueRepository;

    private final CatalogueMapper catalogueMapper;

    public CatalogueServiceImpl(CatalogueRepository catalogueRepository, CatalogueMapper catalogueMapper) {
        this.catalogueRepository = catalogueRepository;
        this.catalogueMapper = catalogueMapper;
    }

    @Override
    public Mono<CatalogueDTO> save(CatalogueDTO catalogueDTO) {
        LOG.debug("Request to save Catalogue : {}", catalogueDTO);
        return catalogueRepository.save(catalogueMapper.toEntity(catalogueDTO)).map(catalogueMapper::toDto);
    }

    @Override
    public Mono<CatalogueDTO> update(CatalogueDTO catalogueDTO) {
        LOG.debug("Request to update Catalogue : {}", catalogueDTO);
        return catalogueRepository.save(catalogueMapper.toEntity(catalogueDTO)).map(catalogueMapper::toDto);
    }

    @Override
    public Mono<CatalogueDTO> partialUpdate(CatalogueDTO catalogueDTO) {
        LOG.debug("Request to partially update Catalogue : {}", catalogueDTO);

        return catalogueRepository
            .findById(catalogueDTO.getId())
            .map(existingCatalogue -> {
                catalogueMapper.partialUpdate(existingCatalogue, catalogueDTO);

                return existingCatalogue;
            })
            .flatMap(catalogueRepository::save)
            .map(catalogueMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<CatalogueDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Catalogues");
        return catalogueRepository.findAllBy(pageable).map(catalogueMapper::toDto);
    }

    public Mono<Long> countAll() {
        return catalogueRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<CatalogueDTO> findOne(Long id) {
        LOG.debug("Request to get Catalogue : {}", id);
        return catalogueRepository.findById(id).map(catalogueMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete Catalogue : {}", id);
        return catalogueRepository.deleteById(id);
    }
}
