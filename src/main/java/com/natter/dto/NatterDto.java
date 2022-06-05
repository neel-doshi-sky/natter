package com.natter.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NatterDto {

  public NatterDto(String id, String body, String parentNatterId, LocalDateTime dateCreated,
                   LocalDateTime dateUpdated, String authorId, String authorName) {
    this.id = id;
    this.body = body;
    this.parentNatterId = parentNatterId;
    this.dateCreated = dateCreated;
    this.dateUpdated = dateUpdated;
    this.authorId = authorId;
    this.authorName = authorName;
  }

  private String id;


  private String body;


  private String parentNatterId;


  private LocalDateTime dateCreated;


  private LocalDateTime dateUpdated;


  private String authorId;


  private String authorName;


  private List<NatterDto> comments;
}
