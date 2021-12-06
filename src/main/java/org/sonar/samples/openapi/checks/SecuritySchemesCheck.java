package org.sonar.samples.openapi.checks;

import com.google.common.collect.ImmutableSet;
import com.sonar.sslr.api.AstNodeType;
import org.sonar.check.Rule;
import org.sonar.plugins.openapi.api.OpenApiCheck;
import org.sonar.plugins.openapi.api.v3.OpenApi3Grammar;
import org.sonar.sslr.yaml.grammar.JsonNode;

import java.util.List;
import java.util.Set;

@Rule(key = "SecuritySchemesCheckRule")
public class SecuritySchemesCheck extends OpenApiCheck {

    @Override
    public Set<AstNodeType> subscribedKinds() {
        return ImmutableSet.of(OpenApi3Grammar.COMPONENTS);
    }
    static final String MESSAGE = "Security Schemes should be defined in the OpenAPI Specification";

    @Override
    public void visitNode(JsonNode node) {
                if (node.getType() == OpenApi3Grammar.COMPONENTS) {
            checksecuritySchemes(node);
        }
    }

    private void checksecuritySchemes(JsonNode node) {
        List<JsonNode> components = node.getJsonChildren();
        if (components.isEmpty()) {
            addIssue(MESSAGE, node);
            //System.out.println("Components is empty, hence there is no securitySchemes defined");
        } else {
            boolean isSecuritySchemesFound = false;
            for (JsonNode component : components) {
                //System.out.println(component.key().getTokenValue());
                if ((component.key().getTokenValue().equals("securitySchemes"))) {
                    isSecuritySchemesFound = true;
                    break;
                }
            }
            if (isSecuritySchemesFound) {
                System.out.println("securitySchemes has found");
            } else
                addIssue(MESSAGE, node);
                //System.out.println("securitySchemes has not found");
        }
    }
}