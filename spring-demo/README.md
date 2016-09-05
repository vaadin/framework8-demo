spring-demo
==============

Example of a full-blown Vaadin application that uses Spring.

Project Structure
=================

The project consists of the following three modules:

- spring-demo: root project with common metadata and configuration
- spring-demo-backend: backend service used by the UI
- spring-demo-ui: main application module, development time

Workflow
========

To compile the entire project, run "mvn install" in the parent project.

Other basic workflow steps:

- getting started
- compiling the whole project
  - run "mvn install" in parent project
- developing the application
  - edit code in the ui module
  - run "mvn spring-boot:run" in ui module
  - open http://localhost:8080/
- client side changes or add-ons
  - edit code/POM in widgetset module
  - run "mvn install" in widgetset module
  - if a new add-on has an embedded theme, run "mvn vaadin:update-theme" in the ui module
- debugging client side code
  - run "mvn vaadin:run-codeserver" in widgetset module
  - activate Super Dev Mode in the debug window of the application
- enabling production mode
  - set "vaadin.servlet.productionMode=true" in application.proeprties file in ui module


Developing a theme using the runtime compiler
-------------------------

When developing the theme, Vaadin can be configured to compile the SASS based
theme at runtime in the server. This way you can just modify the scss files in
your IDE and reload the browser to see changes.

To use on the runtime compilation, open pom.xml of your UI project and comment 
out the compile-theme goal from vaadin-maven-plugin configuration. To remove 
an existing pre-compiled theme, remove the styles.css file in the theme directory.

When using the runtime compiler, running the application in the "run" mode 
(rather than in "debug" mode) can speed up consecutive theme compilations
significantly.
