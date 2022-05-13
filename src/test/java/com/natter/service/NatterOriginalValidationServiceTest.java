package com.natter.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.natter.enums.natter.NatterRequiredFieldsEnum;
import com.natter.model.natter.NatterCreateRequest;
import com.natter.service.natter.NatterValidationService;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Random;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NatterOriginalValidationServiceTest {

  NatterValidationService validationService = new NatterValidationService();

  @Test
  public void whenInvalidBodyWithNullProperties_returnMissingFieldsWithErrors(){
    NatterCreateRequest natter = new NatterCreateRequest();
    natter.setBody(null);

    Map<String, String> result = validationService.validateNatterCreateBody(natter);
    assertAll(
        () -> assertNotNull(result),
        () -> assertEquals(1, result.size())
    );


  }

  @Test
  public void whenValidBody_returnEmptyErrorMap(){
    NatterCreateRequest natter = new NatterCreateRequest();
    natter.setBody("test");
    natter.setParentNatterId(null);

    Map<String, String> result = validationService.validateNatterCreateBody(natter);
    assertAll(
        () -> assertNotNull(result),
        () -> assertTrue(result.isEmpty())
    );
  }

  @Test
  public void whenExceedingCharacterCount_returnError(){
    byte[] array = new byte[600];
    new Random().nextBytes(array);
    String test = new String(array, StandardCharsets.UTF_8);
    Map<String, String> result = validationService.validateStringField(test, NatterRequiredFieldsEnum.BODY);
    assertAll(
        () -> assertNotNull(result),
        () -> assertEquals(1, result.size())
    );
  }

}