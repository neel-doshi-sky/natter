package com.natter.service.user;

import com.natter.dto.ResponseDto;
import com.natter.enums.user.ErrorMessageUserEnum;
import com.natter.enums.user.SuccessMessageUserEnum;
import com.natter.model.user.UserInfo;
import com.natter.repository.user.UserInfoRepository;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

  private final UserInfoRepository userInfoRepository;
  private final UserDatabaseService userDatabaseService;

  public ResponseDto followOrUnfollowUserById(String authUserId, String id, boolean isFollow) {
    ResponseDto response = new ResponseDto();
    if(id == null){
      response.setErrorMessages(Map.of(ErrorMessageUserEnum.USER_ID_NULL.getCode(),
          ErrorMessageUserEnum.USER_ID_NULL.getMessage()));
      response.setStatus(HttpStatus.BAD_REQUEST);
    } else {
      Optional<UserInfo> userOptional = userInfoRepository.findById(id);
      if (userOptional.isPresent()) {
        UserInfo user = userOptional.get();
        if(isFollow) {
          userDatabaseService.updateFollowersForFollow(authUserId, user.getId());
          response.setUserMessages(Map.of(SuccessMessageUserEnum.FOLLOWED_USER.getCode(),
              SuccessMessageUserEnum.FOLLOWED_USER.getMessage()));
        } else {
          userDatabaseService.updateFollowersForUnfollow(authUserId, user.getId());
          response.setUserMessages(Map.of(SuccessMessageUserEnum.UNFOLLOWED_USER.getCode(),
              SuccessMessageUserEnum.UNFOLLOWED_USER.getMessage()));
        }
        response.setStatus(HttpStatus.OK);
      } else {
        response.setErrorMessages(Map.of(ErrorMessageUserEnum.USER_NOT_FOUND.getCode(),
            ErrorMessageUserEnum.USER_NOT_FOUND.getMessage()));
        response.setStatus(HttpStatus.NOT_FOUND);
      }
    }
    return response;
  }

}
