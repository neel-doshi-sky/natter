package com.natter.service.natter;

import com.natter.enums.natter.ErrorMessageNatterEnum;
import com.natter.enums.natter.NatterRequiredFieldsEnum;
import com.natter.model.natter.NatterCreateRequest;
import com.natter.model.natter.NatterUpdateRequest;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class NatterValidationService {

  /**
   * Method to validate the natter create body sent from the client
   *
   * @param createRequest the natter create body request
   * @return map containing any errors in validation
   */
  public Map<String, String> validateNatterCreateBody(NatterCreateRequest createRequest) {
    Map<String, String> validationResults = new HashMap<>();
    validationResults.putAll(
        validateStringField(createRequest.getBody(), NatterRequiredFieldsEnum.BODY));
    validationResults.putAll(validateStringField(createRequest.getParentNatterId(),
        NatterRequiredFieldsEnum.PARENT_NATTER_ID));
    return validationResults;

  }

  /**
   * Method to validate the natter update body sent from the client
   *
   * @param updateRequest the natter create body request
   * @return map containing any errors in validation
   */
  public Map<String, String> validateNatterUpdateBody(NatterUpdateRequest updateRequest) {
    Map<String, String> validationResults = new HashMap<>();
    validationResults.putAll(
        validateStringField(updateRequest.getBody(), NatterRequiredFieldsEnum.BODY));
    validationResults.putAll(
        validateStringField(updateRequest.getId(), NatterRequiredFieldsEnum.ID));


    return validationResults;

  }

  /**
   * Method to validate a string field
   *
   * @param field                    the field to validate
   * @param natterRequiredFieldsEnum the Enum of the field
   * @return the validation result as a map
   */
  public Map<String, String> validateStringField(String field,
                                                 NatterRequiredFieldsEnum natterRequiredFieldsEnum) {
    Map<String, String> errorMessage = new HashMap<>();
    if (field == null && !natterRequiredFieldsEnum.isNullable()) {
      errorMessage.put(natterRequiredFieldsEnum.getField(),
          ErrorMessageNatterEnum.NULL_OR_EMPTY_FIELD.getMessage());
    } else if (natterRequiredFieldsEnum.getCharacterLimit() != null &&
        (field != null && field.length() > natterRequiredFieldsEnum.getCharacterLimit())) {
      long exceeded = field.length() - natterRequiredFieldsEnum.getCharacterLimit();
      errorMessage.put(natterRequiredFieldsEnum.getField(),
          ErrorMessageNatterEnum.EXCEEDED_CHAR_LIMIT.getMessage() + exceeded);
    }
    return errorMessage;
  }


}
