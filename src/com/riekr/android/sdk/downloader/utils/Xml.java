package com.riekr.android.sdk.downloader.utils;

import java.io.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class Xml {

	public static final DocumentBuilderFactory	DOCUMENT_BUILDER_FACTORY	= DocumentBuilderFactory.newInstance();
	public static final TransformerFactory			TRANSFORMER_FACTORY				= TransformerFactory.newInstance();
	static {
		DOCUMENT_BUILDER_FACTORY.setNamespaceAware(false);
	}

	private Xml() {}

	public static String toString(Node node) throws TransformerException, IOException {
		final Transformer transformer = TRANSFORMER_FACTORY.newTransformer();
		try (StringWriter buffer = new StringWriter()) {
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			transformer.transform(new DOMSource(node), new StreamResult(buffer));
			return buffer.toString();
		}
	}

	public static Element fromString(String str) throws ParserConfigurationException, IOException, SAXException {
		final DocumentBuilder docBuilder = DOCUMENT_BUILDER_FACTORY.newDocumentBuilder();
		try (ByteArrayInputStream in = new ByteArrayInputStream(str.getBytes())) {
			final Document document = docBuilder.parse(in);
			return document.getDocumentElement();
		}
	}

	public static <T> T unmarshal(Node node, Class<T> impl) throws JAXBException {
		final JAXBContext context = JAXBContext.newInstance(impl);
		final Unmarshaller unmarshaller = context.createUnmarshaller();
		final JAXBElement<T> jaxbElement = unmarshaller.unmarshal(node, impl);
		return jaxbElement == null ? null : jaxbElement.getValue();
	}

	public static <T> T unmarshal(File file, Class<T> clazz) throws IOException, ParserConfigurationException, SAXException, JAXBException {
		try (InputStream is = new FileInputStream(file)) {
			final DocumentBuilder docBuilder = Xml.DOCUMENT_BUILDER_FACTORY.newDocumentBuilder();
			final Document document = docBuilder.parse(is);
			return unmarshal(document.getDocumentElement(), clazz);
		}
	}
}
