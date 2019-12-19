package com.quim.tfm.similarity.service;

import com.quim.tfm.similarity.entity.Requirement;
import com.quim.tfm.similarity.exception.NotFoundCustomException;
import com.quim.tfm.similarity.model.DependencyType;
import com.quim.tfm.similarity.model.Duplicate;
import com.quim.tfm.similarity.model.DuplicateTag;
import com.quim.tfm.similarity.model.Priority;
import com.quim.tfm.similarity.model.openreq.*;
import com.quim.tfm.similarity.repository.RequirementRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
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

    private RequirementService() {
    }

    public void addRequirements(OpenReqSchema requirements) {
        addRequirements(getRequirementsFromOpenReqSchema(requirements));
    }

    public OpenReqSchema getOpenReqSchema(List<String> requirements, List<String> projects) {
        List<Requirement> foundRequirements;

        if ((requirements == null || requirements.isEmpty()) && (projects == null || projects.isEmpty()))
            foundRequirements = StreamSupport.stream(requirementRepository.findAll().spliterator(), false)
                    .collect(Collectors.toList());
        else if (requirements == null || requirements.isEmpty())
            foundRequirements = requirementRepository.findAllByProject(projects);
        else if (projects == null || projects.isEmpty())
            foundRequirements = StreamSupport.stream(requirementRepository.findAllById(requirements).spliterator(), false)
                    .collect(Collectors.toList());
        else
            foundRequirements = requirementRepository.findAllByIdAndProjects(requirements, projects);

        return convertToOpenReqSchema(foundRequirements, null);
    }

    private void addRequirements(List<Requirement> requirements) {
        preprocessService.preprocessRequirementList(requirements);
        logger.info("Storing requirement list...");
        requirementRepository.saveAll(requirements);
        logger.info("Requirement list stored");
    }

    @Cacheable
    public List<Requirement> getRequirements() {
        return StreamSupport.stream(requirementRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    @Cacheable
    public Requirement getRequirement(String reqId) {
        if (requirementRepository.findById(reqId).isPresent())
            return requirementRepository.findById(reqId).get();
        else
            throw new NotFoundCustomException();
    }

    @Cacheable
    public OpenReqSchema getRequirementInOpenReqSchema(String reqId) {
        if (requirementRepository.findById(reqId).isPresent())
            return convertToOpenReqSchema(Collections.singletonList(requirementRepository.findById(reqId).get()), null);
        else
            throw new NotFoundCustomException();
    }

    public void deleteRequirement(String reqId) {
        if (requirementRepository.findById(reqId).isPresent()) {
            requirementRepository.deleteById(reqId);
        }
        else
            throw new NotFoundCustomException();
    }

    public void deleteAllRequirements() {
        logger.info("Dropping database...");
        requirementRepository.deleteAll();
        logger.info("Database dropped");
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

    public OpenReqSchema convertToOpenReqSchema(List<Requirement> requirementList, List<Duplicate> duplicateList) {
        OpenReqSchema openReqSchema = new OpenReqSchema();
        HashMap<String, List<String>> projects = new HashMap<>();
        if (requirementList != null) {
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
                } else {
                    List<String> rList = new ArrayList<>();
                    rList.add(r.getId());
                    projects.put(r.getProject(), rList);
                }
            }
        }
        //Project
        for (String project : projects.keySet()) {
            OpenReqProject orp = new OpenReqProject(project, projects.get(project));
            openReqSchema.getProjects().add(orp);
        }
        //Duplicate list
        if (duplicateList != null) {
            List<OpenReqDependency> dependencyList = new ArrayList<>();
            for (Duplicate d : duplicateList) {
                OpenReqDependency ord = new OpenReqDependency(d.getReq1Id(), d.getReq2Id(), DependencyType.duplicates, d.getScore());
                ord.setStatus(d.getTag().equals(DuplicateTag.DUPLICATE) ? DependencyStatus.accepted : DependencyStatus.rejected);
                dependencyList.add(ord);
            }
            openReqSchema.setDependencies(dependencyList);
        }
        return openReqSchema;
    }

    public List<Duplicate> getDuplicatesFromOpenReqSchema(OpenReqSchema schema) {
        List<Duplicate> duplicates = new ArrayList<>();
        for (OpenReqDependency ord : schema.getDependencies()) {
            Duplicate d = new Duplicate(ord.getFromid(), ord.getToid(), ord.getDependency_score());
            d.setTag(ord.getStatus().equals(DependencyStatus.accepted) ? DuplicateTag.DUPLICATE : DuplicateTag.NOT_DUPLICATE);
            duplicates.add(d);
        }
        return duplicates;
    }


    @Cacheable
    public List<Requirement> getRequirementsFromOpenReqSchema(OpenReqSchema requirements) {
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

    @Cacheable
    public List<Requirement> getRequirementsByProjects(List<String> projectList) {
        return requirementRepository.findAllByProject(projectList);
    }
}
