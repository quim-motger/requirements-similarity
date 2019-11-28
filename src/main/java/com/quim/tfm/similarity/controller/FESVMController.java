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
    public void FESVMTrain(@RequestBody OpenReqSchema schema) {
        fesvmService.train(schema);
    }

    @PostMapping("/test")
    public OpenReqSchema FESVMTest(@RequestBody OpenReqSchema schema) {
        return fesvmService.test(schema);
    }

    @PostMapping("/train_and_test")
    public Stats FESVMTrainAndTest(@RequestBody OpenReqSchema schema, @RequestParam int k) {
        return fesvmService.trainAndTest(schema, k, Kernel.RBF, fesvmService.C, fesvmService.sigma);
    }

    @PostMapping("/train_and_test_with_optimization")
    public HashMap<String, Stats> FESVMTrainAndTestWithOptimization(@RequestBody OpenReqSchema schema, @RequestParam int k, @RequestParam Kernel kernel,
                                                                    @RequestParam double[] C_values, @RequestParam(required = false) double[] sigma_values) {
        return fesvmService.trainAndTestWithOptimization(schema, k, kernel, C_values, sigma_values);
    }

    @PostMapping("/feature_extraction")
    public void featureExtraction(@RequestBody OpenReqSchema schema) {
        fesvmService.featureExtractionMap(schema);
    }

}
