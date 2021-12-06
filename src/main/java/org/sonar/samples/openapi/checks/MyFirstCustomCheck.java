package org.sonar.samples.openapi.checks;

import com.google.common.collect.ImmutableSet;
import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.AstNodeType;
import org.sonar.check.Rule;
import org.sonar.plugins.openapi.api.OpenApiCheck;
import org.sonar.plugins.openapi.api.v3.OpenApi3Grammar;
import org.sonar.sslr.yaml.grammar.JsonNode;
import org.sonar.sslr.yaml.grammar.Utils;


import java.util.Set;

@Rule(key = "MyFirstCustomRule")
public class MyFirstCustomCheck extends OpenApiCheck {

    @Override
    public Set<AstNodeType> subscribedKinds() {
        return ImmutableSet.of(OpenApi3Grammar.PATH, OpenApi3Grammar.PARAMETER);
    }

    static final String MESSAGE = "You shall not use the 'foo' word!";

    @Override
    public void visitNode(JsonNode node) {
        if (node.getType() == OpenApi3Grammar.PATH) {
            checkPathKeys(node);
        } else if (node.getType() == OpenApi3Grammar.PARAMETER){
            checkParameterDefinition(node);
        }
    }

    private void checkPathKeys(JsonNode node) {
        JsonNode keyNode = node.key();
        String path = keyNode.getTokenValue();
        if (path.contains("foo")) {
            addIssue(MESSAGE, keyNode);
        }
    }

    private void checkParameterDefinition(JsonNode node) {
        JsonNode parameterNode = node.at("/name");
        String name = parameterNode.getTokenValue();
        if (name.contains("foo")) {
            addIssue(MESSAGE, parameterNode);
        }
    }
}
