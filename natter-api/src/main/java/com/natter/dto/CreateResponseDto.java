package com.natter.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateResponseDto<T> extends ResponseDto {
  private T created;
}
