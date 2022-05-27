package com.natter.controller;

import com.natter.dto.ResponseDto;
import com.natter.dto.natter.NatterCreateResponseDto;
import com.natter.dto.ResponseListDto;
import com.natter.model.natter.NatterByAuthor;
import com.natter.model.natter.NatterCreateRequest;
import com.natter.model.natter.NatterUpdateRequest;
import com.natter.service.AuthService;
import com.natter.service.natter.NatterService;
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
  @GetMapping(value = "/user")
  public ResponseEntity<ResponseListDto<NatterByAuthor>> listUsersNatters(
      @AuthenticationPrincipal OAuth2User principal) {

    ResponseListDto<NatterByAuthor> natterListResponse =
        natterService.getNattersForUser(authService.getUserIdFromAuth(principal));
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
  public ResponseEntity<NatterCreateResponseDto> create(
      @AuthenticationPrincipal OAuth2User principal,
      @RequestBody NatterCreateRequest natterCreateRequest) {

    NatterCreateResponseDto result =
        natterService.create(natterCreateRequest, authService.getUserIdFromAuth(principal));
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

}
