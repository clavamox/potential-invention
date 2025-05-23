package com.belon.http;

import com.belon.http.NanoHTTPD;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

/* loaded from: classes.dex */
public class HttpServer extends RouterNanoHTTPD {
    public static String REQ_OTA_PATH = "v1/ota_file";
    public static int SERVER_PORT = 8000;
    private static final String TAG = "HttpServer";

    public HttpServer(int i) {
        super(i);
        addMappings();
    }

    public void setOTAFileByPath(String str) {
        FileParam.getInstance().setFileDataByPath(str);
    }

    @Override // com.belon.http.NanoHTTPD
    public void start() throws IOException {
        if (isAlive()) {
            stop();
        }
        super.start();
    }

    @Override // com.belon.http.NanoHTTPD
    public void stop() {
        super.stop();
    }

    @Override // com.belon.http.RouterNanoHTTPD
    public void addMappings() {
        super.addMappings();
        try {
            addRoute(REQ_OTA_PATH, HttpFileHandler.class, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override // com.belon.http.RouterNanoHTTPD, com.belon.http.NanoHTTPD
    public NanoHTTPD.Response serve(NanoHTTPD.IHTTPSession iHTTPSession) {
        return super.serve(iHTTPSession);
    }

    @Override // com.belon.http.NanoHTTPD
    protected NanoHTTPD.ClientHandler createClientHandler(Socket socket, InputStream inputStream) {
        return super.createClientHandler(socket, inputStream);
    }

    @Override // com.belon.http.NanoHTTPD
    protected NanoHTTPD.ServerRunnable createServerRunnable(int i) {
        return super.createServerRunnable(i);
    }
}