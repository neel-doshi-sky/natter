package com.natter.model.natter;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

@Data
@PrimaryKeyClass
@NoArgsConstructor
public class NatterListPrimaryKey {

  @PrimaryKeyColumn(name = "authorId", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
  private String authorId;

  @PrimaryKeyColumn(name = "created_time_uuid", ordinal = 2, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
  @CassandraType(type = CassandraType.Name.TEXT)
  private String timeId;
}
