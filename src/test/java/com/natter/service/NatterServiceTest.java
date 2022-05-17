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
import com.natter.dto.NatterListResponseDto;
import com.natter.enums.natter.ErrorMessageEnum;
import com.natter.enums.natter.SuccessMessageEnum;
import com.natter.model.natter.NatterById;
import com.natter.model.natter.NatterOriginal;
import com.natter.model.natter.NatterCreateRequest;
import com.natter.dto.NatterCreationResponseDto;
import com.natter.repository.NatterByAuthorRepository;
import com.natter.repository.NatterOriginalRepository;
import com.natter.repository.NatterByIdRepository;
import com.natter.service.natter.NatterService;
import com.natter.service.natter.NatterValidationService;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
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
  NatterOriginalRepository natterOriginalRepository;

  @Mock
  NatterByAuthorRepository natterByAuthorRepository;

  @Mock
  NatterByIdRepository natterByIdRepository;


  @Mock
  NatterValidationService natterValidationService;

  NatterServiceTestHelper natterServiceTestHelper = new NatterServiceTestHelper();


  @Test
  public void whenNatterIsNull_ReturnNoNatterToCreateMessageToUser() {
    NatterCreateRequest natter = null;
    NatterCreationResponseDto response = natterService.create(natter, "123");
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
    natterRequest.setBody("This is a natter!");

    NatterById createdNatterById = new NatterById();
    createdNatterById.setParentNatterId(null);
    createdNatterById.setAuthorId("testUserId");
    createdNatterById.setBody("This is a natter!");
    createdNatterById.setId("12323");
    createdNatterById.setDateCreated(LocalDateTime.now());
    createdNatterById.setDateUpdated(createdNatterById.getDateCreated());

    when(natterValidationService.validateNatterCreateBody(any())).thenReturn(new HashMap<>());
    when(natterByIdRepository.save(any())).thenReturn(createdNatterById);

    NatterCreationResponseDto response = natterService.create(natterRequest, "123");

    assertAll(
        () -> assertNotNull(response),
        () -> assertEquals(1, response.getUserMessages().size()),
        () -> assertEquals("12323", response.getNatterById().getId()),
        () -> assertEquals(SuccessMessageEnum.CREATED_NEW_NATTER.getMessage(),
            response.getUserMessages().get(SuccessMessageEnum.CREATED_NEW_NATTER.getCode())),
        () -> assertEquals(natterRequest.getBody(), response.getNatterById().getBody()),
        () -> assertEquals(natterRequest.getParentNatterId(),
            response.getNatterById().getParentNatterId()),
        () -> assertNotNull(response.getNatterById().getDateCreated()),
        () -> assertNotNull(response.getNatterById().getDateUpdated()),
        () -> assertNotNull(response.getNatterById().getAuthorId()),
        () -> assertTrue(response.getErrorMessages().isEmpty()));

    verify(natterByAuthorRepository, times(1)).save(any());
    verify(natterByIdRepository, times(1)).save(any());
  }

  @Test
  public void whenMissingKeyFieldsArePassedToCreate_returnMissingFieldsErrorMessage() {
    NatterCreateRequest natterRequest = new NatterCreateRequest();
    natterRequest.setBody(null);

    Map<String, String> errors = new HashMap<>();
    errors.put("body", ErrorMessageEnum.NULL_OR_EMPTY_FIELD.getMessage());
    errors.put("authorId", ErrorMessageEnum.NULL_OR_EMPTY_FIELD.getMessage());

    when(natterValidationService.validateNatterCreateBody(any())).thenReturn(errors);
    NatterCreationResponseDto natterCreationResponseDto = natterService.create(natterRequest, "123");
    assertAll(
        () -> assertNotNull(natterCreationResponseDto),
        () -> assertEquals(2, natterCreationResponseDto.getErrorMessages().size())
    );

  }

  @Test
  public void whenNullBodyPassedToCreate_returnNullBodyError(){
    NatterCreationResponseDto natterCreationResponseDto = natterService.create(null, "123");
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
    when(natterOriginalRepository.findByAuthorIdAndNatterId(any(), any())).thenReturn(optional);
    BaseResponseDto responseDto = natterService.delete("123", "123");
    assertAll(
        () -> assertNotNull(responseDto),
        () -> assertNotNull(responseDto.getUserMessages()),
        () -> assertEquals(1, responseDto.getUserMessages().size()),
        () -> assertEquals(SuccessMessageEnum.DELETED_NATTER.getMessage(), responseDto.getUserMessages().get(SuccessMessageEnum.DELETED_NATTER.getCode()))
    );
    verify(natterByIdRepository, times(1)).deleteById(any());
    verify(natterByAuthorRepository, times(1)).deleteById(any());
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
    NatterById natter = new NatterById();
    natter.setAuthorId("4545");
    Optional<NatterById> optional = Optional.of(natter);
    when(natterByIdRepository.findById(any())).thenReturn(optional);
    BaseResponseDto responseDto = natterService.delete("123", "123");
    assertAll(
        () -> assertNotNull(responseDto),
        () -> assertNotNull(responseDto.getErrorMessages()),
        () -> assertEquals(1, responseDto.getErrorMessages().size()),
        () -> assertEquals(ErrorMessageEnum.UNAUTHORISED_ACCESS_NATTER.getMessage(), responseDto.getErrorMessages().get(ErrorMessageEnum.UNAUTHORISED_ACCESS_NATTER.getErrorCode()))
    );
  }

  @Test
  public void whenListUserNatters_returnNatters(){
    List<NatterOriginal> nattersToReturn = natterServiceTestHelper.getListOfNatters();
    when(natterOriginalRepository.getNattersByAuthorId(any())).thenReturn(nattersToReturn);
    NatterListResponseDto responseDto = natterService.getNattersForUser("115826771724477311086");
    assertAll(
        () -> assertNotNull(responseDto),
        () -> assertEquals(nattersToReturn.size(), responseDto.getNatterOriginalList().size())
    );
  }

}