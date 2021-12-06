package org.sonar.samples.openapi;

import org.junit.Test;
import org.sonar.api.rules.RuleType;
import org.sonar.api.server.debt.DebtRemediationFunction.Type;
import org.sonar.api.server.rule.RuleParamType;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.api.server.rule.RulesDefinition.Param;
import org.sonar.api.server.rule.RulesDefinition.Repository;
import org.sonar.api.server.rule.RulesDefinition.Rule;
import org.sonar.samples.openapi.checks.RulesList;

import static org.assertj.core.api.Assertions.assertThat;

public class MyOpenAPIRulesDefinitionTest {
  @Test
  public void test() {
    MyOpenAPIRulesDefinition rulesDefinition = new MyOpenAPIRulesDefinition();
    RulesDefinition.Context context = new RulesDefinition.Context();
    rulesDefinition.define(context);
    Repository repository = context.repository(MyOpenAPIRulesDefinition.REPOSITORY_KEY);

    assertThat(repository.name()).isEqualTo("MyCompany Custom Repository");
    assertThat(repository.language()).isEqualTo("openapi");
    assertThat(repository.rules()).hasSize(RulesList.getChecks().size());

    assertRuleProperties(repository);
    assertParameterProperties(repository);
    assertAllRuleParametersHaveDescription(repository);
  }

  private void assertParameterProperties(Repository repository) {
    Param max = repository.rule("DummyPath").param("segment");
    assertThat(max).isNotNull();
    assertThat(max.defaultValue()).isEqualTo("dummy");
    assertThat(max.description()).isEqualTo("Path segment that should start all paths, without leading /. For instance 'dummy'.");
    assertThat(max.type()).isEqualTo(RuleParamType.STRING);
  }

  private void assertRuleProperties(Repository repository) {
    Rule rule = repository.rule("DummyPath");
    assertThat(rule).isNotNull();
    assertThat(rule.name()).isEqualTo("Title of DummyPath");
    assertThat(rule.debtRemediationFunction().type()).isEqualTo(Type.CONSTANT_ISSUE);
    assertThat(rule.type()).isEqualTo(RuleType.CODE_SMELL);
  }

  private void assertAllRuleParametersHaveDescription(Repository repository) {
    for (Rule rule : repository.rules()) {
      for (Param param : rule.params()) {
        assertThat(param.description()).as("description for " + param.key()).isNotEmpty();
      }
    }
  }
}
