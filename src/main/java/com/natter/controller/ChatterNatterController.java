package com.natter.controller;

import com.natter.model.natter.NatterCreateRequest;
import com.natter.model.natter.NatterCreationResponse;
import com.natter.model.natter.NatterListResponse;
import com.natter.model.user.UserResponseModel;
import com.natter.service.NatterService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:8080")
@RequestMapping("/api/v1/natter")
public class ChatterNatterController {

  private final NatterService natterService;

  @GetMapping(value = "/")
  public ResponseEntity<NatterListResponse> list(
      @AuthenticationPrincipal OAuth2User principal) {

    NatterListResponse natterListResponse = new NatterListResponse();
    natterListResponse.setLastRefreshed(LocalDateTime.now());
    natterListResponse.setNatterList(new ArrayList<>());
    natterListResponse.setUserResponseModel(new UserResponseModel(principal.getAttribute("name"), principal.getAttribute("email")));


    return new ResponseEntity<>(natterListResponse, HttpStatus.OK);

  }


  @ResponseBody
  @PostMapping(value = "/")
  public ResponseEntity<NatterCreationResponse> create(@AuthenticationPrincipal OAuth2User principal, @RequestBody NatterCreateRequest natterCreateRequest) {
    NatterCreationResponse response = natterService.create(natterCreateRequest);
    return new ResponseEntity<>(response, response.getStatus());

  }
}
