package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class TrackingRecordTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static TrackingRecord getTrackingRecordSample1() {
        return new TrackingRecord().id(1L).status("status1").comments("comments1");
    }

    public static TrackingRecord getTrackingRecordSample2() {
        return new TrackingRecord().id(2L).status("status2").comments("comments2");
    }

    public static TrackingRecord getTrackingRecordRandomSampleGenerator() {
        return new TrackingRecord()
            .id(longCount.incrementAndGet())
            .status(UUID.randomUUID().toString())
            .comments(UUID.randomUUID().toString());
    }
}
