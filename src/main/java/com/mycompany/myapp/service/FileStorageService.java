package com.mycompany.myapp.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class FileStorageService {

    private final Path rootLocation = Paths.get("uploads");

    public FileStorageService() {
        try {
            if (!Files.exists(rootLocation)) {
                Files.createDirectories(rootLocation);
            }
        } catch (IOException e) {
            throw new RuntimeException("No se pudo inicializar la carpeta de subida de archivos", e);
        }
    }

    public String save(byte[] fileData, String originalFilename) {
        try {
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            String uniqueFilename = UUID.randomUUID().toString() + extension;

            Path destinationFile = this.rootLocation.resolve(uniqueFilename);

            Files.write(destinationFile, fileData);

            return destinationFile.toAbsolutePath().toString();
        } catch (IOException e) {
            throw new RuntimeException("Fallo al guardar el archivo en el disco duro.", e);
        }
    }
}
