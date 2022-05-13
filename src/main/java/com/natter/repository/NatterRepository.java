package com.natter.repository;

import com.natter.model.natter.Natter;
import java.util.Optional;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NatterRepository extends CassandraRepository<Natter, String> {

  @Query("SELECT id FROM natter WHERE authorId = :authorId AND id = :id LIMIT 1")
  public Optional<String> findByAuthorIdAndNatterId(@Param("authorId") String authorId, @Param("id") String id);
}
