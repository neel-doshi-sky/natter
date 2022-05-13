package com.natter.model.natter;

import lombok.Data;

@Data
public class NatterCreateRequest {


  private String body;

  private String parentNatterId;
}
