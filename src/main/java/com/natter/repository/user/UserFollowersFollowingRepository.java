package com.natter.repository.user;

import com.natter.model.user.UserFollowersFollowing;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserFollowersFollowingRepository extends CassandraRepository<UserFollowersFollowing, String> {
}
