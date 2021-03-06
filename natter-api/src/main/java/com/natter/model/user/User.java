package com.natter.model.user;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

@Table(value = "user")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {

  public User(String id) {
    this.id = id;
  }

  @Id
  @PrimaryKeyColumn(name = "id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
  private String id;

  @Column(value = "first_name")
  private String firstName;

  @Column(value = "last_name")
  private String lastName;

  @Column
  private String email;

  @Column
  private Set<String> followers = new HashSet<>();

  @Column
  private Set<String> following = new HashSet<>();

  @Column(value = "date_created")
  private LocalDateTime dateCreated = LocalDateTime.now();

}
