package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.ChangeRequest;
import com.mycompany.myapp.domain.Department;
import com.mycompany.myapp.domain.TrackingRecord;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.service.dto.ChangeRequestDTO;
import com.mycompany.myapp.service.dto.DepartmentDTO;
import com.mycompany.myapp.service.dto.TrackingRecordDTO;
import com.mycompany.myapp.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity TrackingRecord and its DTO TrackingRecordDTO.
 */
@Mapper(componentModel = "spring", uses = { UserMapper.class, ChangeRequestMapper.class, DepartmentMapper.class })
public interface TrackingRecordMapper extends EntityMapper<TrackingRecordDTO, TrackingRecord> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userLoginManual")
    @Mapping(target = "changeRequest", source = "changeRequest", qualifiedByName = "changeRequestId")
    @Mapping(target = "department", source = "department", qualifiedByName = "departmentName")
    TrackingRecordDTO toDto(TrackingRecord s);

    @Named("partialUpdate")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdate(@MappingTarget TrackingRecord entity, TrackingRecordDTO dto);

    // --- MÉTODOS DE SOPORTE PARA MAPEO DE RELACIONES ---

    // 🚀 SOLUCIÓN: Mapeo manual explícito para obligar a extraer ID y Login
    @Named("userLoginManual")
    default UserDTO toDtoUserLoginManual(User user) {
        if (user == null) {
            return null;
        }
        UserDTO userDto = new UserDTO();
        userDto.setId(user.getId());
        userDto.setLogin(user.getLogin());
        return userDto;
    }

    @Named("changeRequestId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ChangeRequestDTO toDtoChangeRequestId(ChangeRequest changeRequest);

    @Named("departmentName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "departmentName", source = "departmentName")
    DepartmentDTO toDtoDepartmentName(Department department);
}
