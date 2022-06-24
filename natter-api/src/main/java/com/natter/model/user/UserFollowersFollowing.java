package com.natter.model.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("user_followers_following")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserFollowersFollowing {

  public UserFollowersFollowing(String id, int followers, int following) {
    this.id = id;
    this.followers = followers;
    this.following = following;
  }

  @Id
  @PrimaryKeyColumn(name = "id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
  private String id;

  @Column(value = "first_name")
  private String firstName;

  @Column(value = "last_name")
  private String lastName;

  @Column
  private int followers;

  @Column
  private int following;

  @Column
  private String email;

}
