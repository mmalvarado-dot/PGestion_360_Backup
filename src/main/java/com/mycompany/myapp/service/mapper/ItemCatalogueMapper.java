package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Catalogue;
import com.mycompany.myapp.domain.ItemCatalogue;
import com.mycompany.myapp.service.dto.CatalogueDTO;
import com.mycompany.myapp.service.dto.ItemCatalogueDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ItemCatalogue} and its DTO {@link ItemCatalogueDTO}.
 */
@Mapper(componentModel = "spring")
public interface ItemCatalogueMapper extends EntityMapper<ItemCatalogueDTO, ItemCatalogue> {
    @Mapping(target = "catalogue", source = "catalogue", qualifiedByName = "catalogueId")
    ItemCatalogueDTO toDto(ItemCatalogue s);

    @Named("catalogueId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CatalogueDTO toDtoCatalogueId(Catalogue catalogue);
}
