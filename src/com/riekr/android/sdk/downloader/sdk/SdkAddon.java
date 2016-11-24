package com.riekr.android.sdk.downloader.sdk;

import com.riekr.android.sdk.downloader.jaxb.BooleanToEmptyObjectAdapter;
import com.riekr.android.sdk.downloader.utils.Downloadable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.List;

@XmlRootElement(name = "sdk:sdk-addon")
@XmlAccessorType(XmlAccessType.FIELD)
public class SdkAddon {

	@XmlRootElement(name = "sdk:add-on")
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class AddOn implements Downloadable {

		@XmlElement(name = "sdk:obsolete")
		@XmlJavaTypeAdapter(BooleanToEmptyObjectAdapter.class)
		public Boolean obsolete;

		@XmlElement(name = "sdk:archives")
		public Archives archives;

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
			return "AddOn{" +
					"obsolete=" + obsolete +
					", archives=" + archives +
					'}';
		}
	}

	@XmlRootElement(name = "sdk:extra")
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class Extra implements Downloadable {

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
			return "Extra{" +
					"archives=" + archives +
					", obsolete=" + obsolete +
					'}';
		}
	}

	@XmlElement(name = "sdk:add-on")
	public List<AddOn> addOns;

	@XmlElement(name = "sdk:extra")
	public List<Extra> extras;

	@Override
	public String toString() {
		return "SdkAddon{" +
				"addOns=" + addOns +
				", extras=" + extras +
				'}';
	}
}
