package com.belon.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/* loaded from: classes.dex */
public class FileParam {
    private static FileParam instance = new FileParam();
    public byte[] fileData;

    private FileParam() {
    }

    public static FileParam getInstance() {
        return instance;
    }

    public byte[] getFileData() {
        return this.fileData;
    }

    public void setFileDataByPath(String str) {
        try {
            File file = new File(str);
            if (file.exists()) {
                setFileDataFromInputStream(new FileInputStream(file));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:29:0x0029 -> B:18:0x002c). Please report as a decompilation issue!!! */
    public void setFileDataFromInputStream(InputStream inputStream) {
        try {
            try {
            } catch (Throwable th) {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                throw th;
            }
        } catch (IOException e2) {
            e2.printStackTrace();
            inputStream = e2;
        }
        if (inputStream != null) {
            try {
            } catch (IOException e3) {
                e3.printStackTrace();
                if (inputStream != null) {
                    inputStream.close();
                    inputStream = inputStream;
                }
            }
            if (inputStream.available() != 0) {
                byte[] bArr = new byte[inputStream.available()];
                this.fileData = bArr;
                inputStream.read(bArr, 0, bArr.length);
                inputStream = inputStream;
                if (inputStream != null) {
                    inputStream.close();
                    inputStream = inputStream;
                }
                return;
            }
        }
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e4) {
                e4.printStackTrace();
            }
        }
    }
}