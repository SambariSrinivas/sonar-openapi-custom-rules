package org.sonar.samples.openapi.checks;

import org.junit.Test;
import org.sonar.openapi.OpenApiCheckVerifier;

public class DummyPathCheckTest {
  private static DummyPathCheck makeCheck() {
    DummyPathCheck dummyPathCheck = new DummyPathCheck();
    dummyPathCheck.segment = "dummy";
    return dummyPathCheck;
  }

  @Test
  public void verify_dummy_path_in_v2() {
    OpenApiCheckVerifier.verify("src/test/resources/checks/v2/DummyPath.yaml", makeCheck(), true);
  }

  @Test
  public void verify_dummy_path_in_v3() {
    OpenApiCheckVerifier.verify("src/test/resources/checks/v3/DummyPath.yaml", makeCheck(), false);
  }
}
