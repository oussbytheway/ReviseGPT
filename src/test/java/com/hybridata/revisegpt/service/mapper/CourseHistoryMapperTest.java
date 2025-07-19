package com.hybridata.revisegpt.service.mapper;

import static com.hybridata.revisegpt.domain.CourseHistoryAsserts.*;
import static com.hybridata.revisegpt.domain.CourseHistoryTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CourseHistoryMapperTest {

    private CourseHistoryMapper courseHistoryMapper;

    @BeforeEach
    void setUp() {
        courseHistoryMapper = new CourseHistoryMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getCourseHistorySample1();
        var actual = courseHistoryMapper.toEntity(courseHistoryMapper.toDto(expected));
        assertCourseHistoryAllPropertiesEquals(expected, actual);
    }
}
