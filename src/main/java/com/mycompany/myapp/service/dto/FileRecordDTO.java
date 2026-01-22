package com.mycompany.myapp.service.dto;

import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.FileRecord} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class FileRecordDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    private String fileName;

    @NotNull(message = "must not be null")
    private String filePath;

    @NotNull(message = "must not be null")
    private String fileType;

    @Lob
    private byte[] content;

    private String contentContentType;

    private ChangeRequestDTO changeRequest;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public String getContentContentType() {
        return contentContentType;
    }

    public void setContentContentType(String contentContentType) {
        this.contentContentType = contentContentType;
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
        if (!(o instanceof FileRecordDTO)) {
            return false;
        }

        FileRecordDTO fileRecordDTO = (FileRecordDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, fileRecordDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FileRecordDTO{" +
            "id=" + getId() +
            ", fileName='" + getFileName() + "'" +
            ", filePath='" + getFilePath() + "'" +
            ", fileType='" + getFileType() + "'" +
            ", content='" + getContent() + "'" +
            ", changeRequest=" + getChangeRequest() +
            "}";
    }
}
