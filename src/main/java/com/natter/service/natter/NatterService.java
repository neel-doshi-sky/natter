package com.natter.service.natter;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import com.natter.dto.CreateResponseDto;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

  /**
   * Method to create a natter item and save it to the database
   *
   * @param natterCreateRequest the create natterCreateRequest body
   * @param author            the author id
   * @return the result of the operation with any errors
   */
  public CreateResponseDto<NatterById> create(NatterCreateRequest natterCreateRequest, OAuth2User author) {
    CreateResponseDto<NatterById> response = new CreateResponseDto<>();
    if (natterCreateRequest == null) {
      response.setStatus(HttpStatus.BAD_REQUEST);
      response.setErrorMessages(Map.of(ErrorMessageNatterEnum.NATTER_CREATION_ERROR_NULL_BODY.getCode(),
          ErrorMessageNatterEnum.NATTER_CREATION_ERROR_NULL_BODY.getMessage()));
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
          response.setUserMessages(Map.of(SuccessMessageNatterEnum.CREATED_NEW_NATTER.getCode(), SuccessMessageNatterEnum.CREATED_NEW_NATTER.getMessage()));
        } catch (DatabaseErrorException e){
          response.setErrorMessages(Map.of(e.getErrorMessageNatterEnum().getCode(), e.getErrorMessageNatterEnum().getMessage()));
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
   * @param authorId the author id
   * @return the response
   */
  public ResponseDto delete(String idToDelete, @NonNull String authorId) {
    ResponseDto response = new ResponseDto();

    if(idToDelete == null){
      response.setErrorMessages(Map.of(ErrorMessageNatterEnum.NATTER_NULL_ID.getCode(), ErrorMessageNatterEnum.NATTER_NULL_ID.getMessage()));
      response.setStatus(HttpStatus.BAD_REQUEST);

    } else {
      Optional<NatterById> foundById = natterByIdRepository.findById(idToDelete);
      if(foundById.isPresent()) {
       if(foundById.get().getAuthorId().equals(authorId)) {
         natterDatabaseService.delete(idToDelete, authorId);
         response.setStatus(HttpStatus.OK);
         response.setUserMessages(Map.of(SuccessMessageNatterEnum.DELETED_NATTER.getCode(), SuccessMessageNatterEnum.DELETED_NATTER.getMessage()));
       } else {
         response.setErrorMessages(Map.of(ErrorMessageNatterEnum.UNAUTHORISED_ACCESS_NATTER.getCode(), ErrorMessageNatterEnum.UNAUTHORISED_ACCESS_NATTER.getMessage()));
         response.setStatus(HttpStatus.FORBIDDEN);
       }
      } else {
        response.setErrorMessages(Map.of(ErrorMessageNatterEnum.UNABLE_TO_DELETE_RECORD.getCode(), ErrorMessageNatterEnum.UNABLE_TO_DELETE_RECORD.getMessage()));
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
    natterListResponseDto.setList(natterByAuthor);
    natterListResponseDto.setStatus(HttpStatus.OK);
    natterListResponseDto.setUserMessages(
        Map.of(SuccessMessageNatterEnum.FETCHED_NATTERS_BY_AUTHOR.getCode(),
            SuccessMessageNatterEnum.FETCHED_NATTERS_BY_AUTHOR.getMessage()));
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
        responseDto.setUserMessages(Map.of(SuccessMessageNatterEnum.UPDATED_NATTER.getCode(),
            SuccessMessageNatterEnum.UPDATED_NATTER.getMessage()));
        responseDto.setStatus(HttpStatus.OK);
      } else {
        responseDto.setErrorMessages(Map.of(ErrorMessageNatterEnum.UNAUTHORISED_ACCESS_NATTER.getCode(),
            ErrorMessageNatterEnum.UNAUTHORISED_ACCESS_NATTER.getMessage()));
        responseDto.setStatus(HttpStatus.FORBIDDEN);
      }
    } else {
      responseDto.setErrorMessages(validationResult);
      responseDto.setStatus(HttpStatus.BAD_REQUEST);
    }
    return responseDto;
  }

  public CreateResponseDto<NatterById> addComment(NatterCreateRequest commentRequest, OAuth2User author) {
    CreateResponseDto<NatterById> responseDto = new CreateResponseDto<>();
    if(commentRequest.getParentNatterId() == null){
      responseDto.setErrorMessages(Map.of(ErrorMessageNatterEnum.NATTER_NULL_ID.getCode(), ErrorMessageNatterEnum.NATTER_NULL_ID.getMessage()));
      responseDto.setStatus(HttpStatus.BAD_REQUEST);
    } else {
      Optional<NatterById> natterParentOptional = natterByIdRepository.findById(commentRequest.getParentNatterId());
      if(natterParentOptional.isPresent()){
        try {
        NatterById parentNatter = natterParentOptional.get();
        NatterById comment = natterDatabaseService.addComment(commentRequest, author);
        parentNatter.getComments().add(comment.getId());
          natterDatabaseService.updateNatterAfterComment(parentNatter);
          responseDto.setStatus(HttpStatus.OK);
          responseDto.setUserMessages(Map.of(SuccessMessageNatterEnum.CREATED_COMMENT.getCode(), SuccessMessageNatterEnum.CREATED_COMMENT.getMessage()));
          responseDto.setCreated(comment);

        } catch (DatabaseErrorException e){
          responseDto.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
          responseDto.setErrorMessages(Map.of(ErrorMessageNatterEnum.UNABLE_TO_SAVE_RECORD.getCode(), ErrorMessageNatterEnum.UNABLE_TO_SAVE_RECORD.getMessage()));
        }

      } else {
        responseDto.setErrorMessages(Map.of(ErrorMessageNatterEnum.UNAUTHORISED_ACCESS_NATTER.getCode(), ErrorMessageNatterEnum.UNAUTHORISED_ACCESS_NATTER.getMessage()));
        responseDto.setStatus(HttpStatus.BAD_REQUEST);
      }
    }
    return responseDto;
  }
}
