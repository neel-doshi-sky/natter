package com.natter.service;

import com.natter.enums.natter.ErrorMessageEnum;
import com.natter.enums.natter.NatterRequiredFieldsEnum;
import com.natter.model.natter.NatterCreateRequest;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class NatterValidationService {

  public Map<String, String> validateNatterCreateBody(NatterCreateRequest natterRequest){
    Map<String, String> validationResults = new HashMap<>();
    validationResults.putAll(validateStringField(natterRequest.getBody(), NatterRequiredFieldsEnum.BODY));
    validationResults.putAll(validateStringField(natterRequest.getParentNatterId(), NatterRequiredFieldsEnum.PARENT_NATTER_ID));
    validationResults.putAll(validateStringField(natterRequest.getBody(), NatterRequiredFieldsEnum.AUTHOR_ID));


    return validationResults;

  }

  public Map<String, String> validateStringField(String field, NatterRequiredFieldsEnum natterRequiredFieldsEnum){
    Map<String, String> errorMessage = new HashMap<>();
    if(field == null && !natterRequiredFieldsEnum.isNullable()){
      errorMessage.put(natterRequiredFieldsEnum.getField(), ErrorMessageEnum.NULL_OR_EMPTY_FIELD.getMessage());
    } else if(natterRequiredFieldsEnum.getCharacterLimit() != null && (field != null && field.length() > natterRequiredFieldsEnum.getCharacterLimit())) {
      long exceeded = field.length() - natterRequiredFieldsEnum.getCharacterLimit();
      errorMessage.put(natterRequiredFieldsEnum.getField(), ErrorMessageEnum.EXCEEDED_CHAR_LIMIT.getMessage() + exceeded);
    }
    return errorMessage;
  }





}
