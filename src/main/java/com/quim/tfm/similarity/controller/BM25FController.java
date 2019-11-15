package com.quim.tfm.similarity.controller;

import com.quim.tfm.similarity.entity.Requirement;
import com.quim.tfm.similarity.model.Duplicate;
import com.quim.tfm.similarity.service.BM25FService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/requirements-similarity/bm25f")
public class BM25FController {

    @Autowired
    private BM25FService BM25FService;

    @PostMapping("/req")
    public List<Duplicate> bm25fReq(@RequestBody @Valid Requirement requirement, @RequestParam Integer k) {
        return BM25FService.bm25f_req(requirement, k);
    }

    @PostMapping("/train")
    public void bm25fTrain(@RequestBody List<Duplicate> duplicates) {
        BM25FService.bm25f_train(duplicates);
    }

    @PostMapping("/test")
    public HashMap<Integer, Double> bm25fTest(@RequestParam Integer k, @RequestBody List<Duplicate> duplicates) {
        return BM25FService.bm25f_test(duplicates, k);
    }

}
