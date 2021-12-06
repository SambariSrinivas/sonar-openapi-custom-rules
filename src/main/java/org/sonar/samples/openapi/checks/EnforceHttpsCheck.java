package org.sonar.samples.openapi.checks;

import com.google.common.collect.ImmutableSet;
import com.sonar.sslr.api.AstNodeType;
import org.sonar.check.Rule;
import org.sonar.plugins.openapi.api.OpenApiCheck;
import org.sonar.plugins.openapi.api.v3.OpenApi3Grammar;
import org.sonar.sslr.yaml.grammar.JsonNode;


import java.util.Set;

// Rule taken from https://apisecurity.io/encyclopedia/content/oasv3/security/transport/v3-global-http-clear
@Rule(key = "EnforceHttpsRule")
public class EnforceHttpsCheck extends OpenApiCheck {

    @Override
    public Set<AstNodeType> subscribedKinds() {
        return ImmutableSet.of(OpenApi3Grammar.SERVER);
    }

    static final String MESSAGE = "You shall use https only";

    @Override
    public void visitNode(JsonNode node) {
        if (node.getType() == OpenApi3Grammar.SERVER) {
            check(node);
        }
    }

    private void check(JsonNode node) {
        JsonNode keyNode = node.get("url");
        String url = keyNode.getTokenValue();
        if (url.contains("http:")) {
            addIssue(MESSAGE, keyNode);
        }
    }
}
