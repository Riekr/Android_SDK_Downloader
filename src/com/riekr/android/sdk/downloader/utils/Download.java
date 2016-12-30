package com.riekr.android.sdk.downloader.utils;

import java.io.File;
import java.net.URL;
import java.util.concurrent.atomic.AtomicBoolean;

import com.github.axet.wget.SpeedInfo;
import com.github.axet.wget.WGet;
import com.github.axet.wget.info.DownloadInfo;

public class Download {

	private final AtomicBoolean	_stop	= new AtomicBoolean(false);

	public final URL						url;
	public final File						output;

	public Download(URL url, File output) {
		this.url = url;
		this.output = output;
	}

	public void start() {
		final DownloadInfo info = new DownloadInfo(url);
		info.extract();
		// enable multipart download, breaks resume
		//_info.enableMultipart();
		final WGet w = new WGet(info, output);
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
				// notify app or save download state, you can extract information from DownloadInfo;
				//noinspection EnumSwitchStatementWhichMissesCases
				switch (info.getState()) {
					case EXTRACTING :
					case EXTRACTING_DONE :
						System.out.println(info.getState());
						break;
					case DONE :
						// finish speed calculation by adding remaining bytes speed
						speedInfo.end(info.getCount());
						// print speed
						System.out.print(String.format("\r%s\t%s average speed (%s)\n", url, info.getState(), formatSpeed(speedInfo.getAverageSpeed())));
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
							System.out.print(String.format("\r%s\t%.2f%% (%s / %s)", url, p,
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
				", url=" + url +
				", output=" + output +
				'}';
	}
}
