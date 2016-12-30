package com.riekr.android.sdk.downloader.sdk;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.*;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.riekr.android.sdk.downloader.utils.Xml;

@SuppressWarnings({"MismatchedQueryAndUpdateOfCollection", "unused"})
@XmlRootElement(name = "sdk:sdk-repository")
@XmlAccessorType(XmlAccessType.FIELD)
public class SdkRepository {

	@XmlElement(name = "sdk:ndk")
	private List<Downloadable>	ndk;

	@XmlElement(name = "sdk:platform")
	private List<Downloadable>	platforms;

	@XmlElement(name = "sdk:source")
	private List<Downloadable>	sources;

	@XmlElement(name = "sdk:build-tool")
	private List<Downloadable>	buildTools;

	@XmlElement(name = "sdk:platform-tool")
	private List<Downloadable>	platformTools;

	@XmlElement(name = "sdk:tool")
	private List<Downloadable>	tools;

	@XmlElement(name = "sdk:doc")
	private List<Downloadable>	docs;

	@XmlAnyElement(lax = true)
	private List<Object>				others;

	public List<List<Downloadable>> collect() throws JAXBException, TransformerException, IOException, SAXException, ParserConfigurationException {
		final List<Downloadable> otherDownloadables;
		if (others != null && !others.isEmpty()) {
			otherDownloadables = new ArrayList<>(others.size());
			for (Object other : others) {
				if (other instanceof Element) {
					if ("sdk:license".equalsIgnoreCase(((Element)other).getTagName()))
						// unneded, won't download/cache licenses here
						continue;
					final String str = Xml.toString((Node)other);
					other = Xml.unmarshal(Xml.fromString(str), Downloadable.class);
				} else if (other instanceof JAXBElement)
					other = ((JAXBElement)other).getValue();
				if (other instanceof Downloadable)
					otherDownloadables.add((Downloadable)other);
				else
					System.err.println("W: unknown lax element found: " + (other == null ? "NULL" : other.getClass().getName()));
			}
			if (!otherDownloadables.isEmpty())
				System.err.println("W: other downloadables found, application may need to be updated.");
		} else
			otherDownloadables = Collections.emptyList();
		return Arrays.asList(ndk, platforms, sources, buildTools, platformTools, tools, docs, otherDownloadables);
	}

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
				", others=" + others +
				'}';
	}
}
