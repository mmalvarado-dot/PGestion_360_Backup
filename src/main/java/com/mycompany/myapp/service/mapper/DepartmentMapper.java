package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Department;
import com.mycompany.myapp.service.dto.DepartmentDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Department} and its DTO {@link DepartmentDTO}.
 */
@Mapper(componentModel = "spring")
public interface DepartmentMapper extends EntityMapper<DepartmentDTO, Department> {
    @Mapping(target = "parentDepartment", source = "parentDepartment", qualifiedByName = "departmentId")
    DepartmentDTO toDto(Department s);

    @Named("departmentId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    DepartmentDTO toDtoDepartmentId(Department department);
}
