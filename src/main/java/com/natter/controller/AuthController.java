package com.natter.controller;

import com.natter.model.natter.NatterByAuthor;
import com.natter.model.natter.NatterCreateRequest;
import com.natter.model.template.NatterToDisplay;
import com.natter.model.template.UserToDisplay;
import com.natter.model.user.User;
import com.natter.model.user.UserFollowersFollowing;
import com.natter.repository.user.UserRepository;
import com.natter.service.natter.NatterService;
import com.natter.service.user.UserService;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class AuthController {

  private final NatterService natterService;
  private final UserRepository userRepository;

  @GetMapping("/")
  public String home(@AuthenticationPrincipal OAuth2User principal, Model model){
    if(principal == null || principal.getAttribute("sub") == null){
      return "index";
    }
    List<NatterByAuthor> natterByIdList = natterService.getNattersForUser(principal.getAttribute("sub")).getList();
    List<NatterToDisplay> natterToDisplayList = new ArrayList<>();
    for(NatterByAuthor natter : natterByIdList){
      if(natter.getParentAuthorId() == null) {
        natterToDisplayList.add(
            new NatterToDisplay(natter.getId().getId(), natter.getBody(), natter.getAuthorName(),
                natter.getDateCreated().toLocalDate().toString(),
                natter.getCommentCount() + " comment(s)"));
      }
    }

    User user = userRepository.findById(principal.getName()).get();
    UserToDisplay userToDisplay = new UserToDisplay(user.getFirstName(), user.getLastName(),"Followers: " +  (user.getFollowers() != null ? user.getFollowers().size() : "0"),
        "Following: " + (user.getFollowing() != null ? user.getFollowing().size() : 0),
        user.getEmail() );
    model.addAttribute("natters", natterToDisplayList);
    model.addAttribute("user", userToDisplay);
    model.addAttribute("natterCreateRequest", new NatterCreateRequest());

    return "home";

  }
}
