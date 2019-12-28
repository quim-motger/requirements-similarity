package com.quim.tfm.similarity.controller;

import com.quim.tfm.similarity.exception.BadRequestCustomException;
import com.quim.tfm.similarity.entity.Requirement;
import com.quim.tfm.similarity.model.openreq.OpenReqSchema;
import com.quim.tfm.similarity.service.RequirementService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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
    @ApiOperation (value = "Import requirements", notes = "Import a set of requirements to the system, performs a " +
            "Basic NLP pipeline preprocess to their natural language data, and store them into the database.")
    public void addRequirements(@ApiParam(value = "Request body object with the requirements in OpenReq JSON schema format",
            required = true) @RequestBody @Valid OpenReqSchema openReqSchema) {
        if (openReqSchema == null || openReqSchema.getRequirements().isEmpty()) throw new BadRequestCustomException();
        requirementService.addRequirements(openReqSchema);
    }

    @GetMapping("")
    @ApiOperation (value = "Export requirements", notes = "Export a set of requirements of the system.")
    public OpenReqSchema getRequirements(@ApiParam(value = "A list of requirement ids to be exported")
                                             @RequestParam(required = false) List<String> requirements,
                                         @ApiParam(value = "A list of project ids to be exported")
                                         @RequestParam(required = false) List<String> projects) {
        return requirementService.getOpenReqSchema(requirements, projects);
    }

    @GetMapping("/{reqId}")
    @ApiOperation (value = "Get a requirement", notes = "Export a specific requirement of the system.")
    public OpenReqSchema getRequirement(@ApiParam(value = "The requirement id to be exported", required = true) @PathVariable String reqId) {
        return requirementService.getRequirementInOpenReqSchema(reqId);
    }

    @DeleteMapping("/{reqId}")
    @ApiOperation (value = "Delete a requirement", notes = "Deletes a specific requirement of the system.")
    public void deleteRequirement(@ApiParam(value = "The requirement id to be deleted", required = true) @PathVariable String reqId) {
        requirementService.deleteRequirement(reqId);
    }

    @DeleteMapping("")
    @ApiOperation (value = "Drops the database", notes = "Deletes all existing requirements.")
    public void dropDatabase() {
        requirementService.deleteAllRequirements();
    }

}
