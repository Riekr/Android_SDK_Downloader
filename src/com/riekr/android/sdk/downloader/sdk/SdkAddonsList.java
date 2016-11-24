package com.riekr.android.sdk.downloader.sdk;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "sdk:sdk-addons-list")
@XmlAccessorType(XmlAccessType.FIELD)
public class SdkAddonsList {

	public static class SysImgSite {

		@XmlElement(name = "sdk:name")
		public String name;

		@XmlElement(name = "sdk:url")
		public String url;

		@Override
		public String toString() {
			return "SysImgSite{" +
					"name='" + name + '\'' +
					", url='" + url + '\'' +
					'}';
		}
	}

	public static class AddonSite {

		@XmlElement(name = "sdk:name")
		public String name;

		@XmlElement(name = "sdk:url")
		public String url;

		@Override
		public String toString() {
			return "AddonSite{" +
					"name='" + name + '\'' +
					", url='" + url + '\'' +
					'}';
		}
	}

	@XmlElement(name = "sdk:addon-site")
	public List<AddonSite> addonSites;

	@XmlElement(name = "sdk:sys-img-site")
	public List<SysImgSite> sysImgSites;

	@Override
	public String toString() {
		return "SdkAddonsList{" +
				"addonSites=" + addonSites +
				", sysImgSites=" + sysImgSites +
				'}';
	}
}
