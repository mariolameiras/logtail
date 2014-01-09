package com.skzo.logtail.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Timer;

import org.atmosphere.cpr.Broadcaster;
import org.atmosphere.cpr.BroadcasterFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

public class LogFileHandler {
	private static final Logger logger = LoggerFactory.getLogger(LogFileHandler.class);

	private static final int TIME_BETWEEN_CHECKS = 2000; // Milliseconds between
	private static final int BUFFER_SIZE = 10240; // 10KB.

	FileBean fileBean;
	File file;
	Broadcaster broadcaster;

	long lastIndex = 0;
	BufferedReader fileReader;
	FileInputStream fileStream;

	Timer fileCheckTimer;
	private static final Gson GSON = new Gson();

	public LogFileHandler(String name, String path) {
		fileBean = new FileBean(name, path);
		file = new File(path);

		if (!file.canRead()) {
			logger.error("File " + path + " is not readable.");
			return;
		}
	}

	// Called when FileMonitor reports a change has occured on the File
	public void onChange() {
		logger.info("File change detected in " + getFilePath());

		String updatedText = getContentSinceLastUpdate();
		fileBean.setText(updatedText);

		this.broadcaster = BroadcasterFactory.getDefault().lookup("/" + fileBean.getName());
		if (this.broadcaster != null) {
			this.broadcaster.broadcast(GSON.toJson(fileBean));
		}
	}

	public void startMonitoring() {
		logger.info("Will monitor " + this.getFilePath() + " for changes every " + String.valueOf(TIME_BETWEEN_CHECKS) + " milliseconds");

		if (fileCheckTimer == null) {
			fileCheckTimer = new Timer();
			fileCheckTimer.schedule(new LogFileMonitor(this), 1, TIME_BETWEEN_CHECKS);
		}
	}

	public void stopMonitoring() {
		logger.info("Stopping change monitor on " + getFilePath());
		fileCheckTimer.cancel();
		fileCheckTimer = null;
	}

	public String getContentSinceLastUpdate() {
		long startIndex = lastIndex;

		StringBuilder fileContents = new StringBuilder();
		String currentLine = "";

		try {
			fileStream = new FileInputStream(getFilePath());

			// Start in the last 100 lines
			startIndex = startIndex == 0 ? fileStream.getChannel().size() - 100 : startIndex;
			startIndex = (startIndex < 0) ? 0 : startIndex;

			// If the file is smaller than our last known index, set the pointer
			// at the end of the file.
			lastIndex = lastIndex <= fileStream.getChannel().size() ? lastIndex : fileStream.getChannel().size();

			fileStream.skip(lastIndex);
			fileReader = new BufferedReader(new InputStreamReader(fileStream, "UTF-8"), BUFFER_SIZE);

			while ((currentLine = fileReader.readLine()) != null) {
				fileContents.append(currentLine).append("<br>");
			}
		} catch (Exception e) {
			logger.error("Error reading file", e);
		} finally {
			if (fileReader != null) {
				try {
					lastIndex = fileStream.getChannel().position();
					fileReader.close();
				} catch (IOException e) {
					logger.error("Error reading file", e);
				}
			}
		}
		logger.debug("Read file contents from pointer location " + String.valueOf(startIndex) + " to pointer location " + String.valueOf(lastIndex));
		return fileContents.toString().replaceAll("(\\r\\n|\\n)", "<br />");
	}

	public boolean isUpdated() {
		if (fileBean.getLastUpdated() < file.lastModified()) {
			fileBean.setLastUpdated(file.lastModified());
			return true;
		}

		return false;
	}

	public String getName() {
		return fileBean.getName();
	}

	public String getFilePath() {
		return file.getAbsolutePath();
	}

	public FileBean getFile() {
		return fileBean;
	}
}
