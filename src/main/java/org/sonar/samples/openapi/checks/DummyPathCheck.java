package org.sonar.samples.openapi.checks;

import com.google.common.collect.ImmutableSet;
import com.sonar.sslr.api.AstNodeType;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.plugins.openapi.api.OpenApiCheck;
import org.sonar.plugins.openapi.api.v2.OpenApi2Grammar;
import org.sonar.plugins.openapi.api.v3.OpenApi3Grammar;

import java.util.Set;
import org.sonar.sslr.yaml.grammar.JsonNode;

/**
 * An example rule that checks that all paths start with {@code '/dummy'}. Not meant for production use!
 *
 * This class also demonstrates how to parameterize rules.
 */
@Rule(key = DummyPathCheck.CHECK_KEY)
public class DummyPathCheck extends OpenApiCheck {
  protected static final String CHECK_KEY = "DummyPath";
  protected static final String MESSAGE = "All your paths should start with '/%s'.";
  private static final String DEFAULT_VALUE = "dummy";
  /**
   * Name of the annotation to avoid. Value can be set by users in Quality profiles.
   * The key
   */
  @RuleProperty(
    defaultValue = DEFAULT_VALUE,
    description = "Path segment that should start all paths, without leading /. For instance 'dummy'.")
  protected String segment;

  @Override
  public Set<AstNodeType> subscribedKinds() {
    return ImmutableSet.of(OpenApi2Grammar.PATH, OpenApi3Grammar.PATH);
  }

  @Override
  public void visitNode(JsonNode node) {
    JsonNode keyNode = node.key();
    String path = keyNode.getTokenValue();
    if (!path.startsWith("/" + segment)) {
      addIssue(String.format(MESSAGE, segment), keyNode);
    }
  }
}
