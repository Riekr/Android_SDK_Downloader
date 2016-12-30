package com.riekr.android.sdk.downloader;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.DatatypeConverter;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.littleshoot.proxy.impl.DefaultHttpProxyServer;

import com.github.axet.wget.WGet;
import com.github.axet.wget.info.ex.DownloadIOError;
import com.riekr.android.sdk.downloader.sdk.*;
import com.riekr.android.sdk.downloader.serve.SdkServeHttpFiltersSourceAdapter;
import com.riekr.android.sdk.downloader.utils.Download;
import com.riekr.android.sdk.downloader.utils.Xml;

public class Main implements Runnable {

	private Main() {}

	public Main(Action... actions) {
		_actions = Arrays.asList(actions);
	}

	public enum Checksums {
		NEVER, NEW, ALWAYS
	}

	public enum Action {
		UPDATE, SERVE, HELP
	}

	@Option(name = "--base-url", aliases = "-U", usage = "Specify alternative google repository")
	private String				_baseURL				= "https://dl.google.com/android/repository/";

	@Option(name = "--verbose", aliases = "-v", usage = "Verbose output")
	private boolean				_verbose				= false;

	@Option(name = "--obsolete", usage = "Download obsolete packages too")
	private boolean				_obsolete				= false;

	@Option(name = "--dest", aliases = "-P", usage = "Destination path")
	private String				_destPath				= "temp";

	@Option(name = "--lock", usage = "Prevents multiple instances (beta)")
	private boolean				_lock						= false;

	@Option(name = "--dry-run", usage = "Dumps only urls, does not download anything")
	private boolean				_dryRun					= false;

	@Option(name = "--xml-repository", usage = "Specify repository file with version")
	private String				_repositoryXml	= "repository-11.xml";

	@Option(name = "--xml-addons", usage = "Specify addons list file with version")
	private String				_addonsXml			= "addons_list-2.xml";

	@Option(name = "--checksums", usage = "Specify when to check file hashes")
	private Checksums			_checksums			= Checksums.NEW;

	@Option(name = "--retries", aliases = "-R", usage = "Specify number of retries")
	private int						_retries				= 2;

	@Option(name = "--serve-port", usage = "Specify tcp port for proxy server when using \"serve\" argument")
	private int						_servePort			= 8080;

	@Argument(hidden = true)
	private List<Action>	_actions				= new ArrayList<>();

	private int						_successes			= 0;
	private int						_failures				= 0;

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

	public Checksums getChecksums() {
		return _checksums;
	}

	public void setChecksums(Checksums checksums) {
		_checksums = checksums;
	}

	public int getRetries() {
		return _retries;
	}

	public void setRetries(int retries) {
		_retries = retries;
	}

	public int getSuccesses() {
		return _successes;
	}

	public int getFailures() {
		return _failures;
	}

	public List<Action> getActions() {
		return _actions;
	}

	private File download(String fileName) throws IOException {
		File res = new File(_destPath, fileName);
		if (res.isFile()) {
			if (!res.delete()) {
				System.err.println("Unable to delete " + res);
				System.exit(-1);
			}
		}
		final URL urlSpec = new URL(_baseURL + fileName);
		dump("Parsing " + urlSpec);
		new WGet(urlSpec, res).download();
		return res;
	}

	private void dump(Object o) {
		if (!_verbose)
			return;
		System.err.println(o);
	}

	private void download(List<? extends Downloadable> downloadables) throws MalformedURLException, NoSuchAlgorithmException {
		download(null, downloadables);
	}

	private void download(String prefix, List<? extends Downloadable> downloadables) throws MalformedURLException, NoSuchAlgorithmException {
		if (downloadables == null || downloadables.isEmpty())
			return;
		if (prefix == null)
			prefix = "";
		for (Downloadable downloadable : downloadables) {
			if (!_obsolete && downloadable.isObsolete())
				continue;
			final Archives archives = downloadable.getArchives();
			if (archives != null && archives.archives != null) {
				for (Archives.Archive archive : archives.archives) {
					final String url = _baseURL + prefix + archive.url;
					if (_dryRun)
						System.out.println(url);
					else {
						try {
							final Download dl = new Download(url, _destPath + '/' + prefix);
							final File output = dl.getOutput();
							System.out.print(output + "\t(checking)");
							int trials = 1 + Math.max(0, _retries);
							if (checkFile(output, archive, true)) {
								System.out.println("\r" + output + "\tAlready downloaded");
								_successes++;
							} else {
								boolean success;
								do {
									try {
										dl.start();
										success = checkFile(output, archive, false);
									} catch (DownloadIOError e) {
										success = false;
										System.out.println("\r" + output + "\tDownload failed");
									}
									trials--;
								} while (!success && trials > 0);
								if (success) {
									_successes++;
								} else {
									System.err.println("Corrupted file: " + dl);
									_failures++;
								}
							}
						} catch (IOException e) {
							System.err.println("Download failed: " + e.getLocalizedMessage());
							_failures++;
						}
					}
				}
			}
		}

	}

	private boolean checkFile(File output, Archives.Archive archive, boolean firstRound) throws NoSuchAlgorithmException, IOException {
		if (!output.isFile())
			return false;
		if (output.length() != archive.size) {
			System.err.println("\nSize mismatch for " + output + "\nfound:    " + output.length() + "\nexpected: " + archive.size);
			if (!firstRound)
				if (!output.delete())
					System.err.println("Unable to delete " + output);
			return false;
		}
		if (_checksums != Checksums.NEVER && (_checksums == Checksums.ALWAYS || (_checksums == Checksums.NEW && !firstRound)) && archive.checksum != null) {
			final MessageDigest md = MessageDigest.getInstance(archive.checksum.type);
			final byte[] buf = new byte[65536];
			try (InputStream is = new FileInputStream(output); DigestInputStream dis = new DigestInputStream(is, md)) {
				//noinspection StatementWithEmptyBody
				while (dis.read(buf) != -1);
			}
			final String checksum = DatatypeConverter.printHexBinary(md.digest());
			if (!checksum.equalsIgnoreCase(archive.checksum.value)) {
				System.err.println("\nChecksum failed for " + output + "\nfound:    " + checksum + "\nexpected: " + archive.checksum.value);
				if (!output.delete())
					System.err.println("Unable to delete " + output);
				return false;
			}
		}
		return true;
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

	public void update() {
		_successes = _failures = 0;
		try {
			dump(this);
			if (!lock())
				return;
			final SdkRepository sdkRepository = Xml.unmarshal(download(_repositoryXml), SdkRepository.class);
			dump(sdkRepository);
			for (List<Downloadable> downloadables : sdkRepository.collect())
				download(downloadables);
			final SdkAddonsList sdkAddonsList = Xml.unmarshal(download(_addonsXml), SdkAddonsList.class);
			dump(sdkAddonsList);
			/* ADD ONS */
			for (SdkAddonsList.AddonSite addonSite : sdkAddonsList.addonSites) {
				final SdkAddon sdkAddon = Xml.unmarshal(download(addonSite.url), SdkAddon.class);
				dump(sdkAddon);
				final String prefix = getPrefix(addonSite.url);
				if (sdkAddon.addOns != null)
					download(prefix, sdkAddon.addOns);
				if (sdkAddon.extras != null)
					download(prefix, sdkAddon.extras);
			}
			/* SYSTEM IMAGES */
			for (SdkAddonsList.SysImgSite sysImgSite : sdkAddonsList.sysImgSites) {
				final SdkSysImg sdkSysImg = Xml.unmarshal(download(sysImgSite.url), SdkSysImg.class);
				dump(sdkSysImg);
				final String prefix = getPrefix(sysImgSite.url);
				if (sdkSysImg.systemImages != null)
					download(prefix, sdkSysImg.systemImages);
			}
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
		System.err.print("Successes: " + _successes);
		if (_failures > 0)
			System.err.println("\tFailures: " + _failures);
		else
			System.err.println();
	}

	public void serve() {
		final String baseURL;
		if (_baseURL.toLowerCase().startsWith("https://")) {
			baseURL = "http://" + _baseURL.substring(8);
		} else
			baseURL = _baseURL;
		System.err.println("Serving sdk via proxy on port " + _servePort + " for " + baseURL);
		DefaultHttpProxyServer.bootstrap()
				.withPort(8080)
				.withFiltersSource(new SdkServeHttpFiltersSourceAdapter(_destPath, baseURL))
				.start();
	}

	public void run() {
		for (Action action : _actions) {
			switch (action) {
				case UPDATE :
					update();
					break;
				case SERVE :
					serve();
					break;
				case HELP :
					// ignored if not from command line
					break;
			}
		}
	}

	private static void printHelp(CmdLineParser parser, PrintStream out) {
		parser.getProperties().withUsageWidth(132);
		out.println("java " + Main.class.getName() + " [options...] " + Arrays.toString(Action.values()).replace(", ", "|"));
		out.println("Actions:");
		for (Action action : Action.values()) {
			out.print(" " + action + "\t");
			switch (action) {
				case HELP :
					out.println(" : Shows this help screen");
					break;
				case SERVE :
					out.println(" : Serves android sdk through embedded proxy");
					break;
				case UPDATE :
					out.println(" : Updates local android sdk cache from internet");
					break;
			}
		}
		out.println("Options:");
		parser.printUsage(out);
		out.println();
	}

	public static void main(String[] args) {
		final Main main = new Main();
		final CmdLineParser parser = new CmdLineParser(main);
		try {
			parser.parseArgument(args);
			if (main._actions.isEmpty())
				main._actions.add(Action.UPDATE);
			else if (main._actions.contains(Action.HELP)) {
				printHelp(parser, System.out);
				return;
			}
			main.run();
		} catch (CmdLineException e) {
			System.err.println(e.getMessage());
			printHelp(parser, System.err);
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
				", _checksums=" + _checksums +
				", _retries=" + _retries +
				", _servePort=" + _servePort +
				", _actions=" + _actions +
				", _successes=" + _successes +
				", _failures=" + _failures +
				'}';
	}
}
