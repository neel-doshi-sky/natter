package com.natter.repository;

import com.natter.model.natter.NatterByAuthor;
import com.natter.model.natter.NatterByAuthorPrimaryKey;
import java.util.List;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NatterByAuthorRepository
    extends CassandraRepository<NatterByAuthor, NatterByAuthorPrimaryKey> {

  /**
   * Query to select all natters for a given author id
   *
   * @param authorId the author id
   * @return the list of natters for that author id
   */
  @Query("select * from natter_by_author where authorId = :authorId")
  List<NatterByAuthor> findAllByAuthorId(@Param("authorId") String authorId);

}
