package com.natter.service.natter;

import com.natter.dto.BaseResponseDto;
import com.natter.dto.NatterCreationResponseDto;
import com.natter.dto.NatterListResponseDto;
import com.natter.enums.natter.ErrorMessageEnum;
import com.natter.enums.natter.SuccessMessageEnum;
import com.natter.model.natter.Natter;
import com.natter.model.natter.NatterCreateRequest;
import com.natter.repository.NatterRepository;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NatterService {

  private final NatterRepository natterRepository;
  private final NatterValidationService validationService;

  /**
   * Method to create a natter item and save it to the database
   *
   * @param createRequest the create request body
   * @param authorId the author id
   * @return the result of the operation with any errors
   */
  public NatterCreationResponseDto create(NatterCreateRequest createRequest, String authorId) {
    NatterCreationResponseDto response = new NatterCreationResponseDto();
    Map<String, String> errorMessages = new HashMap<>();
    Map<String, String> userMessages = new HashMap<>();
    if (createRequest == null) {
      errorMessages.put(ErrorMessageEnum.NATTER_CREATION_ERROR_NULL_BODY.getErrorCode(),
          ErrorMessageEnum.NATTER_CREATION_ERROR_NULL_BODY.getMessage());
      response.setStatus(HttpStatus.BAD_REQUEST);
    } else {
      errorMessages = validationService.validateNatterCreateBody(createRequest);
      if (errorMessages.isEmpty()) {
        try{
          Natter createdNatter = natterRepository.save(buildNatterEntity(createRequest, authorId));
          response.setCreatedNatter(createdNatter);
          userMessages.put(SuccessMessageEnum.CREATED_NEW_NATTER.getCode(),
              SuccessMessageEnum.CREATED_NEW_NATTER.getMessage());
          response.setStatus(HttpStatus.OK);
        } catch (DataAccessException e) {
          log.error(Arrays.toString(e.getStackTrace()));
          errorMessages.put(ErrorMessageEnum.UNABLE_TO_SAVE_RECORD.getErrorCode(),
              ErrorMessageEnum.UNABLE_TO_SAVE_RECORD.getMessage());
          response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        }
      }
    }
    response.setErrorMessages(errorMessages);
    response.setUserMessages(userMessages);
    return response;
  }

  /**
   * Method to build an entity to save to the database
   *
   * @param natterCreateRequest the request object
   * @return the Entity that has been built
   */
  private Natter buildNatterEntity(NatterCreateRequest natterCreateRequest, String authorId) {
    LocalDateTime now = LocalDateTime.now();
    return Natter.builder()
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
    if(idToDelete != null) {
      Optional<String> checkIfAllowed =
          natterRepository.findByAuthorIdAndNatterId(authorId, idToDelete);
      if (checkIfAllowed.isPresent()) {
        try {
          natterRepository.deleteByNatterId(idToDelete);
          response.setUserMessages(Map.of(SuccessMessageEnum.DELETED_NATTER.getCode(),
              SuccessMessageEnum.DELETED_NATTER.getMessage()));
          response.setStatus(HttpStatus.OK);

        } catch (IllegalArgumentException e) {
          log.error(Arrays.toString(e.getStackTrace()));
          response.setErrorMessages(Map.of(ErrorMessageEnum.UNABLE_TO_DELETE_RECORD.getErrorCode(),
              ErrorMessageEnum.UNABLE_TO_DELETE_RECORD.getMessage()));
          response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);

        }
      } else {
        response.setErrorMessages(Map.of(ErrorMessageEnum.UNAUTHORISED_ACCESS_NATTER.getErrorCode(),
            ErrorMessageEnum.UNAUTHORISED_ACCESS_NATTER.getMessage()));
        response.setStatus(HttpStatus.FORBIDDEN);
      }
    } else {
      response.setErrorMessages(Map.of(ErrorMessageEnum.NATTER_NULL_ID.getErrorCode(),
          ErrorMessageEnum.NATTER_NULL_ID.getMessage()));
      response.setStatus(HttpStatus.BAD_REQUEST);
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
    List<Natter> natterList = natterRepository.getNattersByAuthorId(authorId);
    natterListResponseDto.setNatterList(natterList);
    natterListResponseDto.setStatus(HttpStatus.OK);
    return natterListResponseDto;
  }
}
