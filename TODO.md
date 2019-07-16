

- [x] move away from build.sbt to a /project directory for
SBT build settings
- [x] set proper names and versions for the SBT build
- [ ] accept conf files as arguments; don't rely on
hardcoded reference/application.conf
- [ ] accept output directory as argument; don't rely on
hardcoded ./output location
- [x] allow for multiple different base service types (GCE, GAE, CF, etc)
- [ ] provide a mechanism for determining which files get String Template
variable substitution
- [x] dynamically generate dynamic variable map; don't hardcode vars 
- [ ] pull .tf templates from github, not hardcoded in this repo
- [x] use a real logging library instead of println
- [ ] update service templates to latest/greatest
- [ ] decide on Scala package name (org.broadinstitute...)
- [ ] in general, clean up the code 


