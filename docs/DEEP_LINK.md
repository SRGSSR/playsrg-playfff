Playfff - Deeplink
=============

## About

Playfff is the middleware to help Play SRG mobile applications understanding Play web urls.

## Compatibility

* [Since July 2019](https://github.com/SRGSSR/playsrg-playfff/releases/tag/13), Play iOS applications download `parsePlayUrl.js` v1 file.
* [Since September 2019](https://github.com/SRGSSR/playsrg-playfff/releases/tag/17), Play Android and Play iOS applications download `parsePlayUrl.js` v2 file.

## Convert a web url to application url

The conversion is done with a javascript function, included in the `parsePlayUrl.js` file. The file is downloaded with [the GET API](README.md#deep-link).

### V1

Function is: `parseForPlayApp(scheme, hostname, pathname, queryParams, anchor)`:

* Parameters: the web url to parse, splitted in variables.
* It returns an application url, conform to [an old Play iOS application URL](https://github.com/SRGSSR/playsrg-apple/blob/ios/2.9.5-313/docs/URL_SCHEMES.md) specification.
	
### V2

Function is: `parseForPlayApp(scheme, hostname, pathname, queryParams, anchor, supportedAppHostnames)`:

* Parameters: the web url to parse, splitted in variables.
* `supportedAppHostnames`: an optional array to share the supported application url hostnames. Required to support the new `micropage` or `page` hostnames conversion.
* It returns an application url, conform to [Play iOS application URL](https://github.com/SRGSSR/playsrg-apple/blob/develop/docs/CUSTOM_URLS_AND_UNIVERSAL_LINKS.md) and [Play Android custom URL](https://github.com/SRGSSR/playsrg-android/blob/main/doc/schemeUrl.md) specifications.
* An optional `preview=true` query parameter can be added to the returned application url. It's related to PAC (Play Application Configuration) and it's used to have a preview of the content page.
* If the JS script returns the `unsupported` hostname, the application can share it to the server using [the POST API](README.md#deep-link).

## Get updated

The server pulls IL every 5 minutes to have updated contents (topics).

* Topic list requests: IL `/integrationlayer/2.0/[BU]/topicList/tv`.
* Topic title are converted to *SeoName* value [used by the Play web application](https://github.com/SRGSSR/playsrg-playfff/issues/46) for topic urls.

## How to test

Curently, unit tests are a on private repository, accessible via a public url:

* [Tests v1](https://play-mmf.herokuapp.com/deeplink/tests/v1/index.html)
* [Tests V2](https://play-mmf.herokuapp.com/deeplink/tests/v2/index.html)

To test Play web url conversions, a tool is available:

* [Middleware Prod deeplink tool](https://play-mmf.herokuapp.com/deeplink/index.html)
* [Middleware Dev deeplink tool](https://play-mmf.herokuapp.com/deeplink/index.html#dev)
* [Old V1 deeplink tool](https://play-mmf.herokuapp.com/deeplink/index.html#localv1)
* [Next V2 version deeplink tool](http://play-mmf.herokuapp.com/deeplink/index.html#localv2)
