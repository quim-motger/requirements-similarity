package com.quim.tfm.similarity.service;

import com.quim.tfm.similarity.entity.Requirement;
import com.quim.tfm.similarity.utils.TimingTools;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
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

    private static final String sentenceDetectorPath = "src/main/resources/model"+ File.separator+"en-sent.bin";

    private SentenceDetectorME sentenceDetector;

    private static final CharSequence[] specialChars = {"\\n","\\t","\\r"};

    private Analyzer analyzer;
    private Analyzer analyzerWithoutStemming;

    public PreprocessService() {
        try {
            analyzer = CustomAnalyzer.builder()
                    .withTokenizer(STANDARD)
                    .addTokenFilter(STOPWORD)
                    .addTokenFilter(STEM)
                    .addTokenFilter(LOWERCASE)
                    .build();
            analyzerWithoutStemming = CustomAnalyzer.builder()
                    .withTokenizer(STANDARD)
                    .addTokenFilter(STOPWORD)
                    .addTokenFilter(LOWERCASE)
                    .build();
            sentenceDetector = new SentenceDetectorME(new SentenceModel(new FileInputStream(sentenceDetectorPath)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void preprocessRequirement(Requirement r) {
        r.setSummaryTokens(analyze(r.getSummary()).stream().toArray(String[]::new));
        r.setDescriptionTokens(analyze(r.getDescription()).stream().toArray(String[]::new));
    }

    public List<String> analyzerWithoutStemming(String text) {
        return analyzer(text, analyzerWithoutStemming);
    }

    private List<String> analyzer(String text, Analyzer analyzerWithoutStemming) {
        List<String> tokens = new ArrayList<>();
        try {
            String sentences[] = sentenceDetector.sentDetect(text);

            for (String sentence : sentences) {
                for (CharSequence cs : specialChars)
                    sentence = sentence.replace(cs, " ");
                TokenStream tokenStream = analyzerWithoutStemming.tokenStream(FIELD_NAME, sentence);
                CharTermAttribute attr = tokenStream.addAttribute(CharTermAttribute.class);
                tokenStream.reset();

                while (tokenStream.incrementToken()) {
                    if (!attr.toString().isEmpty()) tokens.add(attr.toString());
                }
                tokenStream.close();
                tokens.add(".");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return tokens;
    }

    private List<String> analyze(String text) {
        return analyzer(text, analyzer);
    }

    public void preprocessRequirementList(List<Requirement> requirements) {
        TimingTools.startTimer("BASIC-NLP-PIPELINE");
        for (Requirement r : requirements) {
            preprocessRequirement(r);
        }
        TimingTools.endTimer("BASIC-NLP-PIPELINE");
    }
}
