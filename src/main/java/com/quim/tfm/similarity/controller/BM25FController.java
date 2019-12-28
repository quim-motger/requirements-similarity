package com.quim.tfm.similarity.controller;

import com.quim.tfm.similarity.model.openreq.OpenReqSchema;
import com.quim.tfm.similarity.service.BM25FService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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
    @ApiOperation(value = "Find duplicates of a requirement", notes = "Given a requirement, the BM25F algorithm is run " +
            "against a set of requirements, and the list of the top *k* most similar requirements is returned.")
    public OpenReqSchema bm25fReq(@ApiParam(value = "The requirement to be analyzed", required = true)
                                      @RequestBody @Valid OpenReqSchema schema,
                                  @ApiParam(value = "The list of projects to use as corpus. If empty, all projects are used")
                                  @RequestParam(required = false) List<String> projectList,
                                  @ApiParam(value = "The number of most similar duplicates to return", required = true)
                                  @RequestParam Integer k) {
        return BM25FService.bm25f_req(schema, projectList, k);
    }

    @PostMapping("/reqReq")
    @ApiOperation(value = "Compare two requirements", notes = "Given a pair of requirements, the similarity score is " +
            "returned.")
    public OpenReqSchema bm25fReqReq(@ApiParam(value = "The requirement to be analyzed", required = true) @RequestBody @Valid OpenReqSchema schema,
                                     @ApiParam(value = "The list of projects to use as corpus. If empty, all projects are used")
                                     @RequestParam(required = false) List<String> projectList) {
        return BM25FService.bm25f_reqReq(schema, projectList);
    }

    @PostMapping("/train")
    @ApiOperation(value = "Optimize free parameters", notes = "Given a set of duplicated requirement pairs, a tuning process" +
            " is run to optimize the weights of the features of the algorithms.")
    public void bm25fTrain(@ApiParam(value = "The set of duplicated pairs", required = true) @RequestBody OpenReqSchema schema,
                           @ApiParam(value = "The list of projects to use as corpus. If empty, all projects are used")
                           @RequestParam(required = false) List<String> projectList) {
        BM25FService.bm25f_train(schema, projectList);
    }

    @PostMapping("/test")
    @ApiOperation(value = "recall-rate@k", notes = "Given a set of requirements and a set of known duplicates, a recall-rate@k analysis is performed")
    public HashMap<Integer, Double> bm25fTest(@ApiParam(value = "The number of most similar duplicates to return", required = true)
                                                  @RequestParam Integer k,
                                              @ApiParam(value = "The list of projects to use as corpus. If empty, all projects are used")
                                              @RequestParam(required = false) List<String> projectList,
                                              @ApiParam(value = "The set of duplicated pairs", required = true)
                                              @RequestBody OpenReqSchema schema) {
        return BM25FService.bm25f_test(schema, projectList, k);
    }

}
