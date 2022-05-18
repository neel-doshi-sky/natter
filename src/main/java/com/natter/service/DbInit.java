package com.natter.service;

import com.natter.model.natter.NatterByAuthor;
import com.natter.model.natter.NatterById;
import com.natter.model.natter.NatterCreateRequest;
import com.natter.repository.NatterByAuthorRepository;
import com.natter.repository.NatterByIdRepository;
import com.natter.repository.UserRepository;
import com.natter.service.natter.NatterService;
import java.util.List;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DbInit {

  @Autowired
  NatterService natterService;

  @PostConstruct
  private void postConstruct() {

    NatterCreateRequest natterCreateRequest = new NatterCreateRequest();
    natterCreateRequest.setParentNatterId(null);
    natterCreateRequest.setBody("Test natter 1");
    NatterCreateRequest natterCreateRequest1 = new NatterCreateRequest();
    natterCreateRequest.setParentNatterId(null);
    natterCreateRequest.setBody("Test natter 2");
    NatterCreateRequest natterCreateRequest2 = new NatterCreateRequest();
    natterCreateRequest.setParentNatterId(null);
    natterCreateRequest.setBody("Test natter 3");
    NatterCreateRequest natterCreateRequest3 = new NatterCreateRequest();
    natterCreateRequest.setParentNatterId(null);
    natterCreateRequest.setBody("Test natter 4");

    List<NatterCreateRequest> natterCreateRequests = List.of(natterCreateRequest1, natterCreateRequest2, natterCreateRequest3, natterCreateRequest);
    natterCreateRequests.forEach(natterCreateRequestObject ->{
      natterService.create(natterCreateRequestObject, "115826771724477311086");
    } );


  }
}
