package com.natter.repository.natter;

import com.natter.model.natter.NatterByAuthor;
import com.natter.model.natter.NatterByAuthorPrimaryKey;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
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
  @Query(value = "select * from natters_by_author where author_id = :authorId")
  List<NatterByAuthor> findAllByAuthorId(@Param("authorId") String authorId);

  /**
   * Query to update the body of a natter by id
   *
   * @param body the body of the natter
   * @param id   the id of the natter to update
   */
  @Query(value = "update natters_by_author set body = :body, date_updated = dateof(now())  where id = :id and author_id = :authorId")
  void updateNatter(@Param("body") final String body, @Param("id") final String id,
                    @Param("authorId") final String authorId);

}
