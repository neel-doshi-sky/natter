package com.natter.repository.user;

import com.natter.model.user.User;
import java.util.List;
import java.util.Set;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CassandraRepository<User, String> {

  @Query(value = "select following from user where id=:authId")
  Set<String> getFollowingList(@Param("authId") final String authId);

}
