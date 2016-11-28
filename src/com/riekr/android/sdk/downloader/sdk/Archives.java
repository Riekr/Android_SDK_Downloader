package com.riekr.android.sdk.downloader.sdk;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

public class Archives {

	public static class Archive {
		@XmlElement(name = "sdk:url")
		public String		url;

		@XmlElement(name = "sdk:size")
		public long			size;

		@XmlElement(name = "sdk:checksum")
		public Checksum	checksum;

		@Override
		public String toString() {
			return "Archive{" +
					"url='" + url + '\'' +
					", size=" + size +
					", checksum=" + checksum +
					'}';
		}
	}

	@XmlElement(name = "sdk:archive")
	public List<Archive> archives;

	@Override
	public String toString() {
		return "Archives{" +
				"archives=" + archives +
				'}';
	}
}
