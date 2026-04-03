package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.FileRecord;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link FileRecord}, with proper type conversions.
 */
@Service
public class FileRecordRowMapper implements BiFunction<Row, String, FileRecord> {

    private final ColumnConverter converter;

    public FileRecordRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link FileRecord} stored in the database.
     */
    @Override
    public FileRecord apply(Row row, String prefix) {
        FileRecord entity = new FileRecord();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setFileName(converter.fromRow(row, prefix + "_file_name", String.class));
        entity.setFilePath(converter.fromRow(row, prefix + "_file_path", String.class));
        entity.setFileType(converter.fromRow(row, prefix + "_file_type", String.class));
        entity.setContentContentType(converter.fromRow(row, prefix + "_content_content_type", String.class));
        entity.setContent(converter.fromRow(row, prefix + "_content", byte[].class));

        entity.setUploadDate(converter.fromRow(row, prefix + "_upload_date", Instant.class));

        entity.setChangeRequestId(converter.fromRow(row, prefix + "_change_request_id", Long.class));
        return entity;
    }
}
