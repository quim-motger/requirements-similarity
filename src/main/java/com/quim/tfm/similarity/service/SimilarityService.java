package com.quim.tfm.similarity.service;

import com.quim.tfm.similarity.entity.Requirement;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
public class SimilarityService {

    private static final Logger logger = LoggerFactory.getLogger(SimilarityService.class);

    private static final String FIELD_NAME = "NLP-PREPROCESS";
    private static final String STANDARD = "standard";
    private static final String STOPWORD = "stop";
    private static final String STEM = "porterstem";
    private static final String LOWERCASE = "lowercase";

    private static final CharSequence[] specialChars = {"\\n","\\t","\\r"};

    private static final double K1 = 2.0;

    @Autowired
    private RequirementService requirementService;

    @Autowired
    private IDFService idfService;

    private Analyzer analyzer;

    public SimilarityService() {
        try {
            analyzer = CustomAnalyzer.builder()
                    .withTokenizer(STANDARD)
                    .addTokenFilter(STOPWORD)
                    .addTokenFilter(STEM)
                    .addTokenFilter(LOWERCASE)
                    .build();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void preprocessRequirementList(List<Requirement> requirements) {
        for (Requirement r : requirements) {
            bm25fPreprocess(r);
        }
    }

    public void bm25f_req(Requirement requirement) {
        logger.info("Init BM25f Preprocess for requirement " + requirement.getId());
        bm25fPreprocess(requirement);

        List<Requirement> requirements = requirementService.getRequirements();
        requirements.add(requirement);

        HashMap<String, Integer> summaryDocumentFrequency = idfService.getDocumentFrequency(requirements);

        double maxScore = 0.0;
        String maxReq = "";
        for (Requirement compReq : requirements) {
            if (!compReq.getId().equals(requirement.getId())) {
                double bm25f_score = bm25f_textPair(requirements, requirement, compReq,
                        summaryDocumentFrequency, requirements.size());
                if (bm25f_score > maxScore) {
                    maxScore = bm25f_score;
                    maxReq = compReq.getId();
                }
            }
        }

        logger.info("Finished BM25f\t" + maxReq + "\t" + maxScore);

    }

    private List<String> getCollect(Requirement req1, Requirement req2) {
        String[] req1Tokens = Arrays.stream(
                Stream.of(req1.getSummaryTokens(), req1.getDescriptionTokens())
                        .flatMap(Stream::of)
                        .toArray(String[]::new))
                .distinct().toArray(String[]::new);
        String[] req2Tokens = Arrays.stream(
                Stream.of(req2.getSummaryTokens(), req2.getDescriptionTokens())
                        .flatMap(Stream::of)
                        .toArray(String[]::new))
                .distinct().toArray(String[]::new);
        return Arrays.stream(req1Tokens).distinct()
                .filter(Arrays.asList(req2Tokens)::contains)
                .collect(Collectors.toList());
    }

    private double bm25f_textPair(List<Requirement> requirements, Requirement req1, Requirement req2,
                                  HashMap<String, Integer> documentFrequency, int corpusSize) {
        List<String> tokensIntersection = getCollect(req1, req2);

        double res = 0.0;
        for (String term : tokensIntersection) {
            double idf = idfService.idf(term, documentFrequency, corpusSize);
            double tf = idfService.tf(term, requirements, req1);
            double tfd = tf / (K1 + tf);
            double wq = 1.0;
            //TODO algorithm execution
            res += idf * tfd * wq;
        }

        return res;
    }

    private void bm25fPreprocess(Requirement r) {
        r.setSummaryTokens(analyze(r.getSummary()).stream().toArray(String[]::new));
        r.setDescriptionTokens(analyze(r.getDescription()).stream().toArray(String[]::new));
    }

    private List<String> analyze(String text) {
        List<String> tokens = new ArrayList<>();
        try {
            for (CharSequence cs : specialChars)
                text = text.replace(cs, " ");
            TokenStream tokenStream = analyzer.tokenStream(FIELD_NAME, text);
            CharTermAttribute attr = tokenStream.addAttribute(CharTermAttribute.class);
            tokenStream.reset();

            while (tokenStream.incrementToken()) {
                tokens.add(attr.toString());
            }
            tokenStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tokens;
    }
}
