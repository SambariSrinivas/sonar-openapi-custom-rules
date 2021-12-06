package org.sonar.samples.openapi;

import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.samples.openapi.checks.RulesList;
import org.sonarsource.analyzer.commons.RuleMetadataLoader;

/**
 * Declare rule metadata in server repository of rules.
 * This allows to list the rules in the page "Rules".
 */
public class MyOpenAPIRulesDefinition implements RulesDefinition {
  public static final String REPOSITORY_KEY = "mycompany-openapi";
  private static final String REPOSITORY_NAME = "MyCompany Custom Repository";
  private static final String RESOURCE_FOLDER = "org/sonar/l10n/openapi/rules/openapi";

  @Override
  public void define(Context context) {
    NewRepository repository = context
      .createRepository(REPOSITORY_KEY, "openapi")
      .setName(REPOSITORY_NAME);
    new RuleMetadataLoader(RESOURCE_FOLDER).addRulesByAnnotatedClass(repository, RulesList.getChecks());
    repository.done();
  }
}
