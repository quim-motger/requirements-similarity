package com.quim.tfm.similarity.controller;

import com.quim.tfm.similarity.model.Duplicate;
import com.quim.tfm.similarity.model.Stats;
import com.quim.tfm.similarity.service.FESVMService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/requirements-similarity/fe-svm")
public class FESVMController {

    @Autowired
    private FESVMService fesvmService;

    @PostMapping("/train")
    public void FESVMTrain(@RequestBody List<Duplicate> duplicates) {
        fesvmService.train(duplicates);
    }

    @PostMapping("/test")
    public List<Duplicate> FESVMTest(@RequestBody List<Duplicate> duplicates) {
        return fesvmService.test(duplicates);
    }

    @PostMapping("/train_and_test")
    public List<Stats> FESVMTrainAndTest(@RequestBody List<Duplicate> duplicates, @RequestParam int k) {
        return fesvmService.trainAndTest(duplicates, k);
    }

}
