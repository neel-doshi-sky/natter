package com.natter.service.natter;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import com.natter.enums.natter.ErrorMessageNatterEnum;
import com.natter.exception.DatabaseErrorException;
import com.natter.model.natter.NatterByAuthor;
import com.natter.model.natter.NatterByAuthorPrimaryKey;
import com.natter.model.natter.NatterById;
import com.natter.model.natter.NatterCreateRequest;
import com.natter.model.natter.NatterUpdateRequest;
import com.natter.repository.natter.NatterByAuthorRepository;
import com.natter.repository.natter.NatterByIdRepository;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class NatterDatabaseService {
  private final NatterByIdRepository natterByIdRepository;
  private final NatterByAuthorRepository natterByAuthorRepository;


  /**
   * Method to save natters to all relevant tables
   *
   * @param id                  the id of the natter
   * @param natterCreateRequest the natter create body
   * @param author            the id of the author
   * @return the created Natter
   * @throws DatabaseErrorException the database exception
   */
  @Transactional
  public NatterById create(@NonNull final String id,
                           @NonNull final NatterCreateRequest natterCreateRequest,
                           @NonNull final OAuth2User author) throws DatabaseErrorException {

    String authorid = author.getAttribute("sub");
    LocalDateTime now = LocalDateTime.now();

    NatterByAuthorPrimaryKey natterByAuthorPrimaryKey = new NatterByAuthorPrimaryKey();
    natterByAuthorPrimaryKey.setId(id);
    natterByAuthorPrimaryKey.setAuthorId(authorid);

    NatterByAuthor natterByAuthor = new NatterByAuthor();
    natterByAuthor.setId(natterByAuthorPrimaryKey);
    natterByAuthor.setBody(natterCreateRequest.getBody());
    natterByAuthor.setDateCreated(now);
    natterByAuthor.setDateUpdated(natterByAuthor.getDateCreated());
    natterByAuthor.setAuthorName(author.getAttribute("name"));
    natterByAuthor.setParentAuthorId(natterCreateRequest.getParentNatterId());

    NatterByAuthor natterByAuthorCreated = natterByAuthorRepository.save(natterByAuthor);
    if (natterByAuthorCreated.getId() == null) {
      throw new DatabaseErrorException(ErrorMessageNatterEnum.UNABLE_TO_SAVE_RECORD);
    }

    NatterById natterById = new NatterById();
    natterById.setId(id);
    natterById.setBody(natterCreateRequest.getBody());
    natterById.setDateCreated(now);
    natterById.setDateUpdated(natterById.getDateCreated());
    natterById.setAuthorId(authorid);
    natterById.setAuthorName(author.getAttribute("name"));
    natterById.setParentNatterId(natterCreateRequest.getParentNatterId());
    NatterById createdNatter = natterByIdRepository.save(natterById);
    if (createdNatter.getId() == null) {
      throw new DatabaseErrorException(ErrorMessageNatterEnum.UNABLE_TO_SAVE_RECORD);
    }
    return createdNatter;
  }

  public void updateNatterAfterComment(NatterById natter) throws DatabaseErrorException {
    natter.setDateUpdated(LocalDateTime.now());

    NatterByAuthorPrimaryKey natterByAuthorPrimaryKey = new NatterByAuthorPrimaryKey();
    natterByAuthorPrimaryKey.setId(natter.getId());
    natterByAuthorPrimaryKey.setAuthorId(natter.getAuthorId());

    NatterByAuthor natterByAuthor = new NatterByAuthor();
    natterByAuthor.setId(natterByAuthorPrimaryKey);
    natterByAuthor.setBody(natter.getBody());
    natterByAuthor.setDateUpdated(natter.getDateUpdated());
    natterByAuthor.setDateCreated(natter.getDateCreated());
    natterByAuthor.setCommentCount(natter.getComments().size());
    natterByAuthor.setAuthorName(natter.getAuthorName());
    natterByAuthor.setParentAuthorId(natter.getParentNatterId());

    NatterByAuthor natterByAuthorCreated = natterByAuthorRepository.save(natterByAuthor);
    if (natterByAuthorCreated.getId() == null) {
      throw new DatabaseErrorException(ErrorMessageNatterEnum.UNABLE_TO_SAVE_RECORD);
    }
    natterByIdRepository.save(natter);

  }

  /**
   * Method to delete natters from all required tables
   *
   * @param idToDelete the id to delete
   * @param authorId   the id of the user
   */
  public void delete(@NonNull final String idToDelete, @NonNull final String authorId) {
    natterByIdRepository.deleteById(idToDelete);
    NatterByAuthorPrimaryKey key = new NatterByAuthorPrimaryKey();
    key.setId(idToDelete);
    key.setAuthorId(authorId);
    natterByAuthorRepository.deleteById(key);
  }

  /**
   * Method to update natters from the relevant tables
   *
   * @param updateRequest the update request
   * @param authorId the authorId
   */
  public void update(@NonNull final NatterUpdateRequest updateRequest,
                     @NonNull final String authorId) {

    natterByIdRepository.updateNatter(updateRequest.getBody(), updateRequest.getId());
    natterByAuthorRepository.updateNatter(updateRequest.getBody(), updateRequest.getId(), authorId);
  }

  public NatterById addComment(NatterCreateRequest commentRequest,
                               OAuth2User authId) throws DatabaseErrorException {
    return create(Uuids.timeBased().toString(), commentRequest, authId);

  }
}
