package com.skzo.logtail.file;

import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.skzo.logtail.file.LogFileHandler;

public class LogFileMonitor extends TimerTask {
	private static final Logger logger = LoggerFactory.getLogger(LogFileMonitor.class);
	LogFileHandler file;

	LogFileMonitor(LogFileHandler file) {
		this.file = file;
	}

	@Override
	public void run() {
		logger.trace("Checking file for changes: " + file.getFilePath());
		if (file.isUpdated()) {
			file.onChange();
		}
	}
}
