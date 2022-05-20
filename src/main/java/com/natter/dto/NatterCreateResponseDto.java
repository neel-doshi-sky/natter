package com.natter.dto;

import com.natter.model.natter.NatterById;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NatterCreateResponseDto extends ResponseDto {

  private NatterById natterById;
}
