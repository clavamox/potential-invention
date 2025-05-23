package com.belon.camera;

/* loaded from: classes.dex */
public enum ErrorCode {
    Vertify,
    CRC,
    InitSocket,
    TimeOut,
    GetBatterying,
    DataFormat,
    UnConnected;

    @Override // java.lang.Enum
    public String toString() {
        return ordinal() == CRC.ordinal() ? "CRC校验失败!" : ordinal() == Vertify.ordinal() ? "连接校验失败!" : ordinal() == TimeOut.ordinal() ? "操作超时!" : ordinal() == InitSocket.ordinal() ? "Socket初始化失败" : ordinal() == GetBatterying.ordinal() ? "正在获取电量" : ordinal() == DataFormat.ordinal() ? "返回数据格式异常" : ordinal() == UnConnected.ordinal() ? "未打开连接" : "";
    }
}