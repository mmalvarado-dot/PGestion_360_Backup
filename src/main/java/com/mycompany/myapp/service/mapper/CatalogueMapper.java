package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Catalogue;
import com.mycompany.myapp.service.dto.CatalogueDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Catalogue} and its DTO {@link CatalogueDTO}.
 */
@Mapper(componentModel = "spring")
public interface CatalogueMapper extends EntityMapper<CatalogueDTO, Catalogue> {}
