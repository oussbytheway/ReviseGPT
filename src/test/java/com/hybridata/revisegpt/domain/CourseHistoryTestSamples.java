package com.hybridata.revisegpt.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class CourseHistoryTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static CourseHistory getCourseHistorySample1() {
        return new CourseHistory()
            .id(1L)
            .generatedResponse("generatedResponse1")
            .generatedFileUrl("generatedFileUrl1")
            .inputFileUrl("inputFileUrl1")
            .tokenUsage("tokenUsage1")
            .responseTimeMs(1)
            .feedbackRating(1);
    }

    public static CourseHistory getCourseHistorySample2() {
        return new CourseHistory()
            .id(2L)
            .generatedResponse("generatedResponse2")
            .generatedFileUrl("generatedFileUrl2")
            .inputFileUrl("inputFileUrl2")
            .tokenUsage("tokenUsage2")
            .responseTimeMs(2)
            .feedbackRating(2);
    }

    public static CourseHistory getCourseHistoryRandomSampleGenerator() {
        return new CourseHistory()
            .id(longCount.incrementAndGet())
            .generatedResponse(UUID.randomUUID().toString())
            .generatedFileUrl(UUID.randomUUID().toString())
            .inputFileUrl(UUID.randomUUID().toString())
            .tokenUsage(UUID.randomUUID().toString())
            .responseTimeMs(intCount.incrementAndGet())
            .feedbackRating(intCount.incrementAndGet());
    }
}
