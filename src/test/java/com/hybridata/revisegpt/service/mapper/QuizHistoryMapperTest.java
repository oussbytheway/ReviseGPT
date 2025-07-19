package com.hybridata.revisegpt.service.mapper;

import static com.hybridata.revisegpt.domain.QuizHistoryAsserts.*;
import static com.hybridata.revisegpt.domain.QuizHistoryTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class QuizHistoryMapperTest {

    private QuizHistoryMapper quizHistoryMapper;

    @BeforeEach
    void setUp() {
        quizHistoryMapper = new QuizHistoryMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getQuizHistorySample1();
        var actual = quizHistoryMapper.toEntity(quizHistoryMapper.toDto(expected));
        assertQuizHistoryAllPropertiesEquals(expected, actual);
    }
}
