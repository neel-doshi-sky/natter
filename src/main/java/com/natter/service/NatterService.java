package com.natter.service;

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
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NatterService {

  private final NatterRepository natterRepository;
  private final NatterValidationService validationService;

  public NatterCreationResponse create(NatterCreateRequest createRequest)
       {
    NatterCreationResponse response = new NatterCreationResponse();
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
          Natter createdNatter = natterRepository.save(buildNatterEntity(createRequest));
          response.setCreatedNatter(createdNatter);
          userMessages.put(SuccessMessageEnum.CREATED_NEW_NATTER.getCode(),
              SuccessMessageEnum.CREATED_NEW_NATTER.getMessage());
          response.setStatus(HttpStatus.OK);
        } catch (DataAccessException e){
          errorMessages.put(ErrorMessageEnum.UNABLE_TO_SAVE_RECORD.getErrorCode(), ErrorMessageEnum.UNABLE_TO_SAVE_RECORD.getMessage());
          response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        }
      }
    }
    response.setErrorMessages(errorMessages);
    response.setUserMessages(userMessages);
    return response;
  }

  private Natter buildNatterEntity(NatterCreateRequest natterCreateRequest) {
    LocalDateTime now = LocalDateTime.now();
    return Natter.builder()
        .id(UUID.randomUUID().toString())
        .body(natterCreateRequest.getBody())
        .parentNatterId(natterCreateRequest.getParentNatterId())
        .timeCreated(now)
        .authorId(natterCreateRequest.getAuthorId())
        .userReactions(new HashSet<>())
        .timeUpdated(now)
        .build();

  }

}
