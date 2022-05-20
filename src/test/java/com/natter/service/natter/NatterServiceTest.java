package com.natter.service.natter;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.natter.dto.ResponseDto;
import com.natter.dto.NatterCreateResponseDto;
import com.natter.dto.NatterListResponseDto;
import com.natter.enums.natter.ErrorMessageNatterEnum;
import com.natter.enums.natter.SuccessMessageNatterEnum;
import com.natter.exception.DatabaseErrorException;
import com.natter.model.natter.NatterByAuthor;
import com.natter.model.natter.NatterByAuthorPrimaryKey;
import com.natter.model.natter.NatterById;
import com.natter.model.natter.NatterCreateRequest;
import com.natter.model.natter.NatterUpdateRequest;
import com.natter.repository.natter.NatterByAuthorRepository;
import com.natter.repository.natter.NatterByIdRepository;
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
  NatterDatabaseService natterDatabaseService;

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
    NatterCreateResponseDto response = natterService.create(natter, "123");
    assertAll(
        () -> assertNotNull(response),
        () -> assertEquals(1, response.getErrorMessages().size()),
        () -> assertEquals(ErrorMessageNatterEnum.NATTER_CREATION_ERROR_NULL_BODY.getMessage(),
            response.getErrorMessages()
                .get(ErrorMessageNatterEnum.NATTER_CREATION_ERROR_NULL_BODY.getCode())),
        () -> assertTrue(response.getUserMessages().isEmpty()));
  }

  @Test
  public void whenValidNatterIsSent_SaveNatterToDatabaseAndReturnSuccessMessage()
      throws DatabaseErrorException {
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
    when(natterDatabaseService.create(any(), any(), any())).thenReturn(createdNatterById);

    NatterCreateResponseDto response = natterService.create(natterRequest, "123");

    assertAll(
        () -> assertNotNull(response),
        () -> assertEquals(1, response.getUserMessages().size()),
        () -> assertEquals("12323", response.getNatterById().getId()),
        () -> assertEquals(SuccessMessageNatterEnum.CREATED_NEW_NATTER.getMessage(),
            response.getUserMessages().get(SuccessMessageNatterEnum.CREATED_NEW_NATTER.getCode())),
        () -> assertEquals(natterRequest.getBody(), response.getNatterById().getBody()),
        () -> assertEquals(natterRequest.getParentNatterId(),
            response.getNatterById().getParentNatterId()),
        () -> assertNotNull(response.getNatterById().getDateCreated()),
        () -> assertNotNull(response.getNatterById().getDateUpdated()),
        () -> assertNotNull(response.getNatterById().getAuthorId()),
        () -> assertTrue(response.getErrorMessages().isEmpty()));


  }

  @Test
  public void whenMissingKeyFieldsArePassedToCreate_returnMissingFieldsErrorMessage() {
    NatterCreateRequest natterRequest = new NatterCreateRequest();
    natterRequest.setBody(null);

    Map<String, String> errors = new HashMap<>();
    errors.put("body", ErrorMessageNatterEnum.NULL_OR_EMPTY_FIELD.getMessage());
    errors.put("authorId", ErrorMessageNatterEnum.NULL_OR_EMPTY_FIELD.getMessage());

    when(natterValidationService.validateNatterCreateBody(any())).thenReturn(errors);
    NatterCreateResponseDto
        natterCreateResponseDto = natterService.create(natterRequest, "123");
    assertAll(
        () -> assertNotNull(natterCreateResponseDto),
        () -> assertEquals(2, natterCreateResponseDto.getErrorMessages().size())
    );

  }

  @Test
  public void whenNullBodyPassedToCreate_returnNullBodyError(){
    NatterCreateResponseDto natterCreateResponseDto = natterService.create(null, "123");
    assertAll(
        () -> assertNotNull(natterCreateResponseDto),
        () -> assertNotNull(natterCreateResponseDto.getErrorMessages()),
        () -> assertEquals(1, natterCreateResponseDto.getErrorMessages().size()),
        () -> assertEquals(ErrorMessageNatterEnum.NATTER_CREATION_ERROR_NULL_BODY.getMessage(), natterCreateResponseDto.getErrorMessages().get(
            ErrorMessageNatterEnum.NATTER_CREATION_ERROR_NULL_BODY.getCode()))
    );

  }

  @Test
  public void whenDeleteValidId_DeleteNatter_ReturnSuccessMessage(){
    NatterById natter = new NatterById();
    natter.setAuthorId("123");
    Optional<NatterById> optional = Optional.of(natter);
    when(natterByIdRepository.findById(any())).thenReturn(optional);
    ResponseDto responseDto = natterService.delete("123", "123");
    assertAll(
        () -> assertNotNull(responseDto),
        () -> assertNotNull(responseDto.getUserMessages()),
        () -> assertEquals(1, responseDto.getUserMessages().size()),
        () -> assertEquals(
            SuccessMessageNatterEnum.DELETED_NATTER.getMessage(), responseDto.getUserMessages().get(
                SuccessMessageNatterEnum.DELETED_NATTER.getCode()))
    );
    verify(natterDatabaseService, times(1)).delete("123", "123");

  }

  @Test
  public void whenDeleteIdIsNull_returnErrorMessage(){
    ResponseDto responseDto = natterService.delete(null, "123");
    assertAll(
        () -> assertNotNull(responseDto),
        () -> assertNotNull(responseDto.getErrorMessages()),
        () -> assertEquals(1, responseDto.getErrorMessages().size()),
        () -> assertEquals(
            ErrorMessageNatterEnum.NATTER_NULL_ID.getMessage(), responseDto.getErrorMessages().get(
                ErrorMessageNatterEnum.NATTER_NULL_ID.getCode()))
    );

  }

  @Test
  public void whenDeleteIdDoesNotBelongToAuthor_returnErrorMessage(){
    NatterById natter = new NatterById();
    natter.setAuthorId("4545");
    Optional<NatterById> optional = Optional.of(natter);
    when(natterByIdRepository.findById(any())).thenReturn(optional);
    ResponseDto responseDto = natterService.delete("123", "123");
    assertAll(
        () -> assertNotNull(responseDto),
        () -> assertNotNull(responseDto.getErrorMessages()),
        () -> assertEquals(1, responseDto.getErrorMessages().size()),
        () -> assertEquals(ErrorMessageNatterEnum.UNAUTHORISED_ACCESS_NATTER.getMessage(), responseDto.getErrorMessages().get(
            ErrorMessageNatterEnum.UNAUTHORISED_ACCESS_NATTER.getCode()))
    );
  }

  @Test
  public void whenListUserNatters_returnNatters(){
    List<NatterByAuthor> nattersToReturn = natterServiceTestHelper.getListOfNatters();
    when(natterByAuthorRepository.findAllByAuthorId(any())).thenReturn(nattersToReturn);
    NatterListResponseDto responseDto = natterService.getNattersForUser("115826771724477311086");
    assertAll(
        () -> assertNotNull(responseDto),
        () -> assertEquals(nattersToReturn.size(), responseDto.getNatterByAuthors().size()),
        () -> assertEquals(1, responseDto.getUserMessages().size()),
        () -> assertEquals(SuccessMessageNatterEnum.FETCHED_NATTERS_BY_AUTHOR.getMessage(), responseDto.getUserMessages().get(
            SuccessMessageNatterEnum.FETCHED_NATTERS_BY_AUTHOR.getCode()))
    );
  }

  @Test
  public void whenNatterUpdateBodyIsValid_AndUserHasAccess_UpdateNatter_ReturnUpdatedNatter(){
    NatterUpdateRequest natterUpdateRequest = new NatterUpdateRequest("123", "UPDATE");
    NatterByAuthorPrimaryKey natterByAuthorPrimaryKey = new NatterByAuthorPrimaryKey();
    natterByAuthorPrimaryKey.setAuthorId("123");
    natterByAuthorPrimaryKey.setId("123");
    NatterByAuthor natterByAuthor = new NatterByAuthor();
    natterByAuthor.setBody("ORIGINAL");
    natterByAuthor.setId(natterByAuthorPrimaryKey);
    when(natterByAuthorRepository.findById(natterByAuthorPrimaryKey)).thenReturn(Optional.of(natterByAuthor));
    when(natterValidationService.validateNatterUpdateBody(any())).thenReturn(new HashMap<>());
    ResponseDto result = natterService.edit(natterUpdateRequest, "123");
    assertAll(
        () -> assertNotNull(result),
        () -> assertEquals(1, result.getUserMessages().size()),
        () -> assertTrue( result.getErrorMessages().isEmpty()),
        () -> assertEquals(
            SuccessMessageNatterEnum.UPDATED_NATTER.getMessage(), result.getUserMessages().get(
                SuccessMessageNatterEnum.UPDATED_NATTER.getCode()))
    );
  }

  @Test
  public void whenNatterUpdateBodyIsInValid_ReturnValidationError(){
    NatterUpdateRequest natterUpdateRequest = new NatterUpdateRequest(null, "UPDATE");
    when(natterValidationService.validateNatterUpdateBody(any())).thenReturn(Map.of("id", ErrorMessageNatterEnum.NULL_OR_EMPTY_FIELD.toString()));
    ResponseDto result = natterService.edit(natterUpdateRequest, "123");
    assertAll(
        () -> assertNotNull(result),
        () -> assertEquals(1, result.getErrorMessages().size()),
        () -> assertTrue( result.getUserMessages().isEmpty())
    );
  }

  @Test
  public void whenNatterUpdateBodyIsValid_AndUserHasNotGotAccess_ReturnAuthError(){
    NatterUpdateRequest natterUpdateRequest = new NatterUpdateRequest("123", "UPDATE");
    NatterByAuthorPrimaryKey natterByAuthorPrimaryKey = new NatterByAuthorPrimaryKey();
    natterByAuthorPrimaryKey.setAuthorId("123");
    natterByAuthorPrimaryKey.setId("123");
    when(natterByAuthorRepository.findById(any())).thenReturn(Optional.empty());
    ResponseDto result = natterService.edit(natterUpdateRequest, "132");
    assertAll(
        () -> assertNotNull(result),
        () -> assertEquals(1, result.getErrorMessages().size()),
        () -> assertTrue( result.getUserMessages().isEmpty()),
        () -> assertEquals(ErrorMessageNatterEnum.UNAUTHORISED_ACCESS_NATTER.getMessage(), result.getErrorMessages().get(
            ErrorMessageNatterEnum.UNAUTHORISED_ACCESS_NATTER.getCode()))
    );
  }

}