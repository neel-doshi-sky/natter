package com.natter.repository.natter;

import com.natter.model.natter.NatterById;
import java.time.LocalDateTime;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NatterByIdRepository extends CassandraRepository<NatterById, String> {

  /**
   * Query to update the body of a natter by id
   *
   * @param body the body of the natter
   * @param id   the id of the natter to update
   */
  @Query(value = "update natters_by_id set body = :body, date_updated = dateof(now())  where id = :id")
  void updateNatter(@Param("body") final String body, @Param("id") final String id);
}
