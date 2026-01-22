package com.mycompany.myapp.domain;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Responsible.
 */
@Table("responsible")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Responsible implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("name")
    private String name;

    @NotNull(message = "must not be null")
    @Column("position")
    private String position;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Responsible id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Responsible name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPosition() {
        return this.position;
    }

    public Responsible position(String position) {
        this.setPosition(position);
        return this;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Responsible)) {
            return false;
        }
        return getId() != null && getId().equals(((Responsible) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Responsible{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", position='" + getPosition() + "'" +
            "}";
    }
}
