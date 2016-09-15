# SBT Project Template

This repo is an evolving view into what I think makes the start for a
successful project. It does its best to provide some useful and sane defaults
for the following:

1. Modern `built.sbt` and standard, useful plugins
2. Build/release/deploy scripts for CI to run
3. Scalastyle and Scalariform sane defaults
4. Best-practice compiler options
5. Make scaladoc and user documentation a first class citizen

## Default SBT Projects

Your `build.sbt` already defines a few projects:

* `test` -- unit, integration tests for all the projects
* `benchmark` -- benchmarking project using `JmhPlugin`
* `core` -- core code to share among server, client
* `server` -- server code to be deployed
* `client` -- client code that can be distributed to other projects
* `all` -- the aggregate project

Each of these projects is its own directory and have dependencies defined in
the `build.sbt`

## Default SBT Plugins

`sbt-release`
: Make sbt do the heavy lifting for releasing
: https://github.com/sbt/sbt-release

`sbt-pgp`
: Sign our releases
: https://github.com/sbt/sbt-pgp

`scalastyle-sbt-plugin`
: Enforce scalastyle rules
: https://github.com/scalastyle/scalastyle-sbt-plugin

`sbt-scoverage`
: Code coverage
: https://github.com/scoverage/sbt-scoverage

`sbt-jmh`
: Benchmark our code
: https://github.com/ktoso/sbt-jmh

`sbt-scalariform`
: Auto-format our code
: https://github.com/sbt/sbt-scalariform

`sbt-coursier`
: Replaces ivy, faster, no mutable cache to share among other projects
: https://github.com/alexarchambault/coursier

`sbt-ghpages`
: Publish user documentation and scaladoc to [Github Pages](https://pages.github.com/)
: https://github.com/sbt/sbt-ghpages

`sbt-assembly`
: Create fat jars for deployment
: https://github.com/sbt/sbt-assembly

`sbt-unidoc`
: Unifies scaladoc/javadoc across multiple projects
: https://github.com/sbt/sbt-unidoc

## Files and Directories

`/scripts`
: The place where all your build, release, deploy scripts will go. Usually these will be run by jenkins.

`scala-style.xml`
: Make your compiler tell you that you are doing it wrong before you hit code review

`build.sbt`
: The default sbt build file

`version.sbt`
: Place to version your project, this makes the sbt release cycle much easier to do
