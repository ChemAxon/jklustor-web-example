JKlustor web example
====================

**This code is under construction. Please note that the web example is not complete. Please note that these example codes might use beta or non public APIs.**

This is a usage example for ChemAxon JKlustor API for a web service environment. This project is intended for active API users of ChemAxon JKlustor API. Simple API usage example is also provided. For fur


Summary
-------

This project will contain a working usage example for ChemAxon JKlustor API in a web environment. A simple chemical clustering workflow is implemented providing the following functionalities:

  - User can upload molecule files
  - A selected clustering algorithm can be launched
  - The results are visualized in a hierarchic dendrogram
  - The dendrogram can be collapsed at an arbitrary similarity level into a non-hierarchical clustering
  - Results can be downloaded in various formats


Getting started
---------------

This project depends on ChemAxon proprietary `jchem.jar` available in JChem Suite platform independent (.zip) distribution. This distribution can be downloaded manually or retrieved from https://repository.chemaxon.com with proper credentials. Both approaches are detailed. Both approaches use project properties to specify the access to ChemAxon proprietary APIs. Descriptions below use the command line argument (`-P<key>=<value>`) approach. Further ways to set project properties are described in <http://mrhaki.blogspot.hu/2010/09/gradle-goodness-different-ways-to-set.html>.


### Installing license

  - Make sure that ChemAxon licenses for the used functionalities are available and installed. For details see ChemAxon Installing Licenses documentation.

### Option: Manual download of JChem distribution

  - Make sure that you have the required registration to access ChemAxon product downloads and download JChem Suite - platform independent (.zip) distribution from https://www.chemaxon.com/download/jchem-suite/.
  - Unpack the downloaded file.
  - Locate file `jchem/lib/jchem.jar` in the unpacked directory.
  - Launch `./gradlew -Pcxn_jchem_jar=<PATH TO JCHEM JAR> bootRun` to start embedded server.
  - Launch `./gradlew -Pcxn_jchem_jar=<PATH TO JCHEM JAR> jar` to compile API examples.

Note that you can point to the location of jchem.jar in an existing JChem Suite installation.

### Option: Using ChemAxon Public Repository

  - Make sure that you have the username and password required to access ChemAxon Public Repository. For access send a request to maven-repo-request@chemaxon.com. For details see https://docs.chemaxon.com/display/jchembase/Introduction+for+Java+applications#IntroductionforJavaapplications-swreq
  - Launch `./gradlew -Pcxn_repo_user=<USERNAME> -Pcxn_repo_pass=<PASSWORD> bootRun` to start embedded server.
  - Launch `./gradlew -Pcxn_repo_user=<USERNAME> -Pcxn_repo_pass=<PASSWORD> jar` to compile API examples

### Connecting to the embedded server

  - After a successful launch with the `bootRun` target use a browser and open <http://localhost:8090>.

### Using API examples

After a successful compilation with the `jar` target invoke the following commands to launch simple API usage examples.

```` bash
cat src/data/molecules/vitamins/vitamins.smi | java -classpath build/libs/jklustor-web-example-0.0.1-SNAPSHOT.jar com.chemaxon.clustering.examples.SphexExample
cat src/data/molecules/drugbank/drugbank-all.sdf.gz | java -classpath build/libs/jklustor-web-example-0.0.1-SNAPSHOT.jar com.chemaxon.clustering.examples.HierarchicClusteringExample
````

### Further steps

  - Invoke `./gradlew eclipse` to download public dependency sources.