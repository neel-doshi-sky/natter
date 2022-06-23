package com.natter.functional.steps;

import static org.assertj.core.api.Assertions.assertThat;

import com.natter.functional.models.HttpRequest;
import com.natter.functional.util.Client;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PrivateSteps {

  private final HttpRequest httpRequest;

  private final Client client;

  @When("the client calls {string}")
  public void theClientCalls(String endpoint) {
    httpRequest.setResponse(client.sendGetRequest(endpoint));
  }

  @Then("the client receives status code of {int}")
  public void theClientReceivesStatusCodeOf(int status) {
    assertThat(httpRequest.getResponseStatusCode()).isEqualTo(status);
  }

  @And("the client receives a body of {string}")
  public void theClientReceivesABodyOf(String body) {
    assertThat(httpRequest.getResponseBody()).contains(body);
  }
}
