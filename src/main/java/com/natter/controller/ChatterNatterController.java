package com.natter.controller;

import com.natter.enums.natter.ErrorMessageEnum;
import com.natter.model.natter.NatterCreateRequest;
import com.natter.model.natter.NatterCreationResponse;
import com.natter.model.natter.NatterListResponse;
import com.natter.repository.NatterRepository;
import com.natter.service.NatterService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatterNatterController {

  private final NatterRepository natterRepository;

  private final NatterService natterService;

  @GetMapping(value = "/")
  public ResponseEntity<NatterListResponse> listNatters(
      @AuthenticationPrincipal OAuth2User principal) {

    NatterListResponse natterListResponse = new NatterListResponse();
    natterListResponse.setLastRefreshed(LocalDateTime.now());
    natterListResponse.setNatterList(new ArrayList<>());


    return new ResponseEntity<>(natterListResponse, HttpStatus.OK);

  }

  @PostMapping(value = "/create")
  public ResponseEntity<NatterCreationResponse> createNatter(@AuthenticationPrincipal OAuth2User principal, NatterCreateRequest natterCreateRequest) {
    NatterCreationResponse response = natterService.create(natterCreateRequest);
    return new ResponseEntity<>(response, response.getStatus());

  }
}
