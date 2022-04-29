package com.natter.model.natter;

import com.natter.model.natter.Natter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NatterListResponse {

  private List<Natter> natterList = new ArrayList<>();
  private LocalDateTime lastRefreshed;
  private List<String> errorMessages = new ArrayList<>();
  private List<String> messages = new ArrayList<>();
}
