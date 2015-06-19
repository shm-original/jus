package org.djodjo.comm.jus.util;


import org.djodjo.comm.jus.Cache;
import org.djodjo.comm.jus.NetworkResponse;
import org.djodjo.comm.jus.toolbox.HttpHeaderParser;

import java.util.Map;

public class ExtendedHttpHeaderParser {
    private ExtendedHttpHeaderParser() {}

    public static Cache.Entry parseIgnoreCacheHeaders(NetworkResponse response) {
        long now = System.currentTimeMillis();

        Map<String, String> headers = response.headers;
        long serverDate = 0;
        String serverEtag = null;
        String headerValue;

        headerValue = headers.get("Date");
        if (headerValue != null) {
            serverDate = HttpHeaderParser.parseDateAsEpoch(headerValue);
        }

        serverEtag = headers.get("ETag");

        final long cacheHitButRefreshed = 10 * 60 * 1000; // in 10 minutes cache will be hit, but also refreshed on background
        final long cacheExpired = 3 * 24 * 60 * 60 * 1000; // in 3 days this cache entry expires completely
        final long softExpire = now + cacheHitButRefreshed;
        final long ttl = now + cacheExpired;

        Cache.Entry entry = new Cache.Entry();
        entry.data = response.data;
        entry.etag = serverEtag;
        entry.softTtl = softExpire;
        entry.ttl = ttl;
        entry.serverDate = serverDate;
        entry.responseHeaders = headers;

        return entry;
    }
}