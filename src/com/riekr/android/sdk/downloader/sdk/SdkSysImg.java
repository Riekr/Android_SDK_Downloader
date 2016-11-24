package com.riekr.android.sdk.downloader.sdk;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "sdk:sdk-sys-img")
@XmlAccessorType(XmlAccessType.FIELD)
public class SdkSysImg {

	@XmlElement(name = "sdk:system-image")
	public List<Downloadable> systemImages;

	@Override
	public String toString() {
		return "SdkSysImg{" +
				"systemImages=" + systemImages +
				'}';
	}
}
