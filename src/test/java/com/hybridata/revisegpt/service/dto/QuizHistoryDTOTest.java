package com.hybridata.revisegpt.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.hybridata.revisegpt.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class QuizHistoryDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(QuizHistoryDTO.class);
        QuizHistoryDTO quizHistoryDTO1 = new QuizHistoryDTO();
        quizHistoryDTO1.setId(1L);
        QuizHistoryDTO quizHistoryDTO2 = new QuizHistoryDTO();
        assertThat(quizHistoryDTO1).isNotEqualTo(quizHistoryDTO2);
        quizHistoryDTO2.setId(quizHistoryDTO1.getId());
        assertThat(quizHistoryDTO1).isEqualTo(quizHistoryDTO2);
        quizHistoryDTO2.setId(2L);
        assertThat(quizHistoryDTO1).isNotEqualTo(quizHistoryDTO2);
        quizHistoryDTO1.setId(null);
        assertThat(quizHistoryDTO1).isNotEqualTo(quizHistoryDTO2);
    }
}
