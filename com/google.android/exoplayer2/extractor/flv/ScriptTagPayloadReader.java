package com.google.android.exoplayer2.extractor.flv;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.extractor.DummyTrackOutput;
import com.google.android.exoplayer2.util.ParsableByteArray;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/* loaded from: classes.dex */
final class ScriptTagPayloadReader extends TagPayloadReader {
    private static final int AMF_TYPE_BOOLEAN = 1;
    private static final int AMF_TYPE_DATE = 11;
    private static final int AMF_TYPE_ECMA_ARRAY = 8;
    private static final int AMF_TYPE_END_MARKER = 9;
    private static final int AMF_TYPE_NUMBER = 0;
    private static final int AMF_TYPE_OBJECT = 3;
    private static final int AMF_TYPE_STRICT_ARRAY = 10;
    private static final int AMF_TYPE_STRING = 2;
    private static final String KEY_DURATION = "duration";
    private static final String NAME_METADATA = "onMetaData";
    private long durationUs;

    @Override // com.google.android.exoplayer2.extractor.flv.TagPayloadReader
    protected boolean parseHeader(ParsableByteArray parsableByteArray) {
        return true;
    }

    @Override // com.google.android.exoplayer2.extractor.flv.TagPayloadReader
    public void seek() {
    }

    public ScriptTagPayloadReader() {
        super(new DummyTrackOutput());
        this.durationUs = C.TIME_UNSET;
    }

    public long getDurationUs() {
        return this.durationUs;
    }

    @Override // com.google.android.exoplayer2.extractor.flv.TagPayloadReader
    protected boolean parsePayload(ParsableByteArray parsableByteArray, long j) throws ParserException {
        if (readAmfType(parsableByteArray) != 2) {
            throw new ParserException();
        }
        if (!NAME_METADATA.equals(readAmfString(parsableByteArray)) || readAmfType(parsableByteArray) != 8) {
            return false;
        }
        HashMap<String, Object> readAmfEcmaArray = readAmfEcmaArray(parsableByteArray);
        if (readAmfEcmaArray.containsKey(KEY_DURATION)) {
            double doubleValue = ((Double) readAmfEcmaArray.get(KEY_DURATION)).doubleValue();
            if (doubleValue > 0.0d) {
                this.durationUs = (long) (doubleValue * 1000000.0d);
            }
        }
        return false;
    }

    private static int readAmfType(ParsableByteArray parsableByteArray) {
        return parsableByteArray.readUnsignedByte();
    }

    private static Boolean readAmfBoolean(ParsableByteArray parsableByteArray) {
        return Boolean.valueOf(parsableByteArray.readUnsignedByte() == 1);
    }

    private static Double readAmfDouble(ParsableByteArray parsableByteArray) {
        return Double.valueOf(Double.longBitsToDouble(parsableByteArray.readLong()));
    }

    private static String readAmfString(ParsableByteArray parsableByteArray) {
        int readUnsignedShort = parsableByteArray.readUnsignedShort();
        int position = parsableByteArray.getPosition();
        parsableByteArray.skipBytes(readUnsignedShort);
        return new String(parsableByteArray.data, position, readUnsignedShort);
    }

    private static ArrayList<Object> readAmfStrictArray(ParsableByteArray parsableByteArray) {
        int readUnsignedIntToInt = parsableByteArray.readUnsignedIntToInt();
        ArrayList<Object> arrayList = new ArrayList<>(readUnsignedIntToInt);
        for (int i = 0; i < readUnsignedIntToInt; i++) {
            Object readAmfData = readAmfData(parsableByteArray, readAmfType(parsableByteArray));
            if (readAmfData != null) {
                arrayList.add(readAmfData);
            }
        }
        return arrayList;
    }

    private static HashMap<String, Object> readAmfObject(ParsableByteArray parsableByteArray) {
        HashMap<String, Object> hashMap = new HashMap<>();
        while (true) {
            String readAmfString = readAmfString(parsableByteArray);
            int readAmfType = readAmfType(parsableByteArray);
            if (readAmfType == 9) {
                return hashMap;
            }
            Object readAmfData = readAmfData(parsableByteArray, readAmfType);
            if (readAmfData != null) {
                hashMap.put(readAmfString, readAmfData);
            }
        }
    }

    private static HashMap<String, Object> readAmfEcmaArray(ParsableByteArray parsableByteArray) {
        int readUnsignedIntToInt = parsableByteArray.readUnsignedIntToInt();
        HashMap<String, Object> hashMap = new HashMap<>(readUnsignedIntToInt);
        for (int i = 0; i < readUnsignedIntToInt; i++) {
            String readAmfString = readAmfString(parsableByteArray);
            Object readAmfData = readAmfData(parsableByteArray, readAmfType(parsableByteArray));
            if (readAmfData != null) {
                hashMap.put(readAmfString, readAmfData);
            }
        }
        return hashMap;
    }

    private static Date readAmfDate(ParsableByteArray parsableByteArray) {
        Date date = new Date((long) readAmfDouble(parsableByteArray).doubleValue());
        parsableByteArray.skipBytes(2);
        return date;
    }

    private static Object readAmfData(ParsableByteArray parsableByteArray, int i) {
        if (i == 0) {
            return readAmfDouble(parsableByteArray);
        }
        if (i == 1) {
            return readAmfBoolean(parsableByteArray);
        }
        if (i == 2) {
            return readAmfString(parsableByteArray);
        }
        if (i == 3) {
            return readAmfObject(parsableByteArray);
        }
        if (i == 8) {
            return readAmfEcmaArray(parsableByteArray);
        }
        if (i == 10) {
            return readAmfStrictArray(parsableByteArray);
        }
        if (i != 11) {
            return null;
        }
        return readAmfDate(parsableByteArray);
    }
}