package com.hybridata.revisegpt.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.hybridata.revisegpt.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CourseHistoryDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CourseHistoryDTO.class);
        CourseHistoryDTO courseHistoryDTO1 = new CourseHistoryDTO();
        courseHistoryDTO1.setId(1L);
        CourseHistoryDTO courseHistoryDTO2 = new CourseHistoryDTO();
        assertThat(courseHistoryDTO1).isNotEqualTo(courseHistoryDTO2);
        courseHistoryDTO2.setId(courseHistoryDTO1.getId());
        assertThat(courseHistoryDTO1).isEqualTo(courseHistoryDTO2);
        courseHistoryDTO2.setId(2L);
        assertThat(courseHistoryDTO1).isNotEqualTo(courseHistoryDTO2);
        courseHistoryDTO1.setId(null);
        assertThat(courseHistoryDTO1).isNotEqualTo(courseHistoryDTO2);
    }
}
