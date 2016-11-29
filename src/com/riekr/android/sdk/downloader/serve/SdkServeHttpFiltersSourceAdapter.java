package com.riekr.android.sdk.downloader.serve;

import java.io.File;
import java.io.FileNotFoundException;

import org.littleshoot.proxy.HttpFilters;
import org.littleshoot.proxy.HttpFiltersAdapter;
import org.littleshoot.proxy.HttpFiltersSourceAdapter;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.*;

public class SdkServeHttpFiltersSourceAdapter extends HttpFiltersSourceAdapter {

	private final File		_local;
	private final String	_remote;

	public SdkServeHttpFiltersSourceAdapter(String local, String remote) {
		_local = new File(local);
		_remote = remote;
		if (!_local.isDirectory())
			throw new IllegalArgumentException("Local respository does not exists in " + _local);
	}

	public HttpFilters filterRequest(HttpRequest originalRequest, ChannelHandlerContext ctx) {
		return new HttpFiltersAdapter(originalRequest) {

			@Override
			public void proxyToServerConnectionSucceeded(ChannelHandlerContext serverCtx) {
				ChannelPipeline pipeline = serverCtx.pipeline();
				if (pipeline.get("inflater") != null)
					pipeline.remove("inflater");
				if (pipeline.get("aggregator") != null)
					pipeline.remove("aggregator");
				super.proxyToServerConnectionSucceeded(serverCtx);
			}

			@Override
			public HttpResponse clientToProxyRequest(HttpObject httpObject) {
				if (httpObject instanceof DefaultHttpRequest) {
					final DefaultHttpRequest req = (DefaultHttpRequest)httpObject;
					if (req.getUri().startsWith(_remote)) {
						try {
							final String relPart = req.getUri().substring(_remote.length());
							final File local = new File(_local, relPart);
							final ByteBuf buffer = new LocalFileByteBuf(local);
							final HttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_0, HttpResponseStatus.OK, buffer);
							HttpHeaders.setContentLength(response, local.length());
							HttpHeaders.setHeader(response, HttpHeaders.Names.CONTENT_TYPE, "application/octet-stream");
							return response;
						} catch (FileNotFoundException e) {
							return new DefaultHttpResponse(HttpVersion.HTTP_1_0, HttpResponseStatus.NOT_FOUND);
						}
					}
				}
				return new DefaultHttpResponse(HttpVersion.HTTP_1_0, HttpResponseStatus.FORBIDDEN);
			}

			@Override
			public HttpObject serverToProxyResponse(HttpObject httpObject) {
				// implement your filtering here
				return httpObject;
			}
		};
	}
}
