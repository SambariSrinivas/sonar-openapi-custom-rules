package org.sonar.samples.openapi;

import org.sonar.api.Plugin;

/**
 * Entry point of your plugin containing your custom rules.
 */
public class MyOpenAPIRulesPlugin implements Plugin {

  @Override
  public void define(Context context) {
    context.addExtensions(
      // server extensions -> objects are instantiated during server start
      MyOpenAPIProfileDefinition.class,
      MyOpenAPIRulesDefinition.class,
      // batch extensions -> objects are instantiated during code analysis
      MyOpenAPICustomRegistry.class);
  }

}
