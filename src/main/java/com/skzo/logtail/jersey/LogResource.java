package com.skzo.logtail.jersey;

import java.io.IOException;
import java.util.HashMap;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import org.apache.commons.lang.StringUtils;
import org.atmosphere.annotation.Broadcast;
import org.atmosphere.annotation.Suspend;
import org.atmosphere.config.service.AtmosphereService;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResourceEvent;
import org.atmosphere.cpr.AtmosphereResourceEventListenerAdapter;
import org.atmosphere.cpr.Broadcaster;
import org.atmosphere.cpr.BroadcasterFactory;
import org.atmosphere.jersey.JerseyBroadcaster;
import org.atmosphere.websocket.WebSocketEventListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.skzo.logtail.file.LogFileHandler;

@Path("/")
@AtmosphereService(broadcaster = JerseyBroadcaster.class)
public class LogResource {
	private static final Logger logger = LoggerFactory.getLogger(LogResource.class);

	// private LogFileHandler file = new LogFileHandler("log test",
	// "/home/skzo/opt/logs/test.log");
	private final static HashMap<String, LogFileHandler> handlers = new HashMap<String, LogFileHandler>();
	static {
		LogFileHandler handler = new LogFileHandler("test.log", "/home/skzo/opt/logs/test.log");
		handlers.put("/home/skzo/opt/logs/test.log", handler);
	}

	/**
	 * Suspend the response without writing anything back to the client.
	 * 
	 * @return a white space
	 */
	@Context
	AtmosphereResource resource;

	@Suspend(contentType = "application/json", listeners = { OnDisconnect.class })
	@GET
	public String suspend() {
		String logFile = resource.getRequest().getHeader("logFile");
		if (StringUtils.isNotEmpty(logFile)) {
			logger.error("Registering listener for log file: " + logFile);

			// Create a private channel to broadcast modifications to this file
			Broadcaster privateChannel = BroadcasterFactory.getDefault().lookup("/" + logFile, true);

			// Add current browser resource to the privatechannel
			privateChannel.addAtmosphereResource(resource);

			// Start monitoring the file for changes
			if (handlers.get("/home/skzo/opt/logs/test.log") != null) {
				handlers.get("/home/skzo/opt/logs/test.log").startMonitoring();
			} else {
				// LogFileHandler handler = new LogFileHandler(logFile,
				// "/home/skzo/opt/logs/test.log");
				// handler.startMonitoring();
				// handlers.put("/home/skzo/opt/logs/test.log", handler);
			}
		} else {
			try {
				resource.close();
			} catch (IOException e) {
				logger.error("Error closing resource", e);
			}
		}
		return "";
	}

	/**
	 * Broadcast the received message object to all suspended response. Do not
	 * write back the message to the calling connection.
	 * 
	 * @param message
	 *            a {@link Message}
	 * @return a {@link Response}
	 */
	@Broadcast(writeEntity = false)
	@POST
	@Produces("application/json")
	public Response broadcast(Message message) {
		logger.info("broadcasting " + message.getMessage());

		return new Response(message.getAuthor(), message.getMessage());
	}

	public static final class OnConnect extends WebSocketEventListenerAdapter {
		private final Logger logger = LoggerFactory.getLogger(LogResource.class);

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void onConnect(WebSocketEvent event) {
			logger.error("Not an error! just connected");
		}
	}

	public static final class OnDisconnect extends AtmosphereResourceEventListenerAdapter {
		private final Logger logger = LoggerFactory.getLogger(LogResource.class);

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void onDisconnect(AtmosphereResourceEvent event) {
			if (event.isCancelled()) {
				logger.info("Browser {} closed from clientside", event.getResource().uuid());
			} else if (event.isClosedByClient()) {
				logger.info("Browser {} closed the connection", event.getResource().uuid());
			}
		}
	}

}