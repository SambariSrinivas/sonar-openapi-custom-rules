package org.sonar.samples.openapi.checks;

import com.google.common.collect.ImmutableSet;
import com.sonar.sslr.api.AstNodeType;
import org.sonar.check.Rule;
import org.sonar.plugins.openapi.api.OpenApiCheck;
import org.sonar.plugins.openapi.api.v3.OpenApi3Grammar;
import org.sonar.sslr.yaml.grammar.JsonNode;

import java.util.Set;

// Rule taken from https://apisecurity.io/encyclopedia/content/oasv3/security/authentication/v3-global-securityrequirement-emptyarray
@Rule(key = "EmptySecurityArrayRule")
public class EmptySecurityArrayCheck extends OpenApiCheck {

    @Override
    public Set<AstNodeType> subscribedKinds() {
        return ImmutableSet.of(OpenApi3Grammar.ROOT, OpenApi3Grammar.OPERATION);
    }

    static final String MESSAGE = "'Security' field should not be an empty array";

    @Override
    public void visitNode(JsonNode node) {
        if (node.getType() == OpenApi3Grammar.ROOT) {
            checkSecurityRequirement(node);
        } else if (node.getType() == OpenApi3Grammar.OPERATION) {
            checkSecurityRequirement(node);
        }
    }

    private void checkSecurityRequirement(JsonNode node) {
        JsonNode keyNode = node.get("security");
        if (!keyNode.isMissing() && keyNode.elements().isEmpty()) {
            addIssue(MESSAGE, keyNode);
        }
    }
}