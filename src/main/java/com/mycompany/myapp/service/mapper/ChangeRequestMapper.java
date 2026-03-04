package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.ChangeRequest;
import com.mycompany.myapp.domain.ItemCatalogue;
import com.mycompany.myapp.domain.User; // <-- Importamos User
import com.mycompany.myapp.service.dto.ChangeRequestDTO;
import com.mycompany.myapp.service.dto.ItemCatalogueDTO;
import com.mycompany.myapp.service.dto.UserDTO; // <-- Importamos UserDTO
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ChangeRequest} and its DTO {@link ChangeRequestDTO}.
 */
@Mapper(componentModel = "spring")
public interface ChangeRequestMapper extends EntityMapper<ChangeRequestDTO, ChangeRequest> {
    // Cambiamos 'responsible' por 'user'
    @Mapping(target = "user", source = "user", qualifiedByName = "userId")
    @Mapping(target = "itemCatalogue", source = "itemCatalogue", qualifiedByName = "itemCatalogueId")
    ChangeRequestDTO toDto(ChangeRequest s);

    // Creamos el traductor para el User
    @Named("userId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login") // Traemos el nombre de usuario
    UserDTO toDtoUserId(User user);

    @Named("itemCatalogueId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ItemCatalogueDTO toDtoItemCatalogueId(ItemCatalogue itemCatalogue);
}
