package com.natter.exception;

import com.natter.enums.natter.ErrorMessageNatterEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DatabaseErrorException extends Exception{
  ErrorMessageNatterEnum errorMessageNatterEnum;
}
