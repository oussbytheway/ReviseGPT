package com.hybridata.revisegpt.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class KnowledgeBaseTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static KnowledgeBase getKnowledgeBaseSample1() {
        return new KnowledgeBase()
            .id(1L)
            .fileName("fileName1")
            .title("title1")
            .description("description1")
            .vectorId("vectorId1")
            .fileUrl("fileUrl1");
    }

    public static KnowledgeBase getKnowledgeBaseSample2() {
        return new KnowledgeBase()
            .id(2L)
            .fileName("fileName2")
            .title("title2")
            .description("description2")
            .vectorId("vectorId2")
            .fileUrl("fileUrl2");
    }

    public static KnowledgeBase getKnowledgeBaseRandomSampleGenerator() {
        return new KnowledgeBase()
            .id(longCount.incrementAndGet())
            .fileName(UUID.randomUUID().toString())
            .title(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString())
            .vectorId(UUID.randomUUID().toString())
            .fileUrl(UUID.randomUUID().toString());
    }
}
