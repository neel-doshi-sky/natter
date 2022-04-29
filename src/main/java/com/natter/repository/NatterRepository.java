package com.natter.repository;

import com.natter.model.natter.Natter;
import org.springframework.data.cassandra.repository.CassandraRepository;

public interface NatterRepository extends CassandraRepository<Natter, String> {
}
