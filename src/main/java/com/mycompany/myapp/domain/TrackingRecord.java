package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
// 1. IMPORT NECESARIO PARA QUE EL MAPPER FUNCIONE
import com.mycompany.myapp.domain.Department;
import com.mycompany.myapp.domain.enumeration.TrackingActionType;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A TrackingRecord.
 */
@Table("tracking_record")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TrackingRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    // CAMBIO 1: Usamos Instant en vez de LocalDate
    @NotNull(message = "must not be null")
    @Column("change_date")
    private Instant changeDate;

    @NotNull(message = "must not be null")
    @Column("status")
    private String status;

    // CAMBIO 2: Aquí guardamos si fue EDICION o CAMBIO_ESTADO
    @Column("action_type")
    private TrackingActionType actionType;

    @Column("comments")
    private String comments;

    // --- Relaciones (IDs en Base de Datos) ---

    @Column("user_id")
    private Long userId;

    @Column("responsible_id")
    private Long responsibleId;

    @Column("change_request_id")
    private Long changeRequestId;

    // CAMBIO 3: ID del departamento
    @Column("department_id")
    private Long departmentId;

    // --- Objetos Transients (Para que Java y el Mapper los vean, pero no se guardan directo en tabla) ---

    @org.springframework.data.annotation.Transient
    private User user;

    @org.springframework.data.annotation.Transient
    private Responsible responsible;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "responsible", "itemCatalogue" }, allowSetters = true)
    private ChangeRequest changeRequest;

    // 2. AGREGADO: OBJETO DEPARTAMENTO (El Mapper busca esto)
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "trackingRecords" }, allowSetters = true)
    private Department department;

    // --- GETTERS Y SETTERS ---

    public Long getId() {
        return this.id;
    }

    public TrackingRecord id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getChangeDate() {
        return this.changeDate;
    }

    public TrackingRecord changeDate(Instant changeDate) {
        this.setChangeDate(changeDate);
        return this;
    }

    public void setChangeDate(Instant changeDate) {
        this.changeDate = changeDate;
    }

    public String getStatus() {
        return this.status;
    }

    public TrackingRecord status(String status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public TrackingActionType getActionType() {
        return this.actionType;
    }

    public TrackingRecord actionType(TrackingActionType actionType) {
        this.setActionType(actionType);
        return this;
    }

    public void setActionType(TrackingActionType actionType) {
        this.actionType = actionType;
    }

    public String getComments() {
        return this.comments;
    }

    public TrackingRecord comments(String comments) {
        this.setComments(comments);
        return this;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    // --- IDs ---

    public Long getUserId() {
        return this.userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getResponsibleId() {
        return this.responsibleId;
    }

    public void setResponsibleId(Long responsibleId) {
        this.responsibleId = responsibleId;
    }

    public Long getChangeRequestId() {
        return this.changeRequestId;
    }

    public void setChangeRequestId(Long changeRequestId) {
        this.changeRequestId = changeRequestId;
    }

    public Long getDepartmentId() {
        return this.departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    // --- Transients (Objetos Completos) ---

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
        this.userId = user != null ? user.getId() : null;
    }

    public TrackingRecord user(User user) {
        this.setUser(user);
        return this;
    }

    public Responsible getResponsible() {
        return this.responsible;
    }

    public void setResponsible(Responsible responsible) {
        this.responsible = responsible;
        this.responsibleId = responsible != null ? responsible.getId() : null;
    }

    public TrackingRecord responsible(Responsible responsible) {
        this.setResponsible(responsible);
        return this;
    }

    public ChangeRequest getChangeRequest() {
        return this.changeRequest;
    }

    public void setChangeRequest(ChangeRequest changeRequest) {
        this.changeRequest = changeRequest;
        this.changeRequestId = changeRequest != null ? changeRequest.getId() : null;
    }

    public TrackingRecord changeRequest(ChangeRequest changeRequest) {
        this.setChangeRequest(changeRequest);
        return this;
    }

    // 3. AGREGADO: MÉTODOS PARA EL DEPARTAMENTO (Esto quita el error rojo del Mapper)
    public Department getDepartment() {
        return this.department;
    }

    public void setDepartment(Department department) {
        this.department = department;
        // Importante: Al poner el objeto, actualizamos también el ID numérico
        this.departmentId = department != null ? department.getId() : null;
    }

    public TrackingRecord department(Department department) {
        this.setDepartment(department);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TrackingRecord)) {
            return false;
        }
        return getId() != null && getId().equals(((TrackingRecord) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TrackingRecord{" +
            "id=" + getId() +
            ", changeDate='" + getChangeDate() + "'" +
            ", status='" + getStatus() + "'" +
            ", actionType='" + getActionType() + "'" +
            ", comments='" + getComments() + "'" +
            "}";
    }
}
