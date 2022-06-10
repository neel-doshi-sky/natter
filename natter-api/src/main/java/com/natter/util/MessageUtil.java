package com.natter.util;

import java.util.Map;
import lombok.NonNull;

public class MessageUtil {

  public Map<String, String> returnMessageMap(@NonNull final String code, @NonNull final String message){
    return Map.of(code, message);
  }
}
