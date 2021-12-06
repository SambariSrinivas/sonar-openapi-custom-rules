package org.sonar.samples.openapi.checks;

import org.junit.Test;
import org.sonar.plugins.openapi.api.OpenApiVisitorContext;
import org.sonar.plugins.openapi.api.PreciseIssue;
import org.sonar.plugins.openapi.api.TestOpenApiVisitorRunner;

import java.io.File;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class EmptySecurityArrayCheckTest {

    @Test
    public void checkEmptySecurityArray() {
        OpenApiVisitorContext context = TestOpenApiVisitorRunner.createContext(new File("src/test/resources/checks/v3/EmptySecurityArrayCheck.yaml"));
        List<PreciseIssue> issues = new EmptySecurityArrayCheck().scanFileForIssues(context);
       assertThat(issues)
                .extracting(i -> i.primaryLocation().startLine())
           .containsExactly(30, 24);
    }
}
