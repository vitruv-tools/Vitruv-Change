# Vitruvius Maven Build Parent

*A common parent POM for all builds of vitruv.tools*

## Usage

Set the build parent POM as parent in your project POM.
Remember to replace `x.y.z` with the actual version you want to reference.

```
<!-- Build Parent -->
<parent>
    <groupId>tools.vitruv</groupId>
    <artifactId>parent</artifactId>
    <version>x.y.z</version>
</parent>
```

## Provided Plugins

### `org.openntf.maven:p2-layout-resolver`

- required for adding Eclipse update sites as repositories
- update sites are specified as follows:

```
<repository>
    <id>some-id</id>
    <name>Some Name</name>
    <layout>p2</layout>
    <url>http://abc.xyz/org/example/com/releases/${repo.some-id.version}</url>
</repository>
```

- replace placeholders, such as `some-id`, `Some Name` and the URL, with reasonable values
- it is good practice to store the update site version (if applicable) in a Maven property called `repo.id.version` where `id` is the id of the update site
- to include a dependency from an added update site, use the update site id as `groupId` of the dependency

### `org.codehaus.mojo:build-helper-maven-plugin`

- required for adding code in non-Java languages (such as Xtend), as well as generated code (for Xtend and Ecore) to the classpath
- the `xtend-maven-plugin` relies on Xtend files being available on the classpath
- the `maven-compiler-plugin` relies on generated code being available on the classpath

### `org.codehaus.mojo:exec-maven-plugin`

- the default phase `execute-mwe2-generate` is used to execute an MWE2 workflow which, e.g., generates Java code from Ecore meta-models or Xtext grammars
- if used, the workflow file is required to be named `generate.mwe2` and placed in the top-level directory `workflow`
- the workflow should place generated files in `${project.build.directory}/generated-sources`

### `org.eclipse.xtend:xtend-maven-plugin`

- used to generate Java code from (production and test) Xtend code
- the generated code is placed in `${project.build.directory}/generated-sources/xtend` or `${project.build.directory}/generated-test-sources/xtend`

### `maven-jar-plugin`

- packages the build results in a jar archive
- should be disable for test-only projects by setting an empty `phase` for the execution `default-jar`

## Expected Project Structure

The expected project structure is an extension of the standard Maven project structure.
Not all of the shown directories and files are required for every project, use as applicable.
The files `.project` and `plugin.xml` are only required when code should be generated from Ecore meta-models.

```
src/
- main/
    - ecore/
    - java/
    - xtend/
    - resources/
- test/
    - java/
    - xtend/
    - resources/
workflow/
    - generate.mwe2
.project
plugin.xml
pom.xml
```

### Generated Directories and Files

Generated directories and files should not be touched or committed.

- `META-INF/` contains Eclipse-specific project information
- `target/` contains Maven build results
- `build.properties` and `plugin.properties` also contain Eclipse-specific project information
- `plugin.xml` is generated automatically, but *should be committed* and can be altered with custom settings for Ecore meta-models
