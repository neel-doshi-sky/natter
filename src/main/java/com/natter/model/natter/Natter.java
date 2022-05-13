package com.natter.model.natter;

import java.time.LocalDateTime;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

@Table(value = "natter")
@Getter
@Setter
@Builder
public class Natter {

  @PrimaryKeyColumn(name = "id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
  private String id;
  @PrimaryKeyColumn(name = "authorId",ordinal = 1, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
  private String authorId;
  @Column
  private String body;
  @Column
  private Set<String> userReactions;
  @Column
  private LocalDateTime timeCreated;
  @Column
  private LocalDateTime timeUpdated;
  @Column
  private String parentNatterId;

}
