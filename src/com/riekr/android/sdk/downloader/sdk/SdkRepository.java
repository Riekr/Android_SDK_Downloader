package com.riekr.android.sdk.downloader.sdk;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "sdk:sdk-repository")
@XmlAccessorType(XmlAccessType.FIELD)
public class SdkRepository {

	@XmlElement(name = "sdk:ndk")
	public List<Downloadable> ndk;

	@XmlElement(name = "sdk:platform")
	public List<Downloadable> platforms;

	@XmlElement(name = "sdk:source")
	public List<Downloadable> sources;

	@XmlElement(name = "sdk:build-tool")
	public List<Downloadable> buildTools;

	@XmlElement(name = "sdk:platform-tool")
	public List<Downloadable> platformTools;

	@XmlElement(name = "sdk:tool")
	public List<Downloadable> tools;

	@XmlElement(name = "sdk:doc")
	public List<Downloadable> docs;

	@Override
	public String toString() {
		return "SdkRepository{" +
				"ndk=" + ndk +
				", platforms=" + platforms +
				", sources=" + sources +
				", buildTools=" + buildTools +
				", platformTools=" + platformTools +
				", tools=" + tools +
				", docs=" + docs +
				'}';
	}
}
