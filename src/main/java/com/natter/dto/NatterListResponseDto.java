package com.natter.dto;

import com.natter.model.natter.NatterList;
import com.natter.model.natter.NatterOriginal;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NatterListResponseDto extends BaseResponseDto{

  private List<NatterOriginal> natterOriginalList = new ArrayList<>();
  private List<NatterList> natterLists = new ArrayList<>();
}
