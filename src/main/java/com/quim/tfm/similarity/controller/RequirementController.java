package com.quim.tfm.similarity.controller;

import com.quim.tfm.similarity.exception.BadRequestCustomException;
import com.quim.tfm.similarity.entity.Requirement;
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
    public void addRequirements(@RequestBody @Valid List<Requirement> requirements) {
        if (requirements == null || requirements.isEmpty()) throw new BadRequestCustomException();
        requirementService.addRequirements(requirements);
    }

    @GetMapping("")
    public List<Requirement> getRequirements() {
        return requirementService.getRequirements();
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
