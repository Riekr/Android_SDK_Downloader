package com.riekr.android.sdk.downloader;

import com.riekr.android.sdk.downloader.sdk.*;
import com.riekr.android.sdk.downloader.utils.Download;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.lang.management.ManagementFactory;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class Main implements Runnable {

	private static final DocumentBuilderFactory DOCUMENTBUILDERFACTORY = DocumentBuilderFactory.newInstance();

	@Option(name = "--base-url", aliases = "-U", usage = "Specify alternative google repository")
	private String _baseURL = "https://dl.google.com/android/repository/";

	@Option(name = "--verbose", aliases = "-v", usage = "Verbose output")
	private boolean _verbose = false;

	@Option(name = "--obsolete", usage = "Download obsolete packages too")
	private boolean _obsolete = false;

	@Option(name = "--dest", aliases = "-P", usage = "Destination path")
	private String _destPath = "temp";

	@Option(name = "--lock", usage = "Prevents multiple instances (beta)")
	private boolean _lock = false;

	@Option(name = "--dry-run", usage = "Dumps only urls, does not download anything")
	private boolean _dryRun = false;

	@Option(name = "--xml-repository", usage = "Specify repository file with version (eg: repository-11.xml)")
	private String _repositoryXml = "repository-11.xml";

	@Option(name = "--xml-addons", usage = "Specify addons list file with version (eg: addons_list-2.xml)")
	private String _addonsXml = "addons_list-2.xml";

	public String getBaseURL() {
		return _baseURL;
	}

	public void setBaseURL(String baseURL) {
		_baseURL = baseURL;
	}

	public boolean isVerbose() {
		return _verbose;
	}

	public void setVerbose(boolean verbose) {
		_verbose = verbose;
	}

	public boolean isObsolete() {
		return _obsolete;
	}

	public void setObsolete(boolean obsolete) {
		_obsolete = obsolete;
	}

	public String getDestPath() {
		return _destPath;
	}

	public void setDestPath(String destPath) {
		_destPath = destPath;
	}

	public boolean isLock() {
		return _lock;
	}

	public void setLock(boolean lock) {
		_lock = lock;
	}

	public boolean isDryRun() {
		return _dryRun;
	}

	public void setDryRun(boolean dryRun) {
		_dryRun = dryRun;
	}

	private File download(String url) throws IOException {
		final File res = File.createTempFile("asdkdl", null);
		final String urlSpec = _baseURL + url;
		dump("Parsing " + urlSpec);
		try (InputStream in = new URL(urlSpec).openConnection().getInputStream()) {
			Files.copy(in, res.toPath(), REPLACE_EXISTING);
		}
		return res;
	}

	private void dump(File f) throws IOException {
		if (!_verbose)
			return;
		try (BufferedReader br = new BufferedReader(new FileReader(f))) {
			String line;
			while ((line = br.readLine()) != null)
				System.err.println(line);
		}
	}

	private void dump(Object o) {
		if (!_verbose)
			return;
		System.err.println(o);
	}

	private <T> T unmarshal(File file, Class<T> clazz) throws IOException, ParserConfigurationException, SAXException, JAXBException {
		try (InputStream is = new FileInputStream(file)) {
			final DocumentBuilder docBuilder = DOCUMENTBUILDERFACTORY.newDocumentBuilder();
			final Document document = docBuilder.parse(is);
			final org.w3c.dom.Element varElement = document.getDocumentElement();
			final JAXBContext context = JAXBContext.newInstance(clazz);
			final Unmarshaller unmarshaller = context.createUnmarshaller();
			final JAXBElement<T> loader = unmarshaller.unmarshal(varElement, clazz);
			return loader.getValue();
		}
	}

	private void download(List<? extends Downloadable> downloadables) throws MalformedURLException {
		download(null, downloadables);
	}

	private void download(String prefix, List<? extends Downloadable> downloadables) throws MalformedURLException {
		if (prefix == null)
			prefix = "";
		for (Downloadable downloadable : downloadables) {
			if (!_obsolete && downloadable.isObsolete())
				continue;
			final Archives archives = downloadable.getArchives();
			if (archives.archives != null) {
				for (Archives.Archive archive : archives.archives) {
					final String url = _baseURL + prefix + archive.url;
					if (_dryRun)
						System.out.println(url);
					else
						new Download(url).to(_destPath).mkdirs().run();
				}
			}
		}
	}

	private String getPrefix(String url) {
		if (url == null)
			return "";
		final int pos = url.lastIndexOf('/');
		if (pos == -1)
			return "";
		return url.substring(0, pos + 1);
	}

	private boolean lock() {
		if (!_lock)
			return true;
		final File lock = new File(Main.class.getName() + ".lock");
		if (lock.exists()) {
			System.err.println("Another copy of this program is running.");
			return false;
		}
		try (FileWriter writer = new FileWriter(lock)) {
			writer.write(ManagementFactory.getRuntimeMXBean().getName());
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace(System.err);
		}
		lock.deleteOnExit();
		return true;
	}

	@Override
	public void run() {
		try {
			dump(this);
			if (!lock())
				return;
			final SdkRepository sdkRepository = unmarshal(download(_repositoryXml), SdkRepository.class);
			dump(sdkRepository);
			download(sdkRepository.ndk);
			download(sdkRepository.platforms);
			download(sdkRepository.sources);
			download(sdkRepository.buildTools);
			download(sdkRepository.platformTools);
			download(sdkRepository.tools);
			download(sdkRepository.docs);
			final SdkAddonsList sdkAddonsList = unmarshal(download(_addonsXml), SdkAddonsList.class);
			dump(sdkAddonsList);
			/* ADD ONS */
			for (SdkAddonsList.AddonSite addonSite : sdkAddonsList.addonSites) {
				final SdkAddon sdkAddon = unmarshal(download(addonSite.url), SdkAddon.class);
				dump(sdkAddon);
				final String prefix = getPrefix(addonSite.url);
				if (sdkAddon.addOns != null)
					download(prefix, sdkAddon.addOns);
				if (sdkAddon.extras != null)
					download(prefix, sdkAddon.extras);
			}
			/* SYSTEM IMAGES */
			for (SdkAddonsList.SysImgSite sysImgSite : sdkAddonsList.sysImgSites) {
				final SdkSysImg sdkSysImg = unmarshal(download(sysImgSite.url), SdkSysImg.class);
				dump(sdkSysImg);
				final String prefix = getPrefix(sysImgSite.url);
				if (sdkSysImg.systemImages != null)
					download(prefix, sdkSysImg.systemImages);
			}
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}

	public static void main(String[] args) {
		final Main main = new Main();
		final CmdLineParser parser = new CmdLineParser(main);
		try {
			parser.parseArgument(args);
			main.run();
		} catch (CmdLineException e) {
			System.err.println(e.getMessage());
			System.err.println("java " + main.getClass().getName() + " [options...]");
			parser.printUsage(System.err);
			System.err.println();
		}
	}

	@Override
	public String toString() {
		return "Main{" +
				"_baseURL='" + _baseURL + '\'' +
				", _verbose=" + _verbose +
				", _obsolete=" + _obsolete +
				", _destPath='" + _destPath + '\'' +
				", _lock=" + _lock +
				", _dryRun=" + _dryRun +
				", _repositoryXml='" + _repositoryXml + '\'' +
				", _addonsXml='" + _addonsXml + '\'' +
				'}';
	}
}
