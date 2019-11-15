package com.quim.tfm.similarity;

import com.quim.tfm.similarity.service.BM25FService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class NLPreprocessingTest {

    @Autowired
    private BM25FService BM25FService;

    @Test
    void NLPreprocessPipeline() {


    }

}
