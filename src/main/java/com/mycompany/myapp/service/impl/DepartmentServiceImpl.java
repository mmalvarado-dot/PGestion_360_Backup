package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.repository.DepartmentRepository;
import com.mycompany.myapp.service.DepartmentService;
import com.mycompany.myapp.service.dto.DepartmentDTO;
import com.mycompany.myapp.service.mapper.DepartmentMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.Department}.
 */
@Service
@Transactional
public class DepartmentServiceImpl implements DepartmentService {

    private static final Logger LOG = LoggerFactory.getLogger(DepartmentServiceImpl.class);

    private final DepartmentRepository departmentRepository;

    private final DepartmentMapper departmentMapper;

    public DepartmentServiceImpl(DepartmentRepository departmentRepository, DepartmentMapper departmentMapper) {
        this.departmentRepository = departmentRepository;
        this.departmentMapper = departmentMapper;
    }

    @Override
    public Mono<DepartmentDTO> save(DepartmentDTO departmentDTO) {
        LOG.debug("Request to save Department : {}", departmentDTO);
        return departmentRepository.save(departmentMapper.toEntity(departmentDTO)).map(departmentMapper::toDto);
    }

    @Override
    public Mono<DepartmentDTO> update(DepartmentDTO departmentDTO) {
        LOG.debug("Request to update Department : {}", departmentDTO);
        return departmentRepository.save(departmentMapper.toEntity(departmentDTO)).map(departmentMapper::toDto);
    }

    @Override
    public Mono<DepartmentDTO> partialUpdate(DepartmentDTO departmentDTO) {
        LOG.debug("Request to partially update Department : {}", departmentDTO);

        return departmentRepository
            .findById(departmentDTO.getId())
            .map(existingDepartment -> {
                departmentMapper.partialUpdate(existingDepartment, departmentDTO);

                return existingDepartment;
            })
            .flatMap(departmentRepository::save)
            .map(departmentMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<DepartmentDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Departments");
        return departmentRepository.findAllBy(pageable).map(departmentMapper::toDto);
    }

    public Mono<Long> countAll() {
        return departmentRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<DepartmentDTO> findOne(Long id) {
        LOG.debug("Request to get Department : {}", id);
        return departmentRepository.findById(id).map(departmentMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete Department : {}", id);
        return departmentRepository.deleteById(id);
    }
}
