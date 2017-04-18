Compilation using fresh virtual machine
=======================================

It is recommended to testing the build configuration and dependency resolution logic on a fresh machine
to provide reproducibility. This [Vagrant](https://www.vagrantup.com/) file provides such an isolated
and reproducible environment.

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
 - In the shell (on the VM) invoke 
   `./gradlew -PcxnHubUser=<YOUR PASS EMAIL> -PcxnHubPass=<YOUR HUB API KEY> bootRun`
 - After startup open a web browser (on the host) and connect to <http://localhost:8090>. Note that
   port 8090 of the VM is forwarded from port 8090 of the host (specified in `Vagrantfile`).
 - To reinitialize the VM use `vagrant destroy && vagrant up`.
 - To list status of all active Vagrant environments use `vagrant global-status` (see
   <https://www.vagrantup.com/docs/cli/global-status.html> for details).


Further information
-------------------

 Feel free to check the contents of the included [Vagrantfile](Vagrantfile). The configuration is based on a default
`ubuntu/trusty64` box with the following modifications:

  - Port 8090 of the host is forwarede to the port 8090 of the VM. This allows an easy connection with a browser (on 
    the host) to the running web application inside the VM.
  - VM memory is set to 2048M.
  - JDK is installed on the VM.
  - Memory for `gradle` build is set to 1768M.
  - ChemAxon license file `license.cxl` is expected to be present and is copied to the appropriate directory 
    `~/.chemaxon`.

Whats missing:

 - The current project ([Jklustor web example](https://github.com/ChemAxon/jklustor-web-example)) is not cloned inside
   the VM by default.
 - Credentials to access `hub.chemaxon.com` is not propagated to the VM.