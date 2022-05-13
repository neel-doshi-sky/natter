package com.natter.repository;

import com.natter.model.natter.NatterList;
import com.natter.model.natter.NatterListPrimaryKey;
import java.util.List;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NatterListRepository extends CassandraRepository<NatterList, NatterListPrimaryKey> {

  @Query("select * from natter_by_author where authorId = :authorId")
  List<NatterList> findAllByAuthorId(@Param("authorId") String authorId);

}
