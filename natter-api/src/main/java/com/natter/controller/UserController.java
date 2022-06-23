package com.natter.controller;

import com.natter.dto.GetResponseDto;
import com.natter.dto.ResponseDto;
import com.natter.dto.ResponseListDto;
import com.natter.model.template.UserToDisplay;
import com.natter.model.user.UserFollowersFollowing;
import com.natter.service.AuthService;
import com.natter.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:8080")
@RequestMapping("/api/v1/user")
public class UserController {

  private final UserService userService;
  private final AuthService authService;

  /**
   * Endpoint to follow a user by id
   *
   * @param principal the authenticated user
   * @param id        the id of the user to follow
   * @return the response entity result
   */
  @PostMapping(value = "/follow/{id}")
  public ResponseEntity<ResponseDto> followUserById(@AuthenticationPrincipal OAuth2User principal,
                                                    @PathVariable(value = "id") String id) {
    ResponseDto response =
        userService.followOrUnfollowUserById(authService.getUserIdFromAuth(principal), id, true);
    return new ResponseEntity<>(response, response.getStatus());
  }

  /**
   * Endpoint to unfollow a user by id
   *
   * @param principal the authenticated user
   * @param id        the id of the user to unfollow
   * @return the response entity result
   */
  @PostMapping(value = "/unfollow/{id}")
  public ResponseEntity<ResponseDto> unfollowUserById(@AuthenticationPrincipal OAuth2User principal,
                                                      @PathVariable(value = "id") String id) {
    ResponseDto response =
        userService.followOrUnfollowUserById(authService.getUserIdFromAuth(principal), id, false);
    return new ResponseEntity<>(response, response.getStatus());
  }

  /**
   * Endpoint to get a list of followers for a user id
   *
   * @param principal the authenticated user
   * @param id        the id of the user
   * @return the response entity result
   */
  @GetMapping(value = {"/followers/{id}", "/followers"})
  public ResponseEntity<ResponseListDto<UserFollowersFollowing>> getFollowersForUserId(
      @AuthenticationPrincipal OAuth2User principal,
      @PathVariable(value = "id", required = false) String id) {
    if (id == null) {
      id = authService.getUserIdFromAuth(principal);
    }
    ResponseListDto<UserFollowersFollowing> response = userService.getFollowersForUserId(id);
    return new ResponseEntity<>(response, response.getStatus());
  }

  /**
   * Endpoint to get a list of following for a user id
   *
   * @param principal the authenticated user
   * @param id        the id of the user
   * @return the response entity result
   */
  @GetMapping(value = {"/following/{id}", "/following"})
  public ResponseEntity<ResponseListDto<UserFollowersFollowing>> getFollowingForUserId(
      @AuthenticationPrincipal OAuth2User principal,
      @PathVariable(value = "id", required = false) String id) {
    if (id == null) {
      id = authService.getUserIdFromAuth(principal);
    }
    ResponseListDto<UserFollowersFollowing> response = userService.getFollowingForUserId(id);
    return new ResponseEntity<>(response, response.getStatus());
  }

  /**
   * Endpoint to get a user by id
   *
   * @param principal the authenticated user
   * @param id        the id of the user to fetch
   * @return the response entity
   */
  @GetMapping(value = {"/{id}", "/"})
  public ResponseEntity<GetResponseDto<UserToDisplay>> getUserById(
      @AuthenticationPrincipal OAuth2User principal, @PathVariable(required = false) String id) {
    if (id == null || id.isEmpty()) {
      id = authService.getUserIdFromAuth(principal);
    }
    GetResponseDto<UserToDisplay> responseDto = userService.getUserById(principal, id);
    return new ResponseEntity<>(responseDto, responseDto.getStatus());
  }

}
