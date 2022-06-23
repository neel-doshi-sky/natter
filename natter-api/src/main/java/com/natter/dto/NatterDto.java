package com.natter.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Transient;

@Data
@AllArgsConstructor
public class NatterDto {

  public NatterDto(String id, String body, String parentNatterId, LocalDateTime dateCreated,
                   LocalDateTime dateUpdated, String authorId, String authorName, boolean isOwnedByAuth, boolean edited, int likes, List<String> userLikes) {
    this.id = id;
    this.body = body;
    this.parentNatterId = parentNatterId;
    this.dateCreated = dateCreated;
    this.dateUpdated = dateUpdated;
    this.authorId = authorId;
    this.authorName = authorName;
    this.isOwnedByAuth = isOwnedByAuth;
    this.edited = edited;
    this.likes = likes;
    this.userLikes = userLikes;
  }

  public NatterDto(String id) {
    this.id = id;
  }

  private String id;


  private String body;


  private String parentNatterId;


  private LocalDateTime dateCreated;


  private LocalDateTime dateUpdated;


  private String authorId;


  private String authorName;


  private List<NatterDto> comments;

  private boolean isOwnedByAuth;

  @Transient
  private boolean edited;


  private int likes = 0;


  private List<String> userLikes = new ArrayList<>();


}
