package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.ChangeRequest;
import com.mycompany.myapp.domain.FileRecord;
import com.mycompany.myapp.service.dto.ChangeRequestDTO;
import com.mycompany.myapp.service.dto.FileRecordDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link FileRecord} and its DTO {@link FileRecordDTO}.
 */
@Mapper(componentModel = "spring")
public interface FileRecordMapper extends EntityMapper<FileRecordDTO, FileRecord> {
    @Mapping(target = "changeRequest", source = "changeRequest", qualifiedByName = "changeRequestId")
    FileRecordDTO toDto(FileRecord s);

    @Named("changeRequestId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ChangeRequestDTO toDtoChangeRequestId(ChangeRequest changeRequest);
}
