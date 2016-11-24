package com.riekr.android.sdk.downloader.utils;

import com.riekr.android.sdk.downloader.sdk.Archives;

public interface Downloadable {

	boolean isObsolete();

	Archives getArchives();

}
