package com.natter.model.template;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NatterToDisplay {

  private String id;
  private String body;
  private String authorName;
  private String dateCreated;
  private String commentCount;
}
