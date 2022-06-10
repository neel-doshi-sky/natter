package com.natter.model.natter;

import java.time.LocalDateTime;
import lombok.Data;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("natters_by_author")
@Data
public class NatterByAuthor {

  @PrimaryKey
  private NatterByAuthorPrimaryKey id;

  @CassandraType(type = CassandraType.Name.TEXT)
  private String body;

  @Column(value = "date_created")
  private LocalDateTime dateCreated;

  @Column(value = "date_updated")
  private LocalDateTime dateUpdated;

  @Column(value = "comment_count")
  private int commentCount = 0;

  @Column(value = "author_name")
  private String authorName;

  @Column(value = "parent_author_id")
  private String parentAuthorId;
}
