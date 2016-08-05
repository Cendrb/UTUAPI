package com.farast.utuapi.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by cendr_000 on 25.07.2016.
 */
public final class HTTPUtil {
    private HTTPUtil() {

    }

    public static InputStream openStream(String stringUrl) throws IOException {
        URL url = new URL(stringUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept-Charset", "UTF-8");
        return connection.getInputStream();
    }
}
