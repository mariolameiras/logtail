$(function() {
	"use strict";

	var content = $('#logArea');
	var status = $('#status');
	var myName = false;
	var author = null;
	var logged = false;
	var socket = $.atmosphere;
	var request = {
		url : document.location.toString() + 'tail',
		contentType : "application/json",
		logLevel : 'debug',
		transport : 'websocket',
		trackMessageLength : true,
		fallbackTransport : 'long-polling',
		headers : {
			logFile : $("#logSelect option:selected").text()
		}
	};

	request.onOpen = function(response) {
		status.text('Atmosphere connected using ' + response.transport);
	};

	request.onMessage = function(response) {
		var message = response.responseBody;
		try {
			var json = jQuery.parseJSON(message);
		} catch (e) {
			console.log('This doesn\'t look like a valid JSON: ', message);
			return;
		}

		if (!logged) {
			logged = true;
		} else {
			var me = json.author == author;
			var date = typeof (json.time) == 'string' ? parseInt(json.time)
					: json.time;
			addMessage(json);
			document.getElementById('logArea').scrollTop = 9999999;
		}
	};

	request.onClose = function(response) {
		logged = false;
	}

	request.onError = function(response) {
		status
				.text('Sorry, but there\'s some problem with your socket or the server is down');
	};

	function createRequest() {
		var header = $("#logSelect option:selected").text();
		var request = {
			url : document.location.toString() + 'tail',
			contentType : "application/json",
			logLevel : 'debug',
			transport : 'websocket',
			trackMessageLength : true,
			fallbackTransport : 'long-polling',
			headers : {
				logFile : header
			}
		};
		return request;
	}

	function addMessage(json) {
		content.append('<p>' + json.text + '</p>');
	}

	// Select box

	$("#logSelect").change(function() {
		$("#control").attr("src", "images/pause.png");
		var subSocket = socket.subscribe(createRequest());
		logged = true;
		$("#control").attr("src", "images/pause.png");
	});

	// Pause / Play controls
	$("#control").click(function() {
		if (logged) {
			logged = false;
			$("#control").attr("src", "images/play.png");
			socket.unsubscribe()
		} else {
			$("#control").attr("src", "images/pause.png");
			logged = true;
			var subSocket = socket.subscribe(createRequest());
		}
	});

});
