# Domino SBT Project Template

This repo is an evolving view into what we think makes the start for a successful project at Domino. It does its best to provide some useful and sane defaults for the following:

1. Modern `built.sbt` and standard, useful plugins
2. Build/release/deploy scripts for CI to run
3. Scalastyle sane defaults
4. Best-practice compiler options

## Default SBT Projects

Your `build.sbt` already defines a few projects:

* `tests` -- unit, integration tests for all the projects
* `benchmarks` -- benchmarking project using `JmhPlugin`
* `core` -- core code to share among server, client
* `server` -- server code to be deployed
* `client` -- client code that can be distributed to other domino projects
* `all` -- the aggregate project

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

## Files and Directories

`/scripts`
: The place where all your build, release, deploy scripts will go. Usually these will be run by jenkins.

`scala-style.xml`
: Make your compiler tell you that you are doing it wrong before you hit code review

`build.sbt`
: The default sbt build file

`version.sbt`
: Place to version your project, this makes the sbt release cycle much easier to do
