package com.natter.service;

import com.natter.model.natter.Natter;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public class NatterServiceTestHelper {

  public List<Natter> getListOfNatters(){
    Natter natter = Natter.builder().id(UUID.randomUUID().toString()).parentNatterId(null)
        .authorId("115826771724477311086").body("NATTER 1").timeCreated(
            LocalDateTime.now()).timeUpdated(LocalDateTime.now()).userReactions(new HashSet<>())
        .build();
    Natter natter2 = Natter.builder().id(UUID.randomUUID().toString()).parentNatterId(null)
        .authorId("115826771724477311086").body("NATTER 2").timeCreated(
            LocalDateTime.now()).timeUpdated(LocalDateTime.now()).userReactions(new HashSet<>())
        .build();
    Natter natter3 = Natter.builder().id(UUID.randomUUID().toString()).parentNatterId(null)
        .authorId("115826771724477311086").body("NATTER 3").timeCreated(
            LocalDateTime.now()).timeUpdated(LocalDateTime.now()).userReactions(new HashSet<>())
        .build();
    Natter natter4 = Natter.builder().id(UUID.randomUUID().toString()).parentNatterId(null)
        .authorId("115826771724477311086").body("NATTER 4").timeCreated(
            LocalDateTime.now()).timeUpdated(LocalDateTime.now()).userReactions(new HashSet<>())
        .build();
    Natter natter5 = Natter.builder().id(UUID.randomUUID().toString()).parentNatterId(null)
        .authorId("115826771724477311086").body("NATTER 5").timeCreated(
            LocalDateTime.now()).timeUpdated(LocalDateTime.now()).userReactions(new HashSet<>())
        .build();
    return List.of(natter, natter2, natter3, natter4, natter5);
  }
}
