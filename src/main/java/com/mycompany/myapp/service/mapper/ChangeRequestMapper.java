package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.ChangeRequest;
import com.mycompany.myapp.domain.ItemCatalogue;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.service.dto.ChangeRequestDTO;
import com.mycompany.myapp.service.dto.ItemCatalogueDTO;
import com.mycompany.myapp.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ChangeRequest} and its DTO {@link ChangeRequestDTO}.
 */
@Mapper(componentModel = "spring")
public interface ChangeRequestMapper extends EntityMapper<ChangeRequestDTO, ChangeRequest> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userId")
    @Mapping(target = "itemCatalogue", source = "itemCatalogue", qualifiedByName = "itemCatalogueId")
    ChangeRequestDTO toDto(ChangeRequest s);

    @Named("userId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserId(User user);

    @Named("itemCatalogueId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ItemCatalogueDTO toDtoItemCatalogueId(ItemCatalogue itemCatalogue);
}
