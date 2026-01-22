package com.mycompany.myapp.service.dto;

import com.mycompany.myapp.domain.enumeration.Impacto;
import com.mycompany.myapp.domain.enumeration.prioridad;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.ChangeRequest} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ChangeRequestDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    private String title;

    @NotNull(message = "must not be null")
    private String description;

    @NotNull(message = "must not be null")
    private LocalDate createdDate;

    private LocalDate updatedDate;

    private prioridad priority;

    private Impacto impact;

    @NotNull(message = "must not be null")
    private String status;

    private LocalDate fechaEntrega;

    @Lob
    private String observaciones;

    @Lob
    private byte[] archivoAdjunto;

    private String archivoAdjuntoContentType;

    private String solicitante;

    private String departamento;

    private ResponsibleDTO responsible;

    private ItemCatalogueDTO itemCatalogue;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDate getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(LocalDate updatedDate) {
        this.updatedDate = updatedDate;
    }

    public prioridad getPriority() {
        return priority;
    }

    public void setPriority(prioridad priority) {
        this.priority = priority;
    }

    public Impacto getImpact() {
        return impact;
    }

    public void setImpact(Impacto impact) {
        this.impact = impact;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getFechaEntrega() {
        return fechaEntrega;
    }

    public void setFechaEntrega(LocalDate fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public byte[] getArchivoAdjunto() {
        return archivoAdjunto;
    }

    public void setArchivoAdjunto(byte[] archivoAdjunto) {
        this.archivoAdjunto = archivoAdjunto;
    }

    public String getArchivoAdjuntoContentType() {
        return archivoAdjuntoContentType;
    }

    public void setArchivoAdjuntoContentType(String archivoAdjuntoContentType) {
        this.archivoAdjuntoContentType = archivoAdjuntoContentType;
    }

    public String getSolicitante() {
        return solicitante;
    }

    public void setSolicitante(String solicitante) {
        this.solicitante = solicitante;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public ResponsibleDTO getResponsible() {
        return responsible;
    }

    public void setResponsible(ResponsibleDTO responsible) {
        this.responsible = responsible;
    }

    public ItemCatalogueDTO getItemCatalogue() {
        return itemCatalogue;
    }

    public void setItemCatalogue(ItemCatalogueDTO itemCatalogue) {
        this.itemCatalogue = itemCatalogue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ChangeRequestDTO)) {
            return false;
        }

        ChangeRequestDTO changeRequestDTO = (ChangeRequestDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, changeRequestDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ChangeRequestDTO{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", description='" + getDescription() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", updatedDate='" + getUpdatedDate() + "'" +
            ", priority='" + getPriority() + "'" +
            ", impact='" + getImpact() + "'" +
            ", status='" + getStatus() + "'" +
            ", fechaEntrega='" + getFechaEntrega() + "'" +
            ", observaciones='" + getObservaciones() + "'" +
            ", archivoAdjunto='" + getArchivoAdjunto() + "'" +
            ", solicitante='" + getSolicitante() + "'" +
            ", departamento='" + getDepartamento() + "'" +
            ", responsible=" + getResponsible() +
            ", itemCatalogue=" + getItemCatalogue() +
            "}";
    }
}
