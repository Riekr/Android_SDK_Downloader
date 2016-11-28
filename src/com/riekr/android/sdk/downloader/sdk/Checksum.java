package com.riekr.android.sdk.downloader.sdk;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

@XmlAccessorType(XmlAccessType.FIELD)
public class Checksum {

	@XmlAttribute
	public String	type;

	@XmlValue
	public String	value;

	@Override
	public String toString() {
		return "Checksum{" +
				"type='" + type + '\'' +
				", value='" + value + '\'' +
				'}';
	}
}
