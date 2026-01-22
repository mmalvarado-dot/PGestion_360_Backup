package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Responsible;
import com.mycompany.myapp.service.dto.ResponsibleDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Responsible} and its DTO {@link ResponsibleDTO}.
 */
@Mapper(componentModel = "spring")
public interface ResponsibleMapper extends EntityMapper<ResponsibleDTO, Responsible> {}
