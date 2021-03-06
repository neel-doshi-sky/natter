package com.natter.service.natter;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.natter.dao.NatterDao;
import com.natter.dto.CreateResponseDto;
import com.natter.dto.GetResponseDto;
import com.natter.dto.NatterDto;
import com.natter.dto.ResponseDto;
import com.natter.dto.ResponseListDto;
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
import com.natter.service.user.UserService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

@ExtendWith(MockitoExtension.class)
class NatterServiceTest {

  @InjectMocks
  NatterService natterService;

  @Mock
  NatterDao natterDao;

  @Mock
  NatterByAuthorRepository natterByAuthorRepository;

  @Mock
  NatterByIdRepository natterByIdRepository;

  @Mock
  NatterValidationService natterValidationService;

  @Mock
  UserService userService;

  NatterServiceTestHelper natterServiceTestHelper = new NatterServiceTestHelper();

  OAuth2User oAuth2User = new DefaultOAuth2User(new ArrayList<>(),
      Map.of("sub", "115826771724477311086", "name", "Neel Doshi"), "name");


  @Test
  public void whenNatterIsNull_ReturnNoNatterToCreateMessageToUser() {
    CreateResponseDto<NatterById> response = natterService.create(null, oAuth2User);
    assertAll(
        () -> assertNotNull(response),
        () -> assertEquals(1, response.getErrorMessages().size()),
        () -> assertEquals(ErrorMessageNatterEnum.NULL_BODY.getMessage(),
            response.getErrorMessages()
                .get(ErrorMessageNatterEnum.NULL_BODY.getCode())),
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
    when(natterDao.create(any(), any(), any())).thenReturn(createdNatterById);

    CreateResponseDto<NatterById> response = natterService.create(natterRequest, oAuth2User);

    assertAll(
        () -> assertNotNull(response),
        () -> assertEquals(1, response.getUserMessages().size()),
        () -> assertEquals("12323", response.getCreated().getId()),
        () -> assertEquals(SuccessMessageNatterEnum.CREATED_NEW_NATTER.getMessage(),
            response.getUserMessages().get(SuccessMessageNatterEnum.CREATED_NEW_NATTER.getCode())),
        () -> assertEquals(natterRequest.getBody(), response.getCreated().getBody()),
        () -> assertEquals(natterRequest.getParentNatterId(),
            response.getCreated().getParentNatterId()),
        () -> assertNotNull(response.getCreated().getDateCreated()),
        () -> assertNotNull(response.getCreated().getDateUpdated()),
        () -> assertNotNull(response.getCreated().getAuthorId()),
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
    CreateResponseDto<NatterById>
        natterCreateResponseDto = natterService.create(natterRequest, oAuth2User);
    assertAll(
        () -> assertNotNull(natterCreateResponseDto),
        () -> assertEquals(2, natterCreateResponseDto.getErrorMessages().size())
    );

  }

  @Test
  public void whenNullBodyPassedToCreate_returnNullBodyError() {
    CreateResponseDto<NatterById> natterCreateResponseDto = natterService.create(null, oAuth2User);
    assertAll(
        () -> assertNotNull(natterCreateResponseDto),
        () -> assertNotNull(natterCreateResponseDto.getErrorMessages()),
        () -> assertEquals(1, natterCreateResponseDto.getErrorMessages().size()),
        () -> assertEquals(ErrorMessageNatterEnum.NULL_BODY.getMessage(),
            natterCreateResponseDto.getErrorMessages().get(
                ErrorMessageNatterEnum.NULL_BODY.getCode()))
    );

  }

  @Test
  public void whenDatabaseErrorInCreate_returnDatabaseError() throws DatabaseErrorException {
    when(natterDao.create(any(), any(), any())).thenThrow(
        new DatabaseErrorException(ErrorMessageNatterEnum.DATABASE_ERROR.getMessage()));
    CreateResponseDto<NatterById> natterCreateResponseDto =
        natterService.create(new NatterCreateRequest(), oAuth2User);
    assertAll(
        () -> assertNotNull(natterCreateResponseDto),
        () -> assertNotNull(natterCreateResponseDto.getErrorMessages()),
        () -> assertEquals(1, natterCreateResponseDto.getErrorMessages().size()),
        () -> assertEquals(ErrorMessageNatterEnum.DATABASE_ERROR.getMessage(),
            natterCreateResponseDto.getErrorMessages().get(
                ErrorMessageNatterEnum.DATABASE_ERROR.getCode()))
    );

  }

  @Test
  public void whenDeleteValidId_DeleteNatter_ReturnSuccessMessage() throws DatabaseErrorException {
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
    verify(natterDao, times(1)).delete(natter);

  }

  @Test
  public void whenDeleteIdIsNull_returnErrorMessage() {
    ResponseDto responseDto = natterService.delete(null, "123");
    assertAll(
        () -> assertNotNull(responseDto),
        () -> assertNotNull(responseDto.getErrorMessages()),
        () -> assertEquals(1, responseDto.getErrorMessages().size()),
        () -> assertEquals(
            ErrorMessageNatterEnum.NULL_ID.getMessage(), responseDto.getErrorMessages().get(
                ErrorMessageNatterEnum.NULL_ID.getCode()))
    );

  }

  @Test
  public void whenDeleteIdDoesNotBelongToAuthor_returnErrorMessage() {
    NatterById natter = new NatterById();
    natter.setAuthorId("4545");
    Optional<NatterById> optional = Optional.of(natter);
    when(natterByIdRepository.findById(any())).thenReturn(optional);
    ResponseDto responseDto = natterService.delete("123", "123");
    assertAll(
        () -> assertNotNull(responseDto),
        () -> assertNotNull(responseDto.getErrorMessages()),
        () -> assertEquals(1, responseDto.getErrorMessages().size()),
        () -> assertEquals(ErrorMessageNatterEnum.UNAUTHORISED_ACCESS.getMessage(),
            responseDto.getErrorMessages().get(
                ErrorMessageNatterEnum.UNAUTHORISED_ACCESS.getCode()))
    );
  }

  @Test
  public void whenDeleteIdDoesNotExist_returnNotFoundMessage() {
    Optional<NatterById> optional = Optional.empty();
    when(natterByIdRepository.findById(any())).thenReturn(optional);
    ResponseDto responseDto = natterService.delete("123", "123");
    assertAll(
        () -> assertNotNull(responseDto),
        () -> assertNotNull(responseDto.getErrorMessages()),
        () -> assertEquals(1, responseDto.getErrorMessages().size()),
        () -> assertEquals(ErrorMessageNatterEnum.RECORD_NOT_FOUND.getMessage(),
            responseDto.getErrorMessages().get(
                ErrorMessageNatterEnum.RECORD_NOT_FOUND.getCode()))
    );
  }

  @Test
  public void whenDelete_databaseErrorOccurs_returnDatabaseError() throws DatabaseErrorException {
    Optional<NatterById> optional = Optional.of(new NatterById("123", null, "123"));
    when(natterByIdRepository.findById(any())).thenReturn(optional);
    when(natterDao.delete(any())).thenThrow(
        new DatabaseErrorException(ErrorMessageNatterEnum.DATABASE_ERROR.getMessage()));
    ResponseDto responseDto = natterService.delete("123", "123");
    assertAll(
        () -> assertNotNull(responseDto),
        () -> assertNotNull(responseDto.getErrorMessages()),
        () -> assertEquals(1, responseDto.getErrorMessages().size()),
        () -> assertEquals(ErrorMessageNatterEnum.DATABASE_ERROR.getMessage(),
            responseDto.getErrorMessages().get(
                ErrorMessageNatterEnum.DATABASE_ERROR.getCode()))
    );
  }


  @Test
  public void whenListUserNatters_returnNatters() {
    List<NatterByAuthor> nattersToReturn = natterServiceTestHelper.getListOfNatters();
    when(natterByAuthorRepository.findAllByAuthorId(any())).thenReturn(nattersToReturn);
    ResponseListDto<NatterByAuthor> responseDto =
        natterService.getNattersForUser("115826771724477311086");
    assertAll(
        () -> assertNotNull(responseDto),
        () -> assertEquals(nattersToReturn.size(), responseDto.getList().size()),
        () -> assertEquals(1, responseDto.getUserMessages().size()),
        () -> assertEquals(SuccessMessageNatterEnum.FETCHED_NATTERS_BY_AUTHOR.getMessage(),
            responseDto.getUserMessages().get(
                SuccessMessageNatterEnum.FETCHED_NATTERS_BY_AUTHOR.getCode()))
    );
  }

  @Test
  public void whenListAllNatters_returnNatters() {
    List<NatterByAuthor> nattersToReturn = natterServiceTestHelper.getListOfNatters();
    when(natterByAuthorRepository.findAll()).thenReturn(nattersToReturn);
    ResponseListDto<NatterByAuthor> responseDto = natterService.getAllNatters();
    assertAll(
        () -> assertNotNull(responseDto),
        () -> assertEquals(nattersToReturn.size(), responseDto.getList().size()),
        () -> assertEquals(1, responseDto.getUserMessages().size()),
        () -> assertEquals(SuccessMessageNatterEnum.FETCHED_All_NATTERS.getMessage(),
            responseDto.getUserMessages().get(
                SuccessMessageNatterEnum.FETCHED_All_NATTERS.getCode()))
    );
  }

  @Test
  public void whenNatterUpdateBodyIsValid_AndUserHasAccess_UpdateNatter_ReturnUpdatedNatter() {
    NatterUpdateRequest natterUpdateRequest = new NatterUpdateRequest("123", "UPDATE");
    NatterByAuthorPrimaryKey natterByAuthorPrimaryKey = new NatterByAuthorPrimaryKey();
    natterByAuthorPrimaryKey.setAuthorId("123");
    natterByAuthorPrimaryKey.setId("123");
    NatterByAuthor natterByAuthor = new NatterByAuthor();
    natterByAuthor.setBody("ORIGINAL");
    natterByAuthor.setId(natterByAuthorPrimaryKey);
    when(natterByAuthorRepository.findById(natterByAuthorPrimaryKey)).thenReturn(
        Optional.of(natterByAuthor));
    when(natterValidationService.validateNatterUpdateBody(any())).thenReturn(new HashMap<>());
    ResponseDto result = natterService.edit(natterUpdateRequest, "123");
    assertAll(
        () -> assertNotNull(result),
        () -> assertEquals(1, result.getUserMessages().size()),
        () -> assertTrue(result.getErrorMessages().isEmpty()),
        () -> assertEquals(
            SuccessMessageNatterEnum.UPDATED_NATTER.getMessage(), result.getUserMessages().get(
                SuccessMessageNatterEnum.UPDATED_NATTER.getCode()))
    );
  }

  @Test
  public void whenNatterUpdateBodyIsInValid_ReturnValidationError() {
    NatterUpdateRequest natterUpdateRequest = new NatterUpdateRequest(null, "UPDATE");
    when(natterValidationService.validateNatterUpdateBody(any())).thenReturn(
        Map.of("id", ErrorMessageNatterEnum.NULL_OR_EMPTY_FIELD.toString()));
    ResponseDto result = natterService.edit(natterUpdateRequest, "123");
    assertAll(
        () -> assertNotNull(result),
        () -> assertEquals(1, result.getErrorMessages().size()),
        () -> assertTrue(result.getUserMessages().isEmpty())
    );
  }

  @Test
  public void whenNatterUpdateBodyIsNull_ReturnValidationError() {
    NatterUpdateRequest natterUpdateRequest = new NatterUpdateRequest("123", null);
    when(natterValidationService.validateNatterUpdateBody(natterUpdateRequest)).thenReturn(
        Map.of(ErrorMessageNatterEnum.NULL_BODY.getCode(),
            ErrorMessageNatterEnum.NULL_BODY.getMessage()));
    ResponseDto result = natterService.edit(natterUpdateRequest, "123");
    assertAll(
        () -> assertNotNull(result),
        () -> assertEquals(1, result.getErrorMessages().size()),
        () -> assertEquals(ErrorMessageNatterEnum.NULL_BODY.getMessage(),
            result.getErrorMessages().get(ErrorMessageNatterEnum.NULL_BODY.getCode())),
        () -> assertTrue(result.getUserMessages().isEmpty())
    );
  }

  @Test
  public void whenNatterUpdateRequestIsNull_ReturnNullBodyError() {
    ResponseDto result = natterService.edit(null, "123");
    assertAll(
        () -> assertNotNull(result),
        () -> assertEquals(1, result.getErrorMessages().size()),
        () -> assertEquals(ErrorMessageNatterEnum.NULL_BODY.getMessage(),
            result.getErrorMessages().get(ErrorMessageNatterEnum.NULL_BODY.getCode())),
        () -> assertTrue(result.getUserMessages().isEmpty())
    );
  }

  @Test
  public void whenNatterUpdateBodyIsValid_AndUserHasNotGotAccess_ReturnAuthError() {
    NatterUpdateRequest natterUpdateRequest = new NatterUpdateRequest("123", "UPDATE");
    NatterByAuthorPrimaryKey natterByAuthorPrimaryKey = new NatterByAuthorPrimaryKey();
    natterByAuthorPrimaryKey.setAuthorId("123");
    natterByAuthorPrimaryKey.setId("123");
    when(natterByAuthorRepository.findById(any())).thenReturn(Optional.empty());
    ResponseDto result = natterService.edit(natterUpdateRequest, "132");
    assertAll(
        () -> assertNotNull(result),
        () -> assertEquals(1, result.getErrorMessages().size()),
        () -> assertTrue(result.getUserMessages().isEmpty()),
        () -> assertEquals(ErrorMessageNatterEnum.UNAUTHORISED_ACCESS.getMessage(),
            result.getErrorMessages().get(
                ErrorMessageNatterEnum.UNAUTHORISED_ACCESS.getCode()))
    );
  }

  @Test
  public void whenCommentAdded_parentNatterIdIsNull_returnError() {
    NatterCreateRequest natterCreateRequest = new NatterCreateRequest("Nice!", null);
    CreateResponseDto<NatterById> responseDto =
        natterService.addComment(natterCreateRequest, oAuth2User);
    assertAll(
        () -> assertNotNull(responseDto),
        () -> assertEquals(1, responseDto.getErrorMessages().size()),
        () -> assertNull(responseDto.getCreated())
    );

  }

  @Test
  public void whenCommentAdded_bodyIsNull_returnError() {
    CreateResponseDto<NatterById> responseDto = natterService.addComment(null, oAuth2User);
    assertAll(
        () -> assertNotNull(responseDto),
        () -> assertEquals(1, responseDto.getErrorMessages().size()),
        () -> assertEquals(ErrorMessageNatterEnum.NULL_BODY.getMessage(),
            responseDto.getErrorMessages().get(ErrorMessageNatterEnum.NULL_BODY.getCode())),
        () -> assertNull(responseDto.getCreated())
    );

  }

  @Test
  public void whenCommentAdded_parentNatterIdIsInValid_returnError() {
    NatterCreateRequest natterCreateRequest = new NatterCreateRequest("Nice!", "23");
    when(natterByIdRepository.findById(natterCreateRequest.getParentNatterId())).thenReturn(
        Optional.empty());
    CreateResponseDto<NatterById> responseDto =
        natterService.addComment(natterCreateRequest, oAuth2User);
    assertAll(
        () -> assertNotNull(responseDto),
        () -> assertEquals(1, responseDto.getErrorMessages().size()),
        () -> assertNull(responseDto.getCreated())
    );

  }

  @Test
  public void whenCommentAdded_parentNatterIdIsValid_returnSuccess() throws DatabaseErrorException {
    NatterCreateRequest natterCreateRequest = new NatterCreateRequest("Nice!", "23");
    when(natterByIdRepository.findById(natterCreateRequest.getParentNatterId())).thenReturn(
        Optional.of(new NatterById("23")));
    when(natterDao.addComment(natterCreateRequest, oAuth2User)).thenReturn(
        new NatterById());
    CreateResponseDto<NatterById> responseDto =
        natterService.addComment(natterCreateRequest, oAuth2User);
    assertAll(
        () -> assertNotNull(responseDto),
        () -> assertEquals(1, responseDto.getUserMessages().size()),
        () -> assertNotNull(responseDto.getCreated())
    );

  }

  @Test
  public void whenCommentAdded_databaseErrorOccurs_returnDatabaseError()
      throws DatabaseErrorException {
    NatterCreateRequest natterCreateRequest = new NatterCreateRequest("Nice!", "23");
    when(natterByIdRepository.findById(natterCreateRequest.getParentNatterId())).thenReturn(
        Optional.of(new NatterById("23")));
    when(natterDao.addComment(any(), any())).thenThrow(
        new DatabaseErrorException(ErrorMessageNatterEnum.DATABASE_ERROR.getMessage()));
    CreateResponseDto<NatterById> responseDto =
        natterService.addComment(natterCreateRequest, oAuth2User);
    assertAll(
        () -> assertNotNull(responseDto),
        () -> assertEquals(1, responseDto.getErrorMessages().size()),
        () -> assertEquals(ErrorMessageNatterEnum.DATABASE_ERROR.getMessage(),
            responseDto.getErrorMessages().get(ErrorMessageNatterEnum.DATABASE_ERROR.getCode())),
        () -> assertNull(responseDto.getCreated())
    );

  }

  @Test
  public void whenGetNatterById_natterIdIsNull_returnError() {
    GetResponseDto<NatterDto> responseDto = natterService.getNatterById(null, "123");
    assertAll(
        () -> assertNotNull(responseDto),
        () -> assertNotNull(responseDto.getErrorMessages()),
        () -> assertEquals(1, responseDto.getErrorMessages().size()),
        () -> assertEquals(
            ErrorMessageNatterEnum.NULL_ID.getMessage(), responseDto.getErrorMessages().get(
                ErrorMessageNatterEnum.NULL_ID.getCode()))
    );
  }

  @Test
  public void whenGetNatterById_natterIdIsInvalid_returnError() {
    when(natterByIdRepository.findById(any())).thenReturn(Optional.empty());
    GetResponseDto<NatterDto> responseDto = natterService.getNatterById("1", "123");
    assertAll(
        () -> assertNotNull(responseDto),
        () -> assertNotNull(responseDto.getErrorMessages()),
        () -> assertEquals(1, responseDto.getErrorMessages().size()),
        () -> assertEquals(
            ErrorMessageNatterEnum.UNAUTHORISED_ACCESS.getMessage(),
            responseDto.getErrorMessages().get(
                ErrorMessageNatterEnum.UNAUTHORISED_ACCESS.getCode()))
    );
  }

  @Test
  public void whenGetNatterById_natterIdIsValid_returnNatterByIdWithNoComments() {
    NatterById natter = natterServiceTestHelper.getValidNatterByIdWithoutComments();
    when(natterByIdRepository.findById(any())).thenReturn(Optional.of(natter));
    GetResponseDto<NatterDto> responseDto = natterService.getNatterById("12323", "123");
    assertAll(
        () -> assertNotNull(responseDto),
        () -> assertNotNull(responseDto.getUserMessages()),
        () -> assertNotNull(responseDto.getResponseObject()),
        () -> assertEquals("12323", responseDto.getResponseObject().getId()),
        () -> assertEquals(1, responseDto.getUserMessages().size()),
        () -> assertEquals(
            SuccessMessageNatterEnum.FETCHED_NATTER_BY_ID.getMessage(),
            responseDto.getUserMessages().get(
                SuccessMessageNatterEnum.FETCHED_NATTER_BY_ID.getCode()))
    );
  }

  @Test
  public void whenGetNatterById_natterIdIsValid_returnNatterByIdWithComments() {
    NatterById natter = natterServiceTestHelper.getValidNatterByIdWithComments();
    when(natterByIdRepository.findById(any())).thenReturn(Optional.of(natter));
    when(natterByIdRepository.findAllById(any())).thenReturn(
        natterServiceTestHelper.getCommentsForNatter(natter.getId()));
    GetResponseDto<NatterDto> responseDto = natterService.getNatterById("12323", "123");
    assertAll(
        () -> assertNotNull(responseDto),
        () -> assertNotNull(responseDto.getResponseObject()),
        () -> assertEquals("12323", responseDto.getResponseObject().getId()),
        () -> assertFalse(responseDto.getResponseObject().getComments().isEmpty()),
        () -> assertNotNull(responseDto.getUserMessages()),
        () -> assertEquals(1, responseDto.getUserMessages().size()),
        () -> assertEquals(
            SuccessMessageNatterEnum.FETCHED_NATTER_BY_ID.getMessage(),
            responseDto.getUserMessages().get(
                SuccessMessageNatterEnum.FETCHED_NATTER_BY_ID.getCode()))
    );
  }

  @Test
  public void whenLikeNatter_natterIdIsnull_returnError() throws DatabaseErrorException {
    ResponseDto responseDto = natterService.likeNatter("123", null);
    assertAll(
        () -> assertNotNull(responseDto),
        () -> assertNotNull(responseDto.getErrorMessages()),
        () -> assertEquals(1, responseDto.getErrorMessages().size()),
        () -> assertEquals(
            ErrorMessageNatterEnum.NULL_ID.getMessage(), responseDto.getErrorMessages().get(
                ErrorMessageNatterEnum.NULL_ID.getCode()))
    );
  }

  @Test
  public void whenLikeNatter_natterIdIsInvalid_returnError() throws DatabaseErrorException {
    when(natterByIdRepository.findById(any())).thenReturn(Optional.empty());
    ResponseDto responseDto = natterService.likeNatter("123", "1");
    assertAll(
        () -> assertNotNull(responseDto),
        () -> assertNotNull(responseDto.getErrorMessages()),
        () -> assertEquals(1, responseDto.getErrorMessages().size()),
        () -> assertEquals(
            ErrorMessageNatterEnum.UNAUTHORISED_ACCESS.getMessage(),
            responseDto.getErrorMessages().get(
                ErrorMessageNatterEnum.UNAUTHORISED_ACCESS.getCode()))
    );
  }

  @Test
  public void whenLikeNatter_natterIdIsValid_returnSuccess() throws DatabaseErrorException {
    NatterById natter = natterServiceTestHelper.getValidNatterByIdWithComments();
    when(natterByIdRepository.findById(any())).thenReturn(Optional.of(natter));
    when(natterByAuthorRepository.findById((any()))).thenReturn(Optional.of(new NatterByAuthor()));
    ResponseDto responseDto = natterService.likeNatter("123", "1");
    assertAll(
        () -> assertNotNull(responseDto),
        () -> assertNotNull(responseDto.getUserMessages()),
        () -> assertEquals(1, responseDto.getUserMessages().size()),
        () -> assertEquals(
            SuccessMessageNatterEnum.LIKE_SUCCESS.getMessage(), responseDto.getUserMessages().get(
                SuccessMessageNatterEnum.LIKE_SUCCESS.getCode()))
    );
    verify(natterByIdRepository).save(any());
    verify(natterByAuthorRepository).save(any());
  }

  @Test
  public void whenLikeNatter_natterIdIsValid_alreadyLiked_unlikeNatter_returnSuccess()
      throws DatabaseErrorException {
    NatterById natter =
        natterServiceTestHelper.getValidNatterByIdWithCommentsAndLikedByAuthId("123");
    when(natterByIdRepository.findById(any())).thenReturn(Optional.of(natter));
    when(natterByAuthorRepository.findById((any()))).thenReturn(Optional.of(new NatterByAuthor()));
    ResponseDto responseDto = natterService.likeNatter("123", "1");
    assertAll(
        () -> assertNotNull(responseDto),
        () -> assertNotNull(responseDto.getUserMessages()),
        () -> assertEquals(1, responseDto.getUserMessages().size()),
        () -> assertEquals(
            SuccessMessageNatterEnum.UNLIKE_SUCCESS.getMessage(), responseDto.getUserMessages().get(
                SuccessMessageNatterEnum.UNLIKE_SUCCESS.getCode()))
    );
    verify(natterByIdRepository).save(any());
    verify(natterByAuthorRepository).save(any());
  }

  @Test
  public void whenLikeNatter_natterIdIsValid_databaseErrorOccurs_throwDatabaseErrorException() {
    NatterById natter =
        natterServiceTestHelper.getValidNatterByIdWithCommentsAndLikedByAuthId("123");
    when(natterByIdRepository.findById(any())).thenReturn(Optional.of(natter));
    when(natterByAuthorRepository.findById((any()))).thenReturn(Optional.of(new NatterByAuthor()));
    when(natterByAuthorRepository.save(any())).thenThrow(new IllegalArgumentException());
    assertThrows(DatabaseErrorException.class, () -> natterService.likeNatter("123", "1"));
    verify(natterByIdRepository).save(any());
    verify(natterByAuthorRepository).save(any());
  }

  @Test
  public void whenListNattersForFeed_returnNatters() throws DatabaseErrorException {
    List<NatterByAuthor> nattersToReturn = natterServiceTestHelper.getListOfNatters();
    List<String> userIds = new ArrayList<>();
    userIds.add("1");
    userIds.add("2");
    userIds.add("3");
    when(userService.getFollowingIdsForUser(any())).thenReturn(userIds);
    when(natterDao.getAllNattersForFollowing(any())).thenReturn(nattersToReturn);
    ResponseListDto<NatterByAuthor> responseDto = natterService.getNatterFeed("123");
    assertAll(
        () -> assertNotNull(responseDto),
        () -> assertEquals(nattersToReturn.size(), responseDto.getList().size()),
        () -> assertEquals(1, responseDto.getUserMessages().size()),
        () -> assertEquals(SuccessMessageNatterEnum.FETCHED_All_NATTERS.getMessage(),
            responseDto.getUserMessages().get(
                SuccessMessageNatterEnum.FETCHED_All_NATTERS.getCode()))
    );
  }

  @Test
  public void whenListNattersForFeed_databaseErrorOccurs_returnDatabaseError()
      throws DatabaseErrorException {
    List<String> userIds = new ArrayList<>();
    userIds.add("1");
    userIds.add("2");
    userIds.add("3");
    when(userService.getFollowingIdsForUser(any())).thenReturn(userIds);
    when(natterDao.getAllNattersForFollowing(any())).thenThrow(
        new DatabaseErrorException(ErrorMessageNatterEnum.DATABASE_ERROR.getMessage()));
    ResponseListDto<NatterByAuthor> responseDto = natterService.getNatterFeed("123");
    assertAll(
        () -> assertNotNull(responseDto),
        () -> assertEquals(1, responseDto.getErrorMessages().size()),
        () -> assertEquals(ErrorMessageNatterEnum.DATABASE_ERROR.getMessage(),
            responseDto.getErrorMessages().get(
                ErrorMessageNatterEnum.DATABASE_ERROR.getCode()))
    );
  }

}