package com.natter.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.natter.enums.natter.ErrorMessageEnum;
import com.natter.enums.natter.SuccessMessageEnum;
import com.natter.model.natter.Natter;
import com.natter.model.natter.NatterCreateRequest;
import com.natter.model.natter.NatterCreationResponse;
import com.natter.repository.NatterRepository;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NatterServiceTest {

  @InjectMocks
  NatterService natterService;

  @Mock
  NatterRepository natterRepository;


  @Mock
  NatterValidationService natterValidationService;


  @Test
  public void whenNatterIsNull_ReturnNoNatterToCreateMessageToUser() {
    NatterCreateRequest natter = null;
    NatterCreationResponse response = natterService.create(natter);
    assertAll(
        () -> assertNotNull(response),
        () -> assertEquals(1, response.getErrorMessages().size()),
        () -> assertEquals(ErrorMessageEnum.NATTER_CREATION_ERROR_NULL_BODY.getMessage(),
            response.getErrorMessages()
                .get(ErrorMessageEnum.NATTER_CREATION_ERROR_NULL_BODY.getErrorCode())),
        () -> assertTrue(response.getUserMessages().isEmpty()));
  }

  @Test
  public void whenValidNatterIsSent_SaveNatterToDatabaseAndReturnSuccessMessage() {
    NatterCreateRequest natterRequest = new NatterCreateRequest();
    natterRequest.setParentNatterId(null);
    natterRequest.setAuthorId("testUserId");
    natterRequest.setBody("This is a natter!");

    Natter createdNatter = Natter.builder().build();
    createdNatter.setParentNatterId(null);
    createdNatter.setAuthorId("testUserId");
    createdNatter.setUserReactions(new HashSet<>());
    createdNatter.setBody("This is a natter!");
    createdNatter.setId("12323");
    createdNatter.setTimeCreated(LocalDateTime.now());
    createdNatter.setTimeUpdated(createdNatter.getTimeCreated());

    when(natterValidationService.validateNatterCreateBody(any())).thenReturn(new HashMap<>());
    when(natterRepository.save(any())).thenReturn(createdNatter);

    NatterCreationResponse response = natterService.create(natterRequest);

    assertAll(
        () -> assertNotNull(response),
        () -> assertEquals(1, response.getUserMessages().size()),
        () -> assertEquals("12323", response.getCreatedNatter().getId()),
        () -> assertEquals(SuccessMessageEnum.CREATED_NEW_NATTER.getMessage(),
            response.getUserMessages().get(SuccessMessageEnum.CREATED_NEW_NATTER.getCode())),
        () -> assertEquals(natterRequest.getBody(), response.getCreatedNatter().getBody()),
        () -> assertEquals(natterRequest.getParentNatterId(),
            response.getCreatedNatter().getParentNatterId()),
        () -> assertNotNull(response.getCreatedNatter().getTimeCreated()),
        () -> assertNotNull(response.getCreatedNatter().getTimeUpdated()),
        () -> assertEquals(natterRequest.getAuthorId(), response.getCreatedNatter().getAuthorId()),
        () -> assertTrue(response.getErrorMessages().isEmpty()));

    verify(natterRepository, times(1)).save(any());
  }

  @Test
  public void whenMissingKeyFieldsArePassed_returnMissingFieldsErrorMessage() {
    NatterCreateRequest natterRequest = new NatterCreateRequest();
    natterRequest.setBody(null);
    natterRequest.setAuthorId(null);

    Map<String, String> errors = new HashMap<>();
    errors.put("body", ErrorMessageEnum.NULL_OR_EMPTY_FIELD.getMessage());
    errors.put("authorId", ErrorMessageEnum.NULL_OR_EMPTY_FIELD.getMessage());

    when(natterValidationService.validateNatterCreateBody(any())).thenReturn(errors);
    NatterCreationResponse natterCreationResponse = natterService.create(natterRequest);
    assertAll(
        () -> assertNotNull(natterCreationResponse),
        () -> assertEquals(2, natterCreationResponse.getErrorMessages().size())
    );

  }

}