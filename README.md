# Specs Service

[![Build Status](http://build.etilizepak.com/buildStatus/icon?job=specs-service-nightly)](http://build.etilizepak.com/job/specs-service-nightly/)

Specs Service is is used to get the specifications of products from conQuire.

# Building from Source

Clone the git repository using the URL on the Gitlab home page:

    $ git clone http://git.etilizepak.com/etilize/specs-service.git
    $ cd specs-service

## Command Line
Use Maven 2.2 or 3.0, then on the command line:

    $ mvn install

## SpringSource Tool Suite (STS)
In STS (or any Eclipse distro or other IDE with Maven support), import the module directories as existing projects.  They should compile and the tests should run with no additional steps.

# Contributing to Smart Data Extraction
Before checkin make sure to run mvn clean install -Pdev. This will add license and format code according to etilize standards.

Here are some ways for you to get involved:

* Create [JIRA](http://jira.etilizepak.com/browse/SDE) tickets for bugs and new features and comment and vote on the ones that you are interested in.
* If you want to write code, we encourage contributions through merge requests from forks of this repository.
If you want to contribute code this way, please familiarize yourself with the process outlined for contributing to projects here: [Contributor Guidelines](http://git.etilizepak.com/automation/sde/wikis/Contributor-Guidelines).