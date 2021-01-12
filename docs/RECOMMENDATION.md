Playfff - Recommendation
=============

## About

Playfff is the middleware to deliver recommendations for Play SRG mobile applications.

## Compatibility

Since July 2018, Play Android (2.0.207 and more) and Play iOS (2.8.3-272 and more) applications currently use this recommendation service.

## Media recommendation list for a media

The API doesn't not support paginations, therefore mobile applications didn't implement pagination. The media recommendation list must have at least 49 items, the 50th is the requested media.

### Purposes

- `continuousPlayback` is used to display one proposition, with a count down and an autoplay.
- `relatedContent` is used to display some propositions in a swimlane or a grid, without an autoplay.

| BU Content | Continuous playback | Related content |
| :--- | :---: | :---: |
| RSI audios | Pfff RE | Pfff RE |
| RSI videos | Pfff RE | Pfff RE |
| RTR audios | Pfff RE | Pfff RE |
| RTR videos | Pfff RE | Pfff RE |
| RTS audios | Pfff RE | Pfff RE |
| RTS videos | Pfff RE | Pfff RE |
| SRF audios | Pfff RE | **SRF RE** |
| SRF videos | Pfff RE | **SRF RE** |
| SWI videos | Pfff RE | Pfff RE |
| Event videos | N/A | N/A |
| Swisstxt videos | N/A | N/A |

### Recommendation engines used

#### RTS recommendation engine (RTS RE)

- For RTS videos, it can ask `rts-datalab.azure-api` recommendation `continuous_playback_endscreen` service, without personalization.

#### SRF recommendation engine (SRF RE)

- For SRF videos and SRF audios, it can ask `IL-MediaList` recommendation `Recommended-byUrn` service, without personalization.

#### Playfff recommendation engine (Pfff RE)

- For RSI, RTR, RTS, SRF, SWI videos and RSI, RTR, RTS, SRF audios, it can ask `Playfff` recommendation.
- Based on IL requests, without personalization. Here is how it works:
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
- It can get `IL-MediaComposition` if the media urn isn't found, and has not the `CLIP` type.
- It does not return clips if `VIDEO` media type and `standalone == false`.
- If clips are not in `IL-EpisodeComposition`, it fallbacks to full lengths.

#### Swisstxt URNs or other MAMs

- No recommendation provided. It returns an empty list.

## Media recommendation list for a user

The API doesn't not support paginations, therefore mobile applications didn't implement pagination. The media recommendation list must have at least 50 items.

#### RTS

- With and without an `userId`, it asks `rts-datalab.azure-api` recommendation `play_home_personal_rec` service.

#### Other BUs

- Nothing implemented.
