Playfff - Recommendation
=============

## About

Playfff is the middleware to deliver recommendations for Play SRG mobile applications.

## Compatibility

Play Android and Play iOS (2.8.3-272 and more) applications currently use it. 

## Recommendation type

### Media ecommendation list for a media

The API doesn't not support paginations, so mobile applications didn't implement pagination. The media recommendation list must have at least 49 items, the 50th is the requested media itself.

#### RTS `urn.contains(":rts:")`

- For videos `urn.contains(":video:")`, ask Peach recommendation `continuous_playback_mobile`.
- For audios `urn.contains(":audio:")`:
	- Get `IL-Media`. It returns an empty list if it's a `LIVESTREAM` or a `SCHEDULED_LIVESTREAM`.
	- Get `IL-EpisodeComposition`, last 100 episodes. Sort in date ascending order.
	- Determine if the media is a full length or a clip. Separate full length list and the clip list.
	- Get the media position in the related lists. Split oldests and newests.
	- Recommendation list:
		- Newest medias in date ascending order. Then:
		- if `nextUrl` exists (show has more than 100 episodes), oldest medias in date descending order.		- else (show has less than 100 episodes), oldest medias in date ascending order.

#### Other BUs

- No recommendation provided. It returns an empty list.
 
## License

To be defined.
