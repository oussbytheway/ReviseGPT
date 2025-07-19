package com.hybridata.revisegpt.service.mapper;

import static com.hybridata.revisegpt.domain.SessionAsserts.*;
import static com.hybridata.revisegpt.domain.SessionTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SessionMapperTest {

    private SessionMapper sessionMapper;

    @BeforeEach
    void setUp() {
        sessionMapper = new SessionMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getSessionSample1();
        var actual = sessionMapper.toEntity(sessionMapper.toDto(expected));
        assertSessionAllPropertiesEquals(expected, actual);
    }
}
