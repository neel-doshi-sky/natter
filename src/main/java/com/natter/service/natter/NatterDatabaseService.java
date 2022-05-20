package com.natter.service.natter;

import com.natter.enums.natter.ErrorMessageEnum;
import com.natter.exception.DatabaseErrorException;
import com.natter.model.natter.NatterByAuthor;
import com.natter.model.natter.NatterById;
import com.natter.model.natter.NatterCreateRequest;
import com.natter.model.natter.NatterListPrimaryKey;
import com.natter.repository.NatterByAuthorRepository;
import com.natter.repository.NatterByIdRepository;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.NonNull;
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
   * @param timeId              the id of the natter
   * @param natterCreateRequest the natter create body
   * @param authorId            the id of the author
   * @return the created Natter
   * @throws DatabaseErrorException the database exception
   */
  @Transactional
  public NatterById saveNatter(final @NonNull String timeId,
                               @NonNull final NatterCreateRequest natterCreateRequest,
                               @NonNull final String authorId) throws DatabaseErrorException {

    NatterListPrimaryKey natterListPrimaryKey = new NatterListPrimaryKey();
    natterListPrimaryKey.setTimeId(timeId);
    natterListPrimaryKey.setAuthorId(authorId);

    NatterByAuthor natterByAuthor = new NatterByAuthor();
    natterByAuthor.setId(natterListPrimaryKey);
    natterByAuthor.setBody(natterCreateRequest.getBody());
    natterByAuthor.setCreated(LocalDateTime.now());

    NatterByAuthor natterByAuthorCreated = natterByAuthorRepository.save(natterByAuthor);
    if (natterByAuthorCreated.getId() == null) {
      throw new DatabaseErrorException(ErrorMessageEnum.UNABLE_TO_SAVE_RECORD);
    }

    NatterById natterById = new NatterById();
    natterById.setId(timeId);
    natterById.setBody(natterCreateRequest.getBody());
    LocalDateTime now = LocalDateTime.now();
    natterById.setDateCreated(now);
    natterById.setDateUpdated(now);
    natterById.setParentNatterId(null);
    NatterById createdNatter = natterByIdRepository.save(natterById);
    if (createdNatter.getId() == null) {
      throw new DatabaseErrorException(ErrorMessageEnum.UNABLE_TO_SAVE_RECORD);
    }
    return createdNatter;
  }

  /**
   * Method to delete natters from all required tables
   *
   * @param idToDelete the id to delete
   * @param authorId   the id of the user
   */
  public void deleteNatter(@NonNull final String idToDelete, @NonNull final String authorId) {
    natterByIdRepository.deleteById(idToDelete);
    NatterListPrimaryKey key = new NatterListPrimaryKey();
    key.setTimeId(idToDelete);
    key.setAuthorId(authorId);
    natterByAuthorRepository.deleteById(key);
  }
}
