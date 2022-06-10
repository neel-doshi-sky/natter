package com.natter.service.natter;

import com.natter.model.natter.NatterByAuthor;
import com.natter.model.natter.NatterById;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class NatterServiceTestHelper {

  public List<NatterByAuthor> getListOfNatters(){

    NatterByAuthor natterByAuthor = new NatterByAuthor();
    natterByAuthor.setDateUpdated(LocalDateTime.now());
    NatterByAuthor natterByAuthor1 = new NatterByAuthor();
    natterByAuthor1.setDateUpdated(LocalDateTime.now());
    NatterByAuthor natterByAuthor2 = new NatterByAuthor();
    natterByAuthor2.setDateUpdated(LocalDateTime.now());
    NatterByAuthor natterByAuthor3 = new NatterByAuthor();
    natterByAuthor3.setDateUpdated(LocalDateTime.now());
    NatterByAuthor natterByAuthor4 = new NatterByAuthor();
    natterByAuthor4.setDateUpdated(LocalDateTime.now());
    NatterByAuthor natterByAuthor5 = new NatterByAuthor();
    natterByAuthor5.setDateUpdated(LocalDateTime.now());
    return List.of(natterByAuthor, natterByAuthor2,natterByAuthor1, natterByAuthor3, natterByAuthor5, natterByAuthor4);
  }

  public NatterById getValidNatterByIdWithComments(){
    NatterById valid = new NatterById();
    valid.setParentNatterId(null);
    valid.setAuthorId("testUserId");
    valid.setBody("This is a natter!");
    valid.setId("12323");
    valid.setDateCreated(LocalDateTime.now());
    valid.setDateUpdated(valid.getDateCreated());
    valid.setComments(List.of("123, 234"));
    return valid;
  }
  public NatterById getValidNatterByIdWithoutComments(){
    NatterById valid = new NatterById();
    valid.setParentNatterId(null);
    valid.setAuthorId("testUserId");
    valid.setBody("This is a natter!");
    valid.setId("12323");
    valid.setDateCreated(LocalDateTime.now());
    valid.setDateUpdated(valid.getDateCreated());
    return valid;
  }

  public List<NatterById> getCommentsForNatter(String id){
    NatterById valid = new NatterById();
    valid.setParentNatterId(id);
    valid.setAuthorId("testUserId");
    valid.setBody("This is a natter!");
    valid.setId(UUID.randomUUID().toString());
    valid.setDateCreated(LocalDateTime.now());
    valid.setDateUpdated(valid.getDateCreated());
    NatterById valid2 = new NatterById();
    valid2.setParentNatterId(id);
    valid2.setAuthorId("testUserId");
    valid2.setBody("This is a natter!");
    valid2.setId(UUID.randomUUID().toString());
    valid2.setDateCreated(LocalDateTime.now());
    valid2.setDateUpdated(valid2.getDateCreated());
    return List.of(valid, valid2);

  }
}