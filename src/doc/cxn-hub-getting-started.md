Getting started with <https://hub.chemaxon.com/>
================================================

This is a step-by-step guide to use ChemAxons public Maven repository <https://hub.chemaxon.com/> to compile
[JKlustor Web Examples](https://github.com/ChemAxon/jklustor-web-example) open source API example project.
Please note that you will need a license file for most functionalities, to request a free evaluation please
contact us at [sales@chemaxon.com](mailto:sales@chemaxon.com).


Register a ChemAxon Pass user
-----------------------------

Ensure that you have a ChemAxon Pass user registered. To register a new user visit <https://pass.chemaxon.com/sign-up> which is
available from ChemAxon home page <https://www.chemaxon.com>.

![You can access Pass registration from ChemAxon home page](img/hub-010-edit.png)
![On the Pass registration form fill your details](img/hub-030-edit.png)
![An activation email is sent to you](img/hub-040-edit.png)
![Which contains a link to activate your ChemAxon Pass account by setting up your password](img/hub-060-edit.png)
![To make your ChemAxon Pass account ready to use](img/hub-070-edit.png)


Get an API key for <https://hub.chemaxon.com>
---------------------------------------------

Visit <https://hub.chemaxon.com>, select **Or sign in with: CAS** and sign in with your ChemAxon Pass credentials. After
logging in click on your user name (at the top-right) part of the page and click to generate an API key. You can reveal
the API key which will be required to access the repository.


![Choose sign in with CAS](img/hub-110-edit.png)
![Which redirects to CAS login page where you can use your Pass account](img/hub-140-edit.png)
![Click on your user name at the top-right](img/hub-150-edit.png)
![And generate an API key by clicking](img/hub-160-edit.png)
![Which can be revealed by the eye icon](img/hub-170-edit.png)
![Copy the API key](img/hub-180-edit.png)


Ensure that your ChemAxon license file is installed
---------------------------------------------------

```` bash
mkdir -p ~/.chemaxon/
cp license.cxl ~/.chemaxon/
````


Clone `jklustor-web-examples`
-----------------------------

```` bash
git clone https://github.com/ChemAxon/jklustor-web-example.git
cd jklustor-web-examples
````


Use the acquired API key for compilation
----------------------------------------

Use your email associated with your Pass account as `cxnHubUser` and the Hub API key acquired as the `cxnHubPass`:

```` bash
./gradlew -PcxnHubUser=<YOUR PASS EMAIL> -PcxnHubPass=<YOUR HUB API KEY> bootRun
````
