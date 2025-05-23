package com.belon.util;

/* loaded from: classes.dex */
public class ByteUtils {
    public static byte[] reverseBytes(byte[] bArr) {
        int length = bArr.length;
        byte[] bArr2 = new byte[length];
        for (int i = 0; i < length; i++) {
            bArr2[i] = bArr[(bArr.length - 1) - i];
        }
        return bArr2;
    }

    public static String bytes2HexString(byte[] bArr) {
        String str = "";
        if (bArr == null) {
            return "";
        }
        for (byte b : bArr) {
            String hexString = Integer.toHexString(b & 255);
            if (hexString.length() == 1) {
                hexString = "0" + hexString;
            }
            str = str + hexString.toUpperCase();
        }
        return str;
    }

    public static String bytes2HexString(byte[] bArr, int i) {
        String str = "";
        if (bArr == null) {
            return "";
        }
        for (int i2 = 0; i2 < i; i2++) {
            String hexString = Integer.toHexString(bArr[i2] & 255);
            if (hexString.length() == 1) {
                hexString = "0" + hexString;
            }
            str = str + hexString.toUpperCase();
        }
        return str;
    }

    public static String printInt(int[] iArr) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < iArr.length; i++) {
            sb.append("[0x" + Integer.toHexString(i) + "]:" + Integer.toHexString(iArr[i]));
            if (i < iArr.length) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    public static String print10(String str) {
        StringBuffer stringBuffer = new StringBuffer();
        for (String str2 : str.split(" ")) {
            stringBuffer.append(String.valueOf((char) Integer.parseInt(str2, 16)));
        }
        return stringBuffer.toString();
    }

    public static String byte2HexStr(byte[] bArr) {
        if (bArr == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder("");
        for (byte b : bArr) {
            String hexString = Integer.toHexString(b & 255);
            if (hexString.length() == 1) {
                hexString = "0" + hexString;
            }
            sb.append(hexString);
            sb.append(" ");
        }
        return sb.toString().toUpperCase().trim();
    }

    public static String byte2HexStr(byte[] bArr, int i, int i2) {
        if (bArr == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder("");
        while (i < i2) {
            String hexString = Integer.toHexString(bArr[i] & 255);
            if (hexString.length() == 1) {
                hexString = "0" + hexString;
            }
            sb.append(hexString);
            sb.append(" ");
            i++;
        }
        return sb.toString().toUpperCase().trim();
    }

    public static String byte2HexStr(byte[] bArr, int i) {
        if (bArr == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder("");
        for (int i2 = 0; i2 < i; i2++) {
            String hexString = Integer.toHexString(bArr[i2] & 255);
            if (hexString.length() == 1) {
                hexString = "0" + hexString;
            }
            sb.append(hexString);
            sb.append(" ");
        }
        return sb.toString().toUpperCase().trim();
    }
}