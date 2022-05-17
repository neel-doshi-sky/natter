package com.natter.dto;

import com.natter.model.natter.NatterById;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NatterCreationResponseDto extends BaseResponseDto {

  private NatterById natterById;
}
