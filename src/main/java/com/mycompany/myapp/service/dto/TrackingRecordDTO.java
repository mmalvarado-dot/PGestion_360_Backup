package com.mycompany.myapp.service.dto;

import com.mycompany.myapp.domain.enumeration.TrackingActionType;
// Asegúrate de que estos imports existan.
// Si ChangeRequestDTO, ResponsibleDTO, etc. están en este mismo paquete, no hace falta importarlos explícitamente,
// pero UserDTO suele necesitar import si es la versión estándar.
import com.mycompany.myapp.service.dto.UserDTO;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

// Si usas AdminUserDTO, cambia el import y el tipo abajo.

/**
 * A DTO for the {@link com.mycompany.myapp.domain.TrackingRecord} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TrackingRecordDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    private Instant changeDate;

    @NotNull(message = "must not be null")
    private String status;

    private String comments;

    private TrackingActionType actionType;

    // --- RELACIONES COMO OBJETOS COMPLETOS ---
    private ChangeRequestDTO changeRequest;

    private ResponsibleDTO responsible;

    // Lo estándar en JHipster es UserDTO para relaciones.
    // Si tu mapper devuelve AdminUserDTO, cambia esto a AdminUserDTO.
    private UserDTO user;

    // ¡ESTO ES LO QUE ARREGLA TU PROBLEMA!
    private DepartmentDTO department;

    // --- GETTERS Y SETTERS ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getChangeDate() {
        return changeDate;
    }

    public void setChangeDate(Instant changeDate) {
        this.changeDate = changeDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public TrackingActionType getActionType() {
        return actionType;
    }

    public void setActionType(TrackingActionType actionType) {
        this.actionType = actionType;
    }

    public ChangeRequestDTO getChangeRequest() {
        return changeRequest;
    }

    public void setChangeRequest(ChangeRequestDTO changeRequest) {
        this.changeRequest = changeRequest;
    }

    public ResponsibleDTO getResponsible() {
        return responsible;
    }

    public void setResponsible(ResponsibleDTO responsible) {
        this.responsible = responsible;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public DepartmentDTO getDepartment() {
        return department;
    }

    public void setDepartment(DepartmentDTO department) {
        this.department = department;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TrackingRecordDTO)) return false;
        TrackingRecordDTO trackingRecordDTO = (TrackingRecordDTO) o;
        if (this.id == null) return false;
        return Objects.equals(this.id, trackingRecordDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    @Override
    public String toString() {
        return (
            "TrackingRecordDTO{" +
            "id=" +
            getId() +
            ", changeDate='" +
            getChangeDate() +
            "'" +
            ", status='" +
            getStatus() +
            "'" +
            ", comments='" +
            getComments() +
            "'" +
            ", actionType='" +
            getActionType() +
            "'" +
            ", changeRequest=" +
            (getChangeRequest() != null ? getChangeRequest().getId() : "null") +
            ", responsible=" +
            (getResponsible() != null ? getResponsible().getId() : "null") +
            ", user=" +
            (getUser() != null ? getUser().getId() : "null") +
            ", department=" +
            (getDepartment() != null ? getDepartment().getId() : "null") +
            "}"
        );
    }
}
