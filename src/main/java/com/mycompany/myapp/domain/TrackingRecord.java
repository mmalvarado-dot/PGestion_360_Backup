package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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

    @NotNull(message = "must not be null")
    @Column("change_date")
    private Instant changeDate;

    @NotNull(message = "must not be null")
    @Column("status")
    private String status;

    @Column("action_type")
    private TrackingActionType actionType;

    @Column("comments")
    private String comments;

    // --- Relaciones (IDs en Base de Datos) ---

    // ¡Aquí está nuestro Usuario Real!
    @Column("user_id")
    private Long userId;

    // 🧹 ELIMINADO: responsibleId

    @Column("change_request_id")
    private Long changeRequestId;

    @Column("department_id")
    private Long departmentId;

    // --- Objetos Transients (Para que Java y el Mapper los vean) ---

    @org.springframework.data.annotation.Transient
    private User user;

    // 🧹 ELIMINADO: Objeto Transient Responsible

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "itemCatalogue" }, allowSetters = true)
    private ChangeRequest changeRequest;

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

    // 🧹 ELIMINADOS: Getters y Setters de responsibleId

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

    // 🧹 ELIMINADOS: Getters y Setters del objeto Responsible

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

    public Department getDepartment() {
        return this.department;
    }

    public void setDepartment(Department department) {
        this.department = department;
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

    @Override
    public String toString() {
        return (
            "TrackingRecord{" +
            "id=" +
            getId() +
            ", changeDate='" +
            getChangeDate() +
            "'" +
            ", status='" +
            getStatus() +
            "'" +
            ", actionType='" +
            getActionType() +
            "'" +
            ", comments='" +
            getComments() +
            "'" +
            "}"
        );
    }
}
