package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.repository.ItemCatalogueRepository;
import com.mycompany.myapp.service.ItemCatalogueService;
import com.mycompany.myapp.service.dto.ItemCatalogueDTO;
import com.mycompany.myapp.service.mapper.ItemCatalogueMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.ItemCatalogue}.
 */
@Service
@Transactional
public class ItemCatalogueServiceImpl implements ItemCatalogueService {

    private static final Logger LOG = LoggerFactory.getLogger(ItemCatalogueServiceImpl.class);

    private final ItemCatalogueRepository itemCatalogueRepository;

    private final ItemCatalogueMapper itemCatalogueMapper;

    public ItemCatalogueServiceImpl(ItemCatalogueRepository itemCatalogueRepository, ItemCatalogueMapper itemCatalogueMapper) {
        this.itemCatalogueRepository = itemCatalogueRepository;
        this.itemCatalogueMapper = itemCatalogueMapper;
    }

    @Override
    public Mono<ItemCatalogueDTO> save(ItemCatalogueDTO itemCatalogueDTO) {
        LOG.debug("Request to save ItemCatalogue : {}", itemCatalogueDTO);
        return itemCatalogueRepository.save(itemCatalogueMapper.toEntity(itemCatalogueDTO)).map(itemCatalogueMapper::toDto);
    }

    @Override
    public Mono<ItemCatalogueDTO> update(ItemCatalogueDTO itemCatalogueDTO) {
        LOG.debug("Request to update ItemCatalogue : {}", itemCatalogueDTO);
        return itemCatalogueRepository.save(itemCatalogueMapper.toEntity(itemCatalogueDTO)).map(itemCatalogueMapper::toDto);
    }

    @Override
    public Mono<ItemCatalogueDTO> partialUpdate(ItemCatalogueDTO itemCatalogueDTO) {
        LOG.debug("Request to partially update ItemCatalogue : {}", itemCatalogueDTO);

        return itemCatalogueRepository
            .findById(itemCatalogueDTO.getId())
            .map(existingItemCatalogue -> {
                itemCatalogueMapper.partialUpdate(existingItemCatalogue, itemCatalogueDTO);

                return existingItemCatalogue;
            })
            .flatMap(itemCatalogueRepository::save)
            .map(itemCatalogueMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<ItemCatalogueDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all ItemCatalogues");
        return itemCatalogueRepository.findAllBy(pageable).map(itemCatalogueMapper::toDto);
    }

    public Mono<Long> countAll() {
        return itemCatalogueRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<ItemCatalogueDTO> findOne(Long id) {
        LOG.debug("Request to get ItemCatalogue : {}", id);
        return itemCatalogueRepository.findById(id).map(itemCatalogueMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete ItemCatalogue : {}", id);
        return itemCatalogueRepository.deleteById(id);
    }
}
