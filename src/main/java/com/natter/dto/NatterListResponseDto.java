package com.natter.dto;

import com.natter.model.natter.NatterByAuthor;
import com.natter.model.natter.NatterOriginal;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NatterListResponseDto extends BaseResponseDto{

  private List<NatterOriginal> natterOriginalList = new ArrayList<>();
  private List<NatterByAuthor> natterByAuthors = new ArrayList<>();
}
