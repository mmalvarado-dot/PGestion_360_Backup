package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mycompany.myapp.domain.enumeration.Impacto;
import com.mycompany.myapp.domain.enumeration.prioridad;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A ChangeRequest.
 */
@Table("change_request")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ChangeRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("title")
    private String title;

    @NotNull(message = "must not be null")
    @Column("description")
    private String description;

    @NotNull(message = "must not be null")
    @Column("created_date")
    private LocalDate createdDate;

    @Column("updated_date")
    private LocalDate updatedDate;

    @Column("priority")
    private prioridad priority;

    @Column("impact")
    private Impacto impact;

    @NotNull(message = "must not be null")
    @Column("status")
    private String status;

    @Column("fecha_entrega")
    private LocalDate fechaEntrega;

    @Column("observaciones")
    private String observaciones;

    @Column("solicitante")
    private String solicitante;

    @Column("departamento")
    private String departamento;

    @org.springframework.data.annotation.Transient
    private User user;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "catalogue" }, allowSetters = true)
    private ItemCatalogue itemCatalogue;

    @Column("user_id")
    private Long userId;

    @Column("item_catalogue_id")
    private Long itemCatalogueId;

    public Long getId() {
        return this.id;
    }

    public ChangeRequest id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public ChangeRequest title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return this.description;
    }

    public ChangeRequest description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getCreatedDate() {
        return this.createdDate;
    }

    public ChangeRequest createdDate(LocalDate createdDate) {
        this.setCreatedDate(createdDate);
        return this;
    }

    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDate getUpdatedDate() {
        return this.updatedDate;
    }

    public ChangeRequest updatedDate(LocalDate updatedDate) {
        this.setUpdatedDate(updatedDate);
        return this;
    }

    public void setUpdatedDate(LocalDate updatedDate) {
        this.updatedDate = updatedDate;
    }

    public prioridad getPriority() {
        return this.priority;
    }

    public ChangeRequest priority(prioridad priority) {
        this.setPriority(priority);
        return this;
    }

    public void setPriority(prioridad priority) {
        this.priority = priority;
    }

    public Impacto getImpact() {
        return this.impact;
    }

    public ChangeRequest impact(Impacto impact) {
        this.setImpact(impact);
        return this;
    }

    public void setImpact(Impacto impact) {
        this.impact = impact;
    }

    public String getStatus() {
        return this.status;
    }

    public ChangeRequest status(String status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getFechaEntrega() {
        return this.fechaEntrega;
    }

    public ChangeRequest fechaEntrega(LocalDate fechaEntrega) {
        this.setFechaEntrega(fechaEntrega);
        return this;
    }

    public void setFechaEntrega(LocalDate fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }

    public String getObservaciones() {
        return this.observaciones;
    }

    public ChangeRequest observaciones(String observaciones) {
        this.setObservaciones(observaciones);
        return this;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getSolicitante() {
        return this.solicitante;
    }

    public ChangeRequest solicitante(String solicitante) {
        this.setSolicitante(solicitante);
        return this;
    }

    public void setSolicitante(String solicitante) {
        this.solicitante = solicitante;
    }

    public String getDepartamento() {
        return this.departamento;
    }

    public ChangeRequest departamento(String departamento) {
        this.setDepartamento(departamento);
        return this;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
        this.userId = user != null ? user.getId() : null;
    }

    public ChangeRequest user(User user) {
        this.setUser(user);
        return this;
    }

    public Long getUserId() {
        return this.userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public ItemCatalogue getItemCatalogue() {
        return this.itemCatalogue;
    }

    public void setItemCatalogue(ItemCatalogue itemCatalogue) {
        this.itemCatalogue = itemCatalogue;
        this.itemCatalogueId = itemCatalogue != null ? itemCatalogue.getId() : null;
    }

    public ChangeRequest itemCatalogue(ItemCatalogue itemCatalogue) {
        this.setItemCatalogue(itemCatalogue);
        return this;
    }

    public Long getItemCatalogueId() {
        return this.itemCatalogueId;
    }

    public void setItemCatalogueId(Long itemCatalogue) {
        this.itemCatalogueId = itemCatalogue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ChangeRequest)) {
            return false;
        }
        return getId() != null && getId().equals(((ChangeRequest) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return (
            "ChangeRequest{" +
            "id=" +
            getId() +
            ", title='" +
            getTitle() +
            "'" +
            ", description='" +
            getDescription() +
            "'" +
            ", createdDate='" +
            getCreatedDate() +
            "'" +
            ", updatedDate='" +
            getUpdatedDate() +
            "'" +
            ", priority='" +
            getPriority() +
            "'" +
            ", impact='" +
            getImpact() +
            "'" +
            ", status='" +
            getStatus() +
            "'" +
            ", fechaEntrega='" +
            getFechaEntrega() +
            "'" +
            ", observaciones='" +
            getObservaciones() +
            "'" +
            ", solicitante='" +
            getSolicitante() +
            "'" +
            ", departamento='" +
            getDepartamento() +
            "'" +
            "}"
        );
    }
}
