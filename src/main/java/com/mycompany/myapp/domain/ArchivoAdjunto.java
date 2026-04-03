package com.mycompany.myapp.domain;

import java.io.Serializable;
import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("archivo_adjunto")
public class ArchivoAdjunto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("nombre_original")
    private String nombreOriginal;

    @Column("ruta_fisica")
    private String rutaFisica;

    @Column("tamano_bytes")
    private Long tamanoBytes;

    @Column("fecha_subida")
    private LocalDateTime fechaSubida;

    @Column("change_request_id")
    private Long changeRequestId;

    // --- GETTERS Y SETTERS ---
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ArchivoAdjunto id(Long id) {
        this.id = id;
        return this;
    }

    public String getNombreOriginal() {
        return nombreOriginal;
    }

    public void setNombreOriginal(String nombreOriginal) {
        this.nombreOriginal = nombreOriginal;
    }

    public ArchivoAdjunto nombreOriginal(String nombreOriginal) {
        this.nombreOriginal = nombreOriginal;
        return this;
    }

    public String getRutaFisica() {
        return rutaFisica;
    }

    public void setRutaFisica(String rutaFisica) {
        this.rutaFisica = rutaFisica;
    }

    public ArchivoAdjunto rutaFisica(String rutaFisica) {
        this.rutaFisica = rutaFisica;
        return this;
    }

    public Long getTamanoBytes() {
        return tamanoBytes;
    }

    public void setTamanoBytes(Long tamanoBytes) {
        this.tamanoBytes = tamanoBytes;
    }

    public ArchivoAdjunto tamanoBytes(Long tamanoBytes) {
        this.tamanoBytes = tamanoBytes;
        return this;
    }

    public LocalDateTime getFechaSubida() {
        return fechaSubida;
    }

    public void setFechaSubida(LocalDateTime fechaSubida) {
        this.fechaSubida = fechaSubida;
    }

    public ArchivoAdjunto fechaSubida(LocalDateTime fechaSubida) {
        this.fechaSubida = fechaSubida;
        return this;
    }

    public Long getChangeRequestId() {
        return changeRequestId;
    }

    public void setChangeRequestId(Long changeRequestId) {
        this.changeRequestId = changeRequestId;
    }

    public ArchivoAdjunto changeRequestId(Long changeRequestId) {
        this.changeRequestId = changeRequestId;
        return this;
    }
}
