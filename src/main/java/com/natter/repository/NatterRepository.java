package com.natter.repository;

import com.natter.model.natter.Natter;
import java.util.Optional;
import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;

public interface NatterRepository extends CassandraRepository<Natter, String> {

  @Query("SELECT id FROM natter WHERE authorId = :authorId AND id = :id LIMIT 1")
  public Optional<String> findByAuthorIdAndNatterId(@Param("authorId") String authorId, @Param("id") String id);

  @Query("DELETE FROM natter WHERE id = :id")
  public void deleteByNatterId(@Param("id") String id);

}
