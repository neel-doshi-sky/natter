package com.natter.service.user;

import com.natter.dto.ResponseDto;
import com.natter.dto.ResponseListDto;
import com.natter.enums.user.ErrorMessageUserEnum;
import com.natter.enums.user.SuccessMessageUserEnum;
import com.natter.model.user.User;
import com.natter.model.user.UserFollowersFollowing;
import com.natter.model.user.UserInfo;
import com.natter.repository.user.UserFollowersFollowingRepository;
import com.natter.repository.user.UserInfoRepository;
import com.natter.repository.user.UserRepository;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

  private final UserInfoRepository userInfoRepository;
  private final UserRepository userRepository;
  private final UserFollowersFollowingRepository userFollowersFollowingRepository;

  private final UserDatabaseService userDatabaseService;

  private final UpdateFollowerService updateFollowerService;

  /**
   * Method to follow or unfollow a user if they exist
   *
   * @param authUserId               the authenticated user userIdToFollowOrUnfollow
   * @param userIdToFollowOrUnfollow the userIdToFollowOrUnfollow of the user to follow or unfollow
   * @param isFollow                 is this a follow or unfollow request
   * @return the response dto
   */
  public ResponseDto followOrUnfollowUserById(@NonNull final String authUserId,
                                              final String userIdToFollowOrUnfollow,
                                              final boolean isFollow) {
    ResponseDto response = new ResponseDto();
    try {
      if (userIdToFollowOrUnfollow == null) {
        response.setErrorMessages(Map.of(ErrorMessageUserEnum.USER_ID_NULL.getCode(),
            ErrorMessageUserEnum.USER_ID_NULL.getMessage()));
        response.setStatus(HttpStatus.BAD_REQUEST);
      } else {
        UserInfo user = userInfoRepository.findById(userIdToFollowOrUnfollow).orElseThrow();

        if (isFollow) {
          updateFollowerService.followUser(authUserId, user.getId());
          response.setUserMessages(Map.of(SuccessMessageUserEnum.FOLLOWED_USER.getCode(),
              SuccessMessageUserEnum.FOLLOWED_USER.getMessage()));
        } else {
          updateFollowerService.unfollowUser(authUserId, user.getId());
          response.setUserMessages(Map.of(SuccessMessageUserEnum.UNFOLLOWED_USER.getCode(),
              SuccessMessageUserEnum.UNFOLLOWED_USER.getMessage()));
        }
        response.setStatus(HttpStatus.OK);

      }
    } catch (NoSuchElementException e) {
      log.error("unable to find user info record for user id: " + userIdToFollowOrUnfollow + ", error: " + e.getMessage());
      response.setErrorMessages(Map.of(ErrorMessageUserEnum.USER_NOT_FOUND.getCode(),
          ErrorMessageUserEnum.USER_NOT_FOUND.getMessage()));
      response.setStatus(HttpStatus.NOT_FOUND);
    }

    return response;
  }

  /**
   * Method to get followers for a user id
   *
   * @param userId the user id
   * @return the list of followers wrapped around response list dto
   */
  public ResponseListDto<UserFollowersFollowing> getFollowersForUserId(final String userId) {
    ResponseListDto<UserFollowersFollowing> responseListDto = new ResponseListDto<>();
    try {
      User user = userRepository.findById(userId).orElseThrow();
      Set<String> followerIds = user.getFollowers();
      if (followerIds != null && !followerIds.isEmpty()) {
        responseListDto.setList(userFollowersFollowingRepository.findAllById(user.getFollowers()));
      }
      responseListDto.setStatus(HttpStatus.OK);
      responseListDto.setUserMessages(Map.of(SuccessMessageUserEnum.SUCCESS_GENERIC.getCode(),
          SuccessMessageUserEnum.SUCCESS_GENERIC.getMessage()));

    } catch (NoSuchElementException e) {
      log.error("unable to find user record for user id: " + userId + ", error: " + e.getMessage());
      responseListDto.setStatus(HttpStatus.NOT_FOUND);
      responseListDto.setErrorMessages(Map.of(ErrorMessageUserEnum.USER_NOT_FOUND.getCode(),
          ErrorMessageUserEnum.USER_NOT_FOUND.getMessage()));
    }

    return responseListDto;

  }

  /**
   * Method to get following for a user id
   *
   * @param userId the user id
   * @return the list of following wrapped around response list dto
   */
  public ResponseListDto<UserFollowersFollowing> getFollowingForUserId(String userId) {
    ResponseListDto<UserFollowersFollowing> responseListDto = new ResponseListDto<>();
    try {
      User user = userRepository.findById(userId).orElseThrow();
      Set<String> followingIds = user.getFollowing();
      if (followingIds != null && !followingIds.isEmpty()) {
        responseListDto.setList(userFollowersFollowingRepository.findAllById(user.getFollowing()));
      }
      responseListDto.setStatus(HttpStatus.OK);
      responseListDto.setUserMessages(Map.of(SuccessMessageUserEnum.SUCCESS_GENERIC.getCode(),
          SuccessMessageUserEnum.SUCCESS_GENERIC.getMessage()));

    } catch (NoSuchElementException e) {
      log.error("unable to find user record for user id: " + userId + ", error: " + e.getMessage());
      responseListDto.setStatus(HttpStatus.NOT_FOUND);
      responseListDto.setErrorMessages(Map.of(ErrorMessageUserEnum.USER_NOT_FOUND.getCode(),
          ErrorMessageUserEnum.USER_NOT_FOUND.getMessage()));
    }

    return responseListDto;
  }

  /**
   * Method to get a list of user ids that the user follows
   *
   * @param authId the authenticated user
   * @return the following ids in a list
   */
  public List<String> getFollowingIdsForUser(@NonNull final String authId) {
    return userDatabaseService.getFollowingForUser(authId);
  }
}
