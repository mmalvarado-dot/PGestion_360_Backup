package com.mycompany.myapp.domain;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A ItemCatalogue.
 */
@Table("item_catalogue")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ItemCatalogue implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("name")
    private String name;

    @NotNull(message = "must not be null")
    @Column("code")
    private String code;

    @NotNull(message = "must not be null")
    @Column("catalogue_code")
    private String catalogueCode;

    @Column("active")
    private Boolean active;

    @org.springframework.data.annotation.Transient
    private Catalogue catalogue;

    @Column("catalogue_id")
    private Long catalogueId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ItemCatalogue id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public ItemCatalogue name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return this.code;
    }

    public ItemCatalogue code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCatalogueCode() {
        return this.catalogueCode;
    }

    public ItemCatalogue catalogueCode(String catalogueCode) {
        this.setCatalogueCode(catalogueCode);
        return this;
    }

    public void setCatalogueCode(String catalogueCode) {
        this.catalogueCode = catalogueCode;
    }

    public Boolean getActive() {
        return this.active;
    }

    public ItemCatalogue active(Boolean active) {
        this.setActive(active);
        return this;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Catalogue getCatalogue() {
        return this.catalogue;
    }

    public void setCatalogue(Catalogue catalogue) {
        this.catalogue = catalogue;
        this.catalogueId = catalogue != null ? catalogue.getId() : null;
    }

    public ItemCatalogue catalogue(Catalogue catalogue) {
        this.setCatalogue(catalogue);
        return this;
    }

    public Long getCatalogueId() {
        return this.catalogueId;
    }

    public void setCatalogueId(Long catalogue) {
        this.catalogueId = catalogue;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ItemCatalogue)) {
            return false;
        }
        return getId() != null && getId().equals(((ItemCatalogue) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ItemCatalogue{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", code='" + getCode() + "'" +
            ", catalogueCode='" + getCatalogueCode() + "'" +
            ", active='" + getActive() + "'" +
            "}";
    }
}
