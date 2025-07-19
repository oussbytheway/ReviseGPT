package com.hybridata.revisegpt.service.mapper;

import static com.hybridata.revisegpt.domain.ChatHistoryAsserts.*;
import static com.hybridata.revisegpt.domain.ChatHistoryTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ChatHistoryMapperTest {

    private ChatHistoryMapper chatHistoryMapper;

    @BeforeEach
    void setUp() {
        chatHistoryMapper = new ChatHistoryMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getChatHistorySample1();
        var actual = chatHistoryMapper.toEntity(chatHistoryMapper.toDto(expected));
        assertChatHistoryAllPropertiesEquals(expected, actual);
    }
}
