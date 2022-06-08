package com.natter.service.natter;

import com.natter.model.natter.NatterByAuthor;
import java.time.LocalDateTime;
import java.util.List;

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
}
