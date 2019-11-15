package com.quim.tfm.similarity.service;

import com.quim.tfm.similarity.entity.Requirement;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class PreprocessService {

    private static final String FIELD_NAME = "NLP-PREPROCESS";
    private static final String STANDARD = "standard";
    private static final String STOPWORD = "stop";
    private static final String STEM = "porterstem";
    private static final String LOWERCASE = "lowercase";

    private static final CharSequence[] specialChars = {"\\n","\\t","\\r"};

    private Analyzer analyzer;

    public PreprocessService() {
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

    public void preprocessRequirement(Requirement r) {
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

    public void preprocessRequirementList(List<Requirement> requirements) {
        for (Requirement r : requirements) {
            preprocessRequirement(r);
        }
    }
}
