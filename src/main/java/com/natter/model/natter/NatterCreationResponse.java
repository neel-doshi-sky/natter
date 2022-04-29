package com.natter.model.natter;

import com.natter.dto.BaseResponseDto;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class NatterCreationResponse extends BaseResponseDto {

  private Natter createdNatter;
  private HttpStatus status;
}
