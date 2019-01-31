Playfff - Recommendation
=============

## About

Playfff is the middleware to deliver recommendations for Play SRG mobile applications.

## Compatibility

Play Android and Play iOS (2.8.3-272 and more) applications currently use it. 

## Recommendation type

### Recommendation list for a media

The API doesn't not support paginations, mobile applications don't implement also pagination. The recommendation list must have at least 49 items, the 50th is the requested media itself.

#### RTS `urn.contains(":rts:")`

- For videos `urn.contains(":video:")`, ask Peach recommendation `continuous_playback_mobile`.
- For audios `urn.contains(":audio:")`:
	- Get `IL-Media`. It returns an empty list if it's a `LIVESTREAM` or a `SCHEDULED_LIVESTREAM`.
	- Get `IL-EpisodeComposition`, last 100 episodes.
	- Determine if the media is a full length or a clip. Separate full length list and the clip list.
	- Get the media position in the related lists.
	- Recommendation list:
		- Newest media from oldest to the last published. Then:
		- if `nextUrl` (more than 100 episodes), old ones from newest to oldest to newer.
		- else, from the older one to newer.

#### Other BUs

- No recommendation provided. It returns an empty list.
 
## License

To be defined.
