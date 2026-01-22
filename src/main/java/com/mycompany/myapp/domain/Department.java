package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Department.
 */
@Table("department")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Department implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("department_name")
    private String departmentName;

    @Column("field")
    private String field;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "parentDepartment" }, allowSetters = true)
    private Department parentDepartment;

    @Column("parent_department_id")
    private Long parentDepartmentId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Department id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDepartmentName() {
        return this.departmentName;
    }

    public Department departmentName(String departmentName) {
        this.setDepartmentName(departmentName);
        return this;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getField() {
        return this.field;
    }

    public Department field(String field) {
        this.setField(field);
        return this;
    }

    public void setField(String field) {
        this.field = field;
    }

    public Department getParentDepartment() {
        return this.parentDepartment;
    }

    public void setParentDepartment(Department department) {
        this.parentDepartment = department;
        this.parentDepartmentId = department != null ? department.getId() : null;
    }

    public Department parentDepartment(Department department) {
        this.setParentDepartment(department);
        return this;
    }

    public Long getParentDepartmentId() {
        return this.parentDepartmentId;
    }

    public void setParentDepartmentId(Long department) {
        this.parentDepartmentId = department;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Department)) {
            return false;
        }
        return getId() != null && getId().equals(((Department) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Department{" +
            "id=" + getId() +
            ", departmentName='" + getDepartmentName() + "'" +
            ", field='" + getField() + "'" +
            "}";
    }
}
