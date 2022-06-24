package com.natter.dao;

import com.datastax.oss.driver.api.core.AllNodesFailedException;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.servererrors.QueryExecutionException;
import com.datastax.oss.driver.api.core.servererrors.QueryValidationException;
import com.datastax.oss.driver.api.core.type.reflect.GenericType;
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
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

@Slf4j
@Service
@AllArgsConstructor
public class NatterDao {
  private final NatterByIdRepository natterByIdRepository;
  private final NatterByAuthorRepository natterByAuthorRepository;

  private final CqlSession session;


  /**
   * Method to save natters to all relevant tables
   *
   * @param id                  the id of the natter
   * @param natterCreateRequest the natter create body
   * @param author              the id of the author
   * @return the created Natter
   * @throws DatabaseErrorException the database exception
   */
  @Transactional
  public NatterById create(@NonNull final String id,
                           @NonNull final NatterCreateRequest natterCreateRequest,
                           @NonNull final OAuth2User author) throws DatabaseErrorException {

    String authorId = author.getAttribute("sub");
    LocalDateTime now = LocalDateTime.now();

    NatterByAuthorPrimaryKey natterByAuthorPrimaryKey = new NatterByAuthorPrimaryKey();
    natterByAuthorPrimaryKey.setId(id);
    natterByAuthorPrimaryKey.setAuthorId(authorId);

    NatterByAuthor natterByAuthor = new NatterByAuthor();
    natterByAuthor.setId(natterByAuthorPrimaryKey);
    natterByAuthor.setBody(natterCreateRequest.getBody());
    natterByAuthor.setDateCreated(now);
    natterByAuthor.setDateUpdated(natterByAuthor.getDateCreated());
    natterByAuthor.setAuthorName(author.getAttribute("name"));
    natterByAuthor.setParentNatterId(natterCreateRequest.getParentNatterId() == null ? "" :
        natterCreateRequest.getParentNatterId());

    try {
      NatterByAuthor natterByAuthorCreated = natterByAuthorRepository.save(natterByAuthor);
      if (natterByAuthorCreated.getId() == null) {
        log.error(ErrorMessageNatterEnum.DATABASE_ERROR.getMessage() + " natter id: " + id +
            " , natterRequestBody: " +
            natterCreateRequest);
        throw new DatabaseErrorException(
            "unable to create natter by id for natter request: " + natterCreateRequest);
      }
    } catch (IllegalArgumentException e) {
      log.error("error creating natterby id with exception: " + e + " for natter body: " +
          natterCreateRequest);
      throw new DatabaseErrorException(e.getMessage());
    }


    NatterById natterById = new NatterById();
    natterById.setId(id);
    natterById.setBody(natterCreateRequest.getBody());
    natterById.setDateCreated(now);
    natterById.setDateUpdated(natterById.getDateCreated());
    natterById.setAuthorId(authorId);
    natterById.setAuthorName(author.getAttribute("name"));
    natterById.setParentNatterId(natterCreateRequest.getParentNatterId() == null ? "" :
        natterCreateRequest.getParentNatterId());
    try {
      NatterById createdNatter = natterByIdRepository.save(natterById);
      if (createdNatter.getId() == null) {
        log.error(ErrorMessageNatterEnum.DATABASE_ERROR.getMessage() + " natter id: " + id +
            " , natterRequestBody: " +
            natterCreateRequest);
        throw new DatabaseErrorException(
            "unable to create natter by author for natter request: " + natterCreateRequest);
      }
      return createdNatter;
    } catch (IllegalArgumentException e) {
      log.error("error creating natter by author for natter request: " + natterCreateRequest +
          " with error: " + e);
      throw new DatabaseErrorException(e.getMessage());
    }

  }

  public void updateNatterAfterComment(@NonNull final NatterById natter) throws DatabaseErrorException {
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
    natterByAuthor.setParentNatterId(natter.getParentNatterId());

    try {
      NatterByAuthor natterByAuthorCreated = natterByAuthorRepository.save(natterByAuthor);
      if (natterByAuthorCreated.getId() == null) {
        log.error(
            ErrorMessageNatterEnum.DATABASE_ERROR.getMessage() + " natter id: " + natter.getId() +
                " , natter: " +
                natter);
        throw new DatabaseErrorException(
            "error updating natter by author after comment for natter: " + natter);
      }
      NatterById natterById = natterByIdRepository.save(natter);
      if (natterById.getId() == null) {
        log.error(
            ErrorMessageNatterEnum.DATABASE_ERROR.getMessage() + " natter id: " + natter.getId() +
                " , natter: " +
                natter);
        throw new DatabaseErrorException(
            "error updating natter by id after comment for natter: " + natter);
      }
    } catch (IllegalArgumentException e) {
      log.error("error updating natter after comment for natter: " + natter + " with error: " + e);
      throw new DatabaseErrorException(e.getMessage());
    }

  }

  /**
   * Method to delete natters from all required tables
   *
   * @param natterById the id to delete
   */
  public boolean delete(@NonNull final NatterById natterById) throws DatabaseErrorException {
    NatterByAuthorPrimaryKey key =
        new NatterByAuthorPrimaryKey(natterById.getAuthorId(), natterById.getId());
    if (!natterById.getParentNatterId().isEmpty()) {
      deleteCommentAndUpdateCommentCount(natterById);
    }
    try {
      natterByIdRepository.deleteById(natterById.getId());
      if (!natterById.getComments().isEmpty()) {
        List<NatterById> commentsList = natterByIdRepository.findAllById(natterById.getComments());
        natterByIdRepository.deleteAllById(natterById.getComments());
        List<NatterByAuthorPrimaryKey> commentsToDelete = new ArrayList<>();
        for (NatterById comment : commentsList) {
          commentsToDelete.add(
              new NatterByAuthorPrimaryKey(comment.getAuthorId(), comment.getId()));
        }
        natterByAuthorRepository.deleteAllById(commentsToDelete);
      }
      natterByAuthorRepository.deleteById(key);

      return true;

    } catch (IllegalArgumentException e) {
      log.error("error deleting natter with id: " + natterById.getId() + " with error: " + e);
      throw new DatabaseErrorException(e.getMessage());
    }
  }

  /**
   * Method to update natters from the relevant tables
   *
   * @param updateRequest the update request
   * @param authorId      the authorId
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

  /**
   * Method to delete a comment and update the comment count in other tables
   *
   * @param natterById the natter by id to delete
   */
  private void deleteCommentAndUpdateCommentCount(@NonNull final NatterById natterById)
      throws DatabaseErrorException {
    try {
      NatterById parent =
          natterByIdRepository.findById(natterById.getParentNatterId()).orElseThrow();
      if (parent.getComments() != null && parent.getComments().contains(natterById.getId())) {
        parent.getComments().remove(natterById.getId());
        natterByIdRepository.save(parent);
      }
      NatterByAuthor parentByAuthor =
          natterByAuthorRepository.findById(new NatterByAuthorPrimaryKey(parent.getAuthorId(),
              parent.getId())).orElseThrow();
      parentByAuthor.setCommentCount(parentByAuthor.getCommentCount() - 1);
      natterByAuthorRepository.save(parentByAuthor);
    } catch (NoSuchElementException e) {
      log.error("Unable to find comment with id :" + natterById.getId() + ", error: " + e);
    } catch (IllegalArgumentException e) {
      log.error(
          "Error saving natter after delete comment and update comment count with exception: " + e);
      throw new DatabaseErrorException(e.getMessage());
    }
  }

  /**
   * Method to call the database with a query to get all natters for list of following ids
   *
   * @param followingIds the user ids to get the natters for
   * @return the natter list
   */
  public List<NatterByAuthor> getAllNattersForFollowing(@NonNull final Set<String> followingIds)
      throws DatabaseErrorException {

    List<NatterByAuthor> natterByAuthorList = new ArrayList<>();
    try {
      String markers = StringUtils.repeat("?,", followingIds.size() - 1);
      final String query = "select * from natters_by_author where author_id in (" + markers + " ?)";
      PreparedStatement prepared = session.prepare(query);
      BoundStatement bound = prepared.bind(followingIds.toArray()).setIdempotent(true);
      List<Row> rows = session.execute(bound).all();
      for (Row row : rows) {
        natterByAuthorList.add(parseRowIntoNatterByAuthorObject(row));

      }

    } catch (AllNodesFailedException | QueryExecutionException | QueryValidationException e) {
      log.error("error getting natters for following with message: " + e);
      throw new DatabaseErrorException(e.getMessage());
    }

    return natterByAuthorList;
  }

  /**
   * Method to parse the row brought back from the db call into a NatterByAuthor object to return
   *
   * @param row the row of data
   * @return the row parsed into a NatterByAuthor object
   */
  private NatterByAuthor parseRowIntoNatterByAuthorObject(@NonNull final Row row) {

    String author_id = row.get("author_id", GenericType.STRING);
    String id = row.get("id", GenericType.STRING);
    String author_name = row.get("author_name", GenericType.STRING);
    String body = row.get("body", GenericType.STRING);
    int comment_count = row.getInt("comment_count");
    Instant date_created = row.get("date_created", Instant.class);
    Instant date_updated = row.getInstant("date_updated");
    int likes = row.getInt("likes");
    String parent_natter_id = row.getString("parent_natter_id");
    List<String> user_likes = row.getList("user_likes", String.class);
    return new NatterByAuthor(new NatterByAuthorPrimaryKey(author_id, id), body,
        LocalDateTime.ofInstant(date_created, ZoneId.systemDefault()),
        LocalDateTime.ofInstant(date_updated, ZoneId.systemDefault()), comment_count, author_name,
        parent_natter_id, likes, user_likes);
  }
}
