package com.natter.service.user;

import com.natter.dao.UserDao;
import com.natter.dto.GetResponseDto;
import com.natter.dto.ResponseDto;
import com.natter.dto.ResponseListDto;
import com.natter.enums.user.ErrorMessageUserEnum;
import com.natter.enums.user.SuccessMessageUserEnum;
import com.natter.exception.DatabaseErrorException;
import com.natter.model.template.UserToDisplay;
import com.natter.model.user.User;
import com.natter.model.user.UserFollowersFollowing;
import com.natter.model.user.UserInfo;
import com.natter.repository.user.UserFollowersFollowingRepository;
import com.natter.repository.user.UserInfoRepository;
import com.natter.repository.user.UserRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

  private final UserInfoRepository userInfoRepository;
  private final UserRepository userRepository;
  private final UserFollowersFollowingRepository userFollowersFollowingRepository;

  private final UserDao userDao;

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
      log.error(
          "unable to find user info record for user id: " + userIdToFollowOrUnfollow + ", error: " +
              e.getMessage());
      response.setErrorMessages(Map.of(ErrorMessageUserEnum.USER_NOT_FOUND.getCode(),
          ErrorMessageUserEnum.USER_NOT_FOUND.getMessage()));
      response.setStatus(HttpStatus.NOT_FOUND);
    } catch (DatabaseErrorException e) {
      log.error(
          "error " + (isFollow ? "following " : "unfollowing ") + "user with database exception");
      response.setErrorMessages(Map.of(ErrorMessageUserEnum.DATABASE_ERROR.getCode(),
          ErrorMessageUserEnum.DATABASE_ERROR.getMessage()));
      response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    return response;
  }

  /**
   * Method to get followers for a user id
   *
   * @param userId the user id
   * @return the list of followers wrapped around response list dto
   */
  public ResponseListDto<UserFollowersFollowing> getFollowersForUserId(
      @NonNull final String userId) {
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
    return userDao.getFollowingIdsForUser(authId);
  }

  /**
   * Method to get a user by id
   *
   * @param principal the authenticated user
   * @param id        the id of the user
   * @return the GetResponseDto containing the result of the operation
   */
  public GetResponseDto<UserToDisplay> getUserById(@NonNull final OAuth2User principal,
                                                   @NonNull final String id) {
    GetResponseDto<UserToDisplay> responseDto = new GetResponseDto<>();
    try {
      User user = userRepository.findById(id).orElseThrow();
      if (user.getFollowers() == null) {
        user.setFollowers(new HashSet<>());
      }
      if (user.getFollowing() == null) {
        user.setFollowing(new HashSet<>());
      }
      UserToDisplay userToDisplay =
          new UserToDisplay(user.getId(), user.getFirstName(), user.getLastName(),
              "Followers: " + (user.getFollowers() != null ? user.getFollowers().size() : "0"),
              "Following: " + (user.getFollowing() != null ? user.getFollowing().size() : 0),
              user.getEmail(), user.getId().equals(principal.getName()),
              user.getFollowers().contains(principal.getName()),
              user.getFollowing().contains(principal.getName()));

      responseDto.setResponseObject(userToDisplay);
      responseDto.setStatus(HttpStatus.OK);
      responseDto.setUserMessages(Map.of(SuccessMessageUserEnum.SUCCESS_GENERIC.getCode(),
          SuccessMessageUserEnum.SUCCESS_GENERIC.getMessage()));

    } catch (NoSuchElementException e) {
      log.error("error fetching user by id with error: " + e);
      responseDto.setStatus(HttpStatus.NOT_FOUND);
      responseDto.setErrorMessages(Map.of(ErrorMessageUserEnum.USER_NOT_FOUND.getCode(),
          ErrorMessageUserEnum.USER_NOT_FOUND.getMessage()));
    }
    return responseDto;

  }
}
