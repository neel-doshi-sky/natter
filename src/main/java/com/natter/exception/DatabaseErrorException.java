package com.natter.exception;

import com.natter.enums.natter.ErrorMessageEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DatabaseErrorException extends Exception{
  ErrorMessageEnum errorMessageEnum;
}
