package com.natter.dto;

import com.natter.model.natter.Natter;
import com.natter.model.natter.NatterOriginal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NatterCreationResponseDto extends BaseResponseDto {

  private NatterOriginal createdNatterOriginal;
  private Natter natter;
}
