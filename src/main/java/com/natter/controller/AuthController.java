package com.natter.controller;

import com.natter.model.natter.NatterByAuthor;
import com.natter.service.natter.NatterService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RequiredArgsConstructor
public class AuthController {

  private final NatterService natterService;

  @GetMapping("/")
  public String home(@AuthenticationPrincipal OAuth2User principal, Model model){
    if(principal == null || principal.getAttribute("sub") == null){
      return "index";
    }
    List<NatterByAuthor> natterByIdList = natterService.getNattersForUser(principal.getAttribute("sub")).getList();
    model.addAttribute("natters", natterByIdList);
    return "home";

  }
}
