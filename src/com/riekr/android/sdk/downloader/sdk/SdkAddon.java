package com.riekr.android.sdk.downloader.sdk;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "sdk:sdk-addon")
@XmlAccessorType(XmlAccessType.FIELD)
public class SdkAddon {

	@XmlElement(name = "sdk:add-on")
	public List<Downloadable> addOns;

	@XmlElement(name = "sdk:extra")
	public List<Downloadable> extras;

	@Override
	public String toString() {
		return "SdkAddon{" +
				"addOns=" + addOns +
				", extras=" + extras +
				'}';
	}
}
