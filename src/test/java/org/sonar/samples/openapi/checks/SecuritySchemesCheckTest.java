package org.sonar.samples.openapi.checks;

import org.junit.Test;
import org.sonar.plugins.openapi.api.OpenApiVisitorContext;
import org.sonar.plugins.openapi.api.PreciseIssue;
import org.sonar.plugins.openapi.api.TestOpenApiVisitorRunner;

import java.io.File;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class SecuritySchemesCheckTest {

    @Test
    public void securitySchemesCheckTest(){
        OpenApiVisitorContext context = TestOpenApiVisitorRunner.createContext(new File("src/test/resources/checks/v3/MyFirstCustomCheck2.yaml"));
        List<PreciseIssue> issues = new SecuritySchemesCheck().scanFileForIssues(context);
        assertThat(issues).isEmpty();

    }
}
