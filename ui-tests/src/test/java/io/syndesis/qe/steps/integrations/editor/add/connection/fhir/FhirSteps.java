package io.syndesis.qe.steps.integrations.editor.add.connection.fhir;

import io.syndesis.qe.pages.integrations.editor.add.connection.actions.fhir.Create;
import io.syndesis.qe.resource.impl.FHIR;

import java.util.ArrayList;
import java.util.List;

import cucumber.api.java.en.Then;
import io.cucumber.datatable.DataTable;

public class FhirSteps {

    private Create create = new Create();

    @Then("^add FHIR account$")
    public void addFhirAccount() {
        FHIR.addAccount();
    }

    @Then("^select resource type \"([^\"]*)\"$")
    public void selectResourceType(String type) {
        create.selectResourceType(type);
    }

    @Then("^select contained resource types$")
    public void selectContainedResourceTypes(DataTable data) {
        List<String> types = new ArrayList<>();
        data.cells().stream().forEach(row -> types.add(row.get(0)));
        create.selectContainedResourceTypes(types);
    }
}
