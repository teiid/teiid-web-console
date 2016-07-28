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

## Debug

### configure

    Modify *gui/src/main/resources/org/jboss/as/console/TeiidExtension.gwt.xml* for Super Dev Mode.

~~~~
<module rename-to="app">
    <inherits name="org.jboss.as.console.composite.WildFlyDev"/>
    <stylesheet src="teiid.css" />
    <add-linker name="xsiframe"/> 
</module>
~~~~

### Compile 

~~~~
$ cd ${teiid-web-console}
$ mvn -Pdev clean install 
$ cd app
$ mvn package -Peapdev
~~~~

### Deploy

~~~
$ cd ${teiid-web-console)/app/target
$ cp teiid-console-app-***-resources.jar $TeiidServer_HOME/modules/system/layers/dv/org/jboss/as/console/main/
$ vi $TeiidServer_HOME/modules/system/layers/dv/org/jboss/as/console/main/module.xml 
~~~

Modify the value of  resource-root to teiid-console-app-***-resources.jar

### Run

* GWT Code Server
~~~~
    $ cd cd ${teiid-web-console)/app
    $ mvn gwt:run-codeserver -Peapdev  
~~~~

>The server is ready when you can see *[INFO] The code server is ready at http://127.0.0.1:9876/* in terminal.

* Teiid Server

`./bin/standalone.sh`

### Debug

* open your **Chrome** browser and go to http://localhost:9876/
* drag and drop *Dev Mode On* button to your bookmarks panel
* login to http://localhost:9990/console/
* click "Dev Mode On" on bookmark
* click "Compile" button
* open developer debugger (in Chrome CTRL + Shift + I)
* go to Sources tab
* CTRL + P to search for a file you want to debug
* set a breakpoint


## Problems?

Please post any questions to the teiid mailing list:
 teiid-dev@lists.jboss.org

Have fun.
