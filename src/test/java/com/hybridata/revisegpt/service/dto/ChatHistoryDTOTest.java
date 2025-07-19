package com.hybridata.revisegpt.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.hybridata.revisegpt.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ChatHistoryDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ChatHistoryDTO.class);
        ChatHistoryDTO chatHistoryDTO1 = new ChatHistoryDTO();
        chatHistoryDTO1.setId(1L);
        ChatHistoryDTO chatHistoryDTO2 = new ChatHistoryDTO();
        assertThat(chatHistoryDTO1).isNotEqualTo(chatHistoryDTO2);
        chatHistoryDTO2.setId(chatHistoryDTO1.getId());
        assertThat(chatHistoryDTO1).isEqualTo(chatHistoryDTO2);
        chatHistoryDTO2.setId(2L);
        assertThat(chatHistoryDTO1).isNotEqualTo(chatHistoryDTO2);
        chatHistoryDTO1.setId(null);
        assertThat(chatHistoryDTO1).isNotEqualTo(chatHistoryDTO2);
    }
}
