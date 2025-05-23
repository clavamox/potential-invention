package com.mylhyl.circledialog.internal;

import android.text.InputFilter;
import android.text.Spanned;
import java.util.regex.Pattern;

/* loaded from: classes.dex */
public class EmojiFilter implements InputFilter {
    private static final Pattern EMOJI_PATTERN = Pattern.compile("[🀀-🏿]|[🐀-\u1f7ff]|[☀-⟿]", 66);

    private static boolean isEmojiCharacter(char c) {
        return c == 0 || c == '\t' || c == '\n' || c == '\r' || (c >= ' ' && c <= 55295) || ((c >= 57344 && c <= 65533) || (c >= 0 && c <= 65535));
    }

    private static boolean containsEmoji(CharSequence charSequence) {
        int length = charSequence.length();
        for (int i = 0; i < length; i++) {
            if (!isEmojiCharacter(charSequence.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    @Override // android.text.InputFilter
    public CharSequence filter(CharSequence charSequence, int i, int i2, Spanned spanned, int i3, int i4) {
        return (EMOJI_PATTERN.matcher(charSequence).find() || containsEmoji(charSequence)) ? "" : charSequence;
    }
}