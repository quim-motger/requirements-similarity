package com.quim.tfm.similarity.controller;

import com.quim.tfm.similarity.entity.Requirement;
import com.quim.tfm.similarity.model.Duplicate;
import com.quim.tfm.similarity.service.SimilarityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/requirements-similarity/evaluation")
public class SimilarityController {

    @Autowired
    private SimilarityService similarityService;

    @PostMapping("/bm25f")
    public List<Duplicate> bm25fReq(@RequestBody @Valid Requirement requirement, @RequestParam Integer k) {
        return similarityService.bm25f_req(requirement, k);
    }

    @PostMapping("/bm25f_train")
    public void bm25fTrain(@RequestBody List<Duplicate> duplicates) {
        similarityService.bm25f_train(duplicates);
    }

    @PostMapping("/bm25f_test")
    public void bm25fTest(@RequestParam Integer k, @RequestBody List<Duplicate> duplicates) {
        similarityService.bm25f_test(duplicates, k);
    }

}
