package com.riekr.android.sdk.downloader.utils;

import com.github.axet.wget.SpeedInfo;
import com.github.axet.wget.WGet;
import com.github.axet.wget.info.DownloadInfo;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.atomic.AtomicBoolean;

public class Download implements Runnable {

	private final AtomicBoolean _stop = new AtomicBoolean(false);

	private final URL _url;
	private File _toPath;
	private boolean _mkdirs;
	private String _fileName;

	public Download(String url) throws MalformedURLException {
		this(new URL(url));
	}

	public Download(URL url) {
		_url = url;
	}

	public Download mkdirs() {
		_mkdirs = true;
		return this;
	}

	public Download to(File path) {
		_toPath = path;
		return this;
	}

	public Download to(String path) {
		return to(new File(path));
	}

	public Download withFileName(String fileName) {
		_fileName = fileName;
		return this;
	}

	@Override
	public void run() {
		if (_mkdirs && !_toPath.isDirectory())
			_toPath.mkdir();
		// get file remote information
		final DownloadInfo info = new DownloadInfo(_url);
		info.extract();
		if (_fileName == null || _fileName.isEmpty()) {
			_fileName = info.getContentFilename();
			if (_fileName == null || _fileName.isEmpty()) {
				_fileName = _url.getPath();
				_fileName = _fileName.substring(_fileName.lastIndexOf('/') + 1);
			}
		}
		final File file = new File(_toPath, _fileName);
		// enable multipart download, breaks resume
		//info.enableMultipart();
		final WGet w = new WGet(info, file);
		// single thread download. will return here only when file download
		// is complete (or error raised).
		final SpeedInfo speedInfo = new SpeedInfo();
		speedInfo.start(System.currentTimeMillis());
		w.download(_stop, new Runnable() {

			private long last;

			private String formatSpeed(long s) {
				if (s > 0.1 * 1024 * 1024 * 1024) {
					final float f = s / 1024f / 1024f / 1024f;
					return String.format("%.1f GB/s", f);
				} else if (s > 0.1 * 1024 * 1024) {
					final float f = s / 1024f / 1024f;
					return String.format("%.1f MB/s", f);
				} else {
					final float f = s / 1024f;
					return String.format("%.1f kb/s", f);
				}
			}

			@Override
			public void run() {
				// notify app or save download state
				// you can extract information from DownloadInfo info;
				switch (info.getState()) {
					case EXTRACTING:
					case EXTRACTING_DONE:
						System.out.println(info.getState());
						break;
					case DONE:
						// finish speed calculation by adding remaining bytes speed
						speedInfo.end(info.getCount());
						// print speed
						System.out.print(String.format("\r%s\t%s average speed (%s)\n", _url, info.getState(), formatSpeed(speedInfo.getAverageSpeed())));
						break;
					case RETRYING:
						System.out.println(info.getState() + " " + info.getDelay());
						break;
					case DOWNLOADING:
						speedInfo.step(info.getCount());
						final long now = System.currentTimeMillis();
						if (now - 1000 > last) {
							last = now;
							final float p = (info.getCount() / (float) info.getLength()) * 100f;
							System.out.print(String.format("\r%s\t%.2f%% (%s / %s)", _url, p,
									formatSpeed(speedInfo.getCurrentSpeed()),
									formatSpeed(speedInfo.getAverageSpeed())));
						}
						break;
				}
			}
		});
	}

	public void stop() {
		_stop.set(true);
	}

	@Override
	public String toString() {
		return "Download{" +
				"_stop=" + _stop +
				", _url=" + _url +
				", _toPath=" + _toPath +
				", _mkdirs=" + _mkdirs +
				", _fileName='" + _fileName + '\'' +
				'}';
	}
}
