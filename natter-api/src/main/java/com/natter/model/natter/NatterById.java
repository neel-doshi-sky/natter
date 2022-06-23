package com.natter.model.natter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

@Table(value = "natters_by_id")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NatterById {

  public NatterById(String id) {
    this.id = id;
  }

  public NatterById(String body, String parentNatterId) {
    this.body = body;
    this.parentNatterId = parentNatterId;
  }

  @Id
  @PrimaryKeyColumn(name = "id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
  @CassandraType(type = CassandraType.Name.TEXT)
  private String id;

  @CassandraType(type = CassandraType.Name.TEXT)
  private String body;

  @Column(value = "parent_natter_id")
  @CassandraType(type = CassandraType.Name.TEXT)
  private String parentNatterId;

  @Column(value = "date_created")
  private LocalDateTime dateCreated;

  @Column(value = "date_updated")
  private LocalDateTime dateUpdated;

  @CassandraType(type = CassandraType.Name.TEXT)
  @Column(value = "author_id")
  private String authorId;

  @CassandraType(type = CassandraType.Name.TEXT)
  @Column(value = "author_name")
  private String authorName;

  @CassandraType(type = CassandraType.Name.TEXT)
  @Column
  private List<String> comments = new ArrayList<>();

  @CassandraType(type = CassandraType.Name.TEXT)
  @Column
  private List<String> likes = new ArrayList<>();

}
