package com.natter.controller;

import com.natter.dto.CreateResponseDto;
import com.natter.dto.GetResponseDto;
import com.natter.dto.NatterDto;
import com.natter.dto.ResponseDto;
import com.natter.dto.ResponseListDto;
import com.natter.exception.DatabaseErrorException;
import com.natter.model.natter.NatterByAuthor;
import com.natter.model.natter.NatterById;
import com.natter.model.natter.NatterCreateRequest;
import com.natter.model.natter.NatterUpdateRequest;
import com.natter.service.AuthService;
import com.natter.service.natter.NatterService;
import java.util.HashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:8080")
@RequestMapping("/api/v1/natter")
public class NatterController {

  private final NatterService natterService;

  private final AuthService authService;

  /**
   * Endpoint to get all tweets for the authenticated user
   *
   * @param principal the authenticated user
   * @return the response entity containing the list of natters
   */
  @GetMapping(value = {"/user/{id}", "/user"})
  public ResponseEntity<ResponseListDto<NatterByAuthor>> listNattersForUserId(
      @AuthenticationPrincipal OAuth2User principal, @PathVariable(required = false) String id) {
    if (id == null || id.isEmpty()) {
      id = authService.getUserIdFromAuth(principal);
    }

    ResponseListDto<NatterByAuthor> natterListResponse =
        natterService.getNattersForUser(id);
    return new ResponseEntity<>(natterListResponse, HttpStatus.OK);

  }

  /**
   * Endpoint to get all tweets for the authenticated user
   *
   * @return the response entity containing the list of natters
   */
  @GetMapping(value = "/")
  public ResponseEntity<ResponseListDto<NatterByAuthor>> listAllNatters(
      @AuthenticationPrincipal OAuth2User principal) {

    ResponseListDto<NatterByAuthor> natterListResponse =
        natterService.getAllNatters();
    return new ResponseEntity<>(natterListResponse, HttpStatus.OK);

  }

  /**
   * Endpoint to create a new natter
   *
   * @param principal           the authenticated user
   * @param natterCreateRequest the natter create request body
   * @return Response entity with result of operation
   */
  @ResponseBody
  @PostMapping(value = "/")
  public ResponseEntity<CreateResponseDto<NatterById>> create(
      @AuthenticationPrincipal OAuth2User principal,
      @RequestBody NatterCreateRequest natterCreateRequest) {

    CreateResponseDto<NatterById> result =
        natterService.create(natterCreateRequest, principal);
    return new ResponseEntity<>(result, result.getStatus());

  }

  /**
   * Endpoint to delete a natter by id
   *
   * @param principal the authenticated user
   * @param id        the id of the natter
   * @return response entity containing result of operation
   */
  @DeleteMapping(value = "/{id}")
  public ResponseEntity<ResponseDto> delete(@AuthenticationPrincipal OAuth2User principal,
                                            @PathVariable String id) {
    ResponseDto result = natterService.delete(id, authService.getUserIdFromAuth(principal));
    return new ResponseEntity<>(result, result.getStatus());
  }

  /**
   * Endpoint to edit an existing natter
   *
   * @param principal     the authenticated user
   * @param updateRequest the update request body
   * @return the response entity containing result of operation
   */
  @ResponseBody
  @PutMapping(value = "/")
  public ResponseEntity<ResponseDto> edit(@AuthenticationPrincipal OAuth2User principal,
                                          @RequestBody NatterUpdateRequest updateRequest) {
    ResponseDto result =
        natterService.edit(updateRequest, authService.getUserIdFromAuth(principal));
    return new ResponseEntity<>(result, result.getStatus());

  }

  /**
   * Endpoint to add a comment to an existing natter
   *
   * @param principal           the authenticated user
   * @param natterCreateRequest the natter create request body
   * @return the response entity containing result of operation
   */
  @ResponseBody
  @PostMapping(value = "/comment")
  public ResponseEntity<CreateResponseDto<NatterById>> comment(
      @AuthenticationPrincipal OAuth2User principal,
      @RequestBody NatterCreateRequest natterCreateRequest) {

    CreateResponseDto<NatterById> result =
        natterService.addComment(natterCreateRequest, principal);
    return new ResponseEntity<>(result, result.getStatus());

  }

  /**
   * Endpoint to get a natter by id
   *
   * @param principal the authenticated user
   * @param id        the id of the natter to fetch
   * @return the response entity containing the result of the operation
   */
  @GetMapping(value = "/{id}")
  public ResponseEntity<GetResponseDto<NatterDto>> getById(
      @AuthenticationPrincipal OAuth2User principal,
      @PathVariable String id) {
    GetResponseDto<NatterDto> result =
        natterService.getNatterById(id, authService.getUserIdFromAuth(principal));
    return new ResponseEntity<>(result, result.getStatus());
  }

  /**
   * Endpoint to like/unlike a natter by id
   *
   * @param principal the principal
   * @param id        the id of the natter
   * @return the response entity containing the result of the operation
   */
  @PostMapping(value = "/like/{id}")
  public ResponseEntity<ResponseDto> likeNatter(@AuthenticationPrincipal OAuth2User principal,
                                                @PathVariable String id) {
    try {
      ResponseDto result = natterService.likeNatter(authService.getUserIdFromAuth(principal), id);
      return new ResponseEntity<>(result, result.getStatus());

    } catch (DatabaseErrorException e) {
      return new ResponseEntity<>(
          new ResponseDto(natterService.getErrorMessageForEnum(e.getErrorMessageNatterEnum()),
              new HashMap<>(), HttpStatus.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
    }

  }

  /**
   * Endpoint to get natter feed for users that the authenticated user follows
   *
   * @param principal the authenticated user
   * @return list of natters for feed
   */
  @GetMapping(value = "/feed")
  public ResponseEntity<ResponseListDto<NatterByAuthor>> getNatterFeed(
      @AuthenticationPrincipal OAuth2User principal) {

    ResponseListDto<NatterByAuthor> natterListResponse =
        natterService.getNatterFeed(authService.getUserIdFromAuth(principal));
    return new ResponseEntity<>(natterListResponse, natterListResponse.getStatus());

  }
}
