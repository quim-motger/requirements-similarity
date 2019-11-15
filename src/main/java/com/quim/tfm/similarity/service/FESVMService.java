package com.quim.tfm.similarity.service;

import com.quim.tfm.similarity.entity.Requirement;
import com.quim.tfm.similarity.model.Duplicate;
import com.quim.tfm.similarity.model.DuplicateFeatures;
import edu.stanford.nlp.pipeline.Annotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class FESVMService {

    private static final Logger logger = LoggerFactory.getLogger(FESVMService.class);

    private double WS;
    private double WD;

    @Autowired
    private RequirementService requirementService;
    @Autowired
    private StanfordPreprocessService stanfordPreprocessService;

    public FESVMService() {
        WS = 3.0;
        WD = 1.0;
    }

    public void train(List<Duplicate> duplicates) {

    }

    public void test(List<Duplicate> duplicates) {
        HashMap<Duplicate, DuplicateFeatures> duplicateFeaturesHashMap = new HashMap<>();
        for (Duplicate d : duplicates) {

            Requirement r1 = requirementService.getRequirement(d.getReq1Id());
            Requirement r2 = requirementService.getRequirement(d.getReq2Id());

            Annotation summaryReq1 = stanfordPreprocessService.preprocess(r1.getSummary());
            Annotation descriptionReq1 = stanfordPreprocessService.preprocess(r1.getDescription());
            Annotation summaryReq2 = stanfordPreprocessService.preprocess(r2.getSummary());
            Annotation descriptionReq2 = stanfordPreprocessService.preprocess(r2.getDescription());

            DuplicateFeatures duplicateFeatures = extractFeatures(summaryReq1, summaryReq2, descriptionReq1, descriptionReq2);
            duplicateFeaturesHashMap.put(d, duplicateFeatures);
        }
    }

    private DuplicateFeatures extractFeatures(Annotation summaryReq1, Annotation summaryReq2, Annotation descriptionReq1, Annotation descriptionReq2) {
        DuplicateFeatures duplicateFeatures = new DuplicateFeatures();
        duplicateFeatures.setWordOverlapScore(wordOverlapScore(summaryReq1, summaryReq2, descriptionReq1, descriptionReq2));
        //TODO other features
        return duplicateFeatures;
    }

    private double wordOverlapScore(Annotation summaryReq1, Annotation summaryReq2, Annotation descriptionReq1, Annotation descriptionReq2) {
        double wordOverlapScoreSummary = wordOverlapPartialScore(summaryReq1, summaryReq2);
        double wordOverlapScoreDescription = wordOverlapPartialScore(descriptionReq1, descriptionReq2);

        return wordOverlapScoreSummary * WS +
                wordOverlapScoreDescription * WD;
    }

    private double wordOverlapPartialScore(Annotation req1, Annotation req2) {
        List<String> tokensReq1 = stanfordPreprocessService.getUniqueTokens(req1);
        List<String> tokensReq2 = stanfordPreprocessService.getUniqueTokens(req2);

        int intersection = (int) tokensReq1.stream().filter(tokensReq2::contains).count();
        int bagSize = Math.max(tokensReq1.size(), tokensReq2.size());

        return (double) intersection / (double) bagSize;

    }

}
