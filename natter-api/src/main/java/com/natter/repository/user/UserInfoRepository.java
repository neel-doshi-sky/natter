package com.natter.repository.user;

import com.natter.model.user.UserInfo;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserInfoRepository extends CassandraRepository<UserInfo, String> {
}
