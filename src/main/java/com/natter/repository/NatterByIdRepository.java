package com.natter.repository;

import com.natter.model.natter.NatterById;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NatterByIdRepository extends CassandraRepository<NatterById, String> {
}
