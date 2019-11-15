package com.quim.tfm.similarity.controller;

import com.quim.tfm.similarity.entity.Requirement;
import com.quim.tfm.similarity.model.Duplicate;
import com.quim.tfm.similarity.service.BM25FService;
import com.quim.tfm.similarity.service.FESVMService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
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
    public void FESVMTest(@RequestBody List<Duplicate> duplicates) {
        fesvmService.test(duplicates);
    }

}
