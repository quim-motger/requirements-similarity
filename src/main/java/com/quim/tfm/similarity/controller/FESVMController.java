package com.quim.tfm.similarity.controller;

import com.quim.tfm.similarity.model.Duplicate;
import com.quim.tfm.similarity.model.Kernel;
import com.quim.tfm.similarity.model.Stats;
import com.quim.tfm.similarity.model.openreq.OpenReqSchema;
import com.quim.tfm.similarity.service.FESVMService;
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
    public void FESVMTrain(@RequestBody OpenReqSchema schema,
                           @RequestParam(required = false, defaultValue = "true") boolean withLexicalFeatures,
                           @RequestParam(required = false, defaultValue = "true") boolean withSyntacticFeatures,
                           @RequestParam(required = false, defaultValue = "RBF") Kernel kernel,
                           @RequestParam(required = false, defaultValue = "1.0") double C,
                           @RequestParam(required = false, defaultValue = "0.01") double sigma) {
        fesvmService.train(schema, withLexicalFeatures, withSyntacticFeatures, kernel, C, sigma);
    }

    @PostMapping("/test")
    public OpenReqSchema FESVMTest(@RequestBody OpenReqSchema schema,
                                   @RequestParam(required = false, defaultValue = "true") boolean withLexicalFeatures,
                                   @RequestParam(required = false, defaultValue = "true") boolean withSyntacticFeatures) {
        return fesvmService.test(schema, withLexicalFeatures, withSyntacticFeatures);
    }

    @PostMapping("/train_and_test")
    public Stats FESVMTrainAndTest(@RequestBody OpenReqSchema schema, @RequestParam int k,
                                   @RequestParam(required = false, defaultValue = "true") boolean withLexicalFeatures,
                                   @RequestParam(required = false, defaultValue = "true") boolean withSyntacticFeatures,
                                   @RequestParam(required = false, defaultValue = "RBF") Kernel kernel,
                                   @RequestParam(required = false, defaultValue = "1.0") double C,
                                   @RequestParam(required = false, defaultValue = "0.01") double sigma) {
        return fesvmService.trainAndTest(schema, k, kernel, C, sigma, withLexicalFeatures,
                withSyntacticFeatures);
    }

    @PostMapping("/train_and_test_with_optimization")
    public HashMap<String, Stats> FESVMTrainAndTestWithOptimization(@RequestBody OpenReqSchema schema, @RequestParam int k, @RequestParam Kernel kernel,
                                                                    @RequestParam double[] C_values, @RequestParam(required = false) double[] sigma_values,
                                                                    @RequestParam(required = false, defaultValue = "true") boolean withLexicalFeatures,
                                                                    @RequestParam(required = false, defaultValue = "true") boolean withSyntacticFeatures) {
        return fesvmService.trainAndTestWithOptimization(schema, k, kernel, C_values, sigma_values, withLexicalFeatures,
                withSyntacticFeatures);
    }

}
