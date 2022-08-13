# Release workflow

Use the following workflow when releasing a new version of the service.

## State before releasing to production

- On `main` branch, version number is greater than the last tagged number (`project.version` in `pom-xml`). 
- Last commit on `main` branch passed tests (✅ green check mark on Github).
- Last commit on `main` branch is deployed on Heroku `playfff-dev` application (Environments on Github).
- All changes are tested with the Heroku `playfff-dev` application (staging).


## Releasing staging to production

ℹ️ No need to rebuild the application.

- Promote Heroku `playfff-dev` build to Heroku production application, using the pipeline (website or cmd).
- Tag the related commit with the version number as name (`project.version` in `pom-xml`).
- Bump the version number on `main` branch to prepare for the next release.
- Push `main` branch and tag to Github remote repository.
- Close the milestone and issues on Github.
- Create the Github release linked to this tag. Use issues, PRs and a global diff to write release notes.

## Heroku command lines

All command lines can be done on the Heroku website as well.

### Release
- `heroku pipelines:info playfff` to get the pipeline information.
- `heroku releases -a playfff-dev` to get latest deployments on *staging*.
- `heroku releases -a $PROD_APP` to get latest deployments on *production*.
- `heroku pipelines:promote -a playfff-dev` to promote *staging* to *production*.

### Basic health service
- `heroku logs -t -a playfff-dev` to get *staging* server logs.
- `heroku logs -t -a $PROD_APP` to get *production* server logs.
- `heroku dyno:restart -a playfff-dev` tp restart *staging* server.
- `heroku dyno:restart -a $PROD_APP` tp restart *production* server.