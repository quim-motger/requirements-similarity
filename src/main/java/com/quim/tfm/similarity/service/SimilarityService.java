package com.quim.tfm.similarity.service;

import com.quim.tfm.similarity.entity.Requirement;
import com.quim.tfm.similarity.exception.NotFoundCustomException;
import com.quim.tfm.similarity.model.Duplicate;
import com.quim.tfm.similarity.model.Priority;
import com.quim.tfm.similarity.model.TrainTripletBM25F;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
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

    private HashMap<String, Double> freeParameters;

    @Autowired
    private RequirementService requirementService;

    @Autowired
    private IDFService idfService;

    private Analyzer analyzer;

    private List<Requirement> requirements;
    private HashMap<String, Integer> documentFrequency;

    public SimilarityService() {
        try {
            analyzer = CustomAnalyzer.builder()
                    .withTokenizer(STANDARD)
                    .addTokenFilter(STOPWORD)
                    .addTokenFilter(STEM)
                    .addTokenFilter(LOWERCASE)
                    .build();

            freeParameters = new HashMap<>();
            initFreeParameters();
            /*
            freeParameters.put("WF1", 1.163);
            freeParameters.put("WF2", 0.013);
            freeParameters.put("WF3", 2.285);
            freeParameters.put("WF4", 0.032);
            freeParameters.put("WF5", 0.772);
            freeParameters.put("WF6", 0.381);
            freeParameters.put("WF7", 2.427);

            freeParameters.put("WSF1", 2.980);
            freeParameters.put("WDF1", 0.287);
            freeParameters.put("BSF1", 0.703);
            freeParameters.put("BDF1", 1.000);
            freeParameters.put("K1F1", 2.000);
            freeParameters.put("K3F1", 0.382);

            freeParameters.put("WSF2", 2.999);
            freeParameters.put("WDF2", 0.994);
            freeParameters.put("BSF2", 0.504);
            freeParameters.put("BDF2", 1.000);
            freeParameters.put("K1F2", 2.000);
            freeParameters.put("K3F2", 0.001);
            */

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initFreeParameters() {
        freeParameters.put("WF1", 0.9);
        freeParameters.put("WF2", 0.2);
        freeParameters.put("WF3", 2.0);
        freeParameters.put("WF4", 0.0);
        freeParameters.put("WF5", 0.7);
        freeParameters.put("WF6", 0.0);
        freeParameters.put("WF7", 0.0);

        freeParameters.put("WSF1", 3.0);
        freeParameters.put("WDF1", 1.0);
        freeParameters.put("BSF1", 0.5);
        freeParameters.put("BDF1", 1.0);
        freeParameters.put("K1F1", 2.0);
        freeParameters.put("K3F1", 0.0);

        freeParameters.put("WSF2", 3.0);
        freeParameters.put("WDF2", 1.0);
        freeParameters.put("BSF2", 0.5);
        freeParameters.put("BDF2", 1.0);
        freeParameters.put("K1F2", 2.0);
        freeParameters.put("K3F2", 0.0);
    }

    private void init() {
        requirements = requirementService.getRequirements();
        documentFrequency = idfService.getDocumentFrequency(requirements);
    }

    public List<Duplicate> bm25f_req(Requirement requirement, int k) {
        init();
        return bm25f(requirement, k, true);
    }

    private List<Duplicate> bm25f(Requirement requirement, int k, boolean withPreprocess) {
        logger.info("Init BM25f Preprocess for requirement " + requirement.getId());
        init();

        if (withPreprocess) bm25fPreprocess(requirement);
        requirements.add(requirement);

        List<Duplicate> topDuplicates = new ArrayList<>();

        for (Requirement compReq : requirements) {
            if (!compReq.getId().equals(requirement.getId())) {
                double score = sim(requirement, compReq);
                if (topDuplicates.size() < k || topDuplicates.get(k-1).getScore() < score) {
                    Duplicate duplicate = new Duplicate(requirement.getId(), compReq.getId(), score);
                    insertNewTopDuplicate(topDuplicates, duplicate, k);
                }
            }
        }

        logger.info("Finished BM25f");
        return topDuplicates;

    }

    private double sim(Requirement requirement, Requirement compReq) {
        double bm25fScoreUnigram = freeParameters.get("WF1") * bm25f_textPairUnigram(requirement, compReq);
        double bm25fScoreBigram = freeParameters.get("WF2") * bm25f_textPairBigram(requirement, compReq);
        double projectScore = freeParameters.get("WF3") * projectScore(requirement.getProject(), compReq.getProject());
        double typeScore = freeParameters.get("WF4") * typeScore(requirement.getType(), compReq.getType());
        double componentScore = freeParameters.get("WF5") * componentScore(requirement.getComponents(), compReq.getComponents());
        double priorityScore = freeParameters.get("WF6") * priorityScore(requirement.getPriority(), compReq.getPriority());
        double versionScore = freeParameters.get("WF7") * versionsScore(requirement.getVersions(), compReq.getVersions());
        return bm25fScoreUnigram + bm25fScoreBigram + projectScore + typeScore + componentScore + priorityScore + versionScore;
    }

    private double projectScore(String project1, String project2) {
        if (project1 == null || project2 == null) return 0.0;
        return project1.equals(project2) ? 1.0 : 0.0;
    }

    private double typeScore(String type1, String type2) {
        if (type1 == null || type2 == null) return 0.0;
        return type1.equals(type2) ? 1.0 : 0.0;
    }

    private double componentScore(String[] components1, String[] components2) {
        if (components1 == null || components2 == null) return 0.0;
        double sum = 0.0;
        for (String component : components1) {
            if (Arrays.asList(components2).contains(component)) {
                sum += 1.0;
            }
        }
        return components1.length == 0 ? 0 : sum / (double) components1.length;
    }

    private double priorityScore(Priority priority1, Priority priority2) {
        if (priority1 == null || priority2 == null) return 0.0;
        return 1.0 / (1.0 + Math.abs((double) priority1.getValue() - (double) priority2.getValue()));
    }

    private double versionsScore(String[] versions1, String[] versions2) {
        if (versions1 == null || versions2 == null || versions1.length == 0 || versions2.length == 0) return 0.0;
        String version1 = Arrays.stream(versions1).max(String::compareTo).orElse(null);
        String version2 = Arrays.stream(versions2).max(String::compareTo).orElse(null);
        return 1.0 / (1.0 + Math.abs(version1.compareTo(version2)));
    }

    private void insertNewTopDuplicate(List<Duplicate> topDuplicates, Duplicate duplicate, int k) {
        boolean posFound = false;
        int i = 0;
        while (!posFound && i < k) {
            if (i == topDuplicates.size() ||
                    topDuplicates.get(i).getScore() < duplicate.getScore()) {
                posFound = true;
            } else ++i;
        }
        topDuplicates.add(i, duplicate);
        if (topDuplicates.size() > k) topDuplicates.remove(k);
    }

    private List<String> getCollect(Requirement req1, Requirement req2, int n) {

        String[] req1SummaryTokens = getNGrams(req1.getSummaryTokens(), n);
        String[] req1DescriptionTokens = getNGrams(req1.getDescriptionTokens(), n);
        String[] req2SummaryTokens = getNGrams(req2.getSummaryTokens(), n);
        String[] req2DescriptionTokens = getNGrams(req2.getDescriptionTokens(), n);

        String[] req1Tokens = Arrays.stream(
                Stream.of(req1SummaryTokens, req1DescriptionTokens)
                        .flatMap(Stream::of)
                        .toArray(String[]::new))
                .distinct().toArray(String[]::new);
        String[] req2Tokens = Arrays.stream(
                Stream.of(req2SummaryTokens, req2DescriptionTokens)
                        .flatMap(Stream::of)
                        .toArray(String[]::new))
                .distinct().toArray(String[]::new);
        return Arrays.stream(req1Tokens).distinct()
                .filter(Arrays.asList(req2Tokens)::contains)
                .collect(Collectors.toList());
    }

    private String[] getNGrams(String[] tokens, int n) {
       List<String> ngrams = new ArrayList<>();
        for (int i = 1; i < tokens.length; ++i) {
            String ngram = tokens[i-n+1];
            for (int j = 1; j < n; ++j) {
                ngram += " " + tokens[i-n+1+j];
            }
            ngrams.add(ngram);
        }
        return ngrams.stream().toArray(String[]::new);
    }

    private double bm25f_textPairUnigram(Requirement req1, Requirement req2) {
        List<String> unigramIntersection = getCollect(req1, req2, 1);
        return computeScore(req1, req2, documentFrequency, requirements.size(), unigramIntersection,
                freeParameters.get("WSF1"), freeParameters.get("BSF1"), freeParameters.get("WDF1"), freeParameters.get("BDF1"),
                freeParameters.get("K1F1"), freeParameters.get("K3F1"));
    }

    private double bm25f_textPairBigram(Requirement req1, Requirement req2) {
        List<String> bigramIntersection = getCollect(req1, req2, 2);
        return computeScore(req1, req2, documentFrequency, requirements.size(), bigramIntersection,
                freeParameters.get("WSF2"), freeParameters.get("BSF2"), freeParameters.get("WDF2"), freeParameters.get("BDF2"),
                freeParameters.get("K1F2"), freeParameters.get("K3F2"));
    }

    private double computeScore(Requirement req1, Requirement req2,
                                HashMap<String, Integer> documentFrequency, int corpusSize, List<String> intersection,
                                double wsf, double bsf, double wdf, double bdf, double k1, double k3) {
        double score = 0.0;
        for (String term : intersection) {
            double idf = idfService.idf(term, documentFrequency, corpusSize);
            double tf = idfService.tf(term, req1, wsf, bsf, wdf, bdf);
            double tfd = (k1 + tf) == 0 ? 0 : tf / (k1 + tf);
            double tfq = idfService.tfq(term, req2, wsf, wdf);
            double wq = k3 + tfq == 0 ? 0 :(k3 + 1.0) * tfq / (k3 + tfq);
            score += idf * tfd * wq;
        }
        return score;
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

    public void bm25f_train(List<Duplicate> duplicates) {

        init();

        List<TrainTripletBM25F> trainTripletBM25FS = new ArrayList<>();
        for (Duplicate d : duplicates) {
            Requirement irrel = requirementService.findRandomRequirement(Arrays.asList(d.getReq1Id(), d.getReq2Id()), requirements);
            trainTripletBM25FS.add(new TrainTripletBM25F(d.getReq1Id(), d.getReq2Id(), irrel.getId()));
        }

        initFreeParameters();
        tuneParameters(trainTripletBM25FS);
    }

    private void tuneParameters(List<TrainTripletBM25F> trainTripletBM25FS) {
        int nIters = 24;
        double tunningRate = 0.001;
        logger.info("Starting tunning parameters.");
        for (int i = 0; i < nIters; ++i) {
            Collections.shuffle(trainTripletBM25FS);
            logger.info("Iteration nº:\t" + (i+1));
            for (int j = 0; j < trainTripletBM25FS.size(); ++j) {
                if (j%100 == 0) logger.info("Iteration nº:\t" + (i+1) + ", triplet " + (j+1));
                for (String key : freeParameters.keySet()) {
                    double pd = partialDerivativeRFC(trainTripletBM25FS.get(j), key);
                    if (pd != -1.0) {
                        double newValue = freeParameters.get(key) - tunningRate * pd;
                        freeParameters.put(key, newValue);
                    }
                }
            }
        }
    }

    private double partialDerivativeRFC(TrainTripletBM25F trainTripletBM25F, String key) {
        try {
            Requirement q = requirementService.getRequirement(trainTripletBM25F.getQ());
            Requirement irrel = requirementService.getRequirement(trainTripletBM25F.getIrrel());
            Requirement rel = requirementService.getRequirement(trainTripletBM25F.getRel());

            if (key.equals("WF1") || key.equals("WF2") || key.equals("WF3") || key.equals("WF4") ||
                    key.equals("WF5") || key.equals("WF6") || key.equals("WF7")) {
                double b, d;
                switch (key) {
                    case "WF1":
                        b = bm25f_textPairUnigram(q, irrel);
                        d = bm25f_textPairUnigram(q, rel);
                        break;
                    case "WF2":
                        b = bm25f_textPairBigram(q, irrel);
                        d = bm25f_textPairBigram(q, rel);
                        break;
                    case "WF3":
                        b = projectScore(q.getProject(), irrel.getProject());
                        d = projectScore(q.getProject(), rel.getProject());
                        break;
                    case "WF4":
                        b = typeScore(q.getType(), irrel.getType());
                        d = typeScore(q.getType(), rel.getType());
                        break;
                    case "WF5":
                        b = componentScore(q.getComponents(), irrel.getComponents());
                        d = componentScore(q.getComponents(), rel.getComponents());
                        break;
                    case "WF6":
                        b = priorityScore(q.getPriority(), irrel.getPriority());
                        d = priorityScore(q.getPriority(), rel.getPriority());
                        break;
                    default:
                        b = versionsScore(q.getVersions(), irrel.getVersions());
                        d = versionsScore(q.getVersions(), rel.getVersions());
                        break;
                }

                //e^(sim(q,irrel) - sim(q,rel))
                double ef = Math.exp(sim(q, irrel) - sim(q, rel));
                //(b-d)*log(e)*e^(sim(q,irrel) - sim(q,rel))
                double num = (b - d) * Math.log(Math.exp(1)) * ef;
                //e^(sim(q,irrel) - sim(q,rel)) + 1
                double den = ef + 1;
                return num / den;
            }
        } catch (NotFoundCustomException e) {
            //logger.error("Bug not found. Skipping for optimization");
        }
        return -1.0;
    }

    private double RCF(TrainTripletBM25F trainTripletBM25F) {
        Requirement q = requirementService.getRequirement(trainTripletBM25F.getQ());
        Requirement rel = requirementService.getRequirement(trainTripletBM25F.getRel());
        Requirement irrel = requirementService.getRequirement(trainTripletBM25F.getIrrel());

        return Math.log(1 + Math.exp(sim(q, irrel) - sim(q, rel)));
    }

    public HashMap<Integer, Double> bm25f_test(List<Duplicate> duplicates, Integer k) {
        init();
        HashMap<String, List<Duplicate>> duplicateMap = new HashMap<>();
        HashMap<Integer, Double> recallMap = new HashMap<>();
        int count = 0;
        for (Requirement r1 : requirements) {
            List<Duplicate> foundDuplicates = bm25f(r1, k, false);
            duplicateMap.put(r1.getId(), foundDuplicates);
            ++count;
            logger.info("Requirement nº " + count + " (from " + requirements.size() + ")");
        }
        for (Duplicate d : duplicates) {
            for (int i = 1; i <= k; ++i) {
                if (duplicateMap.containsKey(d.getReq1Id())) {
                    List<Duplicate> foundDuplicates = duplicateMap.get(d.getReq1Id());
                    if (foundDuplicates.size() >= i) foundDuplicates = foundDuplicates.subList(0, i);
                    if (foundDuplicates.stream().anyMatch(fd -> fd.getReq2Id().equals(d.getReq2Id()))) {
                        if (recallMap.containsKey(i)) recallMap.put(i, recallMap.get(i) + 1.0);
                        else recallMap.put(i, 1.0);
                    }
                }
            }
        }
        for (int i = 1; i <= k; ++i) {
            recallMap.put(i, recallMap.get(i) / duplicates.size());
        }
        //recall rate@k
        return recallMap;
    }
}
