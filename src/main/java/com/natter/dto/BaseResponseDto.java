package com.natter.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@NoArgsConstructor
public class BaseResponseDto {

  private Map<String, String> errorMessages = new HashMap<>();

  private Map<String, String> userMessages = new HashMap<>();

  private HttpStatus status;
}
