# Argent

A Kotlin multiplatform fullstack checklist app

### Api

Ktor + exposed

### Client

Kotlin react with styled components

## Develop
`docker-compose up -d` to start database in container

`./gradlew run` to build client bundle and serve it from server. Combine it with `./gradlew jvmBuild -t` for automatic reloads. Serves at http://localhost:8008 as default.

`./gradlew jvmTest` will put some data in the db

If you want hot module reloading while developing client you can also run `./gradlew jsBrowserDevelopmentRun -t` and load the application fom webpack dev server http://localhost:3002

Set the environment variable `ORG_GRADLE_PROJECT_isProduction=true` to generate production bundles

## Migrations
Migrations are run by Flyway on startup and should be placed in `src/jvmMain/resources/migrations` 

## Work to be done
- JS testing
- Redux?
- Jib to create images
