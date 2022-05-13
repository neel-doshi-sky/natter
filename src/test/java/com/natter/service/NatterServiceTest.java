package com.natter.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.natter.dto.BaseResponseDto;
import com.natter.enums.natter.ErrorMessageEnum;
import com.natter.enums.natter.SuccessMessageEnum;
import com.natter.model.natter.Natter;
import com.natter.model.natter.NatterCreateRequest;
import com.natter.dto.NatterCreationResponseDto;
import com.natter.repository.NatterRepository;
import com.natter.service.natter.NatterService;
import com.natter.service.natter.NatterValidationService;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
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
    NatterCreationResponseDto response = natterService.create(natter);
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

    NatterCreationResponseDto response = natterService.create(natterRequest);

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
  public void whenMissingKeyFieldsArePassedToCreate_returnMissingFieldsErrorMessage() {
    NatterCreateRequest natterRequest = new NatterCreateRequest();
    natterRequest.setBody(null);
    natterRequest.setAuthorId(null);

    Map<String, String> errors = new HashMap<>();
    errors.put("body", ErrorMessageEnum.NULL_OR_EMPTY_FIELD.getMessage());
    errors.put("authorId", ErrorMessageEnum.NULL_OR_EMPTY_FIELD.getMessage());

    when(natterValidationService.validateNatterCreateBody(any())).thenReturn(errors);
    NatterCreationResponseDto natterCreationResponseDto = natterService.create(natterRequest);
    assertAll(
        () -> assertNotNull(natterCreationResponseDto),
        () -> assertEquals(2, natterCreationResponseDto.getErrorMessages().size())
    );

  }

  @Test
  public void whenNullBodyPassedToCreate_returnNullBodyError(){
    NatterCreationResponseDto natterCreationResponseDto = natterService.create(null);
    assertAll(
        () -> assertNotNull(natterCreationResponseDto),
        () -> assertNotNull(natterCreationResponseDto.getErrorMessages()),
        () -> assertEquals(1, natterCreationResponseDto.getErrorMessages().size()),
        () -> assertEquals(ErrorMessageEnum.NATTER_CREATION_ERROR_NULL_BODY.getMessage(), natterCreationResponseDto.getErrorMessages().get(ErrorMessageEnum.NATTER_CREATION_ERROR_NULL_BODY.getErrorCode()))
    );

  }

  @Test
  public void whenDeleteValidId_DeleteNatter_ReturnSuccessMessage(){
    Optional<String> optional = Optional.of("EXISTS");
    when(natterRepository.findByAuthorIdAndNatterId(any(), any())).thenReturn(optional);
    BaseResponseDto responseDto = natterService.delete("123", "123");
    assertAll(
        () -> assertNotNull(responseDto),
        () -> assertNotNull(responseDto.getUserMessages()),
        () -> assertEquals(1, responseDto.getUserMessages().size()),
        () -> assertEquals(SuccessMessageEnum.DELETED_NATTER.getMessage(), responseDto.getUserMessages().get(SuccessMessageEnum.DELETED_NATTER.getCode()))
    );
    verify(natterRepository, times(1)).deleteById(any());
  }

  @Test
  public void whenDeleteValid_ThrowException_ReturnErrorMessage(){
    doThrow(new IllegalArgumentException()).when(natterRepository).deleteById(any());
    Optional<String> optional = Optional.of("EXISTS");
    when(natterRepository.findByAuthorIdAndNatterId(any(), any())).thenReturn(optional);
    BaseResponseDto responseDto = natterService.delete("123", "123");
    assertAll(
        () -> assertNotNull(responseDto),
        () -> assertNotNull(responseDto.getErrorMessages()),
        () -> assertEquals(1, responseDto.getErrorMessages().size()),
        () -> assertEquals(ErrorMessageEnum.UNABLE_TO_DELETE_RECORD.getMessage(), responseDto.getErrorMessages().get(ErrorMessageEnum.UNABLE_TO_DELETE_RECORD.getErrorCode()))
    );
    verify(natterRepository, times(1)).deleteById(any());
  }

  @Test
  public void whenDeleteIdIsNull_returnErrorMessage(){
    BaseResponseDto responseDto = natterService.delete(null, "123");
    assertAll(
        () -> assertNotNull(responseDto),
        () -> assertNotNull(responseDto.getErrorMessages()),
        () -> assertEquals(1, responseDto.getErrorMessages().size()),
        () -> assertEquals(ErrorMessageEnum.NATTER_NULL_ID.getMessage(), responseDto.getErrorMessages().get(ErrorMessageEnum.NATTER_NULL_ID.getErrorCode()))
    );

  }

  @Test
  public void whenDeleteIdDoesNotBelongToAuthor_returnErrorMessage(){
    Optional<String> optional = Optional.empty();
    when(natterRepository.findByAuthorIdAndNatterId(any(), any())).thenReturn(optional);
    BaseResponseDto responseDto = natterService.delete("123", "123");
    assertAll(
        () -> assertNotNull(responseDto),
        () -> assertNotNull(responseDto.getErrorMessages()),
        () -> assertEquals(1, responseDto.getErrorMessages().size()),
        () -> assertEquals(ErrorMessageEnum.UNAUTHORISED_ACCESS_NATTER.getMessage(), responseDto.getErrorMessages().get(ErrorMessageEnum.UNAUTHORISED_ACCESS_NATTER.getErrorCode()))
    );
  }

}