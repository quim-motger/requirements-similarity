package com.quim.tfm.similarity.repository;

import com.quim.tfm.similarity.model.Duplicate;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface DuplicateRepository  extends CrudRepository<Duplicate, Long> {

    @Query("FROM Duplicate WHERE req1Id = ?1 AND req2Id = ?2")
    List<Duplicate> findByReqsIds(String req1Id, String req2Id);

}
