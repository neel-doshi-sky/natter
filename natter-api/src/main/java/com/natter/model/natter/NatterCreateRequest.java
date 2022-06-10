package com.natter.model.natter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NatterCreateRequest {


  private String body;

  private String parentNatterId;
}
