package com.mycompany.myapp.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.ItemCatalogue} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ItemCatalogueDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    private String name;

    @NotNull(message = "must not be null")
    private String code;

    @NotNull(message = "must not be null")
    private String catalogueCode;

    private Boolean active;

    private CatalogueDTO catalogue;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCatalogueCode() {
        return catalogueCode;
    }

    public void setCatalogueCode(String catalogueCode) {
        this.catalogueCode = catalogueCode;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public CatalogueDTO getCatalogue() {
        return catalogue;
    }

    public void setCatalogue(CatalogueDTO catalogue) {
        this.catalogue = catalogue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ItemCatalogueDTO)) {
            return false;
        }

        ItemCatalogueDTO itemCatalogueDTO = (ItemCatalogueDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, itemCatalogueDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ItemCatalogueDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", code='" + getCode() + "'" +
            ", catalogueCode='" + getCatalogueCode() + "'" +
            ", active='" + getActive() + "'" +
            ", catalogue=" + getCatalogue() +
            "}";
    }
}
