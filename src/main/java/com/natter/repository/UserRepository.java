package com.natter.repository;

import com.natter.model.user.User;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CassandraRepository<User, String> {

  @Query(value = "UPDATE user SET followers = followers + {:followerId} WHERE id = :userIdToFollow;")
  void addUserToFollowerList(@Param("followerId") final String followerId, @Param("userIdToFollow") final String userIdToFollow);

  @Query(value = "UPDATE user SET following = following + {:userIdToFollow} WHERE id = :followerId;")
  void addUserToFollowingList(@Param("followerId") final String followerId, @Param("userIdToFollow") final String userIdToFollow);


  @Query(value = "UPDATE user SET followers = followers - {:followerId} WHERE id = :userIdToFollow;")
  void removeUserFromFollowerList(@Param("followerId") final String followerId, @Param("userIdToFollow") final String userIdToFollow);

  @Query(value = "UPDATE user SET following = following - {:userIdToFollow} WHERE id = :followerId;")
  void removeUserFromFollowingList(@Param("followerId") final String followerId, @Param("userIdToFollow") final String userIdToFollow);
}
