package com.hybridata.revisegpt.domain;

import static com.hybridata.revisegpt.domain.CourseHistoryTestSamples.*;
import static com.hybridata.revisegpt.domain.SessionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.hybridata.revisegpt.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CourseHistoryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CourseHistory.class);
        CourseHistory courseHistory1 = getCourseHistorySample1();
        CourseHistory courseHistory2 = new CourseHistory();
        assertThat(courseHistory1).isNotEqualTo(courseHistory2);

        courseHistory2.setId(courseHistory1.getId());
        assertThat(courseHistory1).isEqualTo(courseHistory2);

        courseHistory2 = getCourseHistorySample2();
        assertThat(courseHistory1).isNotEqualTo(courseHistory2);
    }

    @Test
    void sessionTest() {
        CourseHistory courseHistory = getCourseHistoryRandomSampleGenerator();
        Session sessionBack = getSessionRandomSampleGenerator();

        courseHistory.setSession(sessionBack);
        assertThat(courseHistory.getSession()).isEqualTo(sessionBack);

        courseHistory.session(null);
        assertThat(courseHistory.getSession()).isNull();
    }
}
