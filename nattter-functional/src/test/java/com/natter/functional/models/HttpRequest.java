package com.natter.functional.models;

import io.restassured.http.Headers;
import io.restassured.response.Response;
import org.springframework.stereotype.Component;

@Component
public class HttpRequest {

  private Response response;

  public Response getResponse() {
    return response;
  }

  public void setResponse(Response response) {
    this.response = response;
  }

  public String getResponseBody() {
    return this.response.getBody().asString();
  }

  public int getResponseStatusCode() {
    return this.response.getStatusCode();
  }

  public Headers getResponseHeaders() {
    return this.response.getHeaders();
  }

  public String getResponseHeaderValue(String headerName) {
    return this.response.getHeader(headerName);
  }
}
