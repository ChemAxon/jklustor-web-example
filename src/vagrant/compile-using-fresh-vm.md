Compilation using fresh virtual machine
=======================================

It is recommended to testing the build configuration and dependency resolution logic on a fresh machine
to provide reproducibility. This [Vagrant](https://www.vagrantup.com/) file provides such an isolated
environment.

Install Vagrant
---------------

Follow instructions at <https://www.vagrantup.com/downloads.html>.


Prepare prerequisuites
----------------------

 - Copy the vagrant file to an empty directory
 - Copy your ChemAxon license file `license.cxl` in the directory of the vagrant file.


Create and launch VM
--------------------

 - Invoke `vagrant up` (on the host) to create the VM
 - Invoke `vagrant ssh` (on the host) to access the created VM
 - Clone the repository
 - `cd jklustor-web-examples`
 - In the shell (on the VM) invoke `./gradlew -PcxnHubUser=<USER> -PcxnHubPass=<PASSWORD> bootRun`
 - After startup open a web browser (on the host) and connect to <http://localhost:8090>. Note that
   port 8090 of the VM is forwarded from port 8090 of the host (specified in `Vagrantfile`).
