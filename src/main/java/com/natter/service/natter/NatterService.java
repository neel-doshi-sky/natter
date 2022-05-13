package com.natter.service.natter;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import com.natter.dto.BaseResponseDto;
import com.natter.dto.NatterCreationResponseDto;
import com.natter.dto.NatterListResponseDto;
import com.natter.model.natter.Natter;
import com.natter.model.natter.NatterCreateRequest;
import com.natter.model.natter.NatterList;
import com.natter.model.natter.NatterListPrimaryKey;
import com.natter.model.natter.NatterOriginal;
import com.natter.repository.NatterListRepository;
import com.natter.repository.NatterOriginalRepository;
import com.natter.repository.NatterRepository;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
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
  private final NatterListRepository natterListRepository;
  private final NatterRepository natterRepository;

  /**
   * Method to create a natter item and save it to the database
   *
   * @param request  the create request body
   * @param authorId the author id
   * @return the result of the operation with any errors
   */
  public NatterCreationResponseDto create(NatterCreateRequest request, String authorId) {
    UUID timeId = Uuids.timeBased();
    NatterCreationResponseDto response = new NatterCreationResponseDto();
    NatterListPrimaryKey natterListPrimaryKey = new NatterListPrimaryKey();
    natterListPrimaryKey.setCreated(LocalDateTime.now());
    natterListPrimaryKey.setTimeId(timeId.toString());
    natterListPrimaryKey.setAuthorId(authorId);

    NatterList natterList = new NatterList();
    natterList.setId(natterListPrimaryKey);
    natterList.setBody(request.getBody());

    natterListRepository.save(natterList);

    Natter natter = new Natter();
    natter.setId(timeId.toString());
    natter.setBody(request.getBody());
    Natter created = natterRepository.save(natter);
    response.setNatter(created);
    response.setStatus(HttpStatus.OK);
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
    natterRepository.deleteById(idToDelete);
    response.setStatus(HttpStatus.OK);
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
    List<NatterList> natterList =
        natterListRepository.findAllByAuthorId(authorId);
    natterListResponseDto.setNatterLists(natterList);
    return natterListResponseDto;
  }
}
