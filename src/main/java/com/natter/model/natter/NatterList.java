package com.natter.model.natter;

import lombok.Data;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("natter_by_author")
@Data
public class NatterList {

  @PrimaryKey
  private NatterListPrimaryKey id;

  @CassandraType(type = CassandraType.Name.TEXT)
  private String body;
}
