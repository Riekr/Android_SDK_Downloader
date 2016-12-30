package com.riekr.android.sdk.downloader.sdk;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.riekr.android.sdk.downloader.jaxb.BooleanToEmptyObjectAdapter;

@XmlAccessorType(XmlAccessType.FIELD)
public class Downloadable {

	@XmlElement(name = "sdk:archives")
	public Archives	archives;

	@XmlElement(name = "sdk:obsolete")
	@XmlJavaTypeAdapter(BooleanToEmptyObjectAdapter.class)
	public Boolean	obsolete;

	public boolean isObsolete() {
		return obsolete != null && obsolete;
	}

	public Archives getArchives() {
		return archives;
	}

	@Override
	public String toString() {
		return "Downloadable{" +
				"archives=" + archives +
				", obsolete=" + obsolete +
				'}';
	}
}
