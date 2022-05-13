package com.natter.repository;

import com.natter.model.natter.Natter;
import java.util.UUID;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NatterRepository extends CassandraRepository<Natter, String> {
}
