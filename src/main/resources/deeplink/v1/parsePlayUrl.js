// parsePlayUrl

var parsePlayUrlVersion = 45;
var parsePlayUrlBuild = "mmf";

if (!console) {
	var console = {
		log: function () { }
	}
}

function parseForPlayApp(scheme, hostname, pathname, queryParams, anchor) {
	originalUrl = {
		"scheme": scheme,
		"hostname": hostname,
		"pathname": pathname,
		"queryParams": queryParams,
		"anchor": anchor
	};

	// fix path issue
	pathname = pathname.replace("//", "/");

	// Remove last slash if any
	slashCount = pathname.split("/").length - 1;
	if (slashCount > 2 && pathname.endsWith("/")) {
		pathname = pathname.slice(0, -1)
	}

    originalPathname = pathname

	// Case insensitive
	hostname = hostname.toLowerCase();
	pathname = pathname.toLowerCase();

	// Get BU
	var bu = null;
	switch (true) {
		case hostname.endsWith("tp.srgssr.ch") || hostname.endsWith("player.rts.ch") || hostname.endsWith("player.rsi.ch") || hostname.endsWith("player.rtr.ch") || hostname.endsWith("player.swissinfo.ch") || hostname.endsWith("player.srf.ch") || (hostname.includes("srgssr") && pathname.startsWith("/srgletterbox-web")):
			bu = "lb";
			break;
		case (hostname.includes("rts.ch") && !hostname.includes("play-staging")) || hostname.includes("srgplayer-rts") || (hostname.includes("play-mmf") && pathname.startsWith("/rts/")) || ((hostname.includes("play-web") || hostname.includes("play-staging")) && pathname.startsWith("/rts/")):
			bu = "rts";
			break;
		case hostname.includes("rsi.ch") || hostname.includes("srgplayer-rsi") || (hostname.includes("play-mmf") && pathname.startsWith("/rsi/")) || ((hostname.includes("play-web") || hostname.includes("play-staging")) && pathname.startsWith("/rsi/")):
			bu = "rsi";
			break;
		case hostname.includes("rtr.ch") || hostname.includes("srgplayer-rtr") || (hostname.includes("play-mmf") && pathname.startsWith("/rtr/")) || ((hostname.includes("play-web") || hostname.includes("play-staging")) && pathname.startsWith("/rtr/")):
			bu = "rtr";
			break;
		case hostname.includes("swissinfo.ch") || hostname.includes("srgplayer-swi") || (hostname.includes("play-mmf") && pathname.startsWith("/swi/")) || ((hostname.includes("play-web") || hostname.includes("play-staging")) && pathname.startsWith("/swi/")):
			bu = "swi";
			break;
		case hostname.includes("srf.ch") || hostname.includes("srgplayer-srf") || (hostname.includes("play-mmf") && pathname.startsWith("/srf/")) || ((hostname.includes("play-web") || hostname.includes("play-staging")) && pathname.startsWith("/srf/")):
			bu = "srf";
			break;
		case hostname.includes("play-mmf") && pathname.startsWith("/mmf/"):
			bu = "mmf";
			break;
		case hostname.includes("radioswisspop.ch") || hostname.includes("radioswissclassic.ch") || hostname.includes("radioswissjazz.ch"):
			bu = "radioswiss";
			break;
	}

	if (!bu) {
		console.log("This URL is not a Play SRG URL.");
		return null;
	}

	// Get server
	var server = serverForUrl(hostname, pathname, queryParams);

	/**
	 *  Catch special case: Player web
	 *
	 *  Ex: https://tp.srgssr.ch/p/rts/default?urn=urn:rts:video:6735513
	 *  Ex: https://player.rts.ch/p/rts/default?urn=urn:rts:video:6735513&start=60
	 */
	if (bu == "lb") {
		if (pathname.startsWith("/p/")) {
			var mediaUrn = queryParams["urn"];
			if (mediaUrn) {
				var redirectBu = "lb";
				switch (true) {
					case pathname.startsWith("/p/srf/"):
						redirectBu = "srf";
						break;
					case pathname.startsWith("/p/rts/"):
						redirectBu = "rts";
						break;
					case pathname.startsWith("/p/rsi/"):
						redirectBu = "rsi";
						break;
					case pathname.startsWith("/p/rtr/"):
						redirectBu = "rtr";
						break;
					case pathname.startsWith("/p/swi/"):
						redirectBu = "swi";
						break;
				}
				var startTime = queryParams["start"];
				return openMediaUrn(server, redirectBu, mediaUrn, startTime);
			}
		}
		else if (pathname.startsWith("/srgletterbox-web")) {
			var mediaUrn = queryParams["urn"];
			if (mediaUrn) {
				var startTime = queryParams["pendingSeek"];
				return openMediaUrn(server, "lb", mediaUrn, startTime);
			}
		}
	}

	/**
	 *  Catch special case: radio swiss
	 *
	 *  Ex: https://www.radioswisspop.ch/de/webplayer
	 *  Ex: https://www.radioswissclassic.ch/fr/webplayer
	 *  Ex: https://www.radioswissjazz.ch/it/webplayer
	 */
	if (bu == "radioswiss") {
		var redirectBu = null;
		switch (true) {
			case pathname.startsWith("/de"):
				redirectBu = "srf";
				break;
			case pathname.startsWith("/fr"):
				redirectBu = "rts";
				break;
			case pathname.startsWith("/it"):
				redirectBu = "rsi";
				break;
		}
		if (redirectBu) {
			return openURL(server, redirectBu, originalUrl);
		}
	}

	/**
	 *  Catch special case: Play MMF
	 *
	 *  Ex: https://play-mmf.herokuapp.com/mmf/
	 *  Ex: https://play-mmf.herokuapp.com/mmf/media/urn:rts:video:_rts_info_delay
	 */
	if (bu == "mmf") {
		if (pathname.includes("/media/")) {
			var mediaUrn = pathname.split("/").slice(-1)[0];
			return openMediaUrn(server, bu, mediaUrn, null);
		}

		// Returns default TV homepage
		return openPage(server, bu, "tv:home", null, null);
	}

	if (hostname.includes("play-mmf") && !pathname.startsWith("/mmf/")) {
		pathname = pathname.substring(4);
	}

	if (hostname.includes("play-web") || hostname.includes("play-staging")) {
		pathname = pathname.substring(4);
		pathname = pathname.replace("/production/play", "/play");
		pathname = pathname.replace("/stage/play", "/play");
		pathname = pathname.replace("/test/play", "/play");
	}

	/**
	 *  Catch special case: shared RTS media urls built by RTS MAM.
	 *
	 *  Ex: https://www.rts.ch/video
	 *  Ex: https://www.rts.ch/video/emissions/signes/9901229-la-route-de-lexil-en-langue-des-signes.html
	 *  Ex: https://www.rts.ch/audio/la-1ere
	 */
	if (bu == "rts" && (pathname.startsWith("/video") || pathname.startsWith("/audio"))) {
		var mediaId = null;

		if (pathname.endsWith(".html")) {
			var lastPathComponent = pathname.split("/").slice(-1)[0];
			mediaId = lastPathComponent.split('.')[0].split('-')[0];
		}

		if (mediaId) {
			var mediaType = (pathname.startsWith("/video")) ? "video" : "audio";
			return openMedia(server, bu, mediaType, mediaId, null);
		}
		else if (pathname.startsWith("/video")) {
			// Returns default TV homepage
			return openPage(server, bu, "tv:home", null, null);
		}
		else {
			var channelId = null;
			var paths = pathname.split('/');
			if (paths.length > 2) {
				var radioId = paths[2];
				switch (radioId) {
					case "la-1ere":
						channelId = "a9e7621504c6959e35c3ecbe7f6bed0446cdf8da";
						break;
					case "espace-2":
						channelId = "a83f29dee7a5d0d3f9fccdb9c92161b1afb512db";
						break;
					case "couleur3":
						channelId = "8ceb28d9b3f1dd876d1df1780f908578cbefc3d7";
						break;
					case "option-musique":
						channelId = "f8517e5319a515e013551eea15aa114fa5cfbc3a";
						break;
				}
			}
			// Returns default radio homepage
			return openPage(server, bu, "radio:home", channelId, null);
		}
	}

	if (!(pathname == "/play" || pathname.startsWith("/play/"))) {
		console.log("No /play path component in url.");
		return null;
	}

	/**
	 *  Catch classic media urls
	 *
	 *  Ex: https://www.rts.ch/play/tv/faut-pas-croire/video/exportations-darmes--la-suisse-vend-t-elle-la-guerre-ou-la-paix-?id=9938530
	 *  Ex: https://www.srf.ch/play/tv/_/video/_?urn=urn:srf:scheduled_livestream:video:6d49170d-16e9-45ef-a8aa-00873333a610 (strange)
	 */
	var mediaType = null;
	switch (true) {
		case pathname.includes("/video/"):
			mediaType = "video";
			break;
		case pathname.includes("/audio/"):
			mediaType = "audio";
			break;
	}

	if (mediaType) {
		var mediaUrn = queryParams["urn"];
		var mediaId = queryParams["id"];
		var startTime = queryParams["startTime"];
		if (mediaUrn) {
			return openMediaUrn(server, bu, mediaUrn, startTime);
		}
		else if (mediaId) {
			return openMedia(server, bu, mediaType, mediaId, startTime);
		}
		else {
			mediaType = null;
		}
	}

	/**
	 *  Catch redirect media urls
	 *
	 *  Ex: https://www.rts.ch/play/tv/redirect/detail/9938530
	 *  Ex: https://www.srf.ch/play/tv/redirect/Detail/99f040e9-b1e6-4d7a-bc08-d5639d600aa1
	 *  Ex: https://www.srf.ch/play/tv/redirect/detail/99f040e9-b1e6-4d7a-bc08-d5639d600aa1/
	 *  Ex: https://www.srf.ch/play/tv/redirect/detail/urn:srf:video:96705d59-c9d5-40c2-99c1-89ef5756ef24
	 */
	switch (true) {
		case pathname.includes("/tv/redirect/detail/"):
			mediaType = "video";
			break;
		case pathname.includes("/radio/redirect/detail/"):
			mediaType = "audio";
			break;
	}

	if (mediaType) {
		var mediaIdentifier = originalPathname.split("/").slice(-1)[0];
		var startTime = queryParams["startTime"];
		if (mediaIdentifier.startsWith("urn:")) {
			return openMediaUrn(server, bu, mediaIdentifier, startTime);
		}
		else if (mediaIdentifier) {
			return openMedia(server, bu, mediaType, mediaIdentifier, startTime);
		}
		else {
			mediaType = null;
		}
	}

	/**
	 *  Catch redirect livestream urls
	 *
	 *  Ex: https://www.srf.ch/play/tv/redirect/live/rsi?id=livestream_La1&title=LA+1
	 *  Ex: https://www.rsi.ch/play/tv/redirect/live/rts?id=3608506&title=RTS+1
	 *  Ex: https://www.rts.ch/play/tv/redirect/live/srf?id=c4927fcf-e1a0-0001-7edd-1ef01d441651&title=SRF+1
	 */
	switch (true) {
		case pathname.includes("/tv/redirect/live/"):
			mediaType = "video";
			break;
		case pathname.includes("/radio/redirect/live/"):
			mediaType = "audio";
			break;
	}

	if (mediaType) {
		var mediaBu = getBuFromPathname(pathname);
		var mediaId = queryParams["id"];
		if (mediaBu && mediaId) {
			var mediaUrn = "urn:" + mediaBu + ":" + mediaType + ":" + mediaId;
			return openMediaUrn(server, bu, mediaUrn, null);
		}
		else {
			mediaType = null;
		}
	}

	/**
	 *  Catch embed media urls
	 *
	 *  Ex: https://www.rts.ch/play/embed?urn=urn:rts:video:580545
	 *  Ex: https://www.rts.ch/play/embed?urn=urn:rts:video:580545/
	 *  Ex: https://www.rts.ch/play/embed?urn=urn:rts:video:580545&startTime=60
	 */
	if (pathname.endsWith("/embed")) {
		var mediaUrn = queryParams["urn"];
		if (mediaUrn) {
			var startTime = queryParams["startTime"];
			return openMediaUrn(server, bu, mediaUrn, startTime);
		}
		else {
			// Returns default TV homepage
			return openTvHomePage(server, bu);
		}
	}

	/**
	 *  Catch live TV urls
	 *
	 * 	Ex: https://www.srf.ch/play/tv/live/srf-1?tvLiveId=c4927fcf-e1a0-0001-7edd-1ef01d441651
	 *  Ex: https://www.srf.ch/play/tv/live?tvLiveId=c49c1d73-2f70-0001-138a-
	 *  Ex: https://www.srf.ch/play/tv/live/?tvLiveId=c49c1d73-2f70-0001-138a-15e0c4ccd3d0
	 */
	if (pathname.includes("/tv/live/") || pathname.includes("/tv/direct/") || pathname.endsWith("/tv/live") || pathname.endsWith("/tv/direct") || pathname.endsWith("/tv/live/") || pathname.endsWith("/tv/direct/")) {
		var mediaId = queryParams["tvLiveId"];
		if (mediaId) {
			return openMedia(server, bu, "video", mediaId, null);
		}
		else {
			// Returns livestreams homepage
			return openPage(server, bu, "livestreams", null, null);
		}
	}

	/**
	 *  Catch live radio urls
	 *
	 *  Ex: https://www.rsi.ch/play/radio/livepopup
	 *  Ex: https://www.rsi.ch/play/radio/livepopup/rete-uno
	 *  Ex: https://www.rsi.ch/play/radio/legacy-livepopup/rete-uno
	 */
	if (pathname.endsWith("/radio/livepopup") || pathname.endsWith("/radio/legacy-livepopup") || pathname.endsWith("/radio/livepopup/") || pathname.endsWith("/radio/legacy-livepopup/")) {
		// Returns livestreams homepage
		return openPage(server, bu, "livestreams", null, null);
	}
	else if (pathname.includes("/radio/livepopup/") || pathname.includes("/radio/legacy-livepopup/")) {
		var mediaBu = null;
		var mediaId = null;
		switch (pathname.split("/").slice(-1)[0]) {
			case "radio-srf-1":
				mediaBu = "srf";
				mediaId = "69e8ac16-4327-4af4-b873-fd5cd6e895a7";
				break;
			case "radio-srf-2-kultur":
				mediaBu = "srf";
				mediaId = "c8537421-c9c5-4461-9c9c-c15816458b46";
				break;
			case "radio-srf-3":
				mediaBu = "srf";
				mediaId = "dd0fa1ba-4ff6-4e1a-ab74-d7e49057d96f";
				break;
			case "radio-srf-4-news":
				mediaBu = "srf";
				mediaId = "ee1fb348-2b6a-4958-9aac-ec6c87e190da";
				break;
			case "radio-srf-musikwelle":
				mediaBu = "srf";
				mediaId = "a9c5c070-8899-46c7-ac27-f04f1be902fd";
				break;
			case "radio-srf-virus":
				mediaBu = "srf";
				mediaId = "66815fe2-9008-4853-80a5-f9caaffdf3a9";
				break;
			case "la-1ere":
				mediaBu = "rts";
				mediaId = "3262320";
				break;
			case "espace-2":
				mediaBu = "rts";
				mediaId = "3262362";
				break;
			case "couleur-3":
				mediaBu = "rts";
				mediaId = "3262363";
				break;
			case "option-musique":
				mediaBu = "rts";
				mediaId = "3262364";
				break;
			case "rete-uno":
				mediaBu = "rsi";
				mediaId = "livestream_ReteUno";
				break;
			case "rete-due":
				mediaBu = "rsi";
				mediaId = "livestream_ReteDue";
				break;
			case "rete-tre":
				mediaBu = "rsi";
				mediaId = "livestream_ReteTre";
				break;
			case "rtr":
				mediaBu = "rtr";
				mediaId = "a029e818-77a5-4c2e-ad70-d573bb865e31";
				break;
		}

		if (mediaBu && mediaId) {
			return openMedia(server, mediaBu, "audio", mediaId, null);
		}
		else {
			// Returns livestreams homepage
			return openPage(server, bu, "livestreams", null, null);
		}
	}

	/**
	 *  Catch tv video popup urls
	 *
	 *  Ex: https://www.srf.ch/play/tv/popupvideoplayer?id=b833a5af-63c6-4310-bb80-05341310a4f5
	 */
	if (pathname.includes("/tv/popupvideoplayer")) {
		var mediaId = queryParams["id"];
		if (mediaId) {
			return openMedia(server, bu, "video", mediaId, null);
		}
		else {
			// Returns default TV homepage
			return openPage(server, bu, "tv:home", null, null);
		}
	}

	/**
	 *  Catch radio audio popup urls
	 *
	 *  Ex: https://www.srf.ch/play/radio/popupaudioplayer?id=dc5e9465-ac64-409a-9878-ee47de3d1346
	 */
	if (pathname.includes("/radio/popupaudioplayer")) {
		var mediaId = queryParams["id"];
		if (mediaId) {
			return openMedia(server, bu, "audio", mediaId, null);
		}
		else {
			// Returns default TV homepage
			return openPage(server, bu, "radio:home", null, null);
		}
	}

	/**
	 *  Catch BU livestreams urls
	 *
	 *  Ex: https://www.rtr.ch/play/tv/rtr-livestreams
	 *  Ex: https://www.rts.ch/play/tv/rts-livestreams
	 *  Ex: https://www.srf.ch/play/tv/sport-livestreams
	 *  Ex: https://www.rsi.ch/play/tv/streaming
	 */
	if (pathname.endsWith("-livestreams") || pathname.endsWith("streaming")) {
		// Returns livestreams homepage
		return openPage(server, bu, "livestreams", null, null);
	}

	/**
	 *  Catch classic show urls
	 *
	 *  Ex: https://www.rts.ch/play/tv/emission/faut-pas-croire?id=6176
	 */
	var showTransmission = null;
	switch (true) {
		case pathname.includes("/tv/sendung") || pathname.includes("/tv/emission") || pathname.includes("/tv/programma") || pathname.includes("/tv/emissiuns"):
			showTransmission = "tv";
			break;
		case pathname.includes("/radio/sendung") || pathname.includes("/radio/emission") || pathname.includes("/radio/programma") || pathname.includes("/radio/emissiuns"):
			showTransmission = "radio";
			break;
	}

	if (showTransmission) {
		var showId = queryParams["id"];
		if (showId) {
			return openShow(server, bu, showTransmission, showId);
		}
		else {
			showTransmission = null;
		}
	}

	/**
	 *  Catch redirect and simple show urls
	 *
	 *  Ex: https://www.rts.ch/play/tv/quicklink/6176
	 *  Ex: https://www.rts.ch/play/tv/show/9674517
	 */
	switch (true) {
		case pathname.includes("/tv/quicklink/"):
		case pathname.includes("/tv/show/"):
			showTransmission = "tv";
			break;
		case pathname.includes("/radio/quicklink/"):
		case pathname.includes("/radio/show/"):
			showTransmission = "radio";
			break;
	}

	if (showTransmission) {
		var showId = pathname.split("/").slice(-1)[0];
		if (showId) {
			return openShow(server, bu, showTransmission, showId);
		}
		else {
			showTransmission = null;
		}
	}

	/**
	 *  Catch home TV urls
	 *
	 *  Ex: https://www.srf.ch/play/tv
	 *  Ex: https://www.srf.ch/play/tv?pageId=b2f5173a-da2c-4451-9101-fedc76e6c3bc
	 */
	if (pathname.endsWith("/tv")) {
		var pageId = queryParams["pageId"];
		if (pageId) {
			return openURL(server, bu, originalUrl);
		}
		else {
			return openPage(server, bu, "tv:home", null, null);
		}
	}

	/**
	 *  Catch home radio urls
	 *
	 *  Ex: https://www.srf.ch/play/radio?station=ee1fb348-2b6a-4958-9aac-ec6c87e190da
	 */
	if (pathname.endsWith("/radio")) {
		var channelId = queryParams["station"];
		return openPage(server, bu, "radio:home", channelId, null);
	}

	/**
	 *  Catch AZ TV urls
	 *
	 *  Ex: https://www.rts.ch/play/tv/emissions?index=G
	 */
	if (pathname.endsWith("/tv/sendungen") || pathname.endsWith("/tv/emissions") || pathname.endsWith("/tv/programmi") || pathname.endsWith("/tv/emissiuns")) {
		var index = queryParams["index"];
		if (index) {
			index = index.toLowerCase();
			index = (index.length > 1) ? null : index;
		}
		var options = new Array({ key: "index", value: index });
		return openPage(server, bu, "tv:az", null, options);
	}

	/**
	 *  Catch AZ radio urls
	 *
	 *  Ex: https://www.rts.ch/play/radio/emissions?index=S&station=8ceb28d9b3f1dd876d1df1780f908578cbefc3d7
	 */
	if (pathname.endsWith("/radio/sendungen") || pathname.endsWith("/radio/emissions") || pathname.endsWith("/radio/programmi") || pathname.endsWith("/radio/emissiuns")) {
		var channelId = queryParams["station"];
		var index = queryParams["index"];
		if (index) {
			index = index.toLowerCase();
			index = (index.length > 1) ? null : index;
		}
		var options = new Array({ key: "index", value: index });
		return openPage(server, bu, "radio:az", channelId, options);
	}

	/**
	 *  Catch old by date TV urls
	 *
	 *  Ex: https://www.rtr.ch/play/tv/emissiuns-tenor-data?date=07-03-2019
	 */
	if (pathname.endsWith("/tv/sendungen-nach-datum") || pathname.endsWith("/tv/emissions-par-dates") || pathname.endsWith("/tv/programmi-per-data") || pathname.endsWith("/tv/emissiuns-tenor-data")) {
		var date = queryParams["date"];
		if (date) {
			// Returns an ISO format
			var dateArray = date.split("-");
			if (dateArray.length == 3 && dateArray[2].length == 4 && dateArray[1].length == 2 && dateArray[0].length == 2) {
				date = dateArray[2] + "-" + dateArray[1] + "-" + dateArray[0];
			}
			else {
				date = null;
			}
		}
		var options = new Array({ key: "date", value: date });
		return openPage(server, bu, "tv:bydate", null, options);
	}

	/**
	 *  Catch new by date TV urls and TV program urls
	 *
	 *  Ex: https://www.rts.ch/play/tv/emissions-par-dates/2021-06-21
	 *  Ex: https://www.srf.ch/play/tv/programm/2021-07-03
	 *  Ex: https://www.rsi.ch/play/tv/guidatv-per-canale/la-2/2024-05-11?channelUrn=urn%3Arsi%3Achannel%3Atv%3Ala2
	 */
	if (pathname.includes("/tv/sendungen-nach-datum") || pathname.includes("/tv/emissions-par-dates") || pathname.includes("/tv/programmi-per-data") || pathname.includes("/tv/emissiuns-tenor-data") ||
		pathname.includes("/tv/programm") || pathname.includes("/tv/programme") || pathname.includes("/tv/guida-programmi") || pathname.includes("/tv/program") ||
		pathname.includes("/tv/programm-nach-sender") || pathname.includes("/tv/programme-par-chaine") || pathname.includes("/tv/guidatv-per-canale") || pathname.includes("/tv/tenor-program-tv")) {
		var lastPathComponent = pathname.split("/").slice(-1)[0];

		var date = null;
		if (lastPathComponent) {
			// Returns an ISO format
			var dateArray = lastPathComponent.split("-");
			if (dateArray.length == 3 && dateArray[0].length == 4 && dateArray[1].length == 2 && dateArray[2].length == 2) {
				date = lastPathComponent;
			}
		}
		var options = new Array({ key: "date", value: date });
		return openPage(server, bu, "tv:bydate", null, options);
	}

	/**
	 *  Catch by date radio urls
	 *
	 *  Ex: https://www.rts.ch/play/radio/emissions-par-dates?date=07-03-2019&station=8ceb28d9b3f1dd876d1df1780f908578cbefc3d7
	 */
	if (pathname.endsWith("/radio/sendungen-nach-datum") || pathname.endsWith("/radio/emissions-par-dates") || pathname.endsWith("/radio/programmi-per-data") || pathname.endsWith("/radio/emissiuns-tenor-data")) {
		var channelId = queryParams["station"];
		var date = queryParams["date"];
		if (date) {
			// Returns an ISO format
			var dateArray = date.split("-");
			if (dateArray.length == 3 && dateArray[2].length == 4 && dateArray[1].length == 2 && dateArray[0].length == 2) {
				date = dateArray[2] + "-" + dateArray[1] + "-" + dateArray[0];
			}
			else {
				date = null;
			}
		}
		var options = new Array({ key: "date", value: date });
		return openPage(server, bu, "radio:bydate", channelId, options);
	}

	/**
	 *  Catch search urls
	 *
	 *  Ex: https://www.rsi.ch/play/ricerca?query=federer%20finale
	 *  Ex: https://www.rtr.ch/play/retschertga?query=Federer%20tennis&mediaType=video
	 */
	if (pathname.endsWith("/suche") || pathname.endsWith("/recherche") || pathname.endsWith("/ricerca") || pathname.endsWith("/retschertga") || pathname.endsWith("/search")) {
		var query = queryParams["query"];
		var mediaType = queryParams["mediaType"];
		var transmission = null;
		if (mediaType) {
			mediaType = mediaType.toLowerCase();
			if (mediaType != "video" && mediaType != "audio") {
				mediaType = null;
			}
		}
		var transmissionComponent = (transmission != null) ? transmission + ":" : "";
		var options = new Array({ key: "query", value: query }, { key: "mediaType", value: mediaType });
		return openPage(server, bu, transmissionComponent + "search", null, options);
	}

	/**
	 *  Catch TV topics urls
	 *
	 *  Ex: https://www.rts.ch/play/tv/categories/info
	 */
	if (pathname.endsWith("/tv/themen") || pathname.endsWith("/tv/categories") || pathname.endsWith("/tv/categorie") || pathname.endsWith("/tv/tematicas") || pathname.endsWith("/tv/topics")) {
		return openPage(server, bu, "tv:home", null, null);
	}
	else if (pathname.includes("/tv/themen") || pathname.includes("/tv/categories") || pathname.includes("/tv/categorie") || pathname.includes("/tv/tematicas") || pathname.includes("/tv/topics")) {
		var lastPathComponent = pathname.split("/").slice(-1)[0];

		var topicId = null;

		/* INJECT TVTOPICS OBJECT */

		if (typeof tvTopics !== 'undefined' && lastPathComponent.length > 0) {
			topicId = tvTopics[server][bu][lastPathComponent];
		}

		if (topicId) {
			return openTopic(server, bu, "tv", topicId);
		}
		else {
			return openPage(server, bu, "tv:home", null, null);
		}
	}

	/**
	 *  Catch TV event urls (support removed - URLs switched to section page urls)
	 *
	 *  Ex: https://www.srf.ch/play/tv/event/10-jahre-auf-und-davon
	 *  Ex: https://www.rsi.ch/play/tv/event/event-playrsi-8858482
	 */
	if (pathname.endsWith("/tv/event") || pathname.includes("/tv/event")) {
		return openPage(server, bu, "tv:home", null, null);
	}

	/**
	 *  Catch TV section page urls
	 *
	 *  Ex: https://www.rts.ch/play/tv/detail/bulles-dair?id=f75179d9-f621-4855-b695-a9ba206864c2
	 *  Ex: https://www.rsi.ch/play/tv/detail/il-giardino-di-albert-lis?id=bd8d8352-4512-4f24-86b0-c380f94ab701
	 */
	if (pathname.endsWith("/tv/detail")) {
		return openPage(server, bu, "tv:home", null, null);
	}
	else if (pathname.includes("/tv/detail")) {
		var sectionId = queryParams["id"];

		if (sectionId) {
			return openSection(server, bu, sectionId);
		}
		else {
			return openPage(server, bu, "tv:home", null, null);
		}
	}

	/**
	 *  Catch base play urls
	 *
	 *  Ex: https://www.srf.ch/play/
	 *  Ex: https://www.rsi.ch/play
	 */
	if (pathname.endsWith("/play/") || pathname.endsWith("/play")) {
		return openPage(server, bu, "tv:home", null, null);
	}

	/**
	 *  Catch legacy bowser urls
	 *
	 *  Ex: https://www.srf.ch/play/legacy-browser
	 *. Ex: https://www.rsi.ch/play/legacy-browser
	 */
	if (pathname.endsWith("/legacy-browser")) {
		return openPage(server, bu, "tv:home", null, null);
	}

	/**
	 *  Catch sitemap urls
	 *
	 *  Ex: https://www.srf.ch/play/sitemap/tv/pages
	 *. Ex: https://www.rts.ch/play/sitemap/tv/pages
	 */
	if (pathname.includes("/play/sitemap/")) {
		return openPage(server, bu, "tv:home", null, null);
	}

	/**
	 *  Catch help page urls
	 *
	 *  Ex: https://www.srf.ch/play/tv/hilfe
	 *  Ex: https://www.srf.ch/play/tv/hilfe/geoblock
	 *  Ex: https://www.rts.ch/play/tv/aide
	 *  Ex: https://www.rts.ch/play/tv/aide/geoblocke
	 *  Ex: https://www.rsi.ch/play/tv/guida
	 *  Ex: https://www.rsi.ch/play/tv/guida/geobloccato
	 *  Ex: https://www.rtr.ch/play/tv/agid
	 *  Ex: https://www.rtr.ch/play/tv/agid/geo-blocking
	 *  Ex: https://play.swissinfo.ch/play/tv/help
	 *  Ex: https://play-mmf.herokuapp.com/srf/play/tv/hilfe
	 *
	 *  Ex: playsrf://www.srf.ch/play/tv/hilfe
	 */
	if (pathname.endsWith("/hilfe") || pathname.includes("/hilfe/") || pathname.endsWith("/aide") || pathname.includes("/aide/") || pathname.endsWith("/guida") || pathname.includes("/guida/") || pathname.endsWith("/agid") || pathname.includes("/agid/") || pathname.endsWith("/help") || pathname.includes("/help/")) {
		return openURL(server, bu, originalUrl);
	}

	/**
	 * Catch micro page urls
	 *
	 * Ex: https://www.srf.ch/play/tv/micropages/test-?pageId=3c2674b9-37a7-4e76-9398-bb710bd135ee
	 *
	 * Ex: playsrf://www.srf.ch/play/tv/micropages/test-?pageId=3c2674b9-37a7-4e76-9398-bb710bd135ee
	 */
	if (pathname.includes("/micropages/")) {
		return openURL(server, bu, originalUrl);
	}

	/**
	 *  Catch parameters page urls
	 *
	 *  Ex: https://www.srf.ch/play/tv/einstellungen
	 *  Ex: https://www.rts.ch/play/tv/parametres
	 *  Ex: https://www.rsi.ch/play/tv/impostazioni
	 *  Ex: https://www.rtr.ch/play/tv/configuraziuns
	 *  Ex: https://play.swissinfo.ch/play/tv/settings
	 */
	if (pathname.endsWith("/einstellungen") || pathname.endsWith("/parametres") || pathname.endsWith("/impostazioni") || pathname.endsWith("/configuraziuns") || pathname.endsWith("/settings")) {
		return openPage(server, bu, "tv:home", null, null);
	}

	/**
	 * Catch PAC preview urls
	 * 
	 * https://play-web.herokuapp.com/rts/production/play/tv/pac?pageId=b2f5173a-da2c-4451-9101-fedc76e6c3bc&type=LANDING_PAGE
	 * https://play-web.herokuapp.com/rts/production/play/tv/pac?pageId=758a8e8d-5f27-49a5-bcdc-384cf7f68d2e&topicUrn=urn%3Arts%3Atopic%3Atv%3A2743&type=TOPIC_PAGE
	 * https://play-web.herokuapp.com/rts/production/play/tv/pac?pageId=09cf1258-054d-4318-866f-c826e3d0c209&showUrn=urn%3Arts%3Ashow%3Atv%3A9356413&type=SHOW_PAGE
	 * https://play-web.herokuapp.com/rts/production/play/tv/pac?pageId=09cf1258-054d-4318-866f-c826e3d0c209&showUrn=urn%3Arts%3Ashow%3Atv%3A9356413&type=DEFAULT_SHOW_PAGE
	 * https://play-web.herokuapp.com/rts/production/play/tv/pac?pageId=307bb901-9f17-487e-bd1e-d51ccf5c8eb2&type=MICRO_PAGE
	*/
	if (pathname.endsWith("/tv/pac")) {
		var pageId = queryParams["pageId"];
		var type = queryParams["type"];
		if (pageId && type) {
			switch (type) {
				case "LANDING_PAGE":
					return openURL(server, bu, originalUrl);
				case "TOPIC_PAGE":
					var topicUrn = queryParams["topicUrn"];
					if (topicUrn) {
						return openTopicUrn(server, bu, topicUrn);
					}
					break;
				case "SHOW_PAGE":
				case "DEFAULT_SHOW_PAGE":
					var showUrn = queryParams["showUrn"];
					if (showUrn) {
						return openShowUrn(server, bu, showUrn);
					}
					break;
				case "MICRO_PAGE":
					return openURL(server, bu, originalUrl);
			}
		}
	}

	// Redirect fallback.
	console.log("Can't parse Play URL. Unsupported URL.");
	return schemeForBu(bu) + "://unsupported?server=" + server;
};
function openMedia(server, bu, mediaType, mediaId, startTime) {
	var mediaUrn = "urn:" + bu + ":" + mediaType + ":" + mediaId;
	return openMediaUrn(server, bu, mediaUrn, startTime);
}

function openMediaUrn(server, bu, mediaUrn, startTime) {
	var redirect = schemeForBu(bu) + "://open?media=" + mediaUrn;
	if (startTime) {
		redirect = redirect + "&start-time=" + startTime;
	}
	if (server) {
		redirect = redirect + "&server=" + encodeURIComponent(server);
	}
	return redirect;
}

function openShow(server, bu, showTransmission, showId) {
	var showUrn = "urn:" + bu + ":show:" + showTransmission + ":" + showId;
	return openShowUrn(server, bu, showUrn);
}

function openShowUrn(server, bu, showUrn) {
	var redirect = schemeForBu(bu) + "://open?show=" + showUrn;
	if (server) {
		redirect = redirect + "&server=" + encodeURIComponent(server);
	}
	return redirect;
}

function openTopic(server, bu, topicTransmission, topicId) {
	var topicUrn = "urn:" + bu + ":topic:" + topicTransmission + ":" + topicId;
	return openTopicUrn(server, bu, topicUrn);
}

function openTopicUrn(server, bu, topicUrn) {
	var redirect = schemeForBu(bu) + "://open?topic=" + topicUrn;
	if (server) {
		redirect = redirect + "&server=" + encodeURIComponent(server);
	}
	return redirect;
}

function openModule(server, bu, moduleType, moduleId) {
	var redirect = schemeForBu(bu) + "://open?module=urn:" + bu + ":module:" + moduleType + ":" + moduleId;
	if (server) {
		redirect = redirect + "&server=" + encodeURIComponent(server);
	}
	return redirect;
}

function openSection(server, bu, sectionId) {
	var redirect = schemeForBu(bu) + "://open?section=" + sectionId;
	if (server) {
		redirect = redirect + "&server=" + encodeURIComponent(server);
	}
	return redirect;
}

function openPage(server, bu, page, channelId, options) {
	if (!page) {
		page = "tv:home";
	}

	var pageUid = page.split(":").slice(-1)[0];

	if (page.startsWith("radio:") && !channelId) {
		channelId = primaryChannelUidForBu(bu);
	}

	var redirect = schemeForBu(bu) + "://open?page=urn:" + bu + ":page:" + page + "&page-id=" + pageUid;
	if (channelId) {
		redirect = redirect + "&channel-id=" + channelId;
	}
	if (options && Array.isArray(options)) {
		options.forEach(function (option) {
			if (option.key && option.value) {
				redirect = redirect + "&" + option.key + "=" + encodeURIComponent(option.value);
			}
		});
	}
	if (server) {
		redirect = redirect + "&server=" + encodeURIComponent(server);
	}
	return redirect;
}

function openURL(server, bu, originalUrl) {
	var scheme = originalUrl.scheme;
	if (!scheme) {
		scheme = "http";
	}
	else if (isBuScheme(scheme)) {
		scheme = "https";
	}

	var queryParamsString = "";
	if (originalUrl.queryParams) {
		for (var key in originalUrl.queryParams) {
			queryParamsString = queryParamsString + "&" + key + "=" + encodeURIComponent(originalUrl.queryParams[key]);
		}
	}
	if (queryParamsString.length > 0) {
		queryParamsString = queryParamsString.replace('&', '?');
	}

	var anchorString = "";
	if (originalUrl.anchor) {
		anchorString = "#" + originalUrl.anchor;
	}

	var url = scheme + "://" + originalUrl.hostname + originalUrl.pathname + queryParamsString + anchorString;

	var redirect = schemeForBu(bu) + "://open?url=" + encodeURIComponent(url);
	if (server) {
		redirect = redirect + "&server=" + encodeURIComponent(server);
	}
	return redirect;
}

function primaryChannelUidForBu(bu) {
	switch (bu) {
		case "srf":
			return "69e8ac16-4327-4af4-b873-fd5cd6e895a7";
			break;
		case "rts":
			return "a9e7621504c6959e35c3ecbe7f6bed0446cdf8da";
			break;
		case "rsi":
			return "rete-uno";
			break;
		case "rtr":
			return "12fb886e-b7aa-4e55-beb2-45dbc619f3c4";
			break;
		default:
			return null;
	}
}

function schemeForBu(bu) {
	switch (bu) {
		case "srf":
			return "playsrf";
			break;
		case "rts":
			return "playrts";
			break;
		case "rsi":
			return "playrsi";
			break;
		case "rtr":
			return "playrtr";
			break;
		case "swi":
			return "playswi";
			break;
		case "mmf":
		case "lb":
			return "letterbox";
			break;
		default:
			return null;
	}
}

function isBuScheme(scheme) {
	return scheme.includes("playsrf") || scheme.includes("playrts") || scheme.includes("playrsi") || scheme.includes("playrtr") || scheme.includes("playswi") || scheme.includes("letterbox");
}

function serverForUrl(hostname, pathname, queryParams) {
	var server = "production";
	if (hostname.includes("stage")) {
		server = "stage";
	}
	else if (hostname.includes("test")) {
		server = "test";
	}
	else if (hostname.includes("play-mmf")) {
		if (pathname.startsWith("/mmf/")) {
			server = "play mmf";
		}
		else {
			var serverParam = queryParams["server"];
			switch (serverParam) {
				case "stage":
					server = "stage";
					break;
				case "test":
					server = "test";
					break;
			}
		}
	}
	else if (hostname.includes("play-web") || hostname.includes("play-staging")) {
		if (pathname.includes("/stage/play")) {
			server = "stage";
		}
		else if (pathname.includes("/test/play")) {
			server = "test";
		}
	}
	else if (pathname.startsWith("/srgletterbox-web")) {
		var serverParam = queryParams["env"];
		switch (serverParam) {
			case "stage":
			case "il-stage.srgssr.ch":
				server = "stage";
				break;
			case "test":
			case "il-test.srgssr.ch":
				server = "test";
				break;
			case "play mmf":
			case "play+mmf":
			case "mmf":
			case "play-mmf.herokuapp.com":
				server = "play mmf";
				break;
		}
	}
	return server;
}

function getBuFromPathname(pathname) {
	switch (true) {
		case pathname.endsWith("rsi"):
			return "rsi";
		case pathname.endsWith("rtr"):
			return "rtr";
		case pathname.endsWith("rts"):
			return "rts";
		case pathname.endsWith("srf"):
			return "srf";
		case pathname.endsWith("swi"):
			return "swi";
	}
	return null;
}
