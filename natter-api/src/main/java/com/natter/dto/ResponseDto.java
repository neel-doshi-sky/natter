package com.natter.dto;

import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseDto {

  private Map<String, String> errorMessages = new HashMap<>();

  private Map<String, String> userMessages = new HashMap<>();

  private HttpStatus status;
}
