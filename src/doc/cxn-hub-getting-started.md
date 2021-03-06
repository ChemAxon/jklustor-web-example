# Getting started with `hub.chemaxon.com`

This is a step-by-step guide on using ChemAxon public Maven repository `hub.chemaxon.com` to compile
this [JKlustor Web Examples](https://github.com/ChemAxon/jklustor-web-example) open source API example project. This guide
is based on document <https://docs.chemaxon.com/display/docs/Public+Repository>.
Please note that you will need a license file for most functionalities. To request a free evaluation license please
contact us at [sales@chemaxon.com](mailto:sales@chemaxon.com).

Please note that the visited webpages might look slightly differently than in the screenshots.


## Introduction

ChemAxon provides a public repository `hub.chemaxon.com` for our products and 
modules. It is designed for integrators and resellers but is available for any 
registered ChemAxon user as well. How to acquire the required credentials in 
order to use this repository are described below.

Note that the repository server at `hub.chemaxon.com` **does not** allow 
interactive logins.


## Create your ChemAxon Account

If you do not already have a ChemAxon Account, you may create it via filling the 
form at the <https://accounts.chemaxon.com/register> register page. Your e-mail
address will serve as the `<YOUR_PASS_EMAILL>` credential when setting up access
to the repository.


## Generate your API key

To generate your API key visit your account settings page at 
<https://accounts.chemaxon.com/my/settings>. Under the ‘Public Repository’ 
section, you will find a button to ‘Generate API Key’ or your already generated 
API key. This will serve as the `<YOUR_HUB_API_KEY>` credential when setting up 
access to the repository.


## Ensure that your ChemAxon license file is installed

Many of the provided functionalities require a ChemAxon license. To request a 
free evaluation license please contact us
at [sales@chemaxon.com](mailto:sales@chemaxon.com).

```bash
mkdir -p ~/.chemaxon/
cp license.cxl ~/.chemaxon/
```

Further details can be found in
[ChemAxon Installing Licenses](https://docs.chemaxon.com/Installing_Licenses.html)
 documentation.


## Clone `jklustor-web-examples`

```bash
git clone https://github.com/ChemAxon/jklustor-web-example.git
cd jklustor-web-examples
```


## Use the acquired API key for compilation

Use your email associated with your ChemAxon account as `<YOUR_PASS_EMAIL>` and 
the Hub API key acquired as the `<YOUR_HUB_API_KEY>`. For compilation you will 
need Java 1.8 installed.

```bash
./gradlew -PcxnHubUser=<EMAIL> -PcxnHubPass=<API-KEY> bootRun
```
Please note that a Vagrant configuration is also provided which sets up
an Ubuntu based box with Java and ChemAxon licenses installed. For details see 
document
[Compilation using fresh virtual machine](../vagrant/compile-using-fresh-vm.md).
