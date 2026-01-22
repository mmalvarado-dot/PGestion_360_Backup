package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.FileRecordAsserts.*;
import static com.mycompany.myapp.domain.FileRecordTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FileRecordMapperTest {

    private FileRecordMapper fileRecordMapper;

    @BeforeEach
    void setUp() {
        fileRecordMapper = new FileRecordMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getFileRecordSample1();
        var actual = fileRecordMapper.toEntity(fileRecordMapper.toDto(expected));
        assertFileRecordAllPropertiesEquals(expected, actual);
    }
}
