package com.natter.service.natter;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import com.natter.dto.BaseResponseDto;
import com.natter.dto.NatterCreationResponseDto;
import com.natter.dto.NatterListResponseDto;
import com.natter.enums.natter.ErrorMessageEnum;
import com.natter.enums.natter.SuccessMessageEnum;
import com.natter.exception.DatabaseErrorException;
import com.natter.model.natter.NatterById;
import com.natter.model.natter.NatterCreateRequest;
import com.natter.model.natter.NatterByAuthor;
import com.natter.repository.NatterByAuthorRepository;
import com.natter.repository.NatterByIdRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
   * @param natterCreateRequest  the create natterCreateRequest body
   * @param authorId the author id
   * @return the result of the operation with any errors
   */
  public NatterCreationResponseDto create(NatterCreateRequest natterCreateRequest, String authorId) {
    NatterCreationResponseDto response = new NatterCreationResponseDto();
    if(natterCreateRequest == null){
       response.setStatus(HttpStatus.BAD_REQUEST);
       response.setErrorMessages(Map.of(ErrorMessageEnum.NATTER_CREATION_ERROR_NULL_BODY.getErrorCode(), ErrorMessageEnum.NATTER_CREATION_ERROR_NULL_BODY.getMessage()));
    } else {
      Map<String, String> validationResult = validationService.validateNatterCreateBody(natterCreateRequest);
      if(validationResult.isEmpty()) {
        UUID timeId = Uuids.timeBased();

        try {
          NatterById created = natterDatabaseService.saveNatter(timeId.toString(), natterCreateRequest, authorId);
          response.setNatterById(created);
          response.setStatus(HttpStatus.OK);
          response.setUserMessages(Map.of(SuccessMessageEnum.CREATED_NEW_NATTER.getCode(), SuccessMessageEnum.CREATED_NEW_NATTER.getMessage()));
        } catch (DatabaseErrorException e){
          response.setErrorMessages(Map.of(e.getErrorMessageEnum().getErrorCode(), e.getErrorMessageEnum().getMessage()));
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
  public BaseResponseDto delete(String idToDelete, @NonNull String authorId) {
    BaseResponseDto response = new BaseResponseDto();

    if(idToDelete == null){
      response.setErrorMessages(Map.of(ErrorMessageEnum.NATTER_NULL_ID.getErrorCode(), ErrorMessageEnum.NATTER_NULL_ID.getMessage()));
      response.setStatus(HttpStatus.BAD_REQUEST);

    } else {
      Optional<NatterById> foundById = natterByIdRepository.findById(idToDelete);
      if(foundById.isPresent()) {
       if(foundById.get().getAuthorId().equals(authorId)) {
         natterDatabaseService.deleteNatter(idToDelete, authorId);
         response.setStatus(HttpStatus.OK);
         response.setUserMessages(Map.of(SuccessMessageEnum.DELETED_NATTER.getCode(), SuccessMessageEnum.DELETED_NATTER.getMessage()));
       } else {
         response.setErrorMessages(Map.of(ErrorMessageEnum.UNAUTHORISED_ACCESS_NATTER.getErrorCode(), ErrorMessageEnum.UNAUTHORISED_ACCESS_NATTER.getMessage()));
         response.setStatus(HttpStatus.FORBIDDEN);
       }
      } else {
        response.setErrorMessages(Map.of(ErrorMessageEnum.UNABLE_TO_DELETE_RECORD.getErrorCode(), ErrorMessageEnum.UNABLE_TO_DELETE_RECORD.getMessage()));
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
  public NatterListResponseDto getNattersForUser(String authorId) {
    NatterListResponseDto natterListResponseDto = new NatterListResponseDto();
    List<NatterByAuthor> natterByAuthor =
        natterByAuthorRepository.findAllByAuthorId(authorId);
    natterListResponseDto.setNatterByAuthors(natterByAuthor);
    natterListResponseDto.setStatus(HttpStatus.OK);
    natterListResponseDto.setUserMessages(Map.of(SuccessMessageEnum.FETCHED_NATTERS_BY_AUTHOR.getCode(), SuccessMessageEnum.FETCHED_NATTERS_BY_AUTHOR.getMessage()));
    return natterListResponseDto;
  }
}
