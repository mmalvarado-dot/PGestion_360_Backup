package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A FileRecord.
 */
@Table("file_record")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class FileRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("file_name")
    private String fileName;

    @NotNull(message = "must not be null")
    @Column("file_path")
    private String filePath;

    @NotNull(message = "must not be null")
    @Column("file_type")
    private String fileType;

    @Column("content")
    private byte[] content;

    @Column("content_content_type")
    private String contentContentType;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "responsible", "itemCatalogue" }, allowSetters = true)
    private ChangeRequest changeRequest;

    @Column("change_request_id")
    private Long changeRequestId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public FileRecord id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return this.fileName;
    }

    public FileRecord fileName(String fileName) {
        this.setFileName(fileName);
        return this;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return this.filePath;
    }

    public FileRecord filePath(String filePath) {
        this.setFilePath(filePath);
        return this;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileType() {
        return this.fileType;
    }

    public FileRecord fileType(String fileType) {
        this.setFileType(fileType);
        return this;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public byte[] getContent() {
        return this.content;
    }

    public FileRecord content(byte[] content) {
        this.setContent(content);
        return this;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public String getContentContentType() {
        return this.contentContentType;
    }

    public FileRecord contentContentType(String contentContentType) {
        this.contentContentType = contentContentType;
        return this;
    }

    public void setContentContentType(String contentContentType) {
        this.contentContentType = contentContentType;
    }

    public ChangeRequest getChangeRequest() {
        return this.changeRequest;
    }

    public void setChangeRequest(ChangeRequest changeRequest) {
        this.changeRequest = changeRequest;
        this.changeRequestId = changeRequest != null ? changeRequest.getId() : null;
    }

    public FileRecord changeRequest(ChangeRequest changeRequest) {
        this.setChangeRequest(changeRequest);
        return this;
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
        if (!(o instanceof FileRecord)) {
            return false;
        }
        return getId() != null && getId().equals(((FileRecord) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FileRecord{" +
            "id=" + getId() +
            ", fileName='" + getFileName() + "'" +
            ", filePath='" + getFilePath() + "'" +
            ", fileType='" + getFileType() + "'" +
            ", content='" + getContent() + "'" +
            ", contentContentType='" + getContentContentType() + "'" +
            "}";
    }
}
