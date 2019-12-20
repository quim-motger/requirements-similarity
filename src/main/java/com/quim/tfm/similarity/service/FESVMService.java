package com.quim.tfm.similarity.service;

import com.google.common.collect.Lists;
import com.quim.tfm.similarity.entity.Requirement;
import com.quim.tfm.similarity.exception.NotFoundCustomException;
import com.quim.tfm.similarity.exception.NotImplementedKernel;
import com.quim.tfm.similarity.model.*;
import com.quim.tfm.similarity.model.openreq.OpenReqSchema;
import com.quim.tfm.similarity.utils.TimingTools;
import edu.stanford.nlp.trees.GrammaticalStructure;
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

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class FESVMService {

    private static final Logger logger = LoggerFactory.getLogger(FESVMService.class);

    @Autowired
    private RequirementService requirementService;
    @Autowired
    private FENLPService FENLPService;
    @Autowired
    private PreprocessService preprocessService;

    private Dictionary dictionary;
    private SVM svmClassifier;

    public FESVMService() {
        try {
            dictionary = Dictionary.getDefaultResourceInstance();
        } catch (JWNLException e) {
            e.printStackTrace();
        }
    }

    public void train(OpenReqSchema schema, boolean withLexicalFeatures, boolean withSyntacticFeatures, Kernel kernel, 
                      double C, double sigma) {
        List<Duplicate> duplicates = requirementService.getDuplicatesFromOpenReqSchema(schema);
        duplicates = featureExtraction(duplicates, withLexicalFeatures, withSyntacticFeatures);
        TimingTools.startTimer("TRAIN");
        double[][] objects = getObjectsAsDoubleMatrix(duplicates);
        int[] classes = getIntClasses(duplicates);
        buildClassifier(kernel, C, sigma);
        svmClassifier.learn(objects, classes);
        TimingTools.endTimer("TRAIN");
    }

    private void buildClassifier(Kernel kernel, double C, double sigma) {
        if (kernel.equals(Kernel.LINEAR))
            svmClassifier = new SVM(new LinearKernel(), C);
        else
            svmClassifier = new SVM(new GaussianKernel(sigma), C);
    }

    public OpenReqSchema test(OpenReqSchema schema, boolean withLexicalFeatures, boolean withSyntacticFeatures) {
        List<Duplicate> duplicates = requirementService.getDuplicatesFromOpenReqSchema(schema);
        duplicates = featureExtraction(duplicates, withLexicalFeatures, withSyntacticFeatures);
        TimingTools.startTimer("TEST");
        int[] classes = svmClassifier.predict(transformDuplicateFeaturesIntoDoubleMatrix(duplicates.toArray(new Duplicate[0])));
        for (int i = 0; i < duplicates.size(); ++i) {
            duplicates.get(i).setTag(DuplicateTag.fromValue(classes[i]));
        }
        TimingTools.endTimer("TEST");
        return requirementService.convertToOpenReqSchema(null, duplicates);
    }

    public Stats trainAndTest(OpenReqSchema schema, int k, Kernel kernel, double C, double sigma, boolean withLexicalFeatures,
                              boolean withSyntacticFeatures) {

        List<Duplicate> duplicates = requirementService.getDuplicatesFromOpenReqSchema(schema);
        Collections.shuffle(duplicates);

        logger.info("Starting feature extraction process...");
        duplicates = featureExtraction(duplicates, withLexicalFeatures, withSyntacticFeatures);
        //duplicates = findDuplicates(duplicates);
        logger.info("Finished feature extraction");

        TimingTools.startTimer("CROSS-VALIDATION");

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
        TimingTools.endTimer("CROSS-VALIDATION");
        return stats;
    }

    public HashMap<String, Stats> trainAndTestWithOptimization(OpenReqSchema schema, int k, Kernel kernel, double[] C_values,
                                                               double[] sigma_values, boolean withLexicalFeatures,
                                                               boolean withSyntacticFeatures) {
        HashMap<String, Stats> statsMap = new HashMap<>();
        for (double C : C_values) {
            if (kernel.equals(Kernel.RBF)) {
                for (double sigma : sigma_values) {
                    Stats stats = trainAndTest(schema, k, kernel, C, sigma, withLexicalFeatures, withSyntacticFeatures);
                    statsMap.put("C = " + C + "," + " sigma = " + sigma, stats);
                }
            } else if (kernel.equals(Kernel.LINEAR)){
                Stats stats = trainAndTest(schema, k, kernel, C, 0.0, withLexicalFeatures, withSyntacticFeatures);
                statsMap.put("C = " + C, stats);
            } else throw new NotImplementedKernel();
        }

        return statsMap;
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
        double[][] matrix = new double[duplicateArray.length][8];
        for (int i = 0; i < duplicateArray.length; ++i) {
            Duplicate df = duplicateArray[i];
            double[] attributes = getDoubles(df);
            matrix[i] = attributes;
        }
        return matrix;
    }

    private double[] getDoubles(Duplicate df) {
        return new double[]{df.getWordOverlapNameScore(), df.getUnigramMatchNameScore(), df.getBigramMatchNameScore(),
        df.getSubjectMatchNameScore(), df.getSubjectMatchNameScore(), df.getObjectVerbMatchNameScore(), df.getNounMatchNameScore(),
        df.getNameEntityNameScore(), df.getWordOverlapTextScore(), df.getUnigramMatchTextScore(), df.getBigramMatchTextScore(),
                df.getSubjectMatchTextScore(), df.getSubjectMatchTextScore(), df.getObjectVerbMatchTextScore(), df.getNounMatchTextScore(),
                df.getNameEntityTextScore()};
    }

    private List<Duplicate> featureExtraction(List<Duplicate> duplicates, boolean withLexicalFeatures, boolean withSyntacticFeatures) {
        List<Duplicate> filteredList = new ArrayList<>();
        int i = 0;
        if (withSyntacticFeatures) {
            TimingTools.startTimer("SYNTACTIC-NLP-PIPELINE");
            TimingTools.pauseTimer("SYNTACTIC-NLP-PIPELINE");
        }
        TimingTools.startTimer("FEATURE-ALIGNMENT");
        TimingTools.pauseTimer("FEATURE-ALIGNMENT");
        for (Duplicate d : duplicates) {
            if ((i+1) %10 == 0) logger.info("Duplicate " + (i+1) + " out of " + duplicates.size());
            try {
                Requirement r1 = requirementService.getRequirement(d.getReq1Id());
                Requirement r2 = requirementService.getRequirement(d.getReq2Id());

                List<String> r1Summary = preprocessService.analyzerWithoutStemming(r1.getSummary());
                List<String> r1Description = preprocessService.analyzerWithoutStemming(r1.getDescription());
                List<String> r2Summary = preprocessService.analyzerWithoutStemming(r2.getSummary());
                List<String> r2Description = preprocessService.analyzerWithoutStemming(r2.getDescription());


                FEPreprocessData summaryReq1 = FENLPService.applyFEPreprocess(r1Summary.toArray(new String[0]),
                        withLexicalFeatures, withSyntacticFeatures);
                FEPreprocessData descriptionReq1 = FENLPService.applyFEPreprocess(r1Description.toArray(new String[0]),
                        withLexicalFeatures, withSyntacticFeatures);
                FEPreprocessData summaryReq2 = FENLPService.applyFEPreprocess(r2Summary.toArray(new String[0]),
                        withLexicalFeatures, withSyntacticFeatures);
                FEPreprocessData descriptionReq2 = FENLPService.applyFEPreprocess(r2Description.toArray(new String[0]),
                        withLexicalFeatures, withSyntacticFeatures);

                extractFeatures(d, summaryReq1, summaryReq2, descriptionReq1, descriptionReq2, withLexicalFeatures,
                        withSyntacticFeatures);
                filteredList.add(d);
            } catch (NotFoundCustomException e) {
                logger.error("Entity not found. Skipping");
            } catch (IOException e) {
                logger.error(e.getLocalizedMessage());
            }
            ++i;
        }
        if (withSyntacticFeatures) {
            TimingTools.restartTimer("SYNTACTIC-NLP-PIPELINE");
            TimingTools.endTimer("SYNTACTIC-NLP-PIPELINE");
        }
        TimingTools.restartTimer("FEATURE-ALIGNMENT");
        TimingTools.endTimer("FEATURE-ALIGNMENT");
        return filteredList;
    }

    private void extractFeatures(Duplicate duplicate, FEPreprocessData summaryReq1, FEPreprocessData summaryReq2,
                                 FEPreprocessData descriptionReq1, FEPreprocessData descriptionReq2,
                                 boolean withLexicalFeatures, boolean withSyntacticFeatures) {
        TimingTools.restartTimer("FEATURE-ALIGNMENT");
        if (withLexicalFeatures) {
            duplicate.setWordOverlapNameScore(wordOverlapScore(summaryReq1, summaryReq2));
            duplicate.setUnigramMatchNameScore(ngramMatchScore(summaryReq1, summaryReq2, 1));
            duplicate.setBigramMatchNameScore(ngramMatchScore(summaryReq1, summaryReq2, 2));
            
            duplicate.setWordOverlapTextScore(wordOverlapScore(descriptionReq1, descriptionReq2));
            duplicate.setUnigramMatchTextScore(ngramMatchScore(descriptionReq1, descriptionReq2, 1));
            duplicate.setBigramMatchTextScore(ngramMatchScore(descriptionReq1, descriptionReq2, 2));
        }
        if (withSyntacticFeatures) {
            DependencyStruct summaryReq1Dependencies = getDependenciesFromData(summaryReq1);
            DependencyStruct descriptionReq1Dependencies = getDependenciesFromData(descriptionReq1);
            DependencyStruct summaryReq2Dependencies = getDependenciesFromData(summaryReq2);
            DependencyStruct descriptionReq2Dependencies = getDependenciesFromData(descriptionReq2);

            duplicate.setSubjectMatchNameScore(subjectScore(summaryReq1Dependencies, summaryReq2Dependencies));
            duplicate.setSubjectVerbMatchNameScore(subjectVerbScore(summaryReq1Dependencies, summaryReq2Dependencies));
            duplicate.setObjectVerbMatchNameScore(objectVerbScore(summaryReq1Dependencies, summaryReq2Dependencies));
            duplicate.setNounMatchNameScore(nounScore(summaryReq1Dependencies, summaryReq2Dependencies));

            duplicate.setSubjectMatchTextScore(subjectScore(descriptionReq1Dependencies, descriptionReq2Dependencies));
            duplicate.setSubjectVerbMatchTextScore(subjectVerbScore(descriptionReq1Dependencies, descriptionReq2Dependencies));
            duplicate.setObjectVerbMatchTextScore(objectVerbScore(descriptionReq1Dependencies, descriptionReq2Dependencies));
            duplicate.setNounMatchTextScore(nounScore(descriptionReq1Dependencies, descriptionReq2Dependencies));
        }
        TimingTools.pauseTimer("FEATURE-ALIGNMENT");
    }

    private DependencyStruct getDependenciesFromData(FEPreprocessData data) {
        List<TypedDependency> ov = new ArrayList<>();
        List<TypedDependency> sv = new ArrayList<>();
        List<TypedDependency> nn = new ArrayList<>();
        List<String> subjects = new ArrayList<>();
        for (GrammaticalStructure sentence : data.getGrammaticalStructureList()) {
            List<TypedDependency> dependencies = new ArrayList<>(sentence.allTypedDependencies());
            for (TypedDependency dependency : dependencies) {
                String[] gov = dependency.gov().toString().split("/");
                String[] dep = dependency.dep().toString().split("/");

                if (dependency.reln().toString().equals("nsubj") || dependency.reln().toString().equals("nsubjpass")) {
                    subjects.add(dependency.dep().word());
                }

                if (dependency.reln().toString().contains("nsubj") && gov[1].contains("VB")) {
                    sv.add(dependency);
                }

                if (gov.length == 2 && dep.length == 2) {
                    if (gov[1].contains("VB") && dep[1].contains("NN")) {
                        ov.add(dependency);
                    }
                    if (dependency.reln().toString().contains("compound") && gov[1].contains("NN") && dep[1].contains("NN")) {
                        nn.add(dependency);
                    }
                }
            }
        }

        DependencyStruct struct = new DependencyStruct();
        struct.setSubjects(subjects);
        struct.setSubjectVerbDependencies(sv);
        struct.setObjectVerbDependencies(ov);
        struct.setNounDependencies(nn);

        return struct;
    }

    private double subjectVerbScore(DependencyStruct req1, DependencyStruct req2) {
        List<TypedDependency> req1SVDep = req1.getSubjectVerbDependencies();
        List<TypedDependency> req2SVDep = req2.getSubjectVerbDependencies();

        return matchSV(req1SVDep, req2SVDep);

    }

    private double objectVerbScore(DependencyStruct req1, DependencyStruct req2) {
        List<TypedDependency> req1SVDep = req1.getObjectVerbDependencies();
        List<TypedDependency> req2SVDep = req2.getObjectVerbDependencies();

        return matchOV(req1SVDep, req2SVDep);
    }

    private double nounScore(DependencyStruct req1, DependencyStruct req2) {
        List<TypedDependency> req1NNDep = req1.getNounDependencies();
        List<TypedDependency> req2NNDep = req2.getNounDependencies();

        return matchNN(req1NNDep, req2NNDep);
    }

    private double matchNN(List<TypedDependency> req1NNDep, List<TypedDependency> req2NNDep) {
        return matchWords(req1NNDep, req2NNDep);
    }

    private double matchWords(List<TypedDependency> req1NNDep, List<TypedDependency> req2NNDep) {
        double match = 0.;
        for (TypedDependency td1 : req1NNDep) {
            boolean found = false;
            int i = 0;
            while (!found && i < req2NNDep.size()) {
                TypedDependency td2 = req2NNDep.get(i);
                if (td1.gov().toString().split("/")[0].equals(td2.gov().toString().split("/")[0])
                        && td1.dep().toString().split("/")[0].equals(td2.dep().toString().split("/")[0])) {
                    found = true;
                    ++match;
                } else {
                    ++i;
                }
            }
        }
        if (req1NNDep.isEmpty() || req2NNDep.isEmpty()) return 0.;
        else {
            return match / (double) Math.min(req1NNDep.size(), req2NNDep.size());
        }
    }

    private double matchOV(List<TypedDependency> req1OVDep, List<TypedDependency> req2OVDep) {
        double match = 0.;
        for (TypedDependency td1 : req1OVDep) {
            boolean found = false;
            int i = 0;
            while (!found && i < req2OVDep.size()) {
                TypedDependency td2 = req2OVDep.get(i);
                if (td1.reln().toString().equals(td2.reln().toString()) &&
                        td1.gov().toString().split("/")[0].equals(td2.gov().toString().split("/")[0])
                        && td1.dep().toString().split("/")[0].equals(td2.dep().toString().split("/")[0])) {
                    found = true;
                    ++match;
                } else {
                    ++i;
                }
            }
        }
        if (req1OVDep.isEmpty() || req2OVDep.isEmpty()) return 0.;
        else {
            return match / (double) Math.min(req1OVDep.size(), req2OVDep.size());
        }
    }


    private double matchSV(List<TypedDependency> req1SVDep, List<TypedDependency> req2SVDep) {
        return matchWords(req1SVDep, req2SVDep);
    }

    private double subjectScore(DependencyStruct req1, DependencyStruct req2) {
        List<String> req1Subjects = req1.getSubjects();
        List<String> req2Subjects = req2.getSubjects();

        if (req1Subjects.isEmpty() || req2Subjects.isEmpty()) return 0.;
        else {
            return req1Subjects.stream().filter(req2Subjects::contains).count() /
                    (double) Math.min(req1Subjects.size(), req2Subjects.size());
        }

    }


    private double ngramMatchScore(FEPreprocessData req1, FEPreprocessData req2, int n) {
        return ngramMatchPartialScore(req1, req2, n);
    }

    private double ngramMatchPartialScore(FEPreprocessData req1, FEPreprocessData req2, int n) {
        List<String> tokensReq1 = FENLPService.getNGrams(req1, n);
        List<String> tokensReq2 = FENLPService.getNGrams(req2, n);
        return jaccardSimilarity(tokensReq1, tokensReq2);
    }

    private double jaccardSimilarity(List<String> req1, List<String> req2) {
        return (req1.size() + req2.size()) > 0 ? (double) req1.stream().distinct().filter(req2::contains).collect(Collectors.toSet()).size() /
                (double) (req1.size() + req2.size()) : 0;
    }

    private double wordOverlapScore(FEPreprocessData req1, FEPreprocessData req2) {
        return wordOverlapPartialScore(req1, req2);
    }

    private double wordOverlapPartialScore(FEPreprocessData req1, FEPreprocessData req2) {
        List<Token> tokensReq1 = FENLPService.getUniqueTokens(req1);
        List<Token> tokensReq2 = FENLPService.getUniqueTokens(req2);

        int intersection = intersection(tokensReq1, tokensReq2);
        int bagSize = tokensReq1.size() + tokensReq2.size();

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
