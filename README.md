This is a github clone of http://code.google.com/p/jst4j/ for testing.

Java has a robust implementation of Javascript with the Rhino project. This project builds upon that great project to add a template facility in Java using Javascript as the template language. Why?

JSP is old and overburdened with bad ideas that aren't up to the challenges of modern web applications. We need to refactor our view to something more modern. Here are just some of the feature severely lacking from JSP (and other template engines for Java):

* Doesn't handle reusing portions of view code elegantly.
* Doesn't support layouts (without some complex extension tiles, etc)
* No encapsulation of HTML construction (you can't just delegate a portion of a view to another JSP)
* No way to parameterize portions of a page code to reuse in across multiple pages.
* Way too monolithic. (i.e. zero reuse).
* Can't be used outside of servlet container.

JST is a portable template library that can be used as a replacement for JSP, velocity, or freemarker. It uses a ubiquitous language every web developer has to know Javascript. It's turing complete! Which means it has all of the features you want from a real language while giving you support for lots of the features you need for building views.

* Don't have to learn a new language.
* Complex layout using a simple interface.
* Parameterized template delegation. (Think partials from Ruby on Rails).
* HTML construction encapsulation.
* Plain old functions.
* Simple support for utility methods.
* Reusable library for common view operations.
* Spring and Spring MVC Integration.
* Email support (write emails with JST templates).
* Transparent Java integration. Call methods on any Java object you want to use.
* Seamless integration with java.util and native Javascript Array/Object types. (v1.2)