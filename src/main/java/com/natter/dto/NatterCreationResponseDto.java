package com.natter.dto;

import com.natter.model.natter.Natter;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NatterCreationResponseDto extends BaseResponseDto {

  private Natter createdNatter;
}
