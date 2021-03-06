package io.syndesis.qe.validation;

import io.syndesis.common.model.metrics.IntegrationMetricsSummary;
import io.syndesis.qe.utils.IntegrationUtils;
import io.syndesis.qe.utils.OpenShiftUtils;
import io.syndesis.qe.utils.TestUtils;
import io.syndesis.server.endpoint.v1.handler.activity.Activity;

import org.assertj.core.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

/**
 * For monitoring of metrics and logs related to integrations.
 */
public class MonitoringValidationSteps {
    @Autowired
    private IntegrationUtils integrationUtils;

    @Then("validate that number of all messages through integration {string} is greater than {int}, period in ms: {int}")
    public void validateThatNumberOfAllMessagesOfIntegrationIsGreaterThanPeriodInMs(String integrationName, int nr, int ms) {
        TestUtils.sleepIgnoreInterrupt(ms);

        IntegrationMetricsSummary summary = integrationUtils.getIntegrationMetrics(integrationName);
        Assertions.assertThat(summary.getMessages()).isGreaterThan(nr);
    }

    @When("^wait until integration (.*) processed at least (\\w+) messages?")
    public void waitForMessage(String integrationName, int numberOfMessages) {
        integrationUtils.waitForMessage(integrationName, numberOfMessages, 60);
    }

    @Then("validate that log of integration {string} has been created and contains {string}")
    public void validateThatLogOfIntegrationHasBeenCreatedPeriodInMs(String integrationName, String contains) {

        List<Activity> activityIntegrationLogs = integrationUtils.getAllIntegrationActivities(integrationName);
        String podName = activityIntegrationLogs.get(0).getPod();
        Assertions.assertThat(OpenShiftUtils.getPodLogs(podName)).isNotEmpty().contains(contains);
    }
}
