Playfff - Recommendation
=============

## About

Playfff is the middleware to deliver recommendations for Play SRG mobile applications.

## Compatibility

Since July 2018, Play Android (2.0.207 and more) and Play iOS (2.8.3-272 and more) applications currently use this recommendation service.

## Recommendation type

### Media recommendation list for a media

The API doesn't not support paginations, therefore mobile applications didn't implement pagination. The media recommendation list must have at least 49 items, the 50th is the requested media.

#### RTS `urn.contains(":rts:")`

- For videos `urn.contains(":video:")`, it asks Peach recommendation `continuous_playback_mobile` service.
- For audios `urn.contains(":audio:")`, it asks Playfff recommendation. Based on IL requests, without personalization. Here is how it works:
	- Get `IL-Media`. It returns an empty list if it's a `LIVESTREAM` or a `SCHEDULED_LIVESTREAM`.
	- Get `IL-EpisodeComposition` with last 100 episodes. Sort episodes with a date ascending order.
	- Determine if the media is a full length or a clip.
	- Separate in a full length list and a clip (audio only) list.
	- Get the requested media position in the related list. Split oldests and newests.
	- Recommendation list:
		- Newest medias in the date ascending order.
		- Then:
			- *If* `nextUrl` exists (show has more than 100 episodes), oldest medias in the date descending order.
			- *Else* (show has less than 100 episodes), oldest medias in the date ascending order.

#### Other BUs

- No recommendation provided. It returns an empty list.

### Media recommendation list for a user

The API doesn't not support paginations, therefore mobile applications didn't implement pagination. The media recommendation list must have at least 50 items.

#### RTS

- With and without an `userId`, it asks Peach recommendation `play_home_personal_rec` service.
 
## License

To be defined.
