package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.ChangeRequest;
import com.mycompany.myapp.domain.ItemCatalogue;
import com.mycompany.myapp.domain.Responsible;
import com.mycompany.myapp.service.dto.ChangeRequestDTO;
import com.mycompany.myapp.service.dto.ItemCatalogueDTO;
import com.mycompany.myapp.service.dto.ResponsibleDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ChangeRequest} and its DTO {@link ChangeRequestDTO}.
 */
@Mapper(componentModel = "spring")
public interface ChangeRequestMapper extends EntityMapper<ChangeRequestDTO, ChangeRequest> {
    @Mapping(target = "responsible", source = "responsible", qualifiedByName = "responsibleId")
    @Mapping(target = "itemCatalogue", source = "itemCatalogue", qualifiedByName = "itemCatalogueId")
    ChangeRequestDTO toDto(ChangeRequest s);

    @Named("responsibleId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    ResponsibleDTO toDtoResponsibleId(Responsible responsible);

    @Named("itemCatalogueId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ItemCatalogueDTO toDtoItemCatalogueId(ItemCatalogue itemCatalogue);
}
