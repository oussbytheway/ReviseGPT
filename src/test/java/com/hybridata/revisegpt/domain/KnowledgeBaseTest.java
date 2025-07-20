package com.hybridata.revisegpt.domain;

import static com.hybridata.revisegpt.domain.KnowledgeBaseTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.hybridata.revisegpt.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class KnowledgeBaseTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(KnowledgeBase.class);
        KnowledgeBase knowledgeBase1 = getKnowledgeBaseSample1();
        KnowledgeBase knowledgeBase2 = new KnowledgeBase();
        assertThat(knowledgeBase1).isNotEqualTo(knowledgeBase2);

        knowledgeBase2.setId(knowledgeBase1.getId());
        assertThat(knowledgeBase1).isEqualTo(knowledgeBase2);

        knowledgeBase2 = getKnowledgeBaseSample2();
        assertThat(knowledgeBase1).isNotEqualTo(knowledgeBase2);
    }
}
