package com.quim.tfm.similarity.controller;

import com.quim.tfm.similarity.entity.Requirement;
import com.quim.tfm.similarity.exception.BadRequestCustomException;
import com.quim.tfm.similarity.model.RequirementPair;
import com.quim.tfm.similarity.service.SimilarityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/requirements-similarity/evaluation")
public class SimilarityController {

    @Autowired
    private SimilarityService similarityService;

    @PostMapping("/bm25f_Req")
    public void bm25fReq(@RequestBody @Valid Requirement requirement) {
        similarityService.bm25f_req(requirement);
    }

}
