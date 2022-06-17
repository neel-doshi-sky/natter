package com.natter.functional.steps;

import io.cucumber.java.en.When;

public class PrivateSteps {
  @When("the client calls {string}")
  public void theClientCalls(String endpoint) {

  }
}
