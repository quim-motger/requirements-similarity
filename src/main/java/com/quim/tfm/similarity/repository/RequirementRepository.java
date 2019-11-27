package com.quim.tfm.similarity.repository;

import com.quim.tfm.similarity.entity.Requirement;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RequirementRepository extends CrudRepository<Requirement, String> {

    @Query("FROM Requirement WHERE project in (?1)")
    List<Requirement> findAllByProject(List<String> projectList);

    @Query("FROM Requirement WHERE project in (?2) AND id in (?1)")
    List<Requirement> findAllByIdAndProjects(List<String> requirements, List<String> projects);

}
