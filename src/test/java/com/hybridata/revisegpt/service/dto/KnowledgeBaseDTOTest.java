package com.hybridata.revisegpt.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.hybridata.revisegpt.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class KnowledgeBaseDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(KnowledgeBaseDTO.class);
        KnowledgeBaseDTO knowledgeBaseDTO1 = new KnowledgeBaseDTO();
        knowledgeBaseDTO1.setId(1L);
        KnowledgeBaseDTO knowledgeBaseDTO2 = new KnowledgeBaseDTO();
        assertThat(knowledgeBaseDTO1).isNotEqualTo(knowledgeBaseDTO2);
        knowledgeBaseDTO2.setId(knowledgeBaseDTO1.getId());
        assertThat(knowledgeBaseDTO1).isEqualTo(knowledgeBaseDTO2);
        knowledgeBaseDTO2.setId(2L);
        assertThat(knowledgeBaseDTO1).isNotEqualTo(knowledgeBaseDTO2);
        knowledgeBaseDTO1.setId(null);
        assertThat(knowledgeBaseDTO1).isNotEqualTo(knowledgeBaseDTO2);
    }
}
