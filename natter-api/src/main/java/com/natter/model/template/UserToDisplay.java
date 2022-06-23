package com.natter.model.template;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserToDisplay {

  public UserToDisplay(String id) {
    this.id = id;
  }

  private String id;

  private String firstName;


  private String lastName;


  private String followers;


  private String following;


  private String email;

  private boolean isLoggedInUser;

  private boolean userIsFollowing;

  private boolean userIsAFollower;
}
