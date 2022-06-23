package com.natter.functional.util;


import static io.restassured.RestAssured.given;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class Client {

  @Value("${service.base.url}")
  private String baseURL;

  RequestSpecBuilder specification = new RequestSpecBuilder();


  public void resetSpecification() {
    specification = new RequestSpecBuilder();
    specification.setBaseUri(baseURL);
    log.info("BASE URL : " + baseURL);
  }

  public Response sendGetRequest(String path) {
    RequestSpecification request = specification.build();
    log.info("Getting from this url: -->" + baseURL + path);
    return given(request).get(path);
  }

  public void addHeader(String key, String value) {
    if (value != null) {
      specification.addHeader(key, value);
    }
  }
}
