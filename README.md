# teiid-console

This repository contains the code for Teiid Web Console that is based on the JBoss AS7 Console.

## Running in hosted mode:

1.) Make sure JBoss 7 is started and teiid is installed

2.) Make sure you build the top level module first (mvn -Pdev clean install).

3.) cd 'app'

Start the GWT shell with

`mvn gwt:<run|debug>`

When the hosted browser is started, it's enough to hit the 'refresh' button to recompile
and verify changes. You can get the OOPHM Plugin, required for attaching your browser to the
hosted mode execution here: http://gwt.google.com/samples/MissingPlugin/MissingPlugin.html


## Running in web mode:

`cd dist
mvn package`

Produces a zip file in target/teiid-console*.zip, which contains the overlays for an AS7 installation.


## EAP Build Profile

To run a customised EAP build (L&F) follow these steps:

- Create a dedicated version number (i.e. 1.0.0.EAP.CR2)
- Rebuild with the EAP profile enabled: 

`mvn -Peap clean install`


## Development Profile

Due to the increased number of permutations (additional languages) the full compile times
have increased quiet drastically. To work around this problem during development, we've added
a development build profile that restricts the languages to english and the browser permutations to firefox:

`mvn -Pdev clean install`

## Bind Address

In some cases you may want to bind both the AS and the hosted mode to a specific address.
A typical scenario is running a different OS (i.e windows) in a virtual machine.
To make such a setup work you need to bind the hosted mode environment and the application server
to a specific inet address that can be access from the virtual machine:

1) start the AS on a specific address:

`./bin/standalone.sh -Djboss.bind.address=192.168.2.126 
  -Djboss.bind.address.management=192.168.2.126`

2) launch hosted mode on a specific address:

`mvn clean -Dgwt.bindAddress=192.168.2.126 gwt:run`


## Problems?

Please post any questions to the teiid mailing list:
 teiid-dev@lists.jboss.org

Have fun.
