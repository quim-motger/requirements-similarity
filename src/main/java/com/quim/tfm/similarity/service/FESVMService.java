package com.quim.tfm.similarity.service;

import com.google.common.collect.Lists;
import com.quim.tfm.similarity.entity.Requirement;
import com.quim.tfm.similarity.exception.NotFoundCustomException;
import com.quim.tfm.similarity.exception.NotImplementedKernel;
import com.quim.tfm.similarity.model.*;
import com.quim.tfm.similarity.repository.DuplicateRepository;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.trees.TypedDependency;
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
import smile.classification.SVM;
import smile.math.kernel.GaussianKernel;
import smile.math.kernel.LinearKernel;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FESVMService {

    private static final Logger logger = LoggerFactory.getLogger(FESVMService.class);

    private double WS;
    private double WD;

    public static double C;
    public static double sigma;

    @Autowired
    private RequirementService requirementService;
    @Autowired
    private StanfordPreprocessService stanfordPreprocessService;
    @Autowired
    private DuplicateRepository duplicateRepository;

    private Dictionary dictionary;
    private SVM svmClassifier;

    public FESVMService() {
        try {
            C = 10;
            sigma = 0.01;
            svmClassifier = new SVM(new GaussianKernel(sigma), C);
            dictionary = Dictionary.getDefaultResourceInstance();
        } catch (JWNLException e) {
            e.printStackTrace();
        }

        WS = 3.0;
        WD = 1.0;
    }

    public void train(List<Duplicate> duplicates) {
        duplicates = featureExtraction(duplicates);
        double[][] objects = getObjectsAsDoubleMatrix(duplicates);
        int[] classes = getIntClasses(duplicates);
        svmClassifier.learn(objects, classes);
    }

    public List<Duplicate> test(List<Duplicate> duplicates) {
        duplicates = featureExtraction(duplicates);
        int[] classes = svmClassifier.predict(transformDuplicateFeaturesIntoDoubleMatrix(duplicates.toArray(new Duplicate[0])));
        for (int i = 0; i < duplicates.size(); ++i) {
            duplicates.get(i).setTag(DuplicateTag.fromValue(classes[i]));
        }
        return duplicates;
    }

    public Stats trainAndTest(List<Duplicate> duplicates, int k, Kernel kernel, double C, double sigma) {
        Collections.shuffle(duplicates);

        logger.info("Starting feature extraction process...");
        //duplicates = featureExtraction(duplicates);
        duplicates = findDuplicates(duplicates);
        logger.info("Finished feature extraction");

        List<List<Duplicate>> chunks = Lists.partition(duplicates, duplicates.size() / k);

        int tp, tn, fp, fn;
        tp = tn = fp = fn = 0;

        for (int i = 0; i < k; ++i) {
            logger.info("Starting test " + (i+1));

            SVM classifier;
            switch (kernel) {
                case LINEAR:
                    classifier = new SVM(new LinearKernel(), C);
                    break;
                case RBF:
                    classifier = new SVM(new GaussianKernel(sigma), C);
                    break;
                default:
                    throw new NotImplementedKernel();
            }

            List<Duplicate> testSet = chunks.get(i);
            List<Duplicate> trainSet = new ArrayList<>();
            for (int j = 0; j < k; ++j) {
                if (i != j) trainSet.addAll(chunks.get(j));
            }

            double[][] matrix = getObjectsAsDoubleMatrix(trainSet);
            int[] classes = getIntClasses(trainSet);
            classifier.learn(matrix, classes);

            for (Duplicate d : testSet) {
                DuplicateTag predictedTag = DuplicateTag.fromValue(classifier.predict(getDoubles(d)));
                if (predictedTag == DuplicateTag.DUPLICATE && d.getTag() == DuplicateTag.DUPLICATE)
                    ++tp;
                else if (predictedTag == DuplicateTag.NOT_DUPLICATE && d.getTag() == DuplicateTag.NOT_DUPLICATE)
                    ++tn;
                else if (predictedTag == DuplicateTag.NOT_DUPLICATE && d.getTag() == DuplicateTag.DUPLICATE)
                    ++fn;
                else
                    ++fp;
            }

        }
        Stats stats = new Stats(tp, tn, fp, fn);
        return stats;
    }

    public HashMap<String, Stats> trainAndTestWithOptimization(List<Duplicate> duplicates, int k, Kernel kernel, double[] C_values,
                                                               double[] sigma_values) {
        HashMap<String, Stats> statsMap = new HashMap<>();
        for (double C : C_values) {
            if (kernel.equals(Kernel.RBF)) {
                for (double sigma : sigma_values) {
                    Stats stats = trainAndTest(duplicates, k, kernel, C, sigma);
                    statsMap.put("C = " + C + "," + " sigma = " + sigma, stats);
                }
            } else if (kernel.equals(Kernel.LINEAR)){
                Stats stats = trainAndTest(duplicates, k, kernel, C, sigma);
                statsMap.put("C = " + C, stats);
            } else throw new NotImplementedKernel();
        }

        return statsMap;
    }

    private List<Duplicate> findDuplicates(List<Duplicate> duplicates) {
        List<Duplicate> list = new ArrayList<>();
        for (Duplicate d : duplicates) {
            Duplicate found = getDuplicate(d.getReq1Id(), d.getReq2Id());
            if (found != null) list.add(found);
        }
        return list;
    }

    public void featureExtractionMap(List<Duplicate> duplicates) {
        duplicateRepository.deleteAll();
        duplicates = featureExtraction(duplicates);
        duplicateRepository.saveAll(duplicates);
    }

    private Duplicate getDuplicate(String req1Id, String req2Id) {
        return duplicateRepository.findByReqsIds(req1Id, req2Id).stream().findFirst().orElse(null);
    }

    private int[] getIntClasses(List<Duplicate> duplicateFeaturesList) {
        return duplicateFeaturesList.stream()
                .map(Duplicate::getTag)
                .mapToInt(DuplicateTag::getValue)
                .toArray();
    }

    private double[][] getObjectsAsDoubleMatrix(List<Duplicate> duplicateFeaturesList) {
        Duplicate[] duplicateArray = duplicateFeaturesList.toArray(new Duplicate[0]);
        return transformDuplicateFeaturesIntoDoubleMatrix(duplicateArray);
    }

    private double[][] transformDuplicateFeaturesIntoDoubleMatrix(Duplicate[] duplicateArray) {
        double[][] matrix = new double[duplicateArray.length][3];
        for (int i = 0; i < duplicateArray.length; ++i) {
            Duplicate df = duplicateArray[i];
            double[] attributes = getDoubles(df);
            matrix[i] = attributes;
        }
        return matrix;
    }

    private double[] getDoubles(Duplicate df) {
        return new double[]{df.getWordOverlapScore(), df.getUnigramMatchScore(), df.getBigramMatchScore()};
    }

    private List<Duplicate> featureExtraction(List<Duplicate> duplicates) {
        List<Duplicate> filteredList = new ArrayList<>();
        int i = 0;
        for (Duplicate d : duplicates) {
            if ((i+1) %10 == 0) logger.info("Duplicate " + (i+1) + " out of " + duplicates.size());
            try {
                Requirement r1 = requirementService.getRequirement(d.getReq1Id());
                Requirement r2 = requirementService.getRequirement(d.getReq2Id());

                Annotation summaryReq1 = stanfordPreprocessService.preprocess(r1.getSummary());
                Annotation descriptionReq1 = stanfordPreprocessService.preprocess(r1.getDescription());
                Annotation summaryReq2 = stanfordPreprocessService.preprocess(r2.getSummary());
                Annotation descriptionReq2 = stanfordPreprocessService.preprocess(r2.getDescription());

                extractFeatures(d, summaryReq1, summaryReq2, descriptionReq1, descriptionReq2);
                filteredList.add(d);
            } catch (NotFoundCustomException e) {
                logger.error("Entity not found. Skipping");
            }
            ++i;
        }
        return filteredList;
    }

    private void extractFeatures(Duplicate duplicate, Annotation summaryReq1, Annotation summaryReq2,
                                              Annotation descriptionReq1, Annotation descriptionReq2) {
        duplicate.setWordOverlapScore(wordOverlapScore(summaryReq1, summaryReq2, descriptionReq1, descriptionReq2));
        duplicate.setUnigramMatchScore(unigramMatchScore(summaryReq1, summaryReq2, descriptionReq1, descriptionReq2, 1));
        duplicate.setBigramMatchScore(unigramMatchScore(summaryReq1, summaryReq2, descriptionReq1, descriptionReq2, 2));
        duplicate.setSubjectMatchScore(subjectScore(summaryReq1, summaryReq2, descriptionReq1, descriptionReq2));
        duplicate.setSubjectVerbMatchScore(subjectVerbScore(summaryReq1, summaryReq2, descriptionReq1, descriptionReq2));
        duplicate.setObjectVerbMatchScore(objectVerbScore(summaryReq1, summaryReq2, descriptionReq1, descriptionReq2));
        duplicate.setNounMatchScore(nounScore(summaryReq1, summaryReq2, descriptionReq1, descriptionReq2));
        duplicate.setNameEntityScore(nameEntityScore(summaryReq1, summaryReq2, descriptionReq1, descriptionReq2));
        //TODO other features
    }

    private double nameEntityScore(Annotation summaryReq1, Annotation summaryReq2, Annotation descriptionReq1, Annotation descriptionReq2) {
        return 0;
    }

    private double nounScore(Annotation summaryReq1, Annotation summaryReq2, Annotation descriptionReq1, Annotation descriptionReq2) {
        return 0;
    }

    private double objectVerbScore(Annotation summaryReq1, Annotation summaryReq2, Annotation descriptionReq1, Annotation descriptionReq2) {
        return 0;
    }

    private double subjectVerbScore(Annotation summaryReq1, Annotation summaryReq2, Annotation descriptionReq1, Annotation descriptionReq2) {
        return 0;
    }

    private double subjectScore(Annotation summaryReq1, Annotation summaryReq2, Annotation descriptionReq1, Annotation descriptionReq2) {
        stanfordPreprocessService.getSubjects(summaryReq1);
        return 0;
    }

    private String getSubject(List<TypedDependency> dependencies) {
        String rootSubject = null, subject = null;
        for (int i = dependencies.size() - 1; i >= 0; i--) {
            final TypedDependency dependency = dependencies.get(i);
            if (dependency.reln().toString().contains("subj")) {
                rootSubject = subject = dependency.dep().word();
            } else if (dependency.reln().toString().contains("compound") && dependency.gov().word().equals(rootSubject)) {
                subject = dependency.dep().word() + " " + subject;
            }
        }
        if (subject == null) {
            return null;
        }
        String lemmaSubject = "";
        for (final String lemma : new Sentence(subject).lemmas()) {
            lemmaSubject += lemma + " ";
        }
        return lemmaSubject.trim();
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
        if (req1.size() + req2.size() == 0) logger.info("THAT THING HAPPENED");
        return (req1.size() + req2.size()) > 0 ? (double) req1.stream().distinct().filter(req2::contains).collect(Collectors.toSet()).size() /
                (double) (req1.size() + req2.size()) : 0;
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

        if (bagSize == 0) logger.info("THAT THING HAPPENED");
        return bagSize > 0 ? (double) intersection / (double) bagSize : 0;

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
