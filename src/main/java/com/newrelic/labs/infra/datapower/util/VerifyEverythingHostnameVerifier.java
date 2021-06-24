package com.newrelic.labs.infra.datapower.util;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

public class VerifyEverythingHostnameVerifier implements HostnameVerifier {

	@Override
	public boolean verify(String string, SSLSession sslSession) {
		return true;
	}
}