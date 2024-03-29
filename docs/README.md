[![SRG Logger logo](README-images/logo.png)](https://github.com/SRGSSR/pfff)

[![GitHub release (latest by date)](https://img.shields.io/github/v/release/SRGSSR/pfff)](https://github.com/SRGSSR/pfff/releases) [![GitHub license](https://img.shields.io/github/license/SRGSSR/pfff)](https://github.com/SRGSSR/pfff/blob/master/LICENSE) [![GitHub deployments](https://img.shields.io/github/deployments/srgssr/playsrg-playfff/playfff-dev)](https://github.com/SRGSSR/playsrg-playfff/deployments/activity_log?environment=playfff-dev)


## About

Playfff is a SRG micro service to serve extra datas to Play applications. Playfff means "Play Features and Functionalities with Flair".

## Compatibility

- The service uses Spring Boot, a postgresql database and a NodeJS server.
- For admin sessions and multi instances, only sticky sessions are supported.

## Installation

A Java 1.8 development environment with Maven is needed. To run the NodeJS server, `npm` is needed as well.

## Development 
 * Backend: See `pom-xml` for maven configuration.
 * Frontend: See `portal-app/package.json` for Angular application.
 * Run locally with in a memory database: `mvn spring-boot:run -Dspring-boot.run.jvmArguments="-DPFFF_PASSWORD=test -DPFFF_USER=test"`

## Release workflow

A [release workflow](RELEASE_WORFLOW.md) is described to ensure stable releases.

## Available environment variables

A wide list of parameters are available.

* `PFFF_USER` (optional, string): A user login to admin service.
* `PFFF_PASSWORD` (optional, string): A user password to admin service.
* `DEEP_LINK_REFRESH_DELAY_MS` (optional, integer): Scheduled fixed delay before refreshing the deep link script cache. If not set, defaults is `300000`.
* `DEEP_LINK_REFRESH_INITIAL_DELAY_MS` (optional, integer): Scheduled fixed initial delay before refreshing the deep link cache. If not set, defaults is `0`.
* `MAX_DEEP_LINK_REPORTS` (optional, integer): Maximum number of deep link reports in the database. If not set, defaults is `2500`.
* `DEEP_LINK_ENVIRONMENTS` (optional, string, multiple): List of `Environment`s to pull deep link dynamic informations. If not set, defaults is `PROD`.
* `UPDATE_CHECK_DISABLED` (optional, boolean): Disable checking if a recommended or required update is available, if set to `true`. If not set, defaults is `false`.
* `RTS_RECOMMENDATION_USED` (optional, boolean): Use RTS recommendation engine (RE) in usages validated by the BU, if set to `true`. Otherwise, use default RE. If not set, defaults is `true`.
* `SRF_RECOMMENDATION_USED` (optional, boolean): Use SRF recommendation engine (RE) in usages validated by the BU, if set to `true`. Otherwise, use default RE. If not set, defaults is `true`.
* `ASCENDING_EPISODES_MAX` (optional, integer): max episodes for a show to be considered as a podcast with ascending published date. If not set, defaults is `25`.
* `RSI_LIVECENTER_ONLY_WITH_RESULT` (optional, boolean): For RSI swisstxt media urns, the default RE returns events only with sport result, if set to `true`. If not set, defaults is `true`.
* `RTS_LIVECENTER_ONLY_WITH_RESULT` (optional, boolean): For RTS swisstxt media urns, the default RE returns events only with sport result, if set to `true`. If not set, defaults is `false`.
* `SRF_LIVECENTER_ONLY_WITH_RESULT` (optional, boolean): For SRF swisstxt media urns, the default RE returns events only with sport result, if set to `true`. If not set, defaults is `true`.

## API
 * `urn` (string): an unique identifier.
 * `recommendedList` (object): a recommended result list with proterties:
 	* `recommendationId` (string, optional): the recommendation identifer from the service.
 	* `urns` (array): array of `urn`.
 	* `title` (string, optional): title of the playlist.
 * `package` (string): Android package name or iOS bundle identifier.
 * `version` (string): mobile application version.

### Public APIs

#### Global

* `/api/v1/version` : get service version.

#### Update check

* `/api/v1/update/check?package={package}&version={version}` : get Update object.

#### What's new

* `/api/v1/whatisnew/text?package={package}&version={version}` : get WhatIsNewResult object.
* `/api/v1/whatisnew/html?package={package}&version={version}` : get What's new html format.

#### Deep link

* `/api/v1/deeplink/parsePlayUrl.js` (GET): Get the Play web URL to mobile application scheme URL (v1) script (deep link script). The HTTP ETag caching is supported.
* `/api/v2/deeplink/parsePlayUrl.js` (GET): Get the Play web URL to mobile application scheme URL (v2) script (deep link script). The HTTP ETag caching is supported.
* `/api/v1/deeplink/report` (POST) : create or update a new deep link report object from the JSON body object. Send a report only if the script returns `[scheme]://unsupported`. The JSON object must contains:
  * `clientTime` (string): date of the parsing execution in `yyyy-MM-dd'T'HH:mm:ssXXX` format.
  * `clientId` (string): Bundle id or package name.
  * `jsVersion` (integer): the `parsePlayUrl.js` value of `parsePlayUrlVersion` variable. 
  * `url` (string): the unparsing url.

For more informations, see the [deep link documentation](DEEP_LINK.md).

#### Recommendation for a media

* `/api/v2/playlist/recommendation/continuousPlayback/{urn}` : get recommended medias for a continuous playback purpose.
	* `standalone` (optional, boolean): Recommendation for the playback mode. Default is `false`.
	* Returns a `recommendedList` object.

* `/api/v2/playlist/recommendation/relatedContent/{urn}` : get recommended medias for a related content purpose.
	* `standalone` (optional, boolean): Recommendation for the playback mode. Default is `false`.
	* Returns a `recommendedList` object.

* *Deprecated* `/api/v1/playlist/recommendation/continuousPlayback/{urn}` : get recommended medias for a continuous playback purpose.	* `standalone` (optional, boolean): Recommendation for the playback mode. Default is `false`.
	* `format` (optional, string): If set to `urn`, it returns an URN list. Default is `media` and redirects to an IL media list response.

All those media recommendation APIs return the requested media at the first position in the list in order to create a playlist, unless the recommended list is empty. For more informations, see the [recommendation engine documentation](RECOMMENDATION.md).

#### Personnal recommendation for a user

* `/api/v2/playlist/personalRecommendation` : get personal recommended medias.
	* `userId` (optional, string): `UserId` to use for a personal recommendation.
	* Returns a `recommendedList` object.

## Private APIs

Private APIs need a user authentification.

#### Update check

* `/api/v1/update` (GET) : get All update objects.
* `/api/v1/update` (POST) : create a new update object from the body object.
* `/api/v1/update` (PUT) : update an update object from the body object.
* `/api/v1/update/{id}` (GET) : get update object with `id` identifier.
* `/api/v1/update/{id}` (DELETE) : remove update object with `id` identifier.

#### Deep link

* `/api/v1/deeplink/report` (GET) : get All deep link reports.
* `/api/v1/deeplink/report/{id}` (GET) : get deep link report object with `id` identifier.
* `/api/v1/deeplink/report/{id}` (DELETE) : remove deep link report object with `id` identifier.
 
## License

See the [LICENSE](../LICENSE) file for more information.
