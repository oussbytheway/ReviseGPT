package com.hybridata.revisegpt.domain;

import static com.hybridata.revisegpt.domain.QuizHistoryTestSamples.*;
import static com.hybridata.revisegpt.domain.SessionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.hybridata.revisegpt.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class QuizHistoryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(QuizHistory.class);
        QuizHistory quizHistory1 = getQuizHistorySample1();
        QuizHistory quizHistory2 = new QuizHistory();
        assertThat(quizHistory1).isNotEqualTo(quizHistory2);

        quizHistory2.setId(quizHistory1.getId());
        assertThat(quizHistory1).isEqualTo(quizHistory2);

        quizHistory2 = getQuizHistorySample2();
        assertThat(quizHistory1).isNotEqualTo(quizHistory2);
    }

    @Test
    void sessionTest() {
        QuizHistory quizHistory = getQuizHistoryRandomSampleGenerator();
        Session sessionBack = getSessionRandomSampleGenerator();

        quizHistory.setSession(sessionBack);
        assertThat(quizHistory.getSession()).isEqualTo(sessionBack);

        quizHistory.session(null);
        assertThat(quizHistory.getSession()).isNull();
    }
}
