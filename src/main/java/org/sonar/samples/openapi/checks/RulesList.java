package org.sonar.samples.openapi.checks;

import java.util.Arrays;
import java.util.List;

public final class RulesList {

    private RulesList() {
    }

    public static List<Class> getChecks() {
        return Arrays.asList(
                DummyPathCheck.class,
                MyFirstCustomCheck.class,
                SecuritySchemesCheck.class,
                EnforceHttpsCheck.class,
                EmptySecurityArrayCheck.class
        );
    }
}
