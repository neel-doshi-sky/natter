package com.natter.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DatabaseErrorException extends Exception{
  String errorMessage;
}
