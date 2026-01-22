package com.mycompany.myapp.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.TrackingRecord} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TrackingRecordDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    private LocalDate changeDate;

    @NotNull(message = "must not be null")
    private String status;

    private String comments;

    private UserDTO user;

    private ResponsibleDTO responsible;

    private ChangeRequestDTO changeRequest;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getChangeDate() {
        return changeDate;
    }

    public void setChangeDate(LocalDate changeDate) {
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

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public ResponsibleDTO getResponsible() {
        return responsible;
    }

    public void setResponsible(ResponsibleDTO responsible) {
        this.responsible = responsible;
    }

    public ChangeRequestDTO getChangeRequest() {
        return changeRequest;
    }

    public void setChangeRequest(ChangeRequestDTO changeRequest) {
        this.changeRequest = changeRequest;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TrackingRecordDTO)) {
            return false;
        }

        TrackingRecordDTO trackingRecordDTO = (TrackingRecordDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, trackingRecordDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TrackingRecordDTO{" +
            "id=" + getId() +
            ", changeDate='" + getChangeDate() + "'" +
            ", status='" + getStatus() + "'" +
            ", comments='" + getComments() + "'" +
            ", user=" + getUser() +
            ", responsible=" + getResponsible() +
            ", changeRequest=" + getChangeRequest() +
            "}";
    }
}
