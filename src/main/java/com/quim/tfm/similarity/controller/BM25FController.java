package com.quim.tfm.similarity.controller;

import com.quim.tfm.similarity.model.openreq.OpenReqSchema;
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
    public OpenReqSchema bm25fReq(@RequestBody @Valid OpenReqSchema schema, @RequestParam(required = false) List<String> projectList, @RequestParam Integer k) {
        return BM25FService.bm25f_req(schema, k);
    }

    @PostMapping("/reqReq")
    public OpenReqSchema bm25fReqReq(@RequestBody @Valid OpenReqSchema schema, @RequestParam(required = false) List<String> projectList) {
        return BM25FService.bm25f_reqReq(schema);
    }

    @PostMapping("/train")
    public void bm25fTrain(@RequestBody OpenReqSchema schema, @RequestParam(required = false) List<String> projectList) {
        BM25FService.bm25f_train(schema);
    }

    @PostMapping("/test")
    public HashMap<Integer, Double> bm25fTest(@RequestParam Integer k, @RequestParam(required = false) List<String> projectList,
                                              @RequestBody OpenReqSchema schema) {
        return BM25FService.bm25f_test(schema, projectList, k);
    }

}
