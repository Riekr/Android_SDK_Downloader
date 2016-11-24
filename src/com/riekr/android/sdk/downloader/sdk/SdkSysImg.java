package com.riekr.android.sdk.downloader.sdk;

import com.riekr.android.sdk.downloader.jaxb.BooleanToEmptyObjectAdapter;
import com.riekr.android.sdk.downloader.utils.Downloadable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.List;

@XmlRootElement(name = "sdk:sdk-sys-img")
@XmlAccessorType(XmlAccessType.FIELD)
public class SdkSysImg {

	@XmlRootElement(name = "sdk:system-image")
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class SystemImage implements Downloadable {

		@XmlElement(name = "sdk:archives")
		public Archives archives;

		@XmlElement(name = "sdk:obsolete")
		@XmlJavaTypeAdapter(BooleanToEmptyObjectAdapter.class)
		public Boolean obsolete;

		@Override
		public boolean isObsolete() {
			return obsolete != null && obsolete;
		}

		@Override
		public Archives getArchives() {
			return archives;
		}

		@Override
		public String toString() {
			return "SystemImage{" +
					"archives=" + archives +
					", obsolete=" + obsolete +
					'}';
		}
	}

	@XmlElement(name = "sdk:system-image")
	public List<SystemImage> systemImages;

	@Override
	public String toString() {
		return "SdkSysImg{" +
				"systemImages=" + systemImages +
				'}';
	}
}
