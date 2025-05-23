package com.google.android.exoplayer2.source.hls.playlist;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Base64;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.drm.DrmInitData;
import com.google.android.exoplayer2.extractor.mp4.PsshAtomUtil;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.metadata.icy.IcyHeaders;
import com.google.android.exoplayer2.offline.DownloadRequest;
import com.google.android.exoplayer2.source.UnrecognizedInputFormatException;
import com.google.android.exoplayer2.source.hls.HlsTrackMetadataEntry;
import com.google.android.exoplayer2.source.hls.playlist.HlsMasterPlaylist;
import com.google.android.exoplayer2.source.hls.playlist.HlsMediaPlaylist;
import com.google.android.exoplayer2.upstream.ParsingLoadable;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.UriUtil;
import com.google.android.exoplayer2.util.Util;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.checkerframework.checker.nullness.qual.EnsuresNonNullIf;

/* loaded from: classes.dex */
public final class HlsPlaylistParser implements ParsingLoadable.Parser<HlsPlaylist> {
    private static final String ATTR_CLOSED_CAPTIONS_NONE = "CLOSED-CAPTIONS=NONE";
    private static final String BOOLEAN_FALSE = "NO";
    private static final String BOOLEAN_TRUE = "YES";
    private static final String KEYFORMAT_IDENTITY = "identity";
    private static final String KEYFORMAT_PLAYREADY = "com.microsoft.playready";
    private static final String KEYFORMAT_WIDEVINE_PSSH_BINARY = "urn:uuid:edef8ba9-79d6-4ace-a3c8-27dcd51d21ed";
    private static final String KEYFORMAT_WIDEVINE_PSSH_JSON = "com.widevine";
    private static final String METHOD_AES_128 = "AES-128";
    private static final String METHOD_NONE = "NONE";
    private static final String METHOD_SAMPLE_AES = "SAMPLE-AES";
    private static final String METHOD_SAMPLE_AES_CENC = "SAMPLE-AES-CENC";
    private static final String METHOD_SAMPLE_AES_CTR = "SAMPLE-AES-CTR";
    private static final String PLAYLIST_HEADER = "#EXTM3U";
    private static final String TAG_BYTERANGE = "#EXT-X-BYTERANGE";
    private static final String TAG_DEFINE = "#EXT-X-DEFINE";
    private static final String TAG_DISCONTINUITY = "#EXT-X-DISCONTINUITY";
    private static final String TAG_DISCONTINUITY_SEQUENCE = "#EXT-X-DISCONTINUITY-SEQUENCE";
    private static final String TAG_ENDLIST = "#EXT-X-ENDLIST";
    private static final String TAG_GAP = "#EXT-X-GAP";
    private static final String TAG_INDEPENDENT_SEGMENTS = "#EXT-X-INDEPENDENT-SEGMENTS";
    private static final String TAG_INIT_SEGMENT = "#EXT-X-MAP";
    private static final String TAG_KEY = "#EXT-X-KEY";
    private static final String TAG_MEDIA = "#EXT-X-MEDIA";
    private static final String TAG_MEDIA_DURATION = "#EXTINF";
    private static final String TAG_MEDIA_SEQUENCE = "#EXT-X-MEDIA-SEQUENCE";
    private static final String TAG_PLAYLIST_TYPE = "#EXT-X-PLAYLIST-TYPE";
    private static final String TAG_PREFIX = "#EXT";
    private static final String TAG_PROGRAM_DATE_TIME = "#EXT-X-PROGRAM-DATE-TIME";
    private static final String TAG_SESSION_KEY = "#EXT-X-SESSION-KEY";
    private static final String TAG_START = "#EXT-X-START";
    private static final String TAG_STREAM_INF = "#EXT-X-STREAM-INF";
    private static final String TAG_TARGET_DURATION = "#EXT-X-TARGETDURATION";
    private static final String TAG_VERSION = "#EXT-X-VERSION";
    private static final String TYPE_AUDIO = "AUDIO";
    private static final String TYPE_CLOSED_CAPTIONS = "CLOSED-CAPTIONS";
    private static final String TYPE_SUBTITLES = "SUBTITLES";
    private static final String TYPE_VIDEO = "VIDEO";
    private final HlsMasterPlaylist masterPlaylist;
    private static final Pattern REGEX_AVERAGE_BANDWIDTH = Pattern.compile("AVERAGE-BANDWIDTH=(\\d+)\\b");
    private static final Pattern REGEX_VIDEO = Pattern.compile("VIDEO=\"(.+?)\"");
    private static final Pattern REGEX_AUDIO = Pattern.compile("AUDIO=\"(.+?)\"");
    private static final Pattern REGEX_SUBTITLES = Pattern.compile("SUBTITLES=\"(.+?)\"");
    private static final Pattern REGEX_CLOSED_CAPTIONS = Pattern.compile("CLOSED-CAPTIONS=\"(.+?)\"");
    private static final Pattern REGEX_BANDWIDTH = Pattern.compile("[^-]BANDWIDTH=(\\d+)\\b");
    private static final Pattern REGEX_CHANNELS = Pattern.compile("CHANNELS=\"(.+?)\"");
    private static final Pattern REGEX_CODECS = Pattern.compile("CODECS=\"(.+?)\"");
    private static final Pattern REGEX_RESOLUTION = Pattern.compile("RESOLUTION=(\\d+x\\d+)");
    private static final Pattern REGEX_FRAME_RATE = Pattern.compile("FRAME-RATE=([\\d\\.]+)\\b");
    private static final Pattern REGEX_TARGET_DURATION = Pattern.compile("#EXT-X-TARGETDURATION:(\\d+)\\b");
    private static final Pattern REGEX_VERSION = Pattern.compile("#EXT-X-VERSION:(\\d+)\\b");
    private static final Pattern REGEX_PLAYLIST_TYPE = Pattern.compile("#EXT-X-PLAYLIST-TYPE:(.+)\\b");
    private static final Pattern REGEX_MEDIA_SEQUENCE = Pattern.compile("#EXT-X-MEDIA-SEQUENCE:(\\d+)\\b");
    private static final Pattern REGEX_MEDIA_DURATION = Pattern.compile("#EXTINF:([\\d\\.]+)\\b");
    private static final Pattern REGEX_MEDIA_TITLE = Pattern.compile("#EXTINF:[\\d\\.]+\\b,(.+)");
    private static final Pattern REGEX_TIME_OFFSET = Pattern.compile("TIME-OFFSET=(-?[\\d\\.]+)\\b");
    private static final Pattern REGEX_BYTERANGE = Pattern.compile("#EXT-X-BYTERANGE:(\\d+(?:@\\d+)?)\\b");
    private static final Pattern REGEX_ATTR_BYTERANGE = Pattern.compile("BYTERANGE=\"(\\d+(?:@\\d+)?)\\b\"");
    private static final Pattern REGEX_METHOD = Pattern.compile("METHOD=(NONE|AES-128|SAMPLE-AES|SAMPLE-AES-CENC|SAMPLE-AES-CTR)\\s*(?:,|$)");
    private static final Pattern REGEX_KEYFORMAT = Pattern.compile("KEYFORMAT=\"(.+?)\"");
    private static final Pattern REGEX_KEYFORMATVERSIONS = Pattern.compile("KEYFORMATVERSIONS=\"(.+?)\"");
    private static final Pattern REGEX_URI = Pattern.compile("URI=\"(.+?)\"");
    private static final Pattern REGEX_IV = Pattern.compile("IV=([^,.*]+)");
    private static final Pattern REGEX_TYPE = Pattern.compile("TYPE=(AUDIO|VIDEO|SUBTITLES|CLOSED-CAPTIONS)");
    private static final Pattern REGEX_LANGUAGE = Pattern.compile("LANGUAGE=\"(.+?)\"");
    private static final Pattern REGEX_NAME = Pattern.compile("NAME=\"(.+?)\"");
    private static final Pattern REGEX_GROUP_ID = Pattern.compile("GROUP-ID=\"(.+?)\"");
    private static final Pattern REGEX_CHARACTERISTICS = Pattern.compile("CHARACTERISTICS=\"(.+?)\"");
    private static final Pattern REGEX_INSTREAM_ID = Pattern.compile("INSTREAM-ID=\"((?:CC|SERVICE)\\d+)\"");
    private static final Pattern REGEX_AUTOSELECT = compileBooleanAttrPattern("AUTOSELECT");
    private static final Pattern REGEX_DEFAULT = compileBooleanAttrPattern("DEFAULT");
    private static final Pattern REGEX_FORCED = compileBooleanAttrPattern("FORCED");
    private static final Pattern REGEX_VALUE = Pattern.compile("VALUE=\"(.+?)\"");
    private static final Pattern REGEX_IMPORT = Pattern.compile("IMPORT=\"(.+?)\"");
    private static final Pattern REGEX_VARIABLE_REFERENCE = Pattern.compile("\\{\\$([a-zA-Z0-9\\-_]+)\\}");

    public HlsPlaylistParser() {
        this(HlsMasterPlaylist.EMPTY);
    }

    public HlsPlaylistParser(HlsMasterPlaylist hlsMasterPlaylist) {
        this.masterPlaylist = hlsMasterPlaylist;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.google.android.exoplayer2.upstream.ParsingLoadable.Parser
    public HlsPlaylist parse(Uri uri, InputStream inputStream) throws IOException {
        String trim;
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        ArrayDeque arrayDeque = new ArrayDeque();
        try {
            if (!checkPlaylistHeader(bufferedReader)) {
                throw new UnrecognizedInputFormatException("Input does not start with the #EXTM3U header.", uri);
            }
            while (true) {
                String readLine = bufferedReader.readLine();
                if (readLine != null) {
                    trim = readLine.trim();
                    if (!trim.isEmpty()) {
                        if (trim.startsWith(TAG_STREAM_INF)) {
                            arrayDeque.add(trim);
                            return parseMasterPlaylist(new LineIterator(arrayDeque, bufferedReader), uri.toString());
                        }
                        if (trim.startsWith(TAG_TARGET_DURATION) || trim.startsWith(TAG_MEDIA_SEQUENCE) || trim.startsWith(TAG_MEDIA_DURATION) || trim.startsWith(TAG_KEY) || trim.startsWith(TAG_BYTERANGE) || trim.equals(TAG_DISCONTINUITY) || trim.equals(TAG_DISCONTINUITY_SEQUENCE) || trim.equals(TAG_ENDLIST)) {
                            break;
                        }
                        arrayDeque.add(trim);
                    }
                } else {
                    Util.closeQuietly(bufferedReader);
                    throw new ParserException("Failed to parse the playlist, could not identify any tags.");
                }
            }
            arrayDeque.add(trim);
            return parseMediaPlaylist(this.masterPlaylist, new LineIterator(arrayDeque, bufferedReader), uri.toString());
        } finally {
            Util.closeQuietly(bufferedReader);
        }
    }

    private static boolean checkPlaylistHeader(BufferedReader bufferedReader) throws IOException {
        int read = bufferedReader.read();
        if (read == 239) {
            if (bufferedReader.read() != 187 || bufferedReader.read() != 191) {
                return false;
            }
            read = bufferedReader.read();
        }
        int skipIgnorableWhitespace = skipIgnorableWhitespace(bufferedReader, true, read);
        for (int i = 0; i < 7; i++) {
            if (skipIgnorableWhitespace != PLAYLIST_HEADER.charAt(i)) {
                return false;
            }
            skipIgnorableWhitespace = bufferedReader.read();
        }
        return Util.isLinebreak(skipIgnorableWhitespace(bufferedReader, false, skipIgnorableWhitespace));
    }

    private static int skipIgnorableWhitespace(BufferedReader bufferedReader, boolean z, int i) throws IOException {
        while (i != -1 && Character.isWhitespace(i) && (z || !Util.isLinebreak(i))) {
            i = bufferedReader.read();
        }
        return i;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v5, types: [java.util.List] */
    private static HlsMasterPlaylist parseMasterPlaylist(LineIterator lineIterator, String str) throws IOException {
        char c;
        int parseInt;
        String str2;
        String str3;
        int i;
        String str4;
        int i2;
        int i3;
        float f;
        HashMap hashMap;
        HashSet hashSet;
        ArrayList arrayList;
        ArrayList arrayList2;
        ArrayList arrayList3;
        boolean z;
        int i4;
        int i5;
        int i6;
        String str5 = str;
        HashMap hashMap2 = new HashMap();
        HashMap hashMap3 = new HashMap();
        ArrayList arrayList4 = new ArrayList();
        ArrayList arrayList5 = new ArrayList();
        ArrayList arrayList6 = new ArrayList();
        ArrayList arrayList7 = new ArrayList();
        ArrayList arrayList8 = new ArrayList();
        ArrayList arrayList9 = new ArrayList();
        ArrayList arrayList10 = new ArrayList();
        ArrayList arrayList11 = new ArrayList();
        boolean z2 = false;
        boolean z3 = false;
        while (lineIterator.hasNext()) {
            String next = lineIterator.next();
            if (next.startsWith(TAG_PREFIX)) {
                arrayList11.add(next);
            }
            if (next.startsWith(TAG_DEFINE)) {
                hashMap3.put(parseStringAttr(next, REGEX_NAME, hashMap3), parseStringAttr(next, REGEX_VALUE, hashMap3));
            } else if (next.equals(TAG_INDEPENDENT_SEGMENTS)) {
                z2 = true;
            } else if (next.startsWith(TAG_MEDIA)) {
                arrayList9.add(next);
            } else {
                if (next.startsWith(TAG_SESSION_KEY)) {
                    DrmInitData.SchemeData parseDrmSchemeData = parseDrmSchemeData(next, parseOptionalStringAttr(next, REGEX_KEYFORMAT, KEYFORMAT_IDENTITY, hashMap3), hashMap3);
                    if (parseDrmSchemeData != null) {
                        z = z2;
                        arrayList10.add(new DrmInitData(parseEncryptionScheme(parseStringAttr(next, REGEX_METHOD, hashMap3)), parseDrmSchemeData));
                    } else {
                        z = z2;
                    }
                } else {
                    z = z2;
                    if (next.startsWith(TAG_STREAM_INF)) {
                        boolean contains = z3 | next.contains(ATTR_CLOSED_CAPTIONS_NONE);
                        int parseIntAttr = parseIntAttr(next, REGEX_BANDWIDTH);
                        parseOptionalIntAttr(next, REGEX_AVERAGE_BANDWIDTH, -1);
                        String parseOptionalStringAttr = parseOptionalStringAttr(next, REGEX_CODECS, hashMap3);
                        String parseOptionalStringAttr2 = parseOptionalStringAttr(next, REGEX_RESOLUTION, hashMap3);
                        if (parseOptionalStringAttr2 != null) {
                            String[] split = parseOptionalStringAttr2.split("x");
                            int parseInt2 = Integer.parseInt(split[0]);
                            int parseInt3 = Integer.parseInt(split[1]);
                            if (parseInt2 <= 0 || parseInt3 <= 0) {
                                parseInt2 = -1;
                                i6 = -1;
                            } else {
                                i6 = parseInt3;
                            }
                            i4 = parseInt2;
                            i5 = i6;
                        } else {
                            i4 = -1;
                            i5 = -1;
                        }
                        String parseOptionalStringAttr3 = parseOptionalStringAttr(next, REGEX_FRAME_RATE, hashMap3);
                        float parseFloat = parseOptionalStringAttr3 != null ? Float.parseFloat(parseOptionalStringAttr3) : -1.0f;
                        String parseOptionalStringAttr4 = parseOptionalStringAttr(next, REGEX_VIDEO, hashMap3);
                        String parseOptionalStringAttr5 = parseOptionalStringAttr(next, REGEX_AUDIO, hashMap3);
                        String parseOptionalStringAttr6 = parseOptionalStringAttr(next, REGEX_SUBTITLES, hashMap3);
                        arrayList = arrayList10;
                        String parseOptionalStringAttr7 = parseOptionalStringAttr(next, REGEX_CLOSED_CAPTIONS, hashMap3);
                        if (!lineIterator.hasNext()) {
                            throw new ParserException("#EXT-X-STREAM-INF tag must be followed by another line");
                        }
                        Uri resolveToUri = UriUtil.resolveToUri(str5, replaceVariableReferences(lineIterator.next(), hashMap3));
                        arrayList3 = arrayList8;
                        arrayList4.add(new HlsMasterPlaylist.Variant(resolveToUri, Format.createVideoContainerFormat(Integer.toString(arrayList4.size()), null, MimeTypes.APPLICATION_M3U8, null, parseOptionalStringAttr, null, parseIntAttr, i4, i5, parseFloat, null, 0, 0), parseOptionalStringAttr4, parseOptionalStringAttr5, parseOptionalStringAttr6, parseOptionalStringAttr7));
                        ArrayList arrayList12 = (ArrayList) hashMap2.get(resolveToUri);
                        if (arrayList12 == null) {
                            arrayList12 = new ArrayList();
                            hashMap2.put(resolveToUri, arrayList12);
                        }
                        arrayList2 = arrayList11;
                        arrayList12.add(new HlsTrackMetadataEntry.VariantInfo(parseIntAttr, parseOptionalStringAttr4, parseOptionalStringAttr5, parseOptionalStringAttr6, parseOptionalStringAttr7));
                        z3 = contains;
                        z2 = z;
                        arrayList8 = arrayList3;
                        arrayList11 = arrayList2;
                        arrayList10 = arrayList;
                    }
                }
                arrayList3 = arrayList8;
                arrayList2 = arrayList11;
                arrayList = arrayList10;
                z2 = z;
                arrayList8 = arrayList3;
                arrayList11 = arrayList2;
                arrayList10 = arrayList;
            }
            arrayList3 = arrayList8;
            arrayList2 = arrayList11;
            z = z2;
            arrayList = arrayList10;
            z2 = z;
            arrayList8 = arrayList3;
            arrayList11 = arrayList2;
            arrayList10 = arrayList;
        }
        ArrayList arrayList13 = arrayList8;
        ArrayList arrayList14 = arrayList11;
        boolean z4 = z2;
        ArrayList arrayList15 = arrayList10;
        ArrayList arrayList16 = new ArrayList();
        HashSet hashSet2 = new HashSet();
        int i7 = 0;
        while (i7 < arrayList4.size()) {
            HlsMasterPlaylist.Variant variant = (HlsMasterPlaylist.Variant) arrayList4.get(i7);
            if (hashSet2.add(variant.url)) {
                Assertions.checkState(variant.format.metadata == null);
                hashMap = hashMap2;
                hashSet = hashSet2;
                arrayList16.add(variant.copyWithFormat(variant.format.copyWithMetadata(new Metadata(new HlsTrackMetadataEntry(null, null, (List) Assertions.checkNotNull(hashMap2.get(variant.url)))))));
            } else {
                hashMap = hashMap2;
                hashSet = hashSet2;
            }
            i7++;
            hashSet2 = hashSet;
            hashMap2 = hashMap;
        }
        Format format = null;
        ArrayList arrayList17 = null;
        int i8 = 0;
        while (i8 < arrayList9.size()) {
            String str6 = (String) arrayList9.get(i8);
            String parseStringAttr = parseStringAttr(str6, REGEX_GROUP_ID, hashMap3);
            String parseStringAttr2 = parseStringAttr(str6, REGEX_NAME, hashMap3);
            String parseOptionalStringAttr8 = parseOptionalStringAttr(str6, REGEX_URI, hashMap3);
            Uri resolveToUri2 = parseOptionalStringAttr8 == null ? null : UriUtil.resolveToUri(str5, parseOptionalStringAttr8);
            String parseOptionalStringAttr9 = parseOptionalStringAttr(str6, REGEX_LANGUAGE, hashMap3);
            int parseSelectionFlags = parseSelectionFlags(str6);
            int parseRoleFlags = parseRoleFlags(str6, hashMap3);
            ArrayList arrayList18 = arrayList9;
            Format format2 = format;
            String str7 = parseStringAttr + ":" + parseStringAttr2;
            ArrayList arrayList19 = arrayList16;
            boolean z5 = z3;
            Metadata metadata = new Metadata(new HlsTrackMetadataEntry(parseStringAttr, parseStringAttr2, Collections.emptyList()));
            String parseStringAttr3 = parseStringAttr(str6, REGEX_TYPE, hashMap3);
            parseStringAttr3.hashCode();
            switch (parseStringAttr3.hashCode()) {
                case -959297733:
                    if (parseStringAttr3.equals(TYPE_SUBTITLES)) {
                        c = 0;
                        break;
                    }
                    c = 65535;
                    break;
                case -333210994:
                    if (parseStringAttr3.equals(TYPE_CLOSED_CAPTIONS)) {
                        c = 1;
                        break;
                    }
                    c = 65535;
                    break;
                case 62628790:
                    if (parseStringAttr3.equals(TYPE_AUDIO)) {
                        c = 2;
                        break;
                    }
                    c = 65535;
                    break;
                case 81665115:
                    if (parseStringAttr3.equals(TYPE_VIDEO)) {
                        c = 3;
                        break;
                    }
                    c = 65535;
                    break;
                default:
                    c = 65535;
                    break;
            }
            switch (c) {
                case 0:
                    arrayList7.add(new HlsMasterPlaylist.Rendition(resolveToUri2, Format.createTextContainerFormat(str7, parseStringAttr2, MimeTypes.APPLICATION_M3U8, MimeTypes.TEXT_VTT, null, -1, parseSelectionFlags, parseRoleFlags, parseOptionalStringAttr9).copyWithMetadata(metadata), parseStringAttr, parseStringAttr2));
                    format = format2;
                    break;
                case 1:
                    String parseStringAttr4 = parseStringAttr(str6, REGEX_INSTREAM_ID, hashMap3);
                    if (parseStringAttr4.startsWith("CC")) {
                        parseInt = Integer.parseInt(parseStringAttr4.substring(2));
                        str2 = MimeTypes.APPLICATION_CEA608;
                    } else {
                        parseInt = Integer.parseInt(parseStringAttr4.substring(7));
                        str2 = MimeTypes.APPLICATION_CEA708;
                    }
                    int i9 = parseInt;
                    String str8 = str2;
                    if (arrayList17 == null) {
                        arrayList17 = new ArrayList();
                    }
                    arrayList17.add(Format.createTextContainerFormat(str7, parseStringAttr2, null, str8, null, -1, parseSelectionFlags, parseRoleFlags, parseOptionalStringAttr9, i9));
                    format = format2;
                    break;
                case 2:
                    HlsMasterPlaylist.Variant variantWithAudioGroup = getVariantWithAudioGroup(arrayList4, parseStringAttr);
                    String codecsOfType = variantWithAudioGroup != null ? Util.getCodecsOfType(variantWithAudioGroup.format.codecs, 1) : null;
                    String mediaMimeType = codecsOfType != null ? MimeTypes.getMediaMimeType(codecsOfType) : null;
                    String parseOptionalStringAttr10 = parseOptionalStringAttr(str6, REGEX_CHANNELS, hashMap3);
                    if (parseOptionalStringAttr10 != null) {
                        int parseInt4 = Integer.parseInt(Util.splitAtFirst(parseOptionalStringAttr10, "/")[0]);
                        if (MimeTypes.AUDIO_E_AC3.equals(mediaMimeType) && parseOptionalStringAttr10.endsWith("/JOC")) {
                            mediaMimeType = MimeTypes.AUDIO_E_AC3_JOC;
                        }
                        str3 = mediaMimeType;
                        i = parseInt4;
                    } else {
                        str3 = mediaMimeType;
                        i = -1;
                    }
                    Format createAudioContainerFormat = Format.createAudioContainerFormat(str7, parseStringAttr2, MimeTypes.APPLICATION_M3U8, str3, codecsOfType, null, -1, i, -1, null, parseSelectionFlags, parseRoleFlags, parseOptionalStringAttr9);
                    if (resolveToUri2 != null) {
                        arrayList6.add(new HlsMasterPlaylist.Rendition(resolveToUri2, createAudioContainerFormat.copyWithMetadata(metadata), parseStringAttr, parseStringAttr2));
                        format = format2;
                        break;
                    } else {
                        format = createAudioContainerFormat;
                        break;
                    }
                    break;
                case 3:
                    HlsMasterPlaylist.Variant variantWithVideoGroup = getVariantWithVideoGroup(arrayList4, parseStringAttr);
                    if (variantWithVideoGroup != null) {
                        Format format3 = variantWithVideoGroup.format;
                        String codecsOfType2 = Util.getCodecsOfType(format3.codecs, 2);
                        int i10 = format3.width;
                        int i11 = format3.height;
                        f = format3.frameRate;
                        str4 = codecsOfType2;
                        i2 = i10;
                        i3 = i11;
                    } else {
                        str4 = null;
                        i2 = -1;
                        i3 = -1;
                        f = -1.0f;
                    }
                    Format copyWithMetadata = Format.createVideoContainerFormat(str7, parseStringAttr2, MimeTypes.APPLICATION_M3U8, str4 != null ? MimeTypes.getMediaMimeType(str4) : null, str4, null, -1, i2, i3, f, null, parseSelectionFlags, parseRoleFlags).copyWithMetadata(metadata);
                    if (resolveToUri2 != null) {
                        arrayList5.add(new HlsMasterPlaylist.Rendition(resolveToUri2, copyWithMetadata, parseStringAttr, parseStringAttr2));
                    }
                default:
                    format = format2;
                    break;
            }
            i8++;
            str5 = str;
            arrayList9 = arrayList18;
            arrayList16 = arrayList19;
            z3 = z5;
        }
        ArrayList arrayList20 = arrayList16;
        Format format4 = format;
        if (z3) {
            arrayList17 = Collections.emptyList();
        }
        return new HlsMasterPlaylist(str, arrayList14, arrayList20, arrayList5, arrayList6, arrayList7, arrayList13, format4, arrayList17, z4, hashMap3, arrayList15);
    }

    private static HlsMasterPlaylist.Variant getVariantWithAudioGroup(ArrayList<HlsMasterPlaylist.Variant> arrayList, String str) {
        for (int i = 0; i < arrayList.size(); i++) {
            HlsMasterPlaylist.Variant variant = arrayList.get(i);
            if (str.equals(variant.audioGroupId)) {
                return variant;
            }
        }
        return null;
    }

    private static HlsMasterPlaylist.Variant getVariantWithVideoGroup(ArrayList<HlsMasterPlaylist.Variant> arrayList, String str) {
        for (int i = 0; i < arrayList.size(); i++) {
            HlsMasterPlaylist.Variant variant = arrayList.get(i);
            if (str.equals(variant.videoGroupId)) {
                return variant;
            }
        }
        return null;
    }

    /* JADX WARN: Multi-variable type inference failed */
    private static HlsMediaPlaylist parseMediaPlaylist(HlsMasterPlaylist hlsMasterPlaylist, LineIterator lineIterator, String str) throws IOException {
        long j;
        long j2;
        String hexString;
        TreeMap treeMap;
        long j3;
        DrmInitData drmInitData;
        DrmInitData drmInitData2;
        HlsMasterPlaylist hlsMasterPlaylist2 = hlsMasterPlaylist;
        boolean z = hlsMasterPlaylist2.hasIndependentSegments;
        HashMap hashMap = new HashMap();
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        TreeMap treeMap2 = new TreeMap();
        char c = 0;
        long j4 = C.TIME_UNSET;
        int i = 1;
        int i2 = z ? 1 : 0;
        int i3 = 0;
        int i4 = 0;
        int i5 = 0;
        int i6 = 0;
        boolean z2 = 0;
        int i7 = 0;
        int i8 = 1;
        long j5 = -9223372036854775807L;
        String str2 = "";
        String str3 = null;
        long j6 = 0;
        long j7 = 0;
        DrmInitData drmInitData3 = null;
        long j8 = 0;
        long j9 = 0;
        DrmInitData drmInitData4 = null;
        String str4 = null;
        String str5 = null;
        long j10 = -1;
        long j11 = 0;
        HlsMediaPlaylist.Segment segment = null;
        long j12 = 0;
        while (lineIterator.hasNext()) {
            String next = lineIterator.next();
            if (next.startsWith(TAG_PREFIX)) {
                arrayList2.add(next);
            }
            if (next.startsWith(TAG_PLAYLIST_TYPE)) {
                String parseStringAttr = parseStringAttr(next, REGEX_PLAYLIST_TYPE, hashMap);
                if ("VOD".equals(parseStringAttr)) {
                    i3 = i;
                } else if ("EVENT".equals(parseStringAttr)) {
                    i3 = 2;
                }
            } else if (next.startsWith(TAG_START)) {
                j4 = (long) (parseDoubleAttr(next, REGEX_TIME_OFFSET) * 1000000.0d);
            } else if (next.startsWith(TAG_INIT_SEGMENT)) {
                String parseStringAttr2 = parseStringAttr(next, REGEX_URI, hashMap);
                String parseOptionalStringAttr = parseOptionalStringAttr(next, REGEX_ATTR_BYTERANGE, hashMap);
                if (parseOptionalStringAttr != null) {
                    String[] split = parseOptionalStringAttr.split("@");
                    long parseLong = Long.parseLong(split[c]);
                    if (split.length > i) {
                        j8 = Long.parseLong(split[i]);
                    }
                    j2 = parseLong;
                    j = j8;
                } else {
                    j = j8;
                    j2 = j10;
                }
                if (str4 != null && str5 == null) {
                    throw new ParserException("The encryption IV attribute must be present when an initialization segment is encrypted with METHOD=AES-128.");
                }
                segment = new HlsMediaPlaylist.Segment(parseStringAttr2, j, j2, str4, str5);
                c = 0;
                j8 = 0;
                j10 = -1;
            } else {
                if (next.startsWith(TAG_TARGET_DURATION)) {
                    j5 = parseIntAttr(next, REGEX_TARGET_DURATION) * 1000000;
                    z2 = z2;
                } else if (next.startsWith(TAG_MEDIA_SEQUENCE)) {
                    j9 = parseLongAttr(next, REGEX_MEDIA_SEQUENCE);
                    j7 = j9;
                    z2 = z2;
                } else if (next.startsWith(TAG_VERSION)) {
                    i8 = parseIntAttr(next, REGEX_VERSION);
                    z2 = z2;
                } else {
                    if (next.startsWith(TAG_DEFINE)) {
                        String parseOptionalStringAttr2 = parseOptionalStringAttr(next, REGEX_IMPORT, hashMap);
                        if (parseOptionalStringAttr2 != null) {
                            String str6 = hlsMasterPlaylist2.variableDefinitions.get(parseOptionalStringAttr2);
                            if (str6 != null) {
                                hashMap.put(parseOptionalStringAttr2, str6);
                            }
                        } else {
                            hashMap.put(parseStringAttr(next, REGEX_NAME, hashMap), parseStringAttr(next, REGEX_VALUE, hashMap));
                        }
                    } else if (next.startsWith(TAG_MEDIA_DURATION)) {
                        long parseDoubleAttr = (long) (parseDoubleAttr(next, REGEX_MEDIA_DURATION) * 1000000.0d);
                        str2 = parseOptionalStringAttr(next, REGEX_MEDIA_TITLE, "", hashMap);
                        j12 = parseDoubleAttr;
                        z2 = z2;
                    } else if (next.startsWith(TAG_KEY)) {
                        String parseStringAttr3 = parseStringAttr(next, REGEX_METHOD, hashMap);
                        String parseOptionalStringAttr3 = parseOptionalStringAttr(next, REGEX_KEYFORMAT, KEYFORMAT_IDENTITY, hashMap);
                        if (METHOD_NONE.equals(parseStringAttr3)) {
                            treeMap2.clear();
                            drmInitData4 = null;
                            str4 = null;
                            str5 = null;
                            z2 = z2;
                        } else {
                            String parseOptionalStringAttr4 = parseOptionalStringAttr(next, REGEX_IV, hashMap);
                            if (KEYFORMAT_IDENTITY.equals(parseOptionalStringAttr3)) {
                                if (METHOD_AES_128.equals(parseStringAttr3)) {
                                    str4 = parseStringAttr(next, REGEX_URI, hashMap);
                                    str5 = parseOptionalStringAttr4;
                                    z2 = z2;
                                }
                            } else {
                                if (str3 == null) {
                                    str3 = parseEncryptionScheme(parseStringAttr3);
                                }
                                DrmInitData.SchemeData parseDrmSchemeData = parseDrmSchemeData(next, parseOptionalStringAttr3, hashMap);
                                if (parseDrmSchemeData != null) {
                                    treeMap2.put(parseOptionalStringAttr3, parseDrmSchemeData);
                                    str5 = parseOptionalStringAttr4;
                                    drmInitData4 = null;
                                    str4 = null;
                                    z2 = z2;
                                }
                            }
                            str5 = parseOptionalStringAttr4;
                            str4 = null;
                            z2 = z2;
                        }
                    } else if (next.startsWith(TAG_BYTERANGE)) {
                        String[] split2 = parseStringAttr(next, REGEX_BYTERANGE, hashMap).split("@");
                        j10 = Long.parseLong(split2[0]);
                        z2 = z2;
                        if (split2.length > i) {
                            j8 = Long.parseLong(split2[i]);
                            z2 = z2;
                        }
                    } else if (next.startsWith(TAG_DISCONTINUITY_SEQUENCE)) {
                        i5 = Integer.parseInt(next.substring(next.indexOf(58) + i));
                        i4 = i;
                        z2 = z2;
                    } else if (next.equals(TAG_DISCONTINUITY)) {
                        i7++;
                        z2 = z2;
                    } else if (next.startsWith(TAG_PROGRAM_DATE_TIME)) {
                        if (j6 == 0) {
                            j6 = C.msToUs(Util.parseXsDateTime(next.substring(next.indexOf(58) + i))) - j11;
                            z2 = z2;
                        }
                    } else if (next.equals(TAG_GAP)) {
                        z2 = i;
                    } else if (next.equals(TAG_INDEPENDENT_SEGMENTS)) {
                        i2 = i;
                        z2 = z2;
                    } else if (next.equals(TAG_ENDLIST)) {
                        i6 = i;
                        z2 = z2;
                    } else if (!next.startsWith("#")) {
                        if (str4 == null) {
                            hexString = null;
                        } else {
                            hexString = str5 != null ? str5 : Long.toHexString(j9);
                        }
                        long j13 = j9 + 1;
                        long j14 = j10 == -1 ? 0L : j8;
                        if (drmInitData4 != null || treeMap2.isEmpty()) {
                            treeMap = treeMap2;
                            j3 = j13;
                            drmInitData = drmInitData4;
                        } else {
                            treeMap = treeMap2;
                            DrmInitData.SchemeData[] schemeDataArr = (DrmInitData.SchemeData[]) treeMap2.values().toArray(new DrmInitData.SchemeData[0]);
                            DrmInitData drmInitData5 = new DrmInitData(str3, schemeDataArr);
                            if (drmInitData3 == null) {
                                DrmInitData.SchemeData[] schemeDataArr2 = new DrmInitData.SchemeData[schemeDataArr.length];
                                drmInitData2 = drmInitData5;
                                j3 = j13;
                                for (int i9 = 0; i9 < schemeDataArr.length; i9++) {
                                    schemeDataArr2[i9] = schemeDataArr[i9].copyWithData(null);
                                }
                                drmInitData3 = new DrmInitData(str3, schemeDataArr2);
                            } else {
                                drmInitData2 = drmInitData5;
                                j3 = j13;
                            }
                            drmInitData = drmInitData2;
                        }
                        arrayList.add(new HlsMediaPlaylist.Segment(replaceVariableReferences(next, hashMap), segment, str2, j12, i7, j11, drmInitData, str4, hexString, j14, j10, z2));
                        j11 += j12;
                        if (j10 != -1) {
                            j14 += j10;
                        }
                        j8 = j14;
                        drmInitData4 = drmInitData;
                        str2 = "";
                        j10 = -1;
                        treeMap2 = treeMap;
                        j9 = j3;
                        c = 0;
                        i = 1;
                        z2 = 0;
                        j12 = 0;
                        hlsMasterPlaylist2 = hlsMasterPlaylist;
                    }
                    hlsMasterPlaylist2 = hlsMasterPlaylist;
                    treeMap2 = treeMap2;
                    c = 0;
                    i = 1;
                }
                c = 0;
            }
        }
        return new HlsMediaPlaylist(i3, str, arrayList2, j4, j6, i4, i5, j7, i8, j5, i2, i6, j6 != 0, drmInitData3, arrayList);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v2, types: [int] */
    /* JADX WARN: Type inference failed for: r0v6 */
    /* JADX WARN: Type inference failed for: r0v7 */
    private static int parseSelectionFlags(String str) {
        boolean parseOptionalBooleanAttribute = parseOptionalBooleanAttribute(str, REGEX_DEFAULT, false);
        ?? r0 = parseOptionalBooleanAttribute;
        if (parseOptionalBooleanAttribute(str, REGEX_FORCED, false)) {
            r0 = (parseOptionalBooleanAttribute ? 1 : 0) | 2;
        }
        return parseOptionalBooleanAttribute(str, REGEX_AUTOSELECT, false) ? r0 | 4 : r0;
    }

    private static int parseRoleFlags(String str, Map<String, String> map) {
        String parseOptionalStringAttr = parseOptionalStringAttr(str, REGEX_CHARACTERISTICS, map);
        if (TextUtils.isEmpty(parseOptionalStringAttr)) {
            return 0;
        }
        String[] split = Util.split(parseOptionalStringAttr, ",");
        int i = Util.contains(split, "public.accessibility.describes-video") ? 512 : 0;
        if (Util.contains(split, "public.accessibility.transcribes-spoken-dialog")) {
            i |= 4096;
        }
        if (Util.contains(split, "public.accessibility.describes-music-and-sound")) {
            i |= 1024;
        }
        return Util.contains(split, "public.easy-to-read") ? i | 8192 : i;
    }

    private static DrmInitData.SchemeData parseDrmSchemeData(String str, String str2, Map<String, String> map) throws ParserException {
        String parseOptionalStringAttr = parseOptionalStringAttr(str, REGEX_KEYFORMATVERSIONS, IcyHeaders.REQUEST_HEADER_ENABLE_METADATA_VALUE, map);
        if (KEYFORMAT_WIDEVINE_PSSH_BINARY.equals(str2)) {
            String parseStringAttr = parseStringAttr(str, REGEX_URI, map);
            return new DrmInitData.SchemeData(C.WIDEVINE_UUID, MimeTypes.VIDEO_MP4, Base64.decode(parseStringAttr.substring(parseStringAttr.indexOf(44)), 0));
        }
        if (KEYFORMAT_WIDEVINE_PSSH_JSON.equals(str2)) {
            return new DrmInitData.SchemeData(C.WIDEVINE_UUID, DownloadRequest.TYPE_HLS, Util.getUtf8Bytes(str));
        }
        if (!KEYFORMAT_PLAYREADY.equals(str2) || !IcyHeaders.REQUEST_HEADER_ENABLE_METADATA_VALUE.equals(parseOptionalStringAttr)) {
            return null;
        }
        String parseStringAttr2 = parseStringAttr(str, REGEX_URI, map);
        return new DrmInitData.SchemeData(C.PLAYREADY_UUID, MimeTypes.VIDEO_MP4, PsshAtomUtil.buildPsshAtom(C.PLAYREADY_UUID, Base64.decode(parseStringAttr2.substring(parseStringAttr2.indexOf(44)), 0)));
    }

    private static String parseEncryptionScheme(String str) {
        return (METHOD_SAMPLE_AES_CENC.equals(str) || METHOD_SAMPLE_AES_CTR.equals(str)) ? C.CENC_TYPE_cenc : C.CENC_TYPE_cbcs;
    }

    private static int parseIntAttr(String str, Pattern pattern) throws ParserException {
        return Integer.parseInt(parseStringAttr(str, pattern, Collections.emptyMap()));
    }

    private static int parseOptionalIntAttr(String str, Pattern pattern, int i) {
        Matcher matcher = pattern.matcher(str);
        return matcher.find() ? Integer.parseInt(matcher.group(1)) : i;
    }

    private static long parseLongAttr(String str, Pattern pattern) throws ParserException {
        return Long.parseLong(parseStringAttr(str, pattern, Collections.emptyMap()));
    }

    private static double parseDoubleAttr(String str, Pattern pattern) throws ParserException {
        return Double.parseDouble(parseStringAttr(str, pattern, Collections.emptyMap()));
    }

    private static String parseStringAttr(String str, Pattern pattern, Map<String, String> map) throws ParserException {
        String parseOptionalStringAttr = parseOptionalStringAttr(str, pattern, map);
        if (parseOptionalStringAttr != null) {
            return parseOptionalStringAttr;
        }
        throw new ParserException("Couldn't match " + pattern.pattern() + " in " + str);
    }

    private static String parseOptionalStringAttr(String str, Pattern pattern, Map<String, String> map) {
        return parseOptionalStringAttr(str, pattern, null, map);
    }

    private static String parseOptionalStringAttr(String str, Pattern pattern, String str2, Map<String, String> map) {
        Matcher matcher = pattern.matcher(str);
        if (matcher.find()) {
            str2 = matcher.group(1);
        }
        return (map.isEmpty() || str2 == null) ? str2 : replaceVariableReferences(str2, map);
    }

    private static String replaceVariableReferences(String str, Map<String, String> map) {
        Matcher matcher = REGEX_VARIABLE_REFERENCE.matcher(str);
        StringBuffer stringBuffer = new StringBuffer();
        while (matcher.find()) {
            String group = matcher.group(1);
            if (map.containsKey(group)) {
                matcher.appendReplacement(stringBuffer, Matcher.quoteReplacement(map.get(group)));
            }
        }
        matcher.appendTail(stringBuffer);
        return stringBuffer.toString();
    }

    private static boolean parseOptionalBooleanAttribute(String str, Pattern pattern, boolean z) {
        Matcher matcher = pattern.matcher(str);
        return matcher.find() ? matcher.group(1).equals(BOOLEAN_TRUE) : z;
    }

    private static Pattern compileBooleanAttrPattern(String str) {
        return Pattern.compile(str + "=(NO|YES)");
    }

    private static class LineIterator {
        private final Queue<String> extraLines;
        private String next;
        private final BufferedReader reader;

        public LineIterator(Queue<String> queue, BufferedReader bufferedReader) {
            this.extraLines = queue;
            this.reader = bufferedReader;
        }

        @EnsuresNonNullIf(expression = {"next"}, result = true)
        public boolean hasNext() throws IOException {
            String trim;
            if (this.next != null) {
                return true;
            }
            if (!this.extraLines.isEmpty()) {
                this.next = (String) Assertions.checkNotNull(this.extraLines.poll());
                return true;
            }
            do {
                String readLine = this.reader.readLine();
                this.next = readLine;
                if (readLine == null) {
                    return false;
                }
                trim = readLine.trim();
                this.next = trim;
            } while (trim.isEmpty());
            return true;
        }

        public String next() throws IOException {
            if (hasNext()) {
                String str = this.next;
                this.next = null;
                return str;
            }
            throw new NoSuchElementException();
        }
    }
}