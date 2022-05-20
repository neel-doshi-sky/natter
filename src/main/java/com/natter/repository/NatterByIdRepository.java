package com.natter.repository;

import com.natter.model.natter.NatterById;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NatterByIdRepository extends CassandraRepository<NatterById, String> {

  /**
   * Query to update the body of a natter by id
   *
   * @param id   the id of the natter to update
   * @param body the body of the natter
   */
  @Query("update natters_by_id set body = :body  where id = :id")
  void updateNatter(@Param("id") final String id, @Param("body") final String body);
}
