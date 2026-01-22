package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
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
    private LocalDate changeDate;

    @NotNull(message = "must not be null")
    @Column("status")
    private String status;

    @Column("comments")
    private String comments;

    @org.springframework.data.annotation.Transient
    private User user;

    @org.springframework.data.annotation.Transient
    private Responsible responsible;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "responsible", "itemCatalogue" }, allowSetters = true)
    private ChangeRequest changeRequest;

    @Column("user_id")
    private Long userId;

    @Column("responsible_id")
    private Long responsibleId;

    @Column("change_request_id")
    private Long changeRequestId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

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

    public LocalDate getChangeDate() {
        return this.changeDate;
    }

    public TrackingRecord changeDate(LocalDate changeDate) {
        this.setChangeDate(changeDate);
        return this;
    }

    public void setChangeDate(LocalDate changeDate) {
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

    public Long getUserId() {
        return this.userId;
    }

    public void setUserId(Long user) {
        this.userId = user;
    }

    public Long getResponsibleId() {
        return this.responsibleId;
    }

    public void setResponsibleId(Long responsible) {
        this.responsibleId = responsible;
    }

    public Long getChangeRequestId() {
        return this.changeRequestId;
    }

    public void setChangeRequestId(Long changeRequest) {
        this.changeRequestId = changeRequest;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

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
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TrackingRecord{" +
            "id=" + getId() +
            ", changeDate='" + getChangeDate() + "'" +
            ", status='" + getStatus() + "'" +
            ", comments='" + getComments() + "'" +
            "}";
    }
}
