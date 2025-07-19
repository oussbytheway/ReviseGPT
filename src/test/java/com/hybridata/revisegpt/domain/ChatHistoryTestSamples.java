package com.hybridata.revisegpt.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ChatHistoryTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static ChatHistory getChatHistorySample1() {
        return new ChatHistory()
            .id(1L)
            .prompt("prompt1")
            .generatedResponse("generatedResponse1")
            .tokenUsage(1)
            .responseTimeMs(1)
            .feedbackRating(1);
    }

    public static ChatHistory getChatHistorySample2() {
        return new ChatHistory()
            .id(2L)
            .prompt("prompt2")
            .generatedResponse("generatedResponse2")
            .tokenUsage(2)
            .responseTimeMs(2)
            .feedbackRating(2);
    }

    public static ChatHistory getChatHistoryRandomSampleGenerator() {
        return new ChatHistory()
            .id(longCount.incrementAndGet())
            .prompt(UUID.randomUUID().toString())
            .generatedResponse(UUID.randomUUID().toString())
            .tokenUsage(intCount.incrementAndGet())
            .responseTimeMs(intCount.incrementAndGet())
            .feedbackRating(intCount.incrementAndGet());
    }
}
