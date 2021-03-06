package com.natter.dao;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.natter.exception.DatabaseErrorException;
import com.natter.model.natter.NatterByAuthor;
import com.natter.model.natter.NatterByAuthorPrimaryKey;
import com.natter.model.natter.NatterById;
import com.natter.model.natter.NatterCreateRequest;
import com.natter.repository.natter.NatterByAuthorRepository;
import com.natter.repository.natter.NatterByIdRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

@ExtendWith(MockitoExtension.class)
class NatterDaoTest {

  @InjectMocks
  NatterDao natterDao;

  @Mock
  NatterByAuthorRepository natterByAuthorRepository;

  @Mock
  NatterByIdRepository natterByIdRepository;

  OAuth2User
      oAuth2User = new DefaultOAuth2User(new ArrayList<>(), Map.of("sub","115826771724477311086", "name", "Neel Doshi"), "name");

  @Test
  public void returnCreatedNatter_WhenSuccessfulSaveToDatabase() throws DatabaseErrorException {
    NatterById createdNatterById = new NatterById();
    createdNatterById.setParentNatterId(null);
    createdNatterById.setAuthorId("testUserId");
    createdNatterById.setBody("This is a natter!");
    createdNatterById.setId("12323");
    createdNatterById.setDateCreated(LocalDateTime.now());
    createdNatterById.setDateUpdated(createdNatterById.getDateCreated());

    NatterByAuthorPrimaryKey natterByAuthorPrimaryKey = new NatterByAuthorPrimaryKey();
    natterByAuthorPrimaryKey.setId("123");
    natterByAuthorPrimaryKey.setAuthorId("123");

    NatterByAuthor natterByAuthor = new NatterByAuthor();
    natterByAuthor.setId(natterByAuthorPrimaryKey);
    natterByAuthor.setBody("123 test");
    natterByAuthor.setDateCreated(LocalDateTime.now());

    when(natterByAuthorRepository.save(any())).thenReturn(natterByAuthor);
    when(natterByIdRepository.save(any())).thenReturn(createdNatterById);
    NatterById result = natterDao.create("123", new NatterCreateRequest(), oAuth2User);
    assertAll(
        () -> assertNotNull(result),
        () -> assertEquals(createdNatterById.getId(), result.getId())
    );
    verify(natterByIdRepository, times(1)).save(any());
    verify(natterByAuthorRepository, times(1)).save(any());

  }

  @Test
  public void throwException_whenSaveToAuthorTableReturnsNoId(){
    NatterByAuthor nullId = new NatterByAuthor();
    when(natterByAuthorRepository.save(any())).thenReturn(nullId);
    assertThrows(DatabaseErrorException.class,
        () -> natterDao.create("123", new NatterCreateRequest(), oAuth2User));
  }

  @Test
  public void throwException_whenSaveToIdTableReturnsNoId(){
    NatterByAuthor nullId = new NatterByAuthor();
    when(natterByAuthorRepository.save(any())).thenReturn(nullId);
    assertThrows(DatabaseErrorException.class,
        () -> natterDao.create("123", new NatterCreateRequest(), oAuth2User));
  }

  @Test
  public void throwException_whenUpdateNatterAfterComment_ReturnsNoIdOnSave(){
    NatterById createdNatterById = new NatterById();
    createdNatterById.setParentNatterId(null);
    createdNatterById.setAuthorId("testUserId");
    createdNatterById.setBody("This is a natter!");
    createdNatterById.setId("12323");
    createdNatterById.setDateCreated(LocalDateTime.now());
    createdNatterById.setDateUpdated(createdNatterById.getDateCreated());
    when(natterByAuthorRepository.save(any())).thenReturn(new NatterByAuthor());
    assertThrows(DatabaseErrorException.class,
        () -> natterDao.updateNatterAfterComment(createdNatterById));
  }

  @Test
  public void updateNatterTables_whenValidNatterPassed_onUpdateAfterComment()
      throws DatabaseErrorException {
    NatterById createdNatterById = new NatterById();
    createdNatterById.setParentNatterId(null);
    createdNatterById.setAuthorId("testUserId");
    createdNatterById.setBody("This is a natter!");
    createdNatterById.setId("12323");
    createdNatterById.setDateCreated(LocalDateTime.now());
    createdNatterById.setDateUpdated(createdNatterById.getDateCreated());
    NatterByAuthor natterByAuthor = new NatterByAuthor();
    natterByAuthor.setId(new NatterByAuthorPrimaryKey());
    when(natterByAuthorRepository.save(any())).thenReturn(natterByAuthor);
    when(natterByIdRepository.save(any())).thenReturn(createdNatterById);
    natterDao.updateNatterAfterComment(createdNatterById);
    verify(natterByAuthorRepository).save(any());
    verify(natterByIdRepository).save(any());
  }

}