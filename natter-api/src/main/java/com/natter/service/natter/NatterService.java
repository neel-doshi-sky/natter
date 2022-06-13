package com.natter.service.natter;

import com.datastax.oss.driver.api.core.uuid.Uuids;
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
import com.natter.model.user.UserFollowersFollowing;
import com.natter.repository.natter.NatterByAuthorRepository;
import com.natter.repository.natter.NatterByIdRepository;
import com.natter.service.user.UserService;
import com.natter.util.MessageUtil;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NatterService {

  private final NatterValidationService validationService;
  private final NatterByAuthorRepository natterByAuthorRepository;
  private final NatterByIdRepository natterByIdRepository;
  private final NatterDatabaseService natterDatabaseService;

  private final MessageUtil messageUtil = new MessageUtil();

  private final UserService userService;

  /**
   * Method to create a natter item and save it to the database
   *
   * @param natterCreateRequest the create natterCreateRequest body
   * @param author              the author id
   * @return the result of the operation with any errors
   */
  public CreateResponseDto<NatterById> create(NatterCreateRequest natterCreateRequest,
                                              OAuth2User author) {
    CreateResponseDto<NatterById> response = new CreateResponseDto<>();
    if (natterCreateRequest == null) {
      response.setStatus(HttpStatus.BAD_REQUEST);
      response.setErrorMessages(
          getErrorMessageForEnum(ErrorMessageNatterEnum.NATTER_CREATION_ERROR_NULL_BODY));
    } else {
      Map<String, String> validationResult =
          validationService.validateNatterCreateBody(natterCreateRequest);
      if (validationResult.isEmpty()) {
        UUID timeId = Uuids.timeBased();

        try {
          NatterById created =
              natterDatabaseService.create(timeId.toString(), natterCreateRequest, author);
          response.setCreated(created);
          response.setStatus(HttpStatus.OK);
          response.setUserMessages(
              getSuccessMessageForEnum(SuccessMessageNatterEnum.CREATED_NEW_NATTER));
        } catch (DatabaseErrorException e) {
          response.setErrorMessages(getErrorMessageForEnum(e.getErrorMessageNatterEnum()));
          log.error("Error saving natter to db");
        }
      } else {
        response.setErrorMessages(validationResult);
        response.setStatus(HttpStatus.BAD_REQUEST);

      }
    }
    return response;
  }


  /**
   * Method to delete a natter by id
   *
   * @param idToDelete the id to delete
   * @param authorId   the author id
   * @return the response
   */
  public ResponseDto delete(String idToDelete, @NonNull String authorId) {
    ResponseDto response = new ResponseDto();

    if (idToDelete == null) {
      response.setErrorMessages(getErrorMessageForEnum(ErrorMessageNatterEnum.NATTER_NULL_ID));
      response.setStatus(HttpStatus.BAD_REQUEST);

    } else {
      Optional<NatterById> foundById = natterByIdRepository.findById(idToDelete);
      if (foundById.isPresent()) {
        if (foundById.get().getAuthorId().equals(authorId)) {
          natterDatabaseService.delete(foundById.get());
          response.setStatus(HttpStatus.OK);
          response.setUserMessages(
              getSuccessMessageForEnum(SuccessMessageNatterEnum.DELETED_NATTER));
        } else {
          response.setErrorMessages(
              getErrorMessageForEnum(ErrorMessageNatterEnum.UNAUTHORISED_ACCESS_NATTER));
          response.setStatus(HttpStatus.FORBIDDEN);
        }
      } else {
        response.setErrorMessages(
            getErrorMessageForEnum(ErrorMessageNatterEnum.UNABLE_TO_DELETE_RECORD));
        response.setStatus(HttpStatus.FORBIDDEN);
      }
    }

    return response;
  }

  /**
   * Method to return natters for a particular user
   *
   * @param authorId the user to get natters for
   * @return the dto containing natters
   */
  public ResponseListDto<NatterByAuthor> getNattersForUser(String authorId) {
    ResponseListDto<NatterByAuthor> natterListResponseDto = new ResponseListDto<>();
    List<NatterByAuthor> natterByAuthor =
        natterByAuthorRepository.findAllByAuthorId(authorId);
    natterByAuthor = natterByAuthor.stream().filter(natter ->
            natter.getParentAuthorId() == null)
        .sorted(Comparator.comparing(NatterByAuthor::getDateUpdated).reversed())
        .collect(Collectors.toList());
    natterListResponseDto.setList(natterByAuthor);
    natterListResponseDto.setStatus(HttpStatus.OK);
    natterListResponseDto.setUserMessages(
        getSuccessMessageForEnum(SuccessMessageNatterEnum.FETCHED_NATTERS_BY_AUTHOR));
    return natterListResponseDto;
  }


  /**
   * Method to get all natters in the db
   *
   * @return ResponseListDto containing all the natters
   */
  public ResponseListDto<NatterByAuthor> getAllNatters() {
    //TODO - Paginate the response
    ResponseListDto<NatterByAuthor> natterListResponseDto = new ResponseListDto<>();
    List<NatterByAuthor> natterByAuthor =
        natterByAuthorRepository.findAll();
    natterByAuthor = natterByAuthor.stream().filter(natter ->
            natter.getParentAuthorId() == null)
        .sorted(Comparator.comparing(NatterByAuthor::getDateUpdated).reversed())
        .collect(Collectors.toList());
    natterListResponseDto.setList(natterByAuthor);
    natterListResponseDto.setStatus(HttpStatus.OK);
    natterListResponseDto.setUserMessages(
        getSuccessMessageForEnum(SuccessMessageNatterEnum.FETCHED_All_NATTERS));
    return natterListResponseDto;
  }

  /**
   * Method to edit natter body for the authenticated user
   *
   * @param updateRequest the update request
   * @param authorId      the authenticated user
   * @return the response dto containing result of operation
   */
  public ResponseDto edit(final NatterUpdateRequest updateRequest, final String authorId) {
    ResponseDto responseDto = new ResponseDto();
    Map<String, String> validationResult =
        validationService.validateNatterUpdateBody(updateRequest);
    if (validationResult.isEmpty()) {
      Optional<NatterByAuthor> natterByAuthorOptional = natterByAuthorRepository.findById(
          new NatterByAuthorPrimaryKey(authorId, updateRequest.getId()));
      if (natterByAuthorOptional.isPresent()) {
        natterDatabaseService.update(updateRequest, authorId);
        responseDto.setUserMessages(
            getSuccessMessageForEnum(SuccessMessageNatterEnum.UPDATED_NATTER));
        responseDto.setStatus(HttpStatus.OK);
      } else {
        responseDto.setErrorMessages(
            getErrorMessageForEnum(ErrorMessageNatterEnum.UNAUTHORISED_ACCESS_NATTER));
        responseDto.setStatus(HttpStatus.FORBIDDEN);
      }
    } else {
      responseDto.setErrorMessages(validationResult);
      responseDto.setStatus(HttpStatus.BAD_REQUEST);
    }
    return responseDto;
  }

  /**
   * Method to add a comment to an existing natter
   *
   * @param commentRequest the comment request body from the user
   * @param author         the author of the comment
   * @return the CreateResponseDto containing the response of the operation
   */
  public CreateResponseDto<NatterById> addComment(NatterCreateRequest commentRequest,
                                                  OAuth2User author) {
    CreateResponseDto<NatterById> responseDto = new CreateResponseDto<>();
    if (commentRequest.getParentNatterId() == null) {
      responseDto.setErrorMessages(getErrorMessageForEnum(ErrorMessageNatterEnum.NATTER_NULL_ID));
      responseDto.setStatus(HttpStatus.BAD_REQUEST);
    } else {
      Optional<NatterById> natterParentOptional =
          natterByIdRepository.findById(commentRequest.getParentNatterId());
      if (natterParentOptional.isPresent()) {
        try {
          NatterById parentNatter = natterParentOptional.get();
          NatterById comment = natterDatabaseService.addComment(commentRequest, author);
          parentNatter.getComments().add(comment.getId());
          natterDatabaseService.updateNatterAfterComment(parentNatter);
          responseDto.setStatus(HttpStatus.OK);
          responseDto.setUserMessages(
              getSuccessMessageForEnum(SuccessMessageNatterEnum.CREATED_COMMENT));
          responseDto.setCreated(comment);

        } catch (DatabaseErrorException e) {
          responseDto.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
          responseDto.setErrorMessages(
              getErrorMessageForEnum(ErrorMessageNatterEnum.UNABLE_TO_SAVE_RECORD));
        }

      } else {
        responseDto.setErrorMessages(
            getErrorMessageForEnum(ErrorMessageNatterEnum.UNAUTHORISED_ACCESS_NATTER));
        responseDto.setStatus(HttpStatus.BAD_REQUEST);
      }
    }
    return responseDto;
  }

  /**
   * Method to get a netter object by its id with all comments
   *
   * @param id     the id of the natter
   * @param authId the authenticated id of the user to evaluate edit permissions
   * @return the GetResponseDto object containing the natter or any errors
   */
  public GetResponseDto<NatterDto> getNatterById(final String id, @NonNull final String authId) {
    GetResponseDto<NatterDto> response = new GetResponseDto<>();
    List<NatterDto> commentsDto = new ArrayList<>();
    if (id != null) {
      try {
        NatterById natterById = natterByIdRepository.findById(id).orElseThrow();
        List<String> commentIds = natterById.getComments();
        if (!commentIds.isEmpty()) {
          commentsDto = getCommentsForNatterByCommentIds(commentIds, authId);
        }
        response.setResponseObject(
            new NatterDto(natterById.getId(), natterById.getBody(), natterById.getParentNatterId(),
                natterById.getDateCreated(), natterById.getDateUpdated(), natterById.getAuthorId(),
                natterById.getAuthorName(), commentsDto,
                Objects.equals(authId, natterById.getAuthorId()),
                natterById.getDateUpdated().isAfter(natterById.getDateCreated()), natterById.getLikes().size(), natterById.getLikes()));
        response.setStatus(HttpStatus.OK);
        response.setUserMessages(
            getSuccessMessageForEnum(SuccessMessageNatterEnum.FETCHED_NATTER_BY_ID));

      } catch (NoSuchElementException | InvalidDataAccessApiUsageException e) {
        response.setErrorMessages(
            getErrorMessageForEnum(ErrorMessageNatterEnum.UNAUTHORISED_ACCESS_NATTER));
        response.setStatus(HttpStatus.BAD_REQUEST);
      }
    } else {
      response.setErrorMessages(getErrorMessageForEnum(ErrorMessageNatterEnum.NATTER_NULL_ID));
      response.setStatus(HttpStatus.BAD_REQUEST);
    }

    return response;
  }

  /**
   * Method to get all comments for natter by the comment ids, this will sort and return them by date updated
   *
   * @param commentIds the comment ids from the natter object
   * @param authId     the authenticated user
   * @return list of NatterDto representing the comments
   */
  private List<NatterDto> getCommentsForNatterByCommentIds(@NonNull final List<String> commentIds,
                                                           String authId) {
    List<NatterById> comments = natterByIdRepository.findAllById(commentIds);
    List<NatterDto> commentsDto = new ArrayList<>();
    comments.forEach(comment -> {
      NatterDto natterDto =
          new NatterDto(comment.getId(), comment.getBody(), comment.getParentNatterId(),
              comment.getDateCreated(), comment.getDateUpdated(), comment.getAuthorId(),
              comment.getAuthorName(), Objects.equals(authId, comment.getAuthorId()),
              comment.getDateUpdated().isAfter(comment.getDateCreated()), comment.getLikes().size(), comment.getLikes());
      commentsDto.add(natterDto);
    });
    return commentsDto.stream()
        .sorted(Comparator.comparing(NatterDto::getDateUpdated).reversed()).collect(
            Collectors.toList());
  }

  /**
   * Method to like a natter item
   *
   * @param authId   the id of the authenticated user
   * @param natterId the natter id to like
   * @return
   * @throws DatabaseErrorException
   */
  public ResponseDto likeNatter(@NonNull final String authId, String natterId)
      throws DatabaseErrorException {
    ResponseDto responseDto = new ResponseDto();
    if (natterId == null) {
      responseDto.setStatus(HttpStatus.BAD_REQUEST);
      responseDto.setErrorMessages(getErrorMessageForEnum(ErrorMessageNatterEnum.NATTER_NULL_ID));
    } else {
      try {
        boolean isLike = false;
        NatterById natterById = natterByIdRepository.findById(natterId).orElseThrow();
        if(!natterById.getLikes().contains(authId)) {
          isLike = true;
          natterById.getLikes().add(authId);
        } else {
          natterById.getLikes().remove(authId);
        }
        natterByIdRepository.save(natterById);
        NatterByAuthor natterByAuthor = natterByAuthorRepository.findById(
            new NatterByAuthorPrimaryKey(natterById.getAuthorId(), natterId)).orElseThrow();
        if(isLike){
          natterByAuthor.getUserLikes().add(authId);
        } else {
          natterByAuthor.getUserLikes().remove(authId);
        }
        natterByAuthor.setLikes(natterByAuthor.getUserLikes().size());
        natterByAuthorRepository.save(natterByAuthor);
        responseDto.setUserMessages(
            getSuccessMessageForEnum(SuccessMessageNatterEnum.REACT_SUCCESS));
        responseDto.setStatus(HttpStatus.OK);
      } catch (NoSuchElementException e) {
        responseDto.setStatus(HttpStatus.FORBIDDEN);
        responseDto.setErrorMessages(
            getErrorMessageForEnum(ErrorMessageNatterEnum.UNAUTHORISED_ACCESS_NATTER));
      } catch (IllegalArgumentException e) {
        throw new DatabaseErrorException(ErrorMessageNatterEnum.UNABLE_TO_SAVE_RECORD);
      }

    }
    return responseDto;
  }


  public ResponseListDto<NatterByAuthor> getNattersForFollowing(@NonNull final String authId) {
    //TODO - Paginate the response
    ResponseListDto<NatterByAuthor> natterListResponseDto = new ResponseListDto<>();
    List<UserFollowersFollowing> userFollowersFollowingList = userService.getFollowingForUserId(authId).getList();
    Set<String> followingList = userService.getFollowingIdsForUser(authId);
//    for (UserFollowersFollowing userFollowersFollowing : userFollowersFollowingList){
//      followingList.add(userFollowersFollowing.getId());
//    }
   List<NatterByAuthor> natterByAuthor = new ArrayList<>();
    if(followingList.size() > 0){
      natterByAuthor =
          natterByAuthorRepository.findAllByAuthorIdList(followingList);
      natterByAuthor = natterByAuthor.stream().filter(natter ->
              natter.getParentAuthorId() == null)
          .sorted(Comparator.comparing(NatterByAuthor::getDateUpdated).reversed())
          .collect(Collectors.toList());
    }

    natterListResponseDto.setList(natterByAuthor);
    natterListResponseDto.setStatus(HttpStatus.OK);
    natterListResponseDto.setUserMessages(
        getSuccessMessageForEnum(SuccessMessageNatterEnum.FETCHED_All_NATTERS));
    return natterListResponseDto;
  }

  /**
   * Method to generate a success message to send the client
   *
   * @param successMessageNatterEnum the success message enum
   * @return the message map
   */
  private Map<String, String> getSuccessMessageForEnum(
      SuccessMessageNatterEnum successMessageNatterEnum) {
    return messageUtil.returnMessageMap(successMessageNatterEnum.getCode(),
        successMessageNatterEnum.getMessage());
  }

  /**
   * Method to generate an error message to send the client
   *
   * @param errorMessageNatterEnum the error message enum
   * @return the message map
   */
  public Map<String, String> getErrorMessageForEnum(
      ErrorMessageNatterEnum errorMessageNatterEnum) {
    return messageUtil.returnMessageMap(errorMessageNatterEnum.getCode(),
        errorMessageNatterEnum.getMessage());
  }
}
