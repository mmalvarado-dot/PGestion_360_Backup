package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.TrackingRecord;
import io.r2dbc.spi.Row;
import java.time.LocalDate;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link TrackingRecord}, with proper type conversions.
 */
@Service
public class TrackingRecordRowMapper implements BiFunction<Row, String, TrackingRecord> {

    private final ColumnConverter converter;

    public TrackingRecordRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link TrackingRecord} stored in the database.
     */
    @Override
    public TrackingRecord apply(Row row, String prefix) {
        TrackingRecord entity = new TrackingRecord();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setChangeDate(converter.fromRow(row, prefix + "_change_date", LocalDate.class));
        entity.setStatus(converter.fromRow(row, prefix + "_status", String.class));
        entity.setComments(converter.fromRow(row, prefix + "_comments", String.class));
        entity.setUserId(converter.fromRow(row, prefix + "_user_id", Long.class));
        entity.setResponsibleId(converter.fromRow(row, prefix + "_responsible_id", Long.class));
        entity.setChangeRequestId(converter.fromRow(row, prefix + "_change_request_id", Long.class));
        return entity;
    }
}
