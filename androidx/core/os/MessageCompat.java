package androidx.core.os;

import android.os.Message;

/* loaded from: classes.dex */
public final class MessageCompat {
    private static boolean sTryIsAsynchronous = true;
    private static boolean sTrySetAsynchronous = true;

    public static void setAsynchronous(Message message, boolean z) {
        message.setAsynchronous(z);
    }

    public static boolean isAsynchronous(Message message) {
        return message.isAsynchronous();
    }

    private MessageCompat() {
    }
}