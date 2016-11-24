package com.riekr.android.sdk.downloader.jaxb;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class BooleanToEmptyObjectAdapter extends XmlAdapter<BooleanToEmptyObjectAdapter.EmptyObject, Boolean> {

	public static class EmptyObject {
	}

	@Override
	public EmptyObject marshal(final Boolean v) {
		return v != null && v ? new EmptyObject() : null;
	}

	@Override
	public Boolean unmarshal(final EmptyObject v) {
		return true;
	}
}