Compilation using fresh virtual machine
=======================================

It is recommended to testing the build configuration and dependency resolution logic on a fresh machine
to provide reproducibility. This [Vagrant](https://www.vagrantup.com/) file provides such an isolated
environment.

Install Vagrant
---------------

Follow instructions at <https://www.vagrantup.com/downloads.html> to install Vagrant.


Prepare prerequisuites
----------------------

 - Copy the vagrant file to an empty directory
 - Copy your ChemAxon license file `license.cxl` in the directory of the vagrant file. To request a free evaluation 
   license please contact us at [sales@chemaxon.com](mailto:sales@chemaxon.com).
 - Make sure you have a ChemAxon Pass user and a hub.chemaxon.com API key to be used as credentials. See 
   [Getting started with `hub.chemaxon.com`](../doc/cxn-hub-getting-started.md) for details.

Create and launch VM, clone, compile and run project
----------------------------------------------------

 - Invoke `vagrant up` (on the host) to create the VM
 - Invoke `vagrant ssh` (on the host) to access the created VM
 - Clone the repository using `git clone https://github.com/ChemAxon/jklustor-web-example.git`
 - `cd jklustor-web-examples`
 - In the shell (on the VM) invoke `./gradlew -PcxnHubUser=<YOUR PASS EMAIL> -PcxnHubPass=<YOUR HUB API KEY> bootRun`
 - After startup open a web browser (on the host) and connect to <http://localhost:8090>. Note that
   port 8090 of the VM is forwarded from port 8090 of the host (specified in `Vagrantfile`).
 - To reinitialize the VM use `vagrant destroy && vagrant up`.