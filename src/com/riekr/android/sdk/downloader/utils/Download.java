package com.riekr.android.sdk.downloader.utils;

import com.github.axet.wget.SpeedInfo;
import com.github.axet.wget.WGet;
import com.github.axet.wget.info.DownloadInfo;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.atomic.AtomicBoolean;

public class Download {

	private final AtomicBoolean	_stop	= new AtomicBoolean(false);

	private final URL						_url;
	private final File					_destPath;
	private final File					_output;

	public Download(String url, String destPath) throws IOException {
		_url = new URL(url);
		_destPath = new File(destPath);
		if (!_destPath.isDirectory())
			if (!_destPath.mkdirs())
				throw new IOException("Unable to create destination directory: " + _destPath);
		// get file remote information
		String fileName = _url.getPath();
		fileName = fileName.substring(fileName.lastIndexOf('/') + 1);
		_output = new File(_destPath, fileName);
	}

	public void start() {
		final DownloadInfo info = new DownloadInfo(_url);
		info.extract();
		// enable multipart download, breaks resume
		//_info.enableMultipart();
		final WGet w = new WGet(info, _output);
		// single thread download. will return here only when file download
		// is complete (or error raised).
		final SpeedInfo speedInfo = new SpeedInfo();
		speedInfo.start(System.currentTimeMillis());
		w.download(_stop, new Runnable() {

			private long last;

			private String formatSpeed(long s) {
				if (s > 1024 * 1024 * 1024) {
					final float f = s / 1024f / 1024f / 1024f;
					return String.format("%.1f GB/s", f);
				} else if (s > 1024 * 1024) {
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
				// you can extract information from DownloadInfo _info;
				switch (info.getState()) {
					case EXTRACTING :
					case EXTRACTING_DONE :
						System.out.println(info.getState());
						break;
					case DONE :
						// finish speed calculation by adding remaining bytes speed
						speedInfo.end(info.getCount());
						// print speed
						System.out.print(String.format("\r%s\t%s average speed (%s)\n", _url, info.getState(), formatSpeed(speedInfo.getAverageSpeed())));
						break;
					case RETRYING :
						System.out.println(info.getState() + " " + info.getDelay());
						break;
					case DOWNLOADING :
						speedInfo.step(info.getCount());
						final long now = System.currentTimeMillis();
						if (now - 1000 > last) {
							last = now;
							final float p = (info.getCount() / (float)info.getLength()) * 100f;
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

	public File getOutput() {
		return _output;
	}

	@Override
	public String toString() {
		return "Download{" +
				"_stop=" + _stop +
				", _url=" + _url +
				", _destPath=" + _destPath +
				", _output=" + _output +
				'}';
	}
}
