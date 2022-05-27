package com.natter.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ResponseListDto<T> extends ResponseDto {

  List<T> list = new ArrayList<>();
}
