package com.riekr.android.sdk.downloader.sdk;

import javax.xml.bind.annotation.XmlElement;
import java.util.List;

public class Archives {

	public static class Archive {
		@XmlElement(name = "sdk:url")
		public String url;

		@Override
		public String toString() {
			return "Archive{" +
					"url='" + url + '\'' +
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
