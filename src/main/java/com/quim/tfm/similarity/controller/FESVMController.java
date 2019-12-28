package com.quim.tfm.similarity.controller;

import com.quim.tfm.similarity.model.Duplicate;
import com.quim.tfm.similarity.model.Kernel;
import com.quim.tfm.similarity.model.Stats;
import com.quim.tfm.similarity.model.openreq.OpenReqSchema;
import com.quim.tfm.similarity.service.FESVMService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/requirements-similarity/fe-svm")
public class FESVMController {

    @Autowired
    private FESVMService fesvmService;

    @PostMapping("/train")
    @ApiOperation(value = "Train a SVM model", notes = "Given a set of labelled duplicate and not-duplicate pairs of requirements," +
            " the system trains a SVM classifier with the defined configuration parameters.")
    public void FESVMTrain(@ApiParam(value = "The pair dataset for training", required = true)
                               @RequestBody OpenReqSchema schema,
                           @ApiParam(value = "If *true*, lexical features are used")
                           @RequestParam(required = false, defaultValue = "true") boolean withLexicalFeatures,
                           @ApiParam(value = "If *true*, syntactic features are used")
                               @RequestParam(required = false, defaultValue = "true") boolean withSyntacticFeatures,
                           @ApiParam(value = "The type of kernel to be used (*LINEAR*, *RBF*)")
                               @RequestParam(required = false, defaultValue = "RBF") Kernel kernel,
                           @ApiParam(value = "The value of the *C* parameter")
                               @RequestParam(required = false, defaultValue = "1.0") double C,
                           @ApiParam(value = "The value of the *sigma* parameter (only used for RBF)")
                               @RequestParam(required = false, defaultValue = "0.01") double sigma) {
        fesvmService.train(schema, withLexicalFeatures, withSyntacticFeatures, kernel, C, sigma);
    }

    @PostMapping("/test")
    @ApiOperation(value = "Predict", notes = "Given a set of not-labelled pairs of requirements," +
            " the system predicts their classification using the trained SVM classifier")
    public OpenReqSchema FESVMTest(@ApiParam(value = "The pair dataset for training", required = true)
                                       @RequestBody OpenReqSchema schema,
                                   @ApiParam(value = "If *true*, lexical features are used")
                                   @RequestParam(required = false, defaultValue = "true") boolean withLexicalFeatures,
                                   @ApiParam(value = "If *true*, syntactic features are used")
                                       @RequestParam(required = false, defaultValue = "true") boolean withSyntacticFeatures) {
        return fesvmService.test(schema, withLexicalFeatures, withSyntacticFeatures);
    }

    @PostMapping("/train_and_test")
    @ApiOperation(value = "Cross-validation", notes = "Given a set of duplicate and not-duplicate pairs of requirements," +
            " the system runs a *k*-cross-validation and provides the aggregate confusion matrix results.")
    public Stats FESVMTrainAndTest(@ApiParam(value = "The pair dataset for training", required = true)
                                       @RequestBody OpenReqSchema schema,
                                   @ApiParam(value = "The number of folds to split the data for the cross-validation", required = true)
                                   @RequestParam int k,
                                   @ApiParam(value = "If *true*, lexical features are used")
                                       @RequestParam(required = false, defaultValue = "true") boolean withLexicalFeatures,
                                   @ApiParam(value = "If *true*, syntactic features are used")
                                       @RequestParam(required = false, defaultValue = "true") boolean withSyntacticFeatures,
                                   @ApiParam(value = "The type of kernel to be used (*LINEAR*, *RBF*)")
                                       @RequestParam(required = false, defaultValue = "RBF") Kernel kernel,
                                   @ApiParam(value = "The value of the *C* parameter")
                                       @RequestParam(required = false, defaultValue = "1.0") double C,
                                   @ApiParam(value = "The value of the *sigma* parameter (only used for RBF)")
                                       @RequestParam(required = false, defaultValue = "0.01") double sigma) {
        return fesvmService.trainAndTest(schema, k, kernel, C, sigma, withLexicalFeatures,
                withSyntacticFeatures);
    }

    @PostMapping("/train_and_test_with_optimization")
    @ApiOperation(value = "Cross-validation with configuration optimization", notes = "Given a set of duplicate and " +
            "not-duplicate pairs of requirements," +
            " the system runs a *k*-cross-validation and provides the aggregate confusion matrix results for all" +
            " possible combinations between the request configuration parameters.")
    public HashMap<String, Stats> FESVMTrainAndTestWithOptimization(@ApiParam(value = "The pair dataset for training", required = true)
                                                                        @RequestBody OpenReqSchema schema,
                                                                    @ApiParam(value = "The number of folds to split the data for the cross-validation", required = true)
                                                                    @RequestParam int k,
                                                                    @ApiParam(value = "The type of kernel to be used (*LINEAR*, *RBF*)")
                                                                    @RequestParam(required = false, defaultValue = "RBF") Kernel kernel,
                                                                    @ApiParam(value = "The values of the *C* parameter to be tested")
                                                                        @RequestParam(required = false) double[] C_values,
                                                                    @ApiParam(value = "The values of the *sigma* parameter to be tested (only used for RBF)")
                                                                        @RequestParam(required = false) double[] sigma_values,
                                                                    @ApiParam(value = "If *true*, lexical features are used")
                                                                        @RequestParam(required = false, defaultValue = "true") boolean withLexicalFeatures,
                                                                    @ApiParam(value = "If *true*, syntactic features are used")
                                                                        @RequestParam(required = false, defaultValue = "true") boolean withSyntacticFeatures) {
        return fesvmService.trainAndTestWithOptimization(schema, k, kernel, C_values, sigma_values, withLexicalFeatures,
                withSyntacticFeatures);
    }

}
