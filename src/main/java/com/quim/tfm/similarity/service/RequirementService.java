package com.quim.tfm.similarity.service;

import com.quim.tfm.similarity.entity.Requirement;
import com.quim.tfm.similarity.exception.NotFoundCustomException;
import com.quim.tfm.similarity.model.Priority;
import com.quim.tfm.similarity.model.openreq.OpenReqProject;
import com.quim.tfm.similarity.model.openreq.OpenReqRequirement;
import com.quim.tfm.similarity.model.openreq.OpenReqRequirementPart;
import com.quim.tfm.similarity.model.openreq.OpenReqSchema;
import com.quim.tfm.similarity.repository.RequirementRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
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

    public void addRequirements(OpenReqSchema requirements) {
        addRequirements(convertFromOpenReqList(requirements));
    }

    public OpenReqSchema getOpenReqSchema(List<String> requirements) {
        List<Requirement> foundRequirements;
        if (requirements == null || requirements.isEmpty())
            foundRequirements = StreamSupport.stream(requirementRepository.findAll().spliterator(), false)
                    .collect(Collectors.toList());

        else
            foundRequirements = StreamSupport.stream(requirementRepository.findAllById(requirements).spliterator(), false)
                    .collect(Collectors.toList());

        return convertToOpenReqList(foundRequirements);
    }

    private void addRequirements(List<Requirement> requirements) {
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
        logger.info("Dropping database...");
        requirementRepository.deleteAll();
        logger.info("Database dropped");
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

    private List<Requirement> convertFromOpenReqList(OpenReqSchema requirements) {
        List<Requirement> requirementList = new ArrayList<>();
        for (OpenReqRequirement orr : requirements.getRequirements()) {
            Requirement r = new Requirement();
            r.setId(orr.getId());
            r.setSummary(orr.getName());
            r.setDescription(orr.getText());
            for (OpenReqRequirementPart orrp : orr.getRequirementParts()) {
                switch (orrp.getId()) {
                    case "priority":
                        r.setPriority(Priority.fromString(orrp.getName()));
                        break;
                    case "type":
                        r.setType(orrp.getName());
                        break;
                    case "components":
                        r.setComponents(orrp.getName().split("\n"));
                        break;
                    case "versions":
                        r.setVersions(orrp.getName().split("\n"));
                        break;
                    default:
                        logger.warn("Unrecognized requirement part " + orrp.getId() + " from " + orr.getId());
                }
            }
            r.setProject(requirements.getProjects().stream().filter(p -> p.getSpecifiedRequirements().contains(orr.getId()))
                    .map(OpenReqProject::getId).findFirst().orElse(null));
            requirementList.add(r);
        }
        return requirementList;
    }

    private OpenReqSchema convertToOpenReqList(List<Requirement> requirementList) {
        OpenReqSchema openReqSchema = new OpenReqSchema();
        HashMap<String, List<String>> projects = new HashMap<>();
        for (Requirement r : requirementList) {
            OpenReqRequirement or = new OpenReqRequirement();
            or.setId(r.getId());
            or.setName(r.getSummary());
            or.setText(r.getDescription());
            OpenReqRequirementPart priority = new OpenReqRequirementPart("priority", r.getPriority().toString());
            OpenReqRequirementPart type = new OpenReqRequirementPart("type", r.getType());
            OpenReqRequirementPart components = new OpenReqRequirementPart("components", String.join("\n", r.getComponents()));
            OpenReqRequirementPart versions = new OpenReqRequirementPart("versions", String.join("\n", r.getVersions()));
            or.setRequirementParts(Arrays.asList(priority, type, components, versions));
            openReqSchema.getRequirements().add(or);

            if (projects.containsKey(r.getProject())) {
                projects.get(r.getProject()).add(r.getId());
            }
            else {
                List<String> rList = new ArrayList<>();
                rList.add(r.getId());
                projects.put(r.getProject(), rList);
            }
        }
        for (String project : projects.keySet()) {
            OpenReqProject orp = new OpenReqProject(project, projects.get(project));
            openReqSchema.getProjects().add(orp);
        }
        return openReqSchema;
    }

}
