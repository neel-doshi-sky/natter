package com.natter.service.natter;

import com.natter.model.natter.NatterByAuthor;
import java.util.List;

public class NatterServiceTestHelper {

  public List<NatterByAuthor> getListOfNatters(){

    NatterByAuthor natterByAuthor = new NatterByAuthor();
    NatterByAuthor natterByAuthor1 = new NatterByAuthor();
    NatterByAuthor natterByAuthor2 = new NatterByAuthor();
    NatterByAuthor natterByAuthor3 = new NatterByAuthor();
    NatterByAuthor natterByAuthor4 = new NatterByAuthor();
    NatterByAuthor natterByAuthor5 = new NatterByAuthor();
    return List.of(natterByAuthor, natterByAuthor2,natterByAuthor1, natterByAuthor3, natterByAuthor5, natterByAuthor4);
  }
}
