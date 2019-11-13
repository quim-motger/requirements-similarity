package com.quim.tfm.similarity.service;

import com.quim.tfm.similarity.entity.Requirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class IDFService {

    private static final Logger logger = LoggerFactory.getLogger(IDFService.class);

    private double avgTokenSummaryBagSize;
    private double avgTokenDescriptionBagSize;

    public HashMap<String, Integer> getDocumentFrequency(List<Requirement> requirements) {
        HashMap<String, Integer> documentFrequencyMap = new HashMap<>();

        //Initialize structure of avg token bag size
        initAvgTokenBagSize(requirements);

        for (Requirement r : requirements) {
            String[] terms = Arrays.stream(
                    Stream.of(r.getSummaryTokens(), r.getDescriptionTokens())
                            .flatMap(Stream::of)
                            .toArray(String[]::new))
                    .distinct().toArray(String[]::new);
            for (String term : terms) {
                if (documentFrequencyMap.containsKey(term))
                    documentFrequencyMap.put(term, documentFrequencyMap.get(term) + 1);
                else
                    documentFrequencyMap.put(term, 1);
            }
        }
        return documentFrequencyMap;
    }

    public double idf(String term, HashMap<String, Integer> documentFrequency, int corpusSize) {
        return documentFrequency.containsKey(term) ? Math.log((double) corpusSize / (double) documentFrequency.get(term)) : 0;
    }

    public double tf(String term, Requirement req1, double wsf, double bsf, double wdf, double bdf) {
        //TODO ocurrences of a term can be pre-computed
        double summaryNum = wsf * ocurrences(term, req1.getSummaryTokens());
        double summaryDen = 1 - bsf + bsf * (double) req1.getSummaryTokens().length / avgTokenSummaryBagSize;

        double descriptionNum = wdf * ocurrences(term, req1.getDescriptionTokens());
        double descriptionDen = 1 - bdf + bdf * (double) req1.getDescriptionTokens().length / avgTokenDescriptionBagSize;

        double summary = summaryDen == 0 ? 0 : summaryNum / summaryDen;
        double description = descriptionDen == 0 ? 0 : descriptionNum / descriptionDen;
        return summary + description;
    }

    public double tfq(String term, Requirement req2, double wsf, double wdf) {
        double summaryTfq = wsf * ocurrences(term, req2.getSummaryTokens());
        double descriptionTfq = wdf * ocurrences(term, req2.getDescriptionTokens());
        return summaryTfq + descriptionTfq;
    }

    public void initAvgTokenBagSize(List<Requirement> requirements) {
        avgTokenSummaryBagSize = extractAvg(requirements.stream().map(Requirement::getSummaryTokens).collect(Collectors.toList()));
        avgTokenDescriptionBagSize = extractAvg(requirements.stream().map(Requirement::getDescriptionTokens).collect(Collectors.toList()));
    }

    private double extractAvg(List<String[]> collect) {
        int sum = 0;
        for (String[] tokenBag : collect) {
            sum += tokenBag.length;
        }
        return (double) sum / collect.size();
    }

    private double ocurrences(String term, String[] summaryTokens) {
        return Collections.frequency(Arrays.asList(summaryTokens), term);
    }
}
