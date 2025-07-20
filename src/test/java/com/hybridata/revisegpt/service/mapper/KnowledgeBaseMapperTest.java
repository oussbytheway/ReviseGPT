package com.hybridata.revisegpt.service.mapper;

import static com.hybridata.revisegpt.domain.KnowledgeBaseAsserts.*;
import static com.hybridata.revisegpt.domain.KnowledgeBaseTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class KnowledgeBaseMapperTest {

    private KnowledgeBaseMapper knowledgeBaseMapper;

    @BeforeEach
    void setUp() {
        knowledgeBaseMapper = new KnowledgeBaseMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getKnowledgeBaseSample1();
        var actual = knowledgeBaseMapper.toEntity(knowledgeBaseMapper.toDto(expected));
        assertKnowledgeBaseAllPropertiesEquals(expected, actual);
    }
}
