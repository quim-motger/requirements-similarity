package com.quim.tfm.similarity.controller;

import com.quim.tfm.similarity.exception.BadRequestCustomException;
import com.quim.tfm.similarity.entity.Requirement;
import com.quim.tfm.similarity.model.openreq.OpenReqSchema;
import com.quim.tfm.similarity.service.RequirementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/requirements-similarity/requirement")
public class RequirementController {

    @Autowired
    private RequirementService requirementService;

    @PostMapping("")
    public void addRequirements(@RequestBody @Valid OpenReqSchema openReqSchema) {
        if (openReqSchema == null || openReqSchema.getRequirements().isEmpty()) throw new BadRequestCustomException();
        requirementService.addRequirements(openReqSchema);
    }

    @GetMapping("")
    public OpenReqSchema getRequirements(@RequestParam(required = false) List<String> requirements,
                                         @RequestParam(required = false) List<String> projects) {
        return requirementService.getOpenReqSchema(requirements, projects);
    }

    @GetMapping("/{reqId}")
    public Requirement getRequirement(@PathVariable String reqId) {
        return requirementService.getRequirement(reqId);
    }

    @DeleteMapping("/{reqId}")
    public void deleteRequirement(@PathVariable String reqId) {
        requirementService.deleteRequirement(reqId);
    }

    @DeleteMapping("")
    public void dropDatabase() {
        requirementService.deleteAllRequirements();
    }

}
