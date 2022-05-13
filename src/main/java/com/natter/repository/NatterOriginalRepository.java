package com.natter.repository;

import com.natter.model.natter.NatterOriginal;
import java.util.List;
import java.util.Optional;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NatterOriginalRepository extends CassandraRepository<NatterOriginal, String> {

  @Query("SELECT id FROM natter WHERE authorId = :authorId AND id = :id LIMIT 1")
  public Optional<String> findByAuthorIdAndNatterId(@Param("authorId") String authorId,
                                                    @Param("id") String id);

  @Query("DELETE FROM natter WHERE id = :id")
  public void deleteByNatterId(@Param("id") String id);

  @Query("SELECT * FROM natter WHERE authorId = :authorId")
  List<NatterOriginal> getNattersByAuthorId(@Param("authorId") String authorId);
}
