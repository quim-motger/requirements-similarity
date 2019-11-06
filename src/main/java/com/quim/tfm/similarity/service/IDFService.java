package com.quim.tfm.similarity.service;

import com.quim.tfm.similarity.entity.Requirement;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class IDFService {

    private static final double WF = 1.0;
    private static final double BF = 0.5;

    public HashMap<String, Integer> getDocumentFrequency(List<Requirement> requirements) {
        HashMap<String, Integer> documentFrequencyMap = new HashMap<>();

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

    public double tf(String term, List<Requirement> requirements, Requirement req1) {
        double summaryNum = WF * ocurrences(term, req1.getSummaryTokens());
        double summaryDen = 1 - BF + BF * (double) req1.getSummaryTokens().length /
                avgTokenBagSize(requirements.stream().map(Requirement::getSummaryTokens).collect(Collectors.toList()));

        double descriptionNum = WF * ocurrences(term, req1.getDescriptionTokens());
        double descriptionDen = 1 - BF + BF * (double) req1.getDescriptionTokens().length /
                avgTokenBagSize(requirements.stream().map(Requirement::getDescriptionTokens).collect(Collectors.toList()));

        return summaryNum / summaryDen + descriptionNum / descriptionDen;
    }

    private double avgTokenBagSize(List<String[]> tokenBagList) {
        int sum = 0;
        for (String[] tokenBag : tokenBagList) {
            sum += tokenBag.length;
        }
        return (double) sum / tokenBagList.size();
    }

    private double ocurrences(String term, String[] summaryTokens) {
        return Collections.frequency(Arrays.asList(summaryTokens), term);
    }
}
