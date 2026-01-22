package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class FileRecordTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static FileRecord getFileRecordSample1() {
        return new FileRecord().id(1L).fileName("fileName1").filePath("filePath1").fileType("fileType1");
    }

    public static FileRecord getFileRecordSample2() {
        return new FileRecord().id(2L).fileName("fileName2").filePath("filePath2").fileType("fileType2");
    }

    public static FileRecord getFileRecordRandomSampleGenerator() {
        return new FileRecord()
            .id(longCount.incrementAndGet())
            .fileName(UUID.randomUUID().toString())
            .filePath(UUID.randomUUID().toString())
            .fileType(UUID.randomUUID().toString());
    }
}
