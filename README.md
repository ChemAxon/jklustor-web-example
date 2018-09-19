JKlustor web example
====================



**IMPORTANT:** 

  - Early access version is used for some of the ChemAxon proprietary dependencies. See `build.gradle` for details.
    Please note that the associated workarounds will be removed when the relevant features are available in a regular
    release.

  - ChemAxon [public repository](https://docs.chemaxon.com/display/docs/Public+Repository) (repository.chemaxon.com) 
    used by previous versions is changed. Please 
    [ensure](src/doc/cxn-hub-getting-started.md) that you have access to the new repository (hub.chemaxon.com). Contact us
    at [disco-support@chemaxon.com](mailto:disco-support@chemaxon.com?subject=Question%20regarding%20github.com/ChemAxon/jklustor-web-example)
    with further questions.


**This code is under construction. Please note that the web example is not complete. Please note that these example
codes might use beta or non-public APIs.**

This is a usage example for ChemAxon JKlustor API for a web service environment. This project is intended for active API
users of ChemAxon JKlustor API. Simple API usage example is also provided.


Further docs
------------

 - [Notes on Spring Boot application configuration](src/doc/spring-boot-notes.md)
 - [Getting started with `hub.chemaxon.com`](src/doc/cxn-hub-getting-started.md)

 
Summary
-------

This project will contain a working usage example for ChemAxon JKlustor API in a web environment. A simple chemical
clustering workflow will be implemented providing the following functionalities:

  - User can upload molecule files
  - A selected clustering algorithm can be launched
  - The results are visualized in a hierarchic dendrogram
  - The dendrogram can be collapsed at an arbitrary similarity level into a non-hierarchical clustering
  - Results can be downloaded in various formats

Additionally some further basic examples of associated APIs can be found in 
`src/main/java/com/chemaxon/clustering/examples`.


Getting started
---------------

This project depends on ChemAxon proprietary APIs which are not available from open public repositories. To compile
you need to request access to ChemAxon public repository <https://hub.chemaxon.com> and pass your credentials to the
build script using Gradle properties. Descriptions below use the command line argument (`-P<key>=<value>`) approach.
Further ways to set project properties are described in <http://mrhaki.blogspot.hu/2010/09/gradle-goodness-different-ways-to-set.html>.

See <https://docs.chemaxon.com/display/docs/Public+Repository> and
[Getting started with `https://hub.chemaxon.com`](src/doc/cxn-hub-getting-started.md) for details.

### Installing license

  - Make sure that ChemAxon licenses for the used functionalities are available and installed. For details see
    [ChemAxon Installing Licenses](http://www.chemaxon.com/marvin/help/licensedoc/install.html) documentation.


### Using ChemAxon Public repository

  - **Note that repository <https://repository.chemaxon.com> is deprecated, you need new credentials for accessing
    <https://hub.chemaxon.com>. We expect further major changes in the repository configuration in the near future.**
  - **Manual download and usage of JChem distribution is not supported now.**
  - Make sure that you have the required credentials to access ChemAxon public repository <https://hub.chemaxon.com>
    described above
  - For low memory machines use `export GRADLE_OPTS=-Xmx1768m`
  - Launch `./gradlew -PcxnHubUser=<YOUR_PASS_EMAIL> -PcxnHubPass=<YOUR_HUB_API_KEY> bootRun` to start embedded server.
  - Launch `./gradlew -PcxnHubUser=<YOUR_PASS_EMAIL> -PcxnHubPass=<YOUR_HUB_API_KEY> jar` to compile API examples.
  - Launch `./gradlew -PcxnHubUser=<YOUR_PASS_EMAIL> -PcxnHubPass=<YOUR_HUB_API_KEY> createScripts` to create linux/cygwin `bash`
    launcher scripts.
  - ChemAxon staff with access to internal build environment "Gluon" can use the project without providing these
    credentials.


### Connecting to the embedded server

After a successful launch with the `bootRun` target use a browser and open <http://localhost:8090>.


### Using launcher scripts

After a successful compilation with the `createScript` target invoke the following commands to launch simple API usage examples.

``` bash
cat src/data/molecules/vitamins/vitamins.smi | ./build/scripts/sphexExample
cat src/data/molecules/vitamins/vitamins.smi | ./build/scripts/hierarchicClusteringExample
```


### Using API examples

After a successful compilation with the `jar` target invoke the following commands to launch simple API usage examples.

```` bash
cat src/data/molecules/vitamins/vitamins.smi | java -classpath build/libs/jklustor-web-example-0.0.2-SNAPSHOT.jar com.chemaxon.clustering.examples.SphexExample
cat src/data/molecules/vitamins/vitamins.smi | java -classpath build/libs/jklustor-web-example-0.0.2-SNAPSHOT.jar com.chemaxon.clustering.examples.HierarchicClusteringExample
````


### Further steps

  - Invoke `./gradlew eclipse` to download public dependency sources.


Licensing
---------

**This project** is distributed under the [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0). Some
dependencies of this project are **ChemAxon proprietary products which are not covered by this license**. Please
note that redistribution of ChemAxon proprietary products is not allowed.
