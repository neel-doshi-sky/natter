package com.natter.service.natter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.natter.enums.natter.ErrorMessageEnum;
import com.natter.exception.DatabaseErrorException;
import com.natter.model.natter.NatterByAuthor;
import com.natter.model.natter.NatterById;
import com.natter.model.natter.NatterCreateRequest;
import com.natter.model.natter.NatterListPrimaryKey;
import com.natter.repository.NatterByAuthorRepository;
import com.natter.repository.NatterByIdRepository;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NatterDatabaseServiceTest {

  @InjectMocks
  NatterDatabaseService natterDatabaseService;

  @Mock
  NatterByAuthorRepository natterByAuthorRepository;

  @Mock
  NatterByIdRepository natterByIdRepository;

  @Test
  public void returnCreatedNatter_WhenSuccessfulSaveToDatabase() throws DatabaseErrorException {
    NatterById createdNatterById = new NatterById();
    createdNatterById.setParentNatterId(null);
    createdNatterById.setAuthorId("testUserId");
    createdNatterById.setBody("This is a natter!");
    createdNatterById.setId("12323");
    createdNatterById.setDateCreated(LocalDateTime.now());
    createdNatterById.setDateUpdated(createdNatterById.getDateCreated());

    NatterListPrimaryKey natterListPrimaryKey = new NatterListPrimaryKey();
    natterListPrimaryKey.setTimeId("123");
    natterListPrimaryKey.setAuthorId("123");

    NatterByAuthor natterByAuthor = new NatterByAuthor();
    natterByAuthor.setId(natterListPrimaryKey);
    natterByAuthor.setBody("123 test");
    natterByAuthor.setCreated(LocalDateTime.now());

    when(natterByAuthorRepository.save(any())).thenReturn(natterByAuthor);
    when(natterByIdRepository.save(any())).thenReturn(createdNatterById);
    NatterById result = natterDatabaseService.saveNatter("123", new NatterCreateRequest(), "123");
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
    assertThrows(DatabaseErrorException.class, () -> natterDatabaseService.saveNatter("123", new NatterCreateRequest(), "123"));
  }

  @Test
  public void throwException_whenSaveToIdTableReturnsNoId(){
    NatterByAuthor nullId = new NatterByAuthor();
    when(natterByAuthorRepository.save(any())).thenReturn(nullId);
    assertThrows(DatabaseErrorException.class, () -> natterDatabaseService.saveNatter("123", new NatterCreateRequest(), "123"));
  }

}