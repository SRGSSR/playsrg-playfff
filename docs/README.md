Playfff
=============

## About

Playfff is a SRG micro service to serve extra datas to Play applications. Playfff  means "Play Features and Functionalities with Flair".

## Compatibility

The service uses Spring Boot, a postgresql database and a NodeJS server.

## Installation

A Java development environment with Maven is needed.

## Available environment variables

A wide list of parameters are available.

* `PFFF_USER` (optional, string): A user login to admin service.
* `PFFF_PASSWORD` (optional, string): A user password to admin service. 

## API
 * `urn` (string): an unique identifier.
 * `recommendedList` (object): a recommended result list with proterties:
 	* `recommendationId` (string): the recommendation identifer from the service.
 	* `urns`(array): array of `urn`.
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

#### Recommendation

* `/api/v2/playlist/recommendation/continuousPlayback/{urn}` : get media list object.
	* `standalone` (optional, boolean): Recommendation for the playback mode. Default is `false`.
	* `format` (optional, string): If set to `urn`, it returns a `recommendedList` object. Default is `media` and redirects to an IL media list response.

* `/api/v1/playlist/recommendation/continuousPlayback/{urn}` : get media list object.
	* `standalone` (optional, boolean): Recommendation for the playback mode. Default is `false`.
	* `format` (optional, string): If set to `urn`, it returns an URN list. Default is `media` and redirects to an IL media list response.

## Private APIs

Private APIs need a user authentification.

#### Update check

* `/api/v1/update` (GET) : get All update objects.
* `/api/v1/update` (POST) : create a new update object from the body object.
* `/api/v1/update` (PUT) : update an update object from the body object.
* `/api/v1/update/{id}` (GET) : get update object with `id` identifier.
* `/api/v1/update/{id}` (DELETE) : remove update object with `id` identifier.

* 
 
## License

To be defined.
