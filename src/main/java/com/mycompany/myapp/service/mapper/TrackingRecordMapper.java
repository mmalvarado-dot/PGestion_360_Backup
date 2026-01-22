package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.ChangeRequest;
import com.mycompany.myapp.domain.Responsible;
import com.mycompany.myapp.domain.TrackingRecord;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.service.dto.ChangeRequestDTO;
import com.mycompany.myapp.service.dto.ResponsibleDTO;
import com.mycompany.myapp.service.dto.TrackingRecordDTO;
import com.mycompany.myapp.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link TrackingRecord} and its DTO {@link TrackingRecordDTO}.
 */
@Mapper(componentModel = "spring")
public interface TrackingRecordMapper extends EntityMapper<TrackingRecordDTO, TrackingRecord> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userId")
    @Mapping(target = "responsible", source = "responsible", qualifiedByName = "responsibleId")
    @Mapping(target = "changeRequest", source = "changeRequest", qualifiedByName = "changeRequestId")
    TrackingRecordDTO toDto(TrackingRecord s);

    @Named("userId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UserDTO toDtoUserId(User user);

    @Named("responsibleId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ResponsibleDTO toDtoResponsibleId(Responsible responsible);

    @Named("changeRequestId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ChangeRequestDTO toDtoChangeRequestId(ChangeRequest changeRequest);
}
