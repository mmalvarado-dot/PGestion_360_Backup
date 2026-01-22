package com.mycompany.myapp.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.Responsible} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ResponsibleDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    private String name;

    @NotNull(message = "must not be null")
    private String position;

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

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ResponsibleDTO)) {
            return false;
        }

        ResponsibleDTO responsibleDTO = (ResponsibleDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, responsibleDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ResponsibleDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", position='" + getPosition() + "'" +
            "}";
    }
}
