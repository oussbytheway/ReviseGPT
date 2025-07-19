package com.hybridata.revisegpt.domain;

import static com.hybridata.revisegpt.domain.ChatHistoryTestSamples.*;
import static com.hybridata.revisegpt.domain.SessionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.hybridata.revisegpt.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ChatHistoryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ChatHistory.class);
        ChatHistory chatHistory1 = getChatHistorySample1();
        ChatHistory chatHistory2 = new ChatHistory();
        assertThat(chatHistory1).isNotEqualTo(chatHistory2);

        chatHistory2.setId(chatHistory1.getId());
        assertThat(chatHistory1).isEqualTo(chatHistory2);

        chatHistory2 = getChatHistorySample2();
        assertThat(chatHistory1).isNotEqualTo(chatHistory2);
    }

    @Test
    void sessionTest() {
        ChatHistory chatHistory = getChatHistoryRandomSampleGenerator();
        Session sessionBack = getSessionRandomSampleGenerator();

        chatHistory.setSession(sessionBack);
        assertThat(chatHistory.getSession()).isEqualTo(sessionBack);

        chatHistory.session(null);
        assertThat(chatHistory.getSession()).isNull();
    }
}
