# Custom Rules for OpenAPI

You are using SonarQube and its OpenAPI Analyzer to analyze your projects, but there aren't rules that allow you to target 
some of your org/team's specific needs? Then your logical choice may be to implement your own set of custom OpenAPI rules.

This document is an introduction to custom rule writing for the SonarQube OpenAPI Analyzer. It will cover all the main 
concepts of static analysis required to understand and develop effective rules, relying on the API provided by the 
SonarQube OpenAPI Plugin. 

## Getting started

The rules you are going to develop will be delivered using a dedicated, custom plugin, relying on the SonarQube OpenAPI Plugin API. 
In order to start working efficiently, we provide an existing maven project, that you will fill in while following this tutorial.

[Grab the template project from there](https://github.vodafone.com/VFCPS-QACOE/sonar-openapi-custom-rules) and import it to your IDE.

This project already contains four custom rules. Our goal is to give a demo on how to add custom rule!

### Customizing the POM

A custom plugin is a Maven project, and before diving into code, it is important to notice a few relevant lines related 
to the configuration of your soon-to-be-released custom plugin.

In the code snippet below, note the plugin API version (`<sonar.version>`) provided through the properties. It relates 
to the minimum version of SonarQube your plugin will support, and is generally aligned to your company's SonarQube 
instance. In this template, we rely on the version **6.7.4**.
You'll notice there's a separate property (`<sonarQubeMinVersion>`) defined for the version of
your company instance's SonarQube version. Here we'll use **6.7**. Notice how the two versions
are aligned.

The property `<sonaropenapi.version>` is the minimum version of the OpenAPI Analyzer that will 
be required to run your custom plugin in your SonarQube instance. Consequently, as we will rely 
on version **1.2.3.41** of the OpenAPI plugin, the SonarQube instance which will use the custom 
plugin will need version 1.2.3.41 of the Java Plugin as well.

For the moment, don't touch these two properties.

Other properties such as `<groupId>`, `<artifactId>`, `<version>`, `<name>` and `<description>`
can be freely modified.

```xml
  <groupId>org.sonarsource.samples</groupId>
  <artifactId>openapi-custom-rules</artifactId>
  <version>1.0-SNAPSHOT</version>
  <packaging>sonar-plugin</packaging>

  <name>SonarQube OpenAPI Custom Rules Example</name>
  <description>OpenAPI Custom Rules Example for SonarQube</description>
  <inceptionYear>2018</inceptionYear>

  <properties>
    <sonar.version>6.7.4</sonar.version>
    <sonarQubeMinVersion>6.7</sonarQubeMinVersion>
    <sonaropenapi.version>1.2.3.41</sonaropenapi.version>
    <sonaranalyzer.version>1.6.0.219</sonaranalyzer.version>
  </properties>
```

In the code snippet below, it is important to note that the **entry point of the plugin** is 
provided as the `<pluginClass>` in the configuration of the sonar-packaging-maven plugin, using 
the fully qualified name of the java class `MyOpenAPIRulesPlugin`. If you refactor your code, 
rename, or move the class extending `org.sonar.api.SonarPlugin`, you will have to change this 
configuration.

It is **very important** to set the `basePlugin` property to `openapi`. This will allow your extension
plugin to correctly share the same classpath as the OpenAPI Analyzer plugin, which is necessary
to correctly reference the classes from the common Sonar SSLR API.

```xml
      <plugin>
        <groupId>org.sonarsource.sonar-packaging-maven-plugin</groupId>
        <artifactId>sonar-packaging-maven-plugin</artifactId>
        <version>1.17</version>
        <extensions>true</extensions>
        <configuration>
          <pluginKey>openapi-custom</pluginKey>
          <pluginName>OpenAPI Custom Rules</pluginName>
          <pluginClass>org.sonar.samples.openapi.MyOpenAPIRulesPlugin</pluginClass>
          <skipDependenciesPackaging>true</skipDependenciesPackaging>
          <sonarLintSupported>true</sonarLintSupported>
          <sonarQubeMinVersion>${sonarQubeMinVersion}</sonarQubeMinVersion>
          <basePlugin>openapi</basePlugin>
        </configuration>
      </plugin>
```

## Writing a rule

In this section, we'll write a custom rule from scratch. To do so, we will use a [Test Driven Developement](https://en.wikipedia.org/wiki/Test-driven_development)
(TDD) approach, relying on writing some test cases first, followed by the implementation a solution. The rule we will
develop will check that The security field of your API contract does not list any security schemes to be applied. Instead, it just contains an empty array.
(FYI: security schemes defined in the securitySchemes section can be applied to the whole API or individual operations by adding the security section on the root level or operation level, respectively)
[FYI: This rule is already implemented and available in this repo]

### Three files to forge a rule

When implementing a rule, there is always a minimum of 3 distinct files to create:

1. A test file, which contains OpenAPI code used as input data for testing the rule
2. A test class, which contains the rule's unit test
3. A rule class, which contains the implementation of the rule.

To create our first custom rule (usually called a "*check*"), let's start by creating these 3 files in the template 
project, as described below:

1. In folder `/src/test/resources/checks/v3`, create a new empty file named `EmptySecurityArrayCheck.yaml`, and copy-paste the content 
of the following code snippet.

    ```yaml
   openapi: "3.0.1"
   info:
     version: 1.0.0
   title: Swagger Petstore
   servers: 
       - url: https://my.api.server.com/
         description: API server
   paths:
     /billing_info:
    ```

2. In package `org.sonar.samples.openapi.checks` of `/src/test/java`, create a new test class called `EmptySecurityArrayCheckTest`
and copy-paste the content of the following code snippet.

    ```java
    package org.sonar.samples.openapi.checks;
     
    import org.junit.Test;
     
    public class EmptySecurityArrayCheckTest {
     
      @Test
      public void checkEmptySecurityArray() {
      }
     
    }
    ```

3. In package `org.sonar.samples.openapi.checks` of `/src/main/java`, create a new class called `EmptySecurityArrayCheck` 
extending class `org.sonar.plugins.openapi.api.OpenApiCheck` provided by the OpenPI Plugin API. Then, replace the content 
of the `subscribedKinds()` method with the content from the following code snippet (you may have to import 
`com.google.common.collect.ImmutableSet`). This file will be described when dealing with implementation of the rule!

    ```java
    package org.sonar.samples.openapi.checks;
    
    import com.google.common.collect.ImmutableSet;
    import com.sonar.sslr.api.AstNodeType;
    import org.sonar.plugins.openapi.api.OpenApiCheck;
    
    import java.util.Set;
    
    public class EmptySecurityArrayCheck extends OpenApiCheck {
    
        @Override
        public Set<AstNodeType> subscribedKinds() {
            return ImmutableSet.of();
        }
    
    }
    ```

> More files ?
>
> If the 3 files described above are always the base of rule writing, there are situations where extra files may be 
> needed. For instance, when a rule uses parameters, multiple test files could be required. It is also possible to use 
> external files to describe rule metadata, such as a description in html format. Such situations will be described in 
> other topics of this documentation.

### A test file to rule them all

Because we chose a TDD approach, the first thing to do is to write examples of the code our rule will target. In this
file, we consider numerous cases that our rule may encounter during an analysis, and flag the lines which will require 
our implementation to raise issues.

Covering all the possible cases is not necessarily required, the goal of this file is to cover all the situations which 
may be encountered during an analysis, but also to abstract irrelevant details. For instance, in the context of our first
rule, the content of schemas, the url of the servers make no difference. Note that this sample file 
should be structurally correct.

In the test file `EmptySecurityArrayCheck.yaml` created earlier, copy-paste the following code:

```yaml
openapi: "3.0.1"
info:
   version: 1.0.0
   title: Swagger Petstore
servers:
   - url: https://my.api.server.com/
     description: API server
paths:
   /billing_info:
      get:
         summary: Gets the account billing info
         security:
            - OAuth2: [admin]   # Use OAuth with a different scope
         responses:
            '200':
               description: OK
            '401':
               description: Not authenticated
            '403':
               description: Access token does not have the required scope
   /ping:
      get:
         summary: Checks if the server is running
         security: []   # No security
         responses:
            '200':
               description: Server is up and running
            default:
               description: Something is wrong
security: []  # No Global security
components:
   securitySchemes:
      BasicAuth:
         type: http
         scheme: basic
      BearerAuth:
         type: http
         scheme: bearer
      ApiKeyAuth:
         type: apiKey
         in: header
         name: X-API-Key
      OpenID:
         type: openIdConnect
         openIdConnectUrl: https://example.com/.well-known/openid-configuration
      OAuth2:
         type: oauth2
         flows:
            authorizationCode:
               authorizationUrl: https://example.com/oauth/authorize
               tokenUrl: https://example.com/oauth/token
               scopes:
                  read: Grants read access
                  write: Grants write access
                  admin: Grants access to admin operations
``` 

The test file now contains the following test cases:
* line 30: contains an empty array in security field at Root(or Global) Level
* line 24: contains an empty array in security field at Operation Level
* line 13: contains a non-empty array in security field at Operation Level


### A test class to make it pass

Once the test file is updated, let's update our test class to use it, and link the test to our (not yet implemented) rule.
To do so, get back to our test class `EmptySecurityArrayCheckTest`, and update the `checkEmptySecurityArray()` method as shown in the following 
code snippet (you may need to import some classes):

```java
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
```

For the sake of this test, we are just checking that the check collects the issues at the right line. We can later add
other verifications, such as the message raised by the issue.

Now, let's proceed to the next step of TDD: make the test fail!

To do so, simply execute the test from the test file using JUnit. The test should fail as shown below:

```text
java.lang.AssertionError: 
Actual and expected should have same size but actual size was:
  <0>
while expected size was:
  <2>
Actual was:
  <[]>
Expected was:
  <[30, 24]>
```

### Implementing the rule

Before we start with the implementation of the rule itself, you need a little background.

Prior to running any rule, the SonarQube OpenAPI Analyzer parses a given OpenAPI contract file and produces an equivalent 
data structure: the **Syntax Tree**. Each construction of the OpenAPI specification can be represented with a specific kind 
of Syntax Tree, detailing each of its particularities. Each of these constructions is associated with a specific `AstNodeType`.
For instance, the type associated to the declaration of an operation in an OpenAPI v3 document will be 
`org.sonar.plugins.openapi.api.v3.OpenApi3Grammar.OPERATION`.

The plugin provides types for both versions of the API, in the `org.sonar.plugins.openapi.api.v2.OpenApi2Grammar`
and `org.sonar.plugins.openapi.api.v3.OpenApi3Grammar` enums. For our example rule, we will focus on just OpenAPI v3,
but most of your rules will need also to be compatible with OpenAPI v2. 

Our rule class derives from the `OpenApiCheck` class provided by the Sonar OpenAPI plugin's API. This class, on top of 
providing a bunch of useful methods to raise issues, also **defines the strategy which will be used when analyzing a file**.
It is based on a subscription mechanism, allowing to specify on what kind of tree the rule should react. The list of node 
types to cover is specified through the `subscribedKinds()` method. In the previous steps, we modified the implementation of the method to return an empty list, therefore not subscribing to any node of the syntax tree.

Now its finally time to jump in to the implementation of our first rule! Go back to the `EmptySecurityArrayCheck` class, and 
modify the list of `AstNodeType` returned by the `subscribedKinds()` method. Since our rule targets security field at Global and Operations level, we only need to visit these nodes.
To do so, simply add `OpenApi3Grammar.ROOT` and `OpenApi3Grammar.OPERATION` as a parameter of the returned immutable set, as shown in the following code snippet.

```java
    @Override
    public Set<AstNodeType> subscribedKinds() {
        return ImmutableSet.of(OpenApi3Grammar.ROOT, OpenApi3Grammar.OPERATION);
    }
```

Once the nodes to visit are specified, we have to implement how the rule will react when encountering declarations.
To do so, override method `visitNode(JsonNode node)`, inherited from `OpenApiCheck`.

```java
    @Override
    public void visitNode(JsonNode node) {
    }
```

Now, let's add the checks for the security key. We will use tools provided in `org.sonar.sslr.yaml.grammar.JsonNode` by the 
Sonar OpenAPI plugin API, that help us navigate in the Json node tree. We will navigate through all the **properties**
of the API's `security` section.

The `OpenApiCheck` class provides utility methods to declare new issues. By providing a specific `AstNode or JsonNode`, you can
mark the exact location of the issue.

```java
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
        if (!keyNode.isMissing() && keyNode.elements().size() == 0) {
        addIssue(MESSAGE, keyNode);
        }
    }
```

Let's re-run our test!

```text
 Test Passed
```

That's better! The rule is now capturing the violation on line 30 and 24, caused by the empty security array at global level and also at operation level.


Now, ** execute the test** class again.

Test passed? If not, then check if you somehow missed a step.

If it passed...

<center>

**[Congratulations!]**

**[You implemented your first custom rule for the SonarQube OpenAPI Analyzer!]**
</center>

## Registering the rule in the custom plugin

OK, you are probably quite happy at this point, as our first rule is running as expected... However, we are not really
done yet. Before playing our rule against any real projects, we have to finalize its creation within the custom plugin,
by registering it.

### Rule Metadata

The first thing to do is to provide to our rule all the metadata which will allow us to register it properly in the 
SonarQube platform. To do so, add the `org.sonar.check.Rule` annotation to `EmptySecurityArrayRule` class rule, and provide
a **key**.

```java
@Rule(key = "EmptySecurityArrayRule")
public class EmptySecurityArrayCheck extends OpenApiCheck {

```

The rest of the metadata is provided by a JSON file and an HTML file, that gives a **name**, **description** and
optional **tags**:

1. In folder `/src/main/resources/org/sonar/l10n/openapi/rules/openapi`, create a new empty file named `EmptySecurityArrayRule.html`,
and copy-paste the content of the following code snippet:

```html
    <p>This rule detects if the the Security field is defined properly or not.</p>
    <h2>Noncompliant Code Example</h2>
   <pre>
   The security field of API contract does not list any security schemes to be applied. Instead, it just contains an empty array.
   openapi: "3.0.1"
   info:
   version: 1.0.0
   title: Swagger Petstore
   # ...
   servers:
   - url: https://my.api.server.com/
     description: API server
   # ...
   paths:
      /billing_info:
      get:
       summary: Gets the account billing info
      security: []   # Use OAuth with a different scope
      responses:
        '200':
          description: OK
        '401':
          description: Not authenticated
        '403':
          description: Access token does not have the required scope
   security: []
   </pre>
   <h2>Compliant Solution</h2>
<pre>
   openapi: "3.0.1"
   info:
   version: 1.0.0
   title: Swagger Petstore
   # ...
   servers:
   - url: https://my.api.server.com/
     description: API server
   # ...
   paths:
   /billing_info:
    get:
      summary: Gets the account billing info
      security:
        - OAuth2: [admin]   # Use OAuth with a different scope
      responses:
        '200':
          description: OK
        '401':
          description: Not authenticated
        '403':
          description: Access token does not have the required scope
   security:
   - OAuth2:
       - readOnly
   components:
   securitySchemes:
     OAuth2:
       type: oauth2
       flows:
         authorizationCode:
           scopes:
             readOnly: read objects in your account
           authorizationUrl: https://example.com/oauth/authorize
           tokenUrl: https://example.com/oauth/token
   
 </pre>
```

2. In folder `/src/main/resources/org/sonar/l10n/openapi/rules/openapi`, create a new empty file named `EmptySecurityArrayRule.json`,
   and copy-paste the content of the following code snippet:

    ```json
    {
       "title": "EmptySecurityArrayRule",
       "type": "VULNERABILITY",
       "status": "ready",
       "remediation": {
       "func": "Constant\/Issue",
       "constantCost": "5min"
       },
       "tags": [
           "pitfall",
           "must"
       ],
       "defaultSeverity": "Blocker"
   }
    ```
    
### Rule Activation

The second things to do is to activate the rule within the plugin. To do so, open class **`RulesList`** 
(`org.sonar.samples.openapi.checks.RulesList`). In this class, you will notice the method **`getChecks()`**.
This method is used to register our rules with alongside the rule of the OpenAPI plugin. To register the rule, simply 
add the rule class to the list, as in the following code snippet:

```java
public static List<Class> getChecks() {
    return Arrays.asList(
        // other rules...
        EmptySecurityArrayCheck.class
    );
}
```

## Testing a custom plugin

> **Prerequisite**
>  
> For this chapter, you will need a local/remote instance of SonarQube. If you don't have a SonarQube platform installed 
> now is time to download its latest version from [HERE](https://www.sonarqube.org/downloads/)!

At this point, we've completed the implementation of a first custom rule and registered it into the custom plugin. The
last remaining step is to test it directly with the SonarQube platform and try to analyse a project! 

Start by building the project using maven:

```text
$ pwd
C:\Users\XXXXXXXXX\GitHub\sonar-openapi-custom-rules>
  
$ mvn clean install
[INFO] Scanning for projects...
[INFO]                                                                        
[INFO] ------------------------------------------------------------------------
[INFO] Building OpenAPI Custom Rules - Template 1.0-SNAPSHOT
[INFO] ------------------------------------------------------------------------
  
...
 
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 4.102 s
[INFO] Finished at: 2016-05-23T16:21:55+02:00
[INFO] Final Memory: 25M/436M
[INFO] ------------------------------------------------------------------------
```

Then, grab the jar file **`openapi-custom-rules-1.0-SNAPSHOT.jar`** from the target folder of the project, and move it 
to the extensions folder of your SonarQube instance, which will be located at `$SONAR_HOME/extensions/plugins`.

> **SonarQube Java Plugin compatible version**

> Before going further, be sure to have the adequate version of the SonarQube OpenAPI Plugin with your SonarQube instance.
> The dependency over the OpenAPI Plugin of our custom plugin is defined in its `pom`, as seen in the first chapter of this tutorial.
>
> If you have a fresh install or do not possess the same version, install the adequate version of the OpenAPI Plugin.

Now, (re-)start your SonarQube instance, log as `admin` and navigate to the **Rules** tab.

From there, under the language section, select "**OpenAPI**", and then "**MyCompany Custom Repository**" under the repository section.
Your rule should now be visible (with all the other sample rules). 

Select the rule and activate it in the default quality profile.

*TODO - insert picture here*

Once activated, the only step remaining is to analyse one of your project!

When encountering a security key containing `empty array`, the issue will now raise issue.

*TODO - insert picture here*

## What to do next

* Try to add similar checks for OpenApi v2 grammar
* Add verifications in the test that the correct message is raised in the error
* Add integration tests (TODO - document it a bit)
