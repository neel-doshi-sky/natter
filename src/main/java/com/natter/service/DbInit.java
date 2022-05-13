package com.natter.service;

import com.natter.model.natter.NatterOriginal;
import com.natter.repository.NatterOriginalRepository;
import com.natter.repository.UserRepository;
import com.natter.service.natter.NatterService;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DbInit {

  @Autowired
  private NatterOriginalRepository natterOriginalRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private NatterService natterService;

  @PostConstruct
  private void postConstruct() {
    NatterOriginal natterOriginal = NatterOriginal.builder().id(UUID.randomUUID().toString()).parentNatterId(null)
        .authorId("115826771724477311086").body("NATTER 1").timeCreated(
            LocalDateTime.now()).timeUpdated(LocalDateTime.now()).userReactions(new HashSet<>())
        .build();
    NatterOriginal natterOriginal2 = NatterOriginal.builder().id(UUID.randomUUID().toString()).parentNatterId(null)
        .authorId("115826771724477311086").body("NATTER 2").timeCreated(
            LocalDateTime.now()).timeUpdated(LocalDateTime.now()).userReactions(new HashSet<>())
        .build();
    NatterOriginal natterOriginal3 = NatterOriginal.builder().id(UUID.randomUUID().toString()).parentNatterId(null)
        .authorId("115826771724477311086").body("NATTER 3").timeCreated(
            LocalDateTime.now()).timeUpdated(LocalDateTime.now()).userReactions(new HashSet<>())
        .build();
    NatterOriginal natterOriginal4 = NatterOriginal.builder().id(UUID.randomUUID().toString()).parentNatterId(null)
        .authorId("115826771724477311086").body("NATTER 4").timeCreated(
            LocalDateTime.now()).timeUpdated(LocalDateTime.now()).userReactions(new HashSet<>())
        .build();
    NatterOriginal natterOriginal5 = NatterOriginal.builder().id(UUID.randomUUID().toString()).parentNatterId(null)
        .authorId("115826771724477311086").body("NATTER 5").timeCreated(
            LocalDateTime.now()).timeUpdated(LocalDateTime.now()).userReactions(new HashSet<>())
        .build();
    List<NatterOriginal>
        natterOriginalList = List.of(natterOriginal, natterOriginal2, natterOriginal3,
        natterOriginal4, natterOriginal5);
    natterOriginalRepository.saveAll(natterOriginalList);
  }
}
