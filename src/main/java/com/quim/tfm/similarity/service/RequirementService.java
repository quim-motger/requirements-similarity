package com.quim.tfm.similarity.service;

import com.quim.tfm.similarity.entity.Requirement;
import com.quim.tfm.similarity.exception.NotFoundCustomException;
import com.quim.tfm.similarity.repository.RequirementRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class RequirementService {

    private static final Logger logger = LoggerFactory.getLogger(RequirementService.class);

    @Autowired
    private RequirementRepository requirementRepository;

    @Autowired
    private PreprocessService preprocessService;

    private boolean requiresUpdate;

    private RequirementService() {
        requiresUpdate = true;
    }

    public void addRequirements(List<Requirement> requirements) {
        logger.info("Starting preprocess of requirement list...");
        preprocessService.preprocessRequirementList(requirements);
        logger.info("Storing requirement list...");
        requirementRepository.saveAll(requirements);
        logger.info("Requirement list stored");
        requiresUpdate = true;
    }

    public List<Requirement> getRequirements() {
        return StreamSupport.stream(requirementRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    public Requirement getRequirement(String reqId) {
        if (requirementRepository.findById(reqId).isPresent())
            return requirementRepository.findById(reqId).get();
        else
            throw new NotFoundCustomException();
    }

    public void deleteRequirement(String reqId) {
        if (requirementRepository.findById(reqId).isPresent()) {
            requirementRepository.deleteById(reqId);
            requiresUpdate = true;
        }
        else
            throw new NotFoundCustomException();
    }

    public void deleteAllRequirements() {
        requirementRepository.deleteAll();
        requiresUpdate = true;
    }

    public Requirement findRandomRequirement(List<String> forbiddenRequirements, List<Requirement> requirements) {
        boolean found = false;
        Requirement r = null;
        while (!found) {
            r = requirements.get(new Random().nextInt(requirements.size()));
            if (!forbiddenRequirements.contains(r.getId())) found = true;
        }
        return r;
    }

    public boolean requiresUpdate() {
        return requiresUpdate;
    }

    public void markAsUpdated() {
        this.requiresUpdate = false;
    }
}
