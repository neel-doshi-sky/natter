package com.natter.service.natter;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import com.natter.dto.BaseResponseDto;
import com.natter.dto.NatterCreationResponseDto;
import com.natter.dto.NatterListResponseDto;
import com.natter.enums.natter.ErrorMessageEnum;
import com.natter.enums.natter.SuccessMessageEnum;
import com.natter.model.natter.NatterById;
import com.natter.model.natter.NatterCreateRequest;
import com.natter.model.natter.NatterByAuthor;
import com.natter.model.natter.NatterListPrimaryKey;
import com.natter.model.natter.NatterOriginal;
import com.natter.repository.NatterByAuthorRepository;
import com.natter.repository.NatterOriginalRepository;
import com.natter.repository.NatterByIdRepository;
import java.time.LocalDateTime;
import java.util.HashSet;
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

  private final NatterOriginalRepository natterOriginalRepository;
  private final NatterValidationService validationService;
  private final NatterByAuthorRepository natterByAuthorRepository;
  private final NatterByIdRepository natterByIdRepository;

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
        NatterListPrimaryKey natterListPrimaryKey = new NatterListPrimaryKey();
        natterListPrimaryKey.setTimeId(timeId.toString());
        natterListPrimaryKey.setAuthorId(authorId);

        NatterByAuthor natterByAuthor = new NatterByAuthor();
        natterByAuthor.setId(natterListPrimaryKey);
        natterByAuthor.setBody(natterCreateRequest.getBody());
        natterByAuthor.setCreated(LocalDateTime.now());

        natterByAuthorRepository.save(natterByAuthor);

        NatterById natterById = new NatterById();
        natterById.setId(timeId.toString());
        natterById.setBody(natterCreateRequest.getBody());
        LocalDateTime now = LocalDateTime.now();
        natterById.setDateCreated(now);
        natterById.setDateUpdated(now);
        natterById.setParentNatterId(null);
        NatterById created = natterByIdRepository.save(natterById);
        response.setNatterById(created);
        response.setStatus(HttpStatus.OK);
        response.setUserMessages(Map.of(SuccessMessageEnum.CREATED_NEW_NATTER.getCode(), SuccessMessageEnum.CREATED_NEW_NATTER.getMessage()));
      } else {
        response.setErrorMessages(validationResult);
        response.setStatus(HttpStatus.BAD_REQUEST);

      }
    }
    return response;
  }

  /**
   * Method to build an entity to save to the database
   *
   * @param natterCreateRequest the request object
   * @return the Entity that has been built
   */
  private NatterOriginal buildNatterEntity(NatterCreateRequest natterCreateRequest,
                                           String authorId) {
    LocalDateTime now = LocalDateTime.now();
    return NatterOriginal.builder()
        .id(UUID.randomUUID().toString())
        .body(natterCreateRequest.getBody())
        .parentNatterId(natterCreateRequest.getParentNatterId())
        .timeCreated(now)
        .authorId(authorId)
        .userReactions(new HashSet<>())
        .timeUpdated(now)
        .build();

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
         natterByIdRepository.deleteById(idToDelete);
         NatterListPrimaryKey key = new NatterListPrimaryKey();
         key.setTimeId(idToDelete);
         key.setAuthorId(authorId);
         natterByAuthorRepository.deleteById(key);
         response.setStatus(HttpStatus.OK);
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
    return natterListResponseDto;
  }
}
