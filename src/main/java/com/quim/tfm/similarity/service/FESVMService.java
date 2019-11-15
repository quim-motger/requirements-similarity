package com.quim.tfm.similarity.service;

import com.quim.tfm.similarity.entity.Requirement;
import com.quim.tfm.similarity.model.Duplicate;
import com.quim.tfm.similarity.model.DuplicateFeatures;
import com.quim.tfm.similarity.model.Token;
import edu.stanford.nlp.pipeline.Annotation;
import net.sf.extjwnl.JWNLException;
import net.sf.extjwnl.data.IndexWord;
import net.sf.extjwnl.data.POS;
import net.sf.extjwnl.data.Synset;
import net.sf.extjwnl.data.Word;
import net.sf.extjwnl.dictionary.Dictionary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FESVMService {

    private static final Logger logger = LoggerFactory.getLogger(FESVMService.class);

    private double WS;
    private double WD;

    @Autowired
    private RequirementService requirementService;
    @Autowired
    private StanfordPreprocessService stanfordPreprocessService;

    private Dictionary dictionary;

    public FESVMService() {
        try {
            dictionary = Dictionary.getDefaultResourceInstance();
        } catch (JWNLException e) {
            e.printStackTrace();
        }

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
        duplicateFeatures.setUnigramMatchScore(unigramMatchScore(summaryReq1, summaryReq2, descriptionReq1, descriptionReq2, 1));
        duplicateFeatures.setBigramMatchScore(unigramMatchScore(summaryReq1, summaryReq2, descriptionReq1, descriptionReq2, 2));
        //TODO other features
        return duplicateFeatures;
    }

    private double unigramMatchScore(Annotation summaryReq1, Annotation summaryReq2, Annotation descriptionReq1, Annotation descriptionReq2, int n) {
        double unigramMatchScoreSummary = ngramMatchPartialScore(summaryReq1, summaryReq2, n);
        double unigramMatchScoreDescription = ngramMatchPartialScore(descriptionReq1, descriptionReq2, n);

        return unigramMatchScoreSummary * WS +
                unigramMatchScoreDescription * WD;
    }

    private double ngramMatchPartialScore(Annotation req1, Annotation req2, int n) {
        List<String> tokensReq1 = stanfordPreprocessService.getNGrams(req1, n);
        List<String> tokensReq2 = stanfordPreprocessService.getNGrams(req2, n);
        return jaccardSimilarity(tokensReq1, tokensReq2);
    }

    private double jaccardSimilarity(List<String> req1, List<String> req2) {
        return (double) req1.stream().distinct().filter(req2::contains).collect(Collectors.toSet()).size() /
                (double) (req1.size() + req2.size());
    }

    private double wordOverlapScore(Annotation summaryReq1, Annotation summaryReq2, Annotation descriptionReq1, Annotation descriptionReq2) {
        double wordOverlapScoreSummary = wordOverlapPartialScore(summaryReq1, summaryReq2);
        double wordOverlapScoreDescription = wordOverlapPartialScore(descriptionReq1, descriptionReq2);

        return wordOverlapScoreSummary * WS +
                wordOverlapScoreDescription * WD;
    }

    private double wordOverlapPartialScore(Annotation req1, Annotation req2) {
        List<Token> tokensReq1 = stanfordPreprocessService.getUniqueTokens(req1);
        List<Token> tokensReq2 = stanfordPreprocessService.getUniqueTokens(req2);

        int intersection = intersection(tokensReq1, tokensReq2);
        int bagSize = tokensReq1.size() + tokensReq2.size();

        return (double) intersection / (double) bagSize;

    }

    private int intersection(List<Token> tokensReq1, List<Token> tokensReq2) {
        int synMatchReq1 = findSynonymMatch(tokensReq1, tokensReq2);
        int synMatchReq2 = findSynonymMatch(tokensReq2, tokensReq1);
        return synMatchReq1 + synMatchReq2;
    }

    private boolean synonymy = false;

    private int findSynonymMatch(List<Token> tokensReq1, List<Token> tokensReq2) {
        int sum = 0;
        List<String> lemmasReq2 = tokensReq2.stream().map(Token::getLemma).collect(Collectors.toList());
        for (Token t1 : tokensReq1) {
            List<String> synonyms;
            if (synonymy) synonyms = getSynsetsOf(t1);
            else synonyms = Arrays.asList(t1.getLemma());
            if (synonyms.stream().anyMatch(lemmasReq2::contains)) {
                ++sum;
            }
        }
        return sum;
    }

    public List<String> getSynsetsOf(Token token) {
        List<String> synonyms = new ArrayList<>();
        try {
            POS pos = parsePos(token.getPos());
            if (pos != null) {
                IndexWord indexWord = dictionary.lookupIndexWord(pos, token.getLemma());
                synonyms = indexWordToSet(indexWord);
            }
        } catch (JWNLException e) {
            e.printStackTrace();
        }
        return synonyms;
    }

    private POS parsePos(String pos) {
        if (pos.contains("NN")) {
            return POS.NOUN;
        } else if (pos.contains("VB")) {
            return POS.VERB;
        } else if (pos.equals("JJ")) {
            return POS.ADJECTIVE;
        } else if (pos.equals("RB")){
            return POS.ADVERB;
        } else return null;
    }

    private List<String> indexWordToSet(IndexWord indexWord) {
        Set<Synset> ret = new LinkedHashSet<>();
        if (indexWord!=null) {
            ret.addAll(indexWord.getSenses());
        }
        List<String> tokens = new ArrayList<>();
        for (Synset synset : ret) {
            tokens.addAll(synset.getWords().stream().map(Word::getLemma).distinct().collect(Collectors.toList()));
        }
        return tokens.stream().distinct().collect(Collectors.toList());
    }

}
