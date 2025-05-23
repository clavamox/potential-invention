package com.belon.http;

import com.belon.http.NanoHTTPD;
import com.belon.http.RouterNanoHTTPD;
import java.util.Map;

/* loaded from: classes.dex */
public class HttpFileHandler extends RouterNanoHTTPD.DefaultHandler {
    public static final String TAG = "HttpFileHandler";

    @Override // com.belon.http.RouterNanoHTTPD.DefaultStreamHandler
    public String getMimeType() {
        return null;
    }

    @Override // com.belon.http.RouterNanoHTTPD.DefaultHandler
    public String getText() {
        return null;
    }

    @Override // com.belon.http.RouterNanoHTTPD.DefaultHandler, com.belon.http.RouterNanoHTTPD.DefaultStreamHandler
    public NanoHTTPD.Response.IStatus getStatus() {
        return NanoHTTPD.Response.Status.OK;
    }

    @Override // com.belon.http.RouterNanoHTTPD.DefaultStreamHandler, com.belon.http.RouterNanoHTTPD.UriResponder
    public NanoHTTPD.Response post(RouterNanoHTTPD.UriResource uriResource, Map<String, String> map, NanoHTTPD.IHTTPSession iHTTPSession) {
        return get(uriResource, map, iHTTPSession);
    }

    @Override // com.belon.http.RouterNanoHTTPD.DefaultHandler, com.belon.http.RouterNanoHTTPD.DefaultStreamHandler, com.belon.http.RouterNanoHTTPD.UriResponder
    public NanoHTTPD.Response get(RouterNanoHTTPD.UriResource uriResource, Map<String, String> map, NanoHTTPD.IHTTPSession iHTTPSession) {
        FileParam fileParam = FileParam.getInstance();
        try {
            if (fileParam.getFileData() == null) {
                return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.NOT_FOUND, NanoHTTPD.MIME_PLAINTEXT, "No File found");
            }
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "*/*", new OTAInputStream(fileParam.getFileData()), r0.available());
        } catch (Exception e) {
            e.printStackTrace();
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.NOT_FOUND, NanoHTTPD.MIME_PLAINTEXT, "No File found");
        }
    }
}