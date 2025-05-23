package com.google.android.exoplayer2.source.dash.manifest;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Pair;
import android.util.Xml;
import androidx.exifinterface.media.ExifInterface;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.drm.DrmInitData;
import com.google.android.exoplayer2.metadata.emsg.EventMessage;
import com.google.android.exoplayer2.source.dash.manifest.SegmentBase;
import com.google.android.exoplayer2.text.ttml.TtmlNode;
import com.google.android.exoplayer2.upstream.ParsingLoadable;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.UriUtil;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.util.XmlPullParserUtil;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.xml.sax.helpers.DefaultHandler;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;
import tv.danmaku.ijk.media.player.IjkMediaMeta;

/* loaded from: classes.dex */
public class DashManifestParser extends DefaultHandler implements ParsingLoadable.Parser<DashManifest> {
    private static final String TAG = "MpdParser";
    private final XmlPullParserFactory xmlParserFactory;
    private static final Pattern FRAME_RATE_PATTERN = Pattern.compile("(\\d+)(?:/(\\d+))?");
    private static final Pattern CEA_608_ACCESSIBILITY_PATTERN = Pattern.compile("CC([1-4])=.*");
    private static final Pattern CEA_708_ACCESSIBILITY_PATTERN = Pattern.compile("([1-9]|[1-5][0-9]|6[0-3])=.*");

    public DashManifestParser() {
        try {
            this.xmlParserFactory = XmlPullParserFactory.newInstance();
        } catch (XmlPullParserException e) {
            throw new RuntimeException("Couldn't create XmlPullParserFactory instance", e);
        }
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.google.android.exoplayer2.upstream.ParsingLoadable.Parser
    public DashManifest parse(Uri uri, InputStream inputStream) throws IOException {
        try {
            XmlPullParser newPullParser = this.xmlParserFactory.newPullParser();
            newPullParser.setInput(inputStream, null);
            if (newPullParser.next() != 2 || !"MPD".equals(newPullParser.getName())) {
                throw new ParserException("inputStream does not contain a valid media presentation description");
            }
            return parseMediaPresentationDescription(newPullParser, uri.toString());
        } catch (XmlPullParserException e) {
            throw new ParserException(e);
        }
    }

    protected DashManifest parseMediaPresentationDescription(XmlPullParser xmlPullParser, String str) throws XmlPullParserException, IOException {
        long j;
        long j2;
        DashManifestParser dashManifestParser = this;
        long parseDateTime = parseDateTime(xmlPullParser, "availabilityStartTime", C.TIME_UNSET);
        long parseDuration = parseDuration(xmlPullParser, "mediaPresentationDuration", C.TIME_UNSET);
        long parseDuration2 = parseDuration(xmlPullParser, "minBufferTime", C.TIME_UNSET);
        boolean equals = "dynamic".equals(xmlPullParser.getAttributeValue(null, IjkMediaMeta.IJKM_KEY_TYPE));
        long parseDuration3 = equals ? parseDuration(xmlPullParser, "minimumUpdatePeriod", C.TIME_UNSET) : -9223372036854775807L;
        long parseDuration4 = equals ? parseDuration(xmlPullParser, "timeShiftBufferDepth", C.TIME_UNSET) : -9223372036854775807L;
        long parseDuration5 = equals ? parseDuration(xmlPullParser, "suggestedPresentationDelay", C.TIME_UNSET) : -9223372036854775807L;
        long parseDateTime2 = parseDateTime(xmlPullParser, "publishTime", C.TIME_UNSET);
        ArrayList arrayList = new ArrayList();
        long j3 = equals ? -9223372036854775807L : 0L;
        boolean z = false;
        boolean z2 = false;
        ProgramInformation programInformation = null;
        UtcTimingElement utcTimingElement = null;
        Uri uri = null;
        String str2 = str;
        while (true) {
            xmlPullParser.next();
            long j4 = parseDuration4;
            if (XmlPullParserUtil.isStartTag(xmlPullParser, "BaseURL")) {
                if (z) {
                    j = j3;
                    j2 = parseDuration3;
                    j3 = j;
                } else {
                    str2 = dashManifestParser.parseBaseUrl(xmlPullParser, str2);
                    j2 = parseDuration3;
                    z = true;
                }
            } else if (XmlPullParserUtil.isStartTag(xmlPullParser, "ProgramInformation")) {
                j2 = parseDuration3;
                programInformation = parseProgramInformation(xmlPullParser);
            } else if (XmlPullParserUtil.isStartTag(xmlPullParser, "UTCTiming")) {
                j2 = parseDuration3;
                utcTimingElement = parseUtcTiming(xmlPullParser);
            } else if (XmlPullParserUtil.isStartTag(xmlPullParser, "Location")) {
                j2 = parseDuration3;
                uri = Uri.parse(xmlPullParser.nextText());
            } else {
                if (XmlPullParserUtil.isStartTag(xmlPullParser, "Period") && !z2) {
                    Pair<Period, Long> parsePeriod = dashManifestParser.parsePeriod(xmlPullParser, str2, j3);
                    j = j3;
                    Period period = (Period) parsePeriod.first;
                    j2 = parseDuration3;
                    if (period.startMs != C.TIME_UNSET) {
                        long longValue = ((Long) parsePeriod.second).longValue();
                        long j5 = longValue == C.TIME_UNSET ? C.TIME_UNSET : period.startMs + longValue;
                        arrayList.add(period);
                        j3 = j5;
                    } else {
                        if (!equals) {
                            throw new ParserException("Unable to determine start of period " + arrayList.size());
                        }
                        z2 = true;
                    }
                } else {
                    j = j3;
                    j2 = parseDuration3;
                    maybeSkipTag(xmlPullParser);
                }
                j3 = j;
            }
            if (XmlPullParserUtil.isEndTag(xmlPullParser, "MPD")) {
                if (parseDuration == C.TIME_UNSET) {
                    if (j3 != C.TIME_UNSET) {
                        parseDuration = j3;
                    } else if (!equals) {
                        throw new ParserException("Unable to determine duration of static manifest.");
                    }
                }
                if (arrayList.isEmpty()) {
                    throw new ParserException("No periods found.");
                }
                return buildMediaPresentationDescription(parseDateTime, parseDuration, parseDuration2, equals, j2, j4, parseDuration5, parseDateTime2, programInformation, utcTimingElement, uri, arrayList);
            }
            dashManifestParser = this;
            parseDuration4 = j4;
            parseDuration3 = j2;
        }
    }

    protected DashManifest buildMediaPresentationDescription(long j, long j2, long j3, boolean z, long j4, long j5, long j6, long j7, ProgramInformation programInformation, UtcTimingElement utcTimingElement, Uri uri, List<Period> list) {
        return new DashManifest(j, j2, j3, z, j4, j5, j6, j7, programInformation, utcTimingElement, uri, list);
    }

    protected UtcTimingElement parseUtcTiming(XmlPullParser xmlPullParser) {
        return buildUtcTimingElement(xmlPullParser.getAttributeValue(null, "schemeIdUri"), xmlPullParser.getAttributeValue(null, "value"));
    }

    protected UtcTimingElement buildUtcTimingElement(String str, String str2) {
        return new UtcTimingElement(str, str2);
    }

    protected Pair<Period, Long> parsePeriod(XmlPullParser xmlPullParser, String str, long j) throws XmlPullParserException, IOException {
        String str2;
        String attributeValue = xmlPullParser.getAttributeValue(null, TtmlNode.ATTR_ID);
        long parseDuration = parseDuration(xmlPullParser, TtmlNode.START, j);
        long parseDuration2 = parseDuration(xmlPullParser, "duration", C.TIME_UNSET);
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        String str3 = str;
        boolean z = false;
        SegmentBase segmentBase = null;
        do {
            xmlPullParser.next();
            if (XmlPullParserUtil.isStartTag(xmlPullParser, "BaseURL")) {
                if (z) {
                    str2 = str3;
                } else {
                    str3 = parseBaseUrl(xmlPullParser, str3);
                    z = true;
                }
            } else if (XmlPullParserUtil.isStartTag(xmlPullParser, "AdaptationSet")) {
                str2 = str3;
                arrayList.add(parseAdaptationSet(xmlPullParser, str3, segmentBase, parseDuration2));
            } else {
                str2 = str3;
                if (XmlPullParserUtil.isStartTag(xmlPullParser, "EventStream")) {
                    arrayList2.add(parseEventStream(xmlPullParser));
                } else if (XmlPullParserUtil.isStartTag(xmlPullParser, "SegmentBase")) {
                    segmentBase = parseSegmentBase(xmlPullParser, null);
                } else if (XmlPullParserUtil.isStartTag(xmlPullParser, "SegmentList")) {
                    segmentBase = parseSegmentList(xmlPullParser, null, parseDuration2);
                } else if (XmlPullParserUtil.isStartTag(xmlPullParser, "SegmentTemplate")) {
                    segmentBase = parseSegmentTemplate(xmlPullParser, null, Collections.emptyList(), parseDuration2);
                } else {
                    maybeSkipTag(xmlPullParser);
                }
            }
            str3 = str2;
        } while (!XmlPullParserUtil.isEndTag(xmlPullParser, "Period"));
        return Pair.create(buildPeriod(attributeValue, parseDuration, arrayList, arrayList2), Long.valueOf(parseDuration2));
    }

    protected Period buildPeriod(String str, long j, List<AdaptationSet> list, List<EventStream> list2) {
        return new Period(str, j, list, list2);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r11v1, types: [java.util.ArrayList] */
    /* JADX WARN: Type inference failed for: r11v3 */
    /* JADX WARN: Type inference failed for: r11v4 */
    protected AdaptationSet parseAdaptationSet(XmlPullParser xmlPullParser, String str, SegmentBase segmentBase, long j) throws XmlPullParserException, IOException {
        String str2;
        String str3;
        ArrayList arrayList;
        ArrayList arrayList2;
        ArrayList arrayList3;
        ArrayList<Descriptor> arrayList4;
        ArrayList<DrmInitData.SchemeData> arrayList5;
        String str4;
        String str5;
        int i;
        ArrayList arrayList6;
        DashManifestParser dashManifestParser;
        XmlPullParser xmlPullParser2;
        ArrayList<Descriptor> arrayList7;
        SegmentBase parseSegmentTemplate;
        DashManifestParser dashManifestParser2 = this;
        XmlPullParser xmlPullParser3 = xmlPullParser;
        int parseInt = parseInt(xmlPullParser3, TtmlNode.ATTR_ID, -1);
        int parseContentType = parseContentType(xmlPullParser);
        String str6 = null;
        String attributeValue = xmlPullParser3.getAttributeValue(null, "mimeType");
        String attributeValue2 = xmlPullParser3.getAttributeValue(null, "codecs");
        int parseInt2 = parseInt(xmlPullParser3, "width", -1);
        int parseInt3 = parseInt(xmlPullParser3, "height", -1);
        float parseFrameRate = parseFrameRate(xmlPullParser3, -1.0f);
        int parseInt4 = parseInt(xmlPullParser3, "audioSamplingRate", -1);
        String str7 = "lang";
        String attributeValue3 = xmlPullParser3.getAttributeValue(null, "lang");
        String attributeValue4 = xmlPullParser3.getAttributeValue(null, "label");
        ArrayList arrayList8 = new ArrayList();
        ArrayList<Descriptor> arrayList9 = new ArrayList<>();
        ArrayList arrayList10 = new ArrayList();
        ArrayList arrayList11 = new ArrayList();
        ArrayList arrayList12 = new ArrayList();
        ArrayList arrayList13 = new ArrayList();
        String str8 = str;
        SegmentBase segmentBase2 = segmentBase;
        int i2 = parseContentType;
        int i3 = -1;
        String str9 = attributeValue4;
        String str10 = null;
        boolean z = false;
        String str11 = attributeValue3;
        ?? r11 = arrayList8;
        while (true) {
            xmlPullParser.next();
            if (XmlPullParserUtil.isStartTag(xmlPullParser3, "BaseURL")) {
                if (!z) {
                    z = true;
                    str8 = dashManifestParser2.parseBaseUrl(xmlPullParser3, str8);
                    arrayList = arrayList12;
                    arrayList2 = arrayList11;
                    arrayList3 = arrayList10;
                    arrayList5 = r11;
                    str4 = str7;
                    str5 = str6;
                    xmlPullParser2 = xmlPullParser3;
                    i = i2;
                    arrayList6 = arrayList13;
                    arrayList7 = arrayList9;
                    dashManifestParser = dashManifestParser2;
                }
                str2 = str11;
                str3 = str8;
                arrayList = arrayList12;
                arrayList2 = arrayList11;
                arrayList3 = arrayList10;
                arrayList5 = r11;
                str4 = str7;
                str5 = str6;
                xmlPullParser2 = xmlPullParser3;
                i = i2;
                arrayList6 = arrayList13;
                arrayList7 = arrayList9;
                dashManifestParser = dashManifestParser2;
                str11 = str2;
                str8 = str3;
            } else {
                if (XmlPullParserUtil.isStartTag(xmlPullParser3, "ContentProtection")) {
                    Pair<String, DrmInitData.SchemeData> parseContentProtection = parseContentProtection(xmlPullParser);
                    if (parseContentProtection.first != null) {
                        str10 = (String) parseContentProtection.first;
                    }
                    if (parseContentProtection.second != null) {
                        r11.add(parseContentProtection.second);
                    }
                } else if (XmlPullParserUtil.isStartTag(xmlPullParser3, "ContentComponent")) {
                    str11 = checkLanguageConsistency(str11, xmlPullParser3.getAttributeValue(str6, str7));
                    arrayList = arrayList12;
                    arrayList2 = arrayList11;
                    arrayList3 = arrayList10;
                    arrayList5 = r11;
                    str4 = str7;
                    str5 = str6;
                    xmlPullParser2 = xmlPullParser3;
                    i = checkContentTypeConsistency(i2, parseContentType(xmlPullParser));
                    arrayList6 = arrayList13;
                    arrayList7 = arrayList9;
                    dashManifestParser = dashManifestParser2;
                } else {
                    if (XmlPullParserUtil.isStartTag(xmlPullParser3, "Role")) {
                        arrayList11.add(parseDescriptor(xmlPullParser3, "Role"));
                    } else if (XmlPullParserUtil.isStartTag(xmlPullParser3, "AudioChannelConfiguration")) {
                        i3 = parseAudioChannelConfiguration(xmlPullParser);
                    } else if (XmlPullParserUtil.isStartTag(xmlPullParser3, "Accessibility")) {
                        arrayList10.add(parseDescriptor(xmlPullParser3, "Accessibility"));
                    } else if (XmlPullParserUtil.isStartTag(xmlPullParser3, "SupplementalProperty")) {
                        arrayList12.add(parseDescriptor(xmlPullParser3, "SupplementalProperty"));
                    } else {
                        if (XmlPullParserUtil.isStartTag(xmlPullParser3, "Representation")) {
                            str2 = str11;
                            str3 = str8;
                            ArrayList arrayList14 = arrayList13;
                            arrayList = arrayList12;
                            arrayList2 = arrayList11;
                            arrayList3 = arrayList10;
                            arrayList4 = arrayList9;
                            arrayList5 = r11;
                            str4 = str7;
                            str5 = str6;
                            RepresentationInfo parseRepresentation = parseRepresentation(xmlPullParser, str8, attributeValue, attributeValue2, parseInt2, parseInt3, parseFrameRate, i3, parseInt4, str2, arrayList2, arrayList3, arrayList, segmentBase2, j);
                            dashManifestParser = this;
                            int checkContentTypeConsistency = checkContentTypeConsistency(i2, dashManifestParser.getContentType(parseRepresentation.format));
                            arrayList6 = arrayList14;
                            arrayList6.add(parseRepresentation);
                            xmlPullParser2 = xmlPullParser;
                            i = checkContentTypeConsistency;
                        } else {
                            str2 = str11;
                            str3 = str8;
                            arrayList = arrayList12;
                            arrayList2 = arrayList11;
                            arrayList3 = arrayList10;
                            arrayList4 = arrayList9;
                            arrayList5 = r11;
                            str4 = str7;
                            str5 = str6;
                            i = i2;
                            arrayList6 = arrayList13;
                            dashManifestParser = dashManifestParser2;
                            xmlPullParser2 = xmlPullParser;
                            if (XmlPullParserUtil.isStartTag(xmlPullParser2, "SegmentBase")) {
                                parseSegmentTemplate = dashManifestParser.parseSegmentBase(xmlPullParser2, (SegmentBase.SingleSegmentBase) segmentBase2);
                            } else if (XmlPullParserUtil.isStartTag(xmlPullParser2, "SegmentList")) {
                                parseSegmentTemplate = dashManifestParser.parseSegmentList(xmlPullParser2, (SegmentBase.SegmentList) segmentBase2, j);
                            } else if (XmlPullParserUtil.isStartTag(xmlPullParser2, "SegmentTemplate")) {
                                parseSegmentTemplate = parseSegmentTemplate(xmlPullParser, (SegmentBase.SegmentTemplate) segmentBase2, arrayList, j);
                            } else {
                                if (XmlPullParserUtil.isStartTag(xmlPullParser2, "InbandEventStream")) {
                                    arrayList7 = arrayList4;
                                    arrayList7.add(parseDescriptor(xmlPullParser2, "InbandEventStream"));
                                } else {
                                    arrayList7 = arrayList4;
                                    if (XmlPullParserUtil.isStartTag(xmlPullParser2, "Label")) {
                                        str9 = parseLabel(xmlPullParser);
                                    } else if (XmlPullParserUtil.isStartTag(xmlPullParser)) {
                                        parseAdaptationSetChild(xmlPullParser);
                                    }
                                }
                                str11 = str2;
                                str8 = str3;
                            }
                            segmentBase2 = parseSegmentTemplate;
                        }
                        str11 = str2;
                        str8 = str3;
                        arrayList7 = arrayList4;
                    }
                    str2 = str11;
                    str3 = str8;
                    arrayList = arrayList12;
                    arrayList2 = arrayList11;
                    arrayList3 = arrayList10;
                    arrayList5 = r11;
                    str4 = str7;
                    str5 = str6;
                    xmlPullParser2 = xmlPullParser3;
                    i = i2;
                    arrayList6 = arrayList13;
                    arrayList7 = arrayList9;
                    dashManifestParser = dashManifestParser2;
                    str11 = str2;
                    str8 = str3;
                }
                arrayList = arrayList12;
                arrayList2 = arrayList11;
                arrayList3 = arrayList10;
                arrayList5 = r11;
                str4 = str7;
                str5 = str6;
                xmlPullParser2 = xmlPullParser3;
                i = i2;
                arrayList6 = arrayList13;
                arrayList7 = arrayList9;
                dashManifestParser = dashManifestParser2;
            }
            if (XmlPullParserUtil.isEndTag(xmlPullParser2, "AdaptationSet")) {
                break;
            }
            dashManifestParser2 = dashManifestParser;
            i2 = i;
            arrayList13 = arrayList6;
            xmlPullParser3 = xmlPullParser2;
            arrayList9 = arrayList7;
            arrayList12 = arrayList;
            arrayList11 = arrayList2;
            arrayList10 = arrayList3;
            r11 = arrayList5;
            str7 = str4;
            str6 = str5;
        }
        ArrayList arrayList15 = new ArrayList(arrayList6.size());
        for (int i4 = 0; i4 < arrayList6.size(); i4++) {
            arrayList15.add(buildRepresentation((RepresentationInfo) arrayList6.get(i4), str9, str10, arrayList5, arrayList7));
        }
        return buildAdaptationSet(parseInt, i, arrayList15, arrayList3, arrayList);
    }

    protected AdaptationSet buildAdaptationSet(int i, int i2, List<Representation> list, List<Descriptor> list2, List<Descriptor> list3) {
        return new AdaptationSet(i, i2, list, list2, list3);
    }

    protected int parseContentType(XmlPullParser xmlPullParser) {
        String attributeValue = xmlPullParser.getAttributeValue(null, "contentType");
        if (TextUtils.isEmpty(attributeValue)) {
            return -1;
        }
        if ("audio".equals(attributeValue)) {
            return 1;
        }
        if ("video".equals(attributeValue)) {
            return 2;
        }
        return MimeTypes.BASE_TYPE_TEXT.equals(attributeValue) ? 3 : -1;
    }

    protected int getContentType(Format format) {
        String str = format.sampleMimeType;
        if (TextUtils.isEmpty(str)) {
            return -1;
        }
        if (MimeTypes.isVideo(str)) {
            return 2;
        }
        if (MimeTypes.isAudio(str)) {
            return 1;
        }
        return mimeTypeIsRawText(str) ? 3 : -1;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:28:0x0094  */
    /* JADX WARN: Removed duplicated region for block: B:33:0x00f7  */
    /* JADX WARN: Removed duplicated region for block: B:38:0x009b  */
    /* JADX WARN: Type inference failed for: r3v0 */
    /* JADX WARN: Type inference failed for: r3v1 */
    /* JADX WARN: Type inference failed for: r3v10, types: [byte[]] */
    /* JADX WARN: Type inference failed for: r3v12 */
    /* JADX WARN: Type inference failed for: r3v15 */
    /* JADX WARN: Type inference failed for: r3v18, types: [byte[]] */
    /* JADX WARN: Type inference failed for: r3v2 */
    /* JADX WARN: Type inference failed for: r3v3 */
    /* JADX WARN: Type inference failed for: r3v5, types: [byte[]] */
    /* JADX WARN: Type inference failed for: r3v7, types: [byte[]] */
    /* JADX WARN: Type inference failed for: r3v9 */
    /* JADX WARN: Type inference failed for: r4v1 */
    /* JADX WARN: Type inference failed for: r4v2, types: [java.lang.Object] */
    /* JADX WARN: Type inference failed for: r4v20 */
    /* JADX WARN: Type inference failed for: r4v21 */
    /* JADX WARN: Type inference failed for: r4v22 */
    /* JADX WARN: Type inference failed for: r4v23 */
    /* JADX WARN: Type inference failed for: r4v24 */
    /* JADX WARN: Type inference failed for: r4v25 */
    /* JADX WARN: Type inference failed for: r4v26 */
    /* JADX WARN: Type inference failed for: r4v27 */
    /* JADX WARN: Type inference failed for: r4v4, types: [java.util.UUID] */
    /* JADX WARN: Type inference failed for: r7v0, types: [java.util.UUID] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    protected android.util.Pair<java.lang.String, com.google.android.exoplayer2.drm.DrmInitData.SchemeData> parseContentProtection(org.xmlpull.v1.XmlPullParser r9) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
        /*
            Method dump skipped, instructions count: 284
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.source.dash.manifest.DashManifestParser.parseContentProtection(org.xmlpull.v1.XmlPullParser):android.util.Pair");
    }

    protected void parseAdaptationSetChild(XmlPullParser xmlPullParser) throws XmlPullParserException, IOException {
        maybeSkipTag(xmlPullParser);
    }

    protected RepresentationInfo parseRepresentation(XmlPullParser xmlPullParser, String str, String str2, String str3, int i, int i2, float f, int i3, int i4, String str4, List<Descriptor> list, List<Descriptor> list2, List<Descriptor> list3, SegmentBase segmentBase, long j) throws XmlPullParserException, IOException {
        String str5;
        int i5;
        String str6;
        String str7;
        SegmentBase parseSegmentTemplate;
        boolean z;
        String attributeValue = xmlPullParser.getAttributeValue(null, TtmlNode.ATTR_ID);
        int parseInt = parseInt(xmlPullParser, "bandwidth", -1);
        String parseString = parseString(xmlPullParser, "mimeType", str2);
        String parseString2 = parseString(xmlPullParser, "codecs", str3);
        int parseInt2 = parseInt(xmlPullParser, "width", i);
        int parseInt3 = parseInt(xmlPullParser, "height", i2);
        float parseFrameRate = parseFrameRate(xmlPullParser, f);
        int parseInt4 = parseInt(xmlPullParser, "audioSamplingRate", i4);
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        ArrayList arrayList3 = new ArrayList();
        SegmentBase segmentBase2 = segmentBase;
        String str8 = null;
        boolean z2 = false;
        String str9 = str;
        int i6 = i3;
        while (true) {
            xmlPullParser.next();
            int i7 = i6;
            if (!XmlPullParserUtil.isStartTag(xmlPullParser, "BaseURL")) {
                if (XmlPullParserUtil.isStartTag(xmlPullParser, "AudioChannelConfiguration")) {
                    i6 = parseAudioChannelConfiguration(xmlPullParser);
                    i5 = parseInt;
                } else if (XmlPullParserUtil.isStartTag(xmlPullParser, "SegmentBase")) {
                    i5 = parseInt;
                    str6 = str8;
                    str7 = str9;
                    z = z2;
                    parseSegmentTemplate = parseSegmentBase(xmlPullParser, (SegmentBase.SingleSegmentBase) segmentBase2);
                    i6 = i7;
                } else if (XmlPullParserUtil.isStartTag(xmlPullParser, "SegmentList")) {
                    i5 = parseInt;
                    segmentBase2 = parseSegmentList(xmlPullParser, (SegmentBase.SegmentList) segmentBase2, j);
                    i6 = i7;
                } else {
                    str5 = str9;
                    i5 = parseInt;
                    if (XmlPullParserUtil.isStartTag(xmlPullParser, "SegmentTemplate")) {
                        i6 = i7;
                        str6 = str8;
                        str7 = str5;
                        boolean z3 = z2;
                        parseSegmentTemplate = parseSegmentTemplate(xmlPullParser, (SegmentBase.SegmentTemplate) segmentBase2, list3, j);
                        z = z3;
                    } else {
                        if (XmlPullParserUtil.isStartTag(xmlPullParser, "ContentProtection")) {
                            Pair<String, DrmInitData.SchemeData> parseContentProtection = parseContentProtection(xmlPullParser);
                            if (parseContentProtection.first != null) {
                                str8 = (String) parseContentProtection.first;
                            }
                            if (parseContentProtection.second != null) {
                                arrayList.add(parseContentProtection.second);
                            }
                        } else if (XmlPullParserUtil.isStartTag(xmlPullParser, "InbandEventStream")) {
                            arrayList2.add(parseDescriptor(xmlPullParser, "InbandEventStream"));
                        } else if (XmlPullParserUtil.isStartTag(xmlPullParser, "SupplementalProperty")) {
                            arrayList3.add(parseDescriptor(xmlPullParser, "SupplementalProperty"));
                        } else {
                            maybeSkipTag(xmlPullParser);
                        }
                        i6 = i7;
                        str6 = str8;
                        z = z2;
                        str7 = str5;
                        parseSegmentTemplate = segmentBase2;
                    }
                }
                str6 = str8;
                str7 = str9;
                z = z2;
                parseSegmentTemplate = segmentBase2;
            } else if (z2) {
                str5 = str9;
                i5 = parseInt;
                i6 = i7;
                str6 = str8;
                z = z2;
                str7 = str5;
                parseSegmentTemplate = segmentBase2;
            } else {
                i5 = parseInt;
                parseSegmentTemplate = segmentBase2;
                str6 = str8;
                str7 = parseBaseUrl(xmlPullParser, str9);
                z = true;
                i6 = i7;
            }
            if (XmlPullParserUtil.isEndTag(xmlPullParser, "Representation")) {
                break;
            }
            segmentBase2 = parseSegmentTemplate;
            parseInt = i5;
            z2 = z;
            str9 = str7;
            str8 = str6;
        }
        return new RepresentationInfo(buildFormat(attributeValue, parseString, parseInt2, parseInt3, parseFrameRate, i6, parseInt4, i5, str4, list, list2, parseString2, arrayList3), str7, parseSegmentTemplate != null ? parseSegmentTemplate : new SegmentBase.SingleSegmentBase(), str6, arrayList, arrayList2, -1L);
    }

    protected Format buildFormat(String str, String str2, int i, int i2, float f, int i3, int i4, int i5, String str3, List<Descriptor> list, List<Descriptor> list2, String str4, List<Descriptor> list3) {
        String str5;
        int parseCea708AccessibilityChannel;
        String sampleMimeType = getSampleMimeType(str2, str4);
        int parseSelectionFlagsFromRoleDescriptors = parseSelectionFlagsFromRoleDescriptors(list);
        int parseRoleFlagsFromRoleDescriptors = parseRoleFlagsFromRoleDescriptors(list) | parseRoleFlagsFromAccessibilityDescriptors(list2);
        if (sampleMimeType != null) {
            String parseEac3SupplementalProperties = MimeTypes.AUDIO_E_AC3.equals(sampleMimeType) ? parseEac3SupplementalProperties(list3) : sampleMimeType;
            if (MimeTypes.isVideo(parseEac3SupplementalProperties)) {
                return Format.createVideoContainerFormat(str, null, str2, parseEac3SupplementalProperties, str4, null, i5, i, i2, f, null, parseSelectionFlagsFromRoleDescriptors, parseRoleFlagsFromRoleDescriptors);
            }
            if (MimeTypes.isAudio(parseEac3SupplementalProperties)) {
                return Format.createAudioContainerFormat(str, null, str2, parseEac3SupplementalProperties, str4, null, i5, i3, i4, null, parseSelectionFlagsFromRoleDescriptors, parseRoleFlagsFromRoleDescriptors, str3);
            }
            if (mimeTypeIsRawText(parseEac3SupplementalProperties)) {
                if (MimeTypes.APPLICATION_CEA608.equals(parseEac3SupplementalProperties)) {
                    parseCea708AccessibilityChannel = parseCea608AccessibilityChannel(list2);
                } else {
                    parseCea708AccessibilityChannel = MimeTypes.APPLICATION_CEA708.equals(parseEac3SupplementalProperties) ? parseCea708AccessibilityChannel(list2) : -1;
                }
                return Format.createTextContainerFormat(str, null, str2, parseEac3SupplementalProperties, str4, i5, parseSelectionFlagsFromRoleDescriptors, parseRoleFlagsFromRoleDescriptors, str3, parseCea708AccessibilityChannel);
            }
            str5 = parseEac3SupplementalProperties;
        } else {
            str5 = sampleMimeType;
        }
        return Format.createContainerFormat(str, null, str2, str5, str4, i5, parseSelectionFlagsFromRoleDescriptors, parseRoleFlagsFromRoleDescriptors, str3);
    }

    protected Representation buildRepresentation(RepresentationInfo representationInfo, String str, String str2, ArrayList<DrmInitData.SchemeData> arrayList, ArrayList<Descriptor> arrayList2) {
        Format format = representationInfo.format;
        if (str != null) {
            format = format.copyWithLabel(str);
        }
        if (representationInfo.drmSchemeType != null) {
            str2 = representationInfo.drmSchemeType;
        }
        ArrayList<DrmInitData.SchemeData> arrayList3 = representationInfo.drmSchemeDatas;
        arrayList3.addAll(arrayList);
        if (!arrayList3.isEmpty()) {
            filterRedundantIncompleteSchemeDatas(arrayList3);
            format = format.copyWithDrmInitData(new DrmInitData(str2, arrayList3));
        }
        ArrayList<Descriptor> arrayList4 = representationInfo.inbandEventStreams;
        arrayList4.addAll(arrayList2);
        return Representation.newInstance(representationInfo.revisionId, format, representationInfo.baseUrl, representationInfo.segmentBase, arrayList4);
    }

    protected SegmentBase.SingleSegmentBase parseSegmentBase(XmlPullParser xmlPullParser, SegmentBase.SingleSegmentBase singleSegmentBase) throws XmlPullParserException, IOException {
        long j;
        long j2;
        long parseLong = parseLong(xmlPullParser, "timescale", singleSegmentBase != null ? singleSegmentBase.timescale : 1L);
        long parseLong2 = parseLong(xmlPullParser, "presentationTimeOffset", singleSegmentBase != null ? singleSegmentBase.presentationTimeOffset : 0L);
        long j3 = singleSegmentBase != null ? singleSegmentBase.indexStart : 0L;
        long j4 = singleSegmentBase != null ? singleSegmentBase.indexLength : 0L;
        String attributeValue = xmlPullParser.getAttributeValue(null, "indexRange");
        if (attributeValue != null) {
            String[] split = attributeValue.split("-");
            j2 = Long.parseLong(split[0]);
            j = (Long.parseLong(split[1]) - j2) + 1;
        } else {
            j = j4;
            j2 = j3;
        }
        RangedUri rangedUri = singleSegmentBase != null ? singleSegmentBase.initialization : null;
        do {
            xmlPullParser.next();
            if (XmlPullParserUtil.isStartTag(xmlPullParser, "Initialization")) {
                rangedUri = parseInitialization(xmlPullParser);
            } else {
                maybeSkipTag(xmlPullParser);
            }
        } while (!XmlPullParserUtil.isEndTag(xmlPullParser, "SegmentBase"));
        return buildSingleSegmentBase(rangedUri, parseLong, parseLong2, j2, j);
    }

    protected SegmentBase.SingleSegmentBase buildSingleSegmentBase(RangedUri rangedUri, long j, long j2, long j3, long j4) {
        return new SegmentBase.SingleSegmentBase(rangedUri, j, j2, j3, j4);
    }

    protected SegmentBase.SegmentList parseSegmentList(XmlPullParser xmlPullParser, SegmentBase.SegmentList segmentList, long j) throws XmlPullParserException, IOException {
        long parseLong = parseLong(xmlPullParser, "timescale", segmentList != null ? segmentList.timescale : 1L);
        long parseLong2 = parseLong(xmlPullParser, "presentationTimeOffset", segmentList != null ? segmentList.presentationTimeOffset : 0L);
        long parseLong3 = parseLong(xmlPullParser, "duration", segmentList != null ? segmentList.duration : C.TIME_UNSET);
        long parseLong4 = parseLong(xmlPullParser, "startNumber", segmentList != null ? segmentList.startNumber : 1L);
        List<SegmentBase.SegmentTimelineElement> list = null;
        List<RangedUri> list2 = null;
        RangedUri rangedUri = null;
        do {
            xmlPullParser.next();
            if (XmlPullParserUtil.isStartTag(xmlPullParser, "Initialization")) {
                rangedUri = parseInitialization(xmlPullParser);
            } else if (XmlPullParserUtil.isStartTag(xmlPullParser, "SegmentTimeline")) {
                list = parseSegmentTimeline(xmlPullParser, parseLong, j);
            } else if (XmlPullParserUtil.isStartTag(xmlPullParser, "SegmentURL")) {
                if (list2 == null) {
                    list2 = new ArrayList<>();
                }
                list2.add(parseSegmentUrl(xmlPullParser));
            } else {
                maybeSkipTag(xmlPullParser);
            }
        } while (!XmlPullParserUtil.isEndTag(xmlPullParser, "SegmentList"));
        if (segmentList != null) {
            if (rangedUri == null) {
                rangedUri = segmentList.initialization;
            }
            if (list == null) {
                list = segmentList.segmentTimeline;
            }
            if (list2 == null) {
                list2 = segmentList.mediaSegments;
            }
        }
        return buildSegmentList(rangedUri, parseLong, parseLong2, parseLong4, parseLong3, list, list2);
    }

    protected SegmentBase.SegmentList buildSegmentList(RangedUri rangedUri, long j, long j2, long j3, long j4, List<SegmentBase.SegmentTimelineElement> list, List<RangedUri> list2) {
        return new SegmentBase.SegmentList(rangedUri, j, j2, j3, j4, list, list2);
    }

    protected SegmentBase.SegmentTemplate parseSegmentTemplate(XmlPullParser xmlPullParser, SegmentBase.SegmentTemplate segmentTemplate, List<Descriptor> list, long j) throws XmlPullParserException, IOException {
        long parseLong = parseLong(xmlPullParser, "timescale", segmentTemplate != null ? segmentTemplate.timescale : 1L);
        long parseLong2 = parseLong(xmlPullParser, "presentationTimeOffset", segmentTemplate != null ? segmentTemplate.presentationTimeOffset : 0L);
        long parseLong3 = parseLong(xmlPullParser, "duration", segmentTemplate != null ? segmentTemplate.duration : C.TIME_UNSET);
        long parseLong4 = parseLong(xmlPullParser, "startNumber", segmentTemplate != null ? segmentTemplate.startNumber : 1L);
        long parseLastSegmentNumberSupplementalProperty = parseLastSegmentNumberSupplementalProperty(list);
        List<SegmentBase.SegmentTimelineElement> list2 = null;
        UrlTemplate parseUrlTemplate = parseUrlTemplate(xmlPullParser, "media", segmentTemplate != null ? segmentTemplate.mediaTemplate : null);
        UrlTemplate parseUrlTemplate2 = parseUrlTemplate(xmlPullParser, "initialization", segmentTemplate != null ? segmentTemplate.initializationTemplate : null);
        RangedUri rangedUri = null;
        do {
            xmlPullParser.next();
            if (XmlPullParserUtil.isStartTag(xmlPullParser, "Initialization")) {
                rangedUri = parseInitialization(xmlPullParser);
            } else if (XmlPullParserUtil.isStartTag(xmlPullParser, "SegmentTimeline")) {
                list2 = parseSegmentTimeline(xmlPullParser, parseLong, j);
            } else {
                maybeSkipTag(xmlPullParser);
            }
        } while (!XmlPullParserUtil.isEndTag(xmlPullParser, "SegmentTemplate"));
        if (segmentTemplate != null) {
            if (rangedUri == null) {
                rangedUri = segmentTemplate.initialization;
            }
            if (list2 == null) {
                list2 = segmentTemplate.segmentTimeline;
            }
        }
        return buildSegmentTemplate(rangedUri, parseLong, parseLong2, parseLong4, parseLastSegmentNumberSupplementalProperty, parseLong3, list2, parseUrlTemplate2, parseUrlTemplate);
    }

    protected SegmentBase.SegmentTemplate buildSegmentTemplate(RangedUri rangedUri, long j, long j2, long j3, long j4, long j5, List<SegmentBase.SegmentTimelineElement> list, UrlTemplate urlTemplate, UrlTemplate urlTemplate2) {
        return new SegmentBase.SegmentTemplate(rangedUri, j, j2, j3, j4, j5, list, urlTemplate, urlTemplate2);
    }

    protected EventStream parseEventStream(XmlPullParser xmlPullParser) throws XmlPullParserException, IOException {
        String parseString = parseString(xmlPullParser, "schemeIdUri", "");
        String parseString2 = parseString(xmlPullParser, "value", "");
        long parseLong = parseLong(xmlPullParser, "timescale", 1L);
        ArrayList arrayList = new ArrayList();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(512);
        do {
            xmlPullParser.next();
            if (XmlPullParserUtil.isStartTag(xmlPullParser, "Event")) {
                arrayList.add(parseEvent(xmlPullParser, parseString, parseString2, parseLong, byteArrayOutputStream));
            } else {
                maybeSkipTag(xmlPullParser);
            }
        } while (!XmlPullParserUtil.isEndTag(xmlPullParser, "EventStream"));
        long[] jArr = new long[arrayList.size()];
        EventMessage[] eventMessageArr = new EventMessage[arrayList.size()];
        for (int i = 0; i < arrayList.size(); i++) {
            Pair pair = (Pair) arrayList.get(i);
            jArr[i] = ((Long) pair.first).longValue();
            eventMessageArr[i] = (EventMessage) pair.second;
        }
        return buildEventStream(parseString, parseString2, parseLong, jArr, eventMessageArr);
    }

    protected EventStream buildEventStream(String str, String str2, long j, long[] jArr, EventMessage[] eventMessageArr) {
        return new EventStream(str, str2, j, jArr, eventMessageArr);
    }

    protected Pair<Long, EventMessage> parseEvent(XmlPullParser xmlPullParser, String str, String str2, long j, ByteArrayOutputStream byteArrayOutputStream) throws IOException, XmlPullParserException {
        long parseLong = parseLong(xmlPullParser, TtmlNode.ATTR_ID, 0L);
        long parseLong2 = parseLong(xmlPullParser, "duration", C.TIME_UNSET);
        long parseLong3 = parseLong(xmlPullParser, "presentationTime", 0L);
        long scaleLargeTimestamp = Util.scaleLargeTimestamp(parseLong2, 1000L, j);
        long scaleLargeTimestamp2 = Util.scaleLargeTimestamp(parseLong3, 1000000L, j);
        String parseString = parseString(xmlPullParser, "messageData", null);
        byte[] parseEventObject = parseEventObject(xmlPullParser, byteArrayOutputStream);
        Long valueOf = Long.valueOf(scaleLargeTimestamp2);
        if (parseString != null) {
            parseEventObject = Util.getUtf8Bytes(parseString);
        }
        return Pair.create(valueOf, buildEvent(str, str2, parseLong, scaleLargeTimestamp, parseEventObject));
    }

    protected byte[] parseEventObject(XmlPullParser xmlPullParser, ByteArrayOutputStream byteArrayOutputStream) throws XmlPullParserException, IOException {
        byteArrayOutputStream.reset();
        XmlSerializer newSerializer = Xml.newSerializer();
        newSerializer.setOutput(byteArrayOutputStream, "UTF-8");
        xmlPullParser.nextToken();
        while (!XmlPullParserUtil.isEndTag(xmlPullParser, "Event")) {
            switch (xmlPullParser.getEventType()) {
                case 0:
                    newSerializer.startDocument(null, false);
                    break;
                case 1:
                    newSerializer.endDocument();
                    break;
                case 2:
                    newSerializer.startTag(xmlPullParser.getNamespace(), xmlPullParser.getName());
                    for (int i = 0; i < xmlPullParser.getAttributeCount(); i++) {
                        newSerializer.attribute(xmlPullParser.getAttributeNamespace(i), xmlPullParser.getAttributeName(i), xmlPullParser.getAttributeValue(i));
                    }
                    break;
                case 3:
                    newSerializer.endTag(xmlPullParser.getNamespace(), xmlPullParser.getName());
                    break;
                case 4:
                    newSerializer.text(xmlPullParser.getText());
                    break;
                case 5:
                    newSerializer.cdsect(xmlPullParser.getText());
                    break;
                case 6:
                    newSerializer.entityRef(xmlPullParser.getText());
                    break;
                case 7:
                    newSerializer.ignorableWhitespace(xmlPullParser.getText());
                    break;
                case 8:
                    newSerializer.processingInstruction(xmlPullParser.getText());
                    break;
                case 9:
                    newSerializer.comment(xmlPullParser.getText());
                    break;
                case 10:
                    newSerializer.docdecl(xmlPullParser.getText());
                    break;
            }
            xmlPullParser.nextToken();
        }
        newSerializer.flush();
        return byteArrayOutputStream.toByteArray();
    }

    protected EventMessage buildEvent(String str, String str2, long j, long j2, byte[] bArr) {
        return new EventMessage(str, str2, j2, j, bArr);
    }

    protected List<SegmentBase.SegmentTimelineElement> parseSegmentTimeline(XmlPullParser xmlPullParser, long j, long j2) throws XmlPullParserException, IOException {
        ArrayList arrayList = new ArrayList();
        long j3 = 0;
        long j4 = -9223372036854775807L;
        boolean z = false;
        int i = 0;
        do {
            xmlPullParser.next();
            if (XmlPullParserUtil.isStartTag(xmlPullParser, ExifInterface.LATITUDE_SOUTH)) {
                long parseLong = parseLong(xmlPullParser, "t", C.TIME_UNSET);
                if (z) {
                    j3 = addSegmentTimelineElementsToList(arrayList, j3, j4, i, parseLong);
                }
                if (parseLong == C.TIME_UNSET) {
                    parseLong = j3;
                }
                j4 = parseLong(xmlPullParser, "d", C.TIME_UNSET);
                i = parseInt(xmlPullParser, "r", 0);
                z = true;
                j3 = parseLong;
            } else {
                maybeSkipTag(xmlPullParser);
            }
        } while (!XmlPullParserUtil.isEndTag(xmlPullParser, "SegmentTimeline"));
        if (z) {
            addSegmentTimelineElementsToList(arrayList, j3, j4, i, Util.scaleLargeTimestamp(j2, j, 1000L));
        }
        return arrayList;
    }

    private long addSegmentTimelineElementsToList(List<SegmentBase.SegmentTimelineElement> list, long j, long j2, int i, long j3) {
        int ceilDivide = i >= 0 ? i + 1 : (int) Util.ceilDivide(j3 - j, j2);
        for (int i2 = 0; i2 < ceilDivide; i2++) {
            list.add(buildSegmentTimelineElement(j, j2));
            j += j2;
        }
        return j;
    }

    protected SegmentBase.SegmentTimelineElement buildSegmentTimelineElement(long j, long j2) {
        return new SegmentBase.SegmentTimelineElement(j, j2);
    }

    protected UrlTemplate parseUrlTemplate(XmlPullParser xmlPullParser, String str, UrlTemplate urlTemplate) {
        String attributeValue = xmlPullParser.getAttributeValue(null, str);
        return attributeValue != null ? UrlTemplate.compile(attributeValue) : urlTemplate;
    }

    protected RangedUri parseInitialization(XmlPullParser xmlPullParser) {
        return parseRangedUrl(xmlPullParser, "sourceURL", "range");
    }

    protected RangedUri parseSegmentUrl(XmlPullParser xmlPullParser) {
        return parseRangedUrl(xmlPullParser, "media", "mediaRange");
    }

    protected RangedUri parseRangedUrl(XmlPullParser xmlPullParser, String str, String str2) {
        long j;
        long j2;
        String attributeValue = xmlPullParser.getAttributeValue(null, str);
        String attributeValue2 = xmlPullParser.getAttributeValue(null, str2);
        if (attributeValue2 != null) {
            String[] split = attributeValue2.split("-");
            j = Long.parseLong(split[0]);
            if (split.length == 2) {
                j2 = (Long.parseLong(split[1]) - j) + 1;
                return buildRangedUri(attributeValue, j, j2);
            }
        } else {
            j = 0;
        }
        j2 = -1;
        return buildRangedUri(attributeValue, j, j2);
    }

    protected RangedUri buildRangedUri(String str, long j, long j2) {
        return new RangedUri(str, j, j2);
    }

    protected ProgramInformation parseProgramInformation(XmlPullParser xmlPullParser) throws IOException, XmlPullParserException {
        String str = null;
        String parseString = parseString(xmlPullParser, "moreInformationURL", null);
        String parseString2 = parseString(xmlPullParser, "lang", null);
        String str2 = null;
        String str3 = null;
        while (true) {
            xmlPullParser.next();
            if (XmlPullParserUtil.isStartTag(xmlPullParser, "Title")) {
                str = xmlPullParser.nextText();
            } else if (XmlPullParserUtil.isStartTag(xmlPullParser, "Source")) {
                str2 = xmlPullParser.nextText();
            } else if (XmlPullParserUtil.isStartTag(xmlPullParser, ExifInterface.TAG_COPYRIGHT)) {
                str3 = xmlPullParser.nextText();
            } else {
                maybeSkipTag(xmlPullParser);
            }
            String str4 = str3;
            if (XmlPullParserUtil.isEndTag(xmlPullParser, "ProgramInformation")) {
                return new ProgramInformation(str, str2, str4, parseString, parseString2);
            }
            str3 = str4;
        }
    }

    protected String parseLabel(XmlPullParser xmlPullParser) throws XmlPullParserException, IOException {
        return parseText(xmlPullParser, "Label");
    }

    protected String parseBaseUrl(XmlPullParser xmlPullParser, String str) throws XmlPullParserException, IOException {
        return UriUtil.resolve(str, parseText(xmlPullParser, "BaseURL"));
    }

    protected int parseAudioChannelConfiguration(XmlPullParser xmlPullParser) throws XmlPullParserException, IOException {
        String parseString = parseString(xmlPullParser, "schemeIdUri", null);
        int i = -1;
        if ("urn:mpeg:dash:23003:3:audio_channel_configuration:2011".equals(parseString)) {
            i = parseInt(xmlPullParser, "value", -1);
        } else if ("tag:dolby.com,2014:dash:audio_channel_configuration:2011".equals(parseString) || "urn:dolby:dash:audio_channel_configuration:2011".equals(parseString)) {
            i = parseDolbyChannelConfiguration(xmlPullParser);
        }
        do {
            xmlPullParser.next();
        } while (!XmlPullParserUtil.isEndTag(xmlPullParser, "AudioChannelConfiguration"));
        return i;
    }

    protected int parseSelectionFlagsFromRoleDescriptors(List<Descriptor> list) {
        for (int i = 0; i < list.size(); i++) {
            Descriptor descriptor = list.get(i);
            if ("urn:mpeg:dash:role:2011".equalsIgnoreCase(descriptor.schemeIdUri) && "main".equals(descriptor.value)) {
                return 1;
            }
        }
        return 0;
    }

    protected int parseRoleFlagsFromRoleDescriptors(List<Descriptor> list) {
        int i = 0;
        for (int i2 = 0; i2 < list.size(); i2++) {
            Descriptor descriptor = list.get(i2);
            if ("urn:mpeg:dash:role:2011".equalsIgnoreCase(descriptor.schemeIdUri)) {
                i |= parseDashRoleSchemeValue(descriptor.value);
            }
        }
        return i;
    }

    protected int parseRoleFlagsFromAccessibilityDescriptors(List<Descriptor> list) {
        int parseTvaAudioPurposeCsValue;
        int i = 0;
        for (int i2 = 0; i2 < list.size(); i2++) {
            Descriptor descriptor = list.get(i2);
            if ("urn:mpeg:dash:role:2011".equalsIgnoreCase(descriptor.schemeIdUri)) {
                parseTvaAudioPurposeCsValue = parseDashRoleSchemeValue(descriptor.value);
            } else if ("urn:tva:metadata:cs:AudioPurposeCS:2007".equalsIgnoreCase(descriptor.schemeIdUri)) {
                parseTvaAudioPurposeCsValue = parseTvaAudioPurposeCsValue(descriptor.value);
            }
            i |= parseTvaAudioPurposeCsValue;
        }
        return i;
    }

    protected int parseDashRoleSchemeValue(String str) {
        if (str == null) {
            return 0;
        }
        str.hashCode();
        switch (str) {
        }
        return 0;
    }

    protected int parseTvaAudioPurposeCsValue(String str) {
        if (str == null) {
            return 0;
        }
        str.hashCode();
        switch (str) {
        }
        return 0;
    }

    public static void maybeSkipTag(XmlPullParser xmlPullParser) throws IOException, XmlPullParserException {
        if (XmlPullParserUtil.isStartTag(xmlPullParser)) {
            int i = 1;
            while (i != 0) {
                xmlPullParser.next();
                if (XmlPullParserUtil.isStartTag(xmlPullParser)) {
                    i++;
                } else if (XmlPullParserUtil.isEndTag(xmlPullParser)) {
                    i--;
                }
            }
        }
    }

    private static void filterRedundantIncompleteSchemeDatas(ArrayList<DrmInitData.SchemeData> arrayList) {
        for (int size = arrayList.size() - 1; size >= 0; size--) {
            DrmInitData.SchemeData schemeData = arrayList.get(size);
            if (!schemeData.hasData()) {
                int i = 0;
                while (true) {
                    if (i >= arrayList.size()) {
                        break;
                    }
                    if (arrayList.get(i).canReplace(schemeData)) {
                        arrayList.remove(size);
                        break;
                    }
                    i++;
                }
            }
        }
    }

    private static String getSampleMimeType(String str, String str2) {
        if (MimeTypes.isAudio(str)) {
            return MimeTypes.getAudioMediaMimeType(str2);
        }
        if (MimeTypes.isVideo(str)) {
            return MimeTypes.getVideoMediaMimeType(str2);
        }
        if (mimeTypeIsRawText(str)) {
            return str;
        }
        if (MimeTypes.APPLICATION_MP4.equals(str)) {
            if (str2 != null) {
                if (str2.startsWith("stpp")) {
                    return MimeTypes.APPLICATION_TTML;
                }
                if (str2.startsWith("wvtt")) {
                    return MimeTypes.APPLICATION_MP4VTT;
                }
            }
        } else if (MimeTypes.APPLICATION_RAWCC.equals(str) && str2 != null) {
            if (str2.contains("cea708")) {
                return MimeTypes.APPLICATION_CEA708;
            }
            if (str2.contains("eia608") || str2.contains("cea608")) {
                return MimeTypes.APPLICATION_CEA608;
            }
        }
        return null;
    }

    private static boolean mimeTypeIsRawText(String str) {
        return MimeTypes.isText(str) || MimeTypes.APPLICATION_TTML.equals(str) || MimeTypes.APPLICATION_MP4VTT.equals(str) || MimeTypes.APPLICATION_CEA708.equals(str) || MimeTypes.APPLICATION_CEA608.equals(str);
    }

    private static String checkLanguageConsistency(String str, String str2) {
        if (str == null) {
            return str2;
        }
        if (str2 == null) {
            return str;
        }
        Assertions.checkState(str.equals(str2));
        return str;
    }

    private static int checkContentTypeConsistency(int i, int i2) {
        if (i == -1) {
            return i2;
        }
        if (i2 == -1) {
            return i;
        }
        Assertions.checkState(i == i2);
        return i;
    }

    protected static Descriptor parseDescriptor(XmlPullParser xmlPullParser, String str) throws XmlPullParserException, IOException {
        String parseString = parseString(xmlPullParser, "schemeIdUri", "");
        String parseString2 = parseString(xmlPullParser, "value", null);
        String parseString3 = parseString(xmlPullParser, TtmlNode.ATTR_ID, null);
        do {
            xmlPullParser.next();
        } while (!XmlPullParserUtil.isEndTag(xmlPullParser, str));
        return new Descriptor(parseString, parseString2, parseString3);
    }

    protected static int parseCea608AccessibilityChannel(List<Descriptor> list) {
        for (int i = 0; i < list.size(); i++) {
            Descriptor descriptor = list.get(i);
            if ("urn:scte:dash:cc:cea-608:2015".equals(descriptor.schemeIdUri) && descriptor.value != null) {
                Matcher matcher = CEA_608_ACCESSIBILITY_PATTERN.matcher(descriptor.value);
                if (matcher.matches()) {
                    return Integer.parseInt(matcher.group(1));
                }
                Log.w(TAG, "Unable to parse CEA-608 channel number from: " + descriptor.value);
            }
        }
        return -1;
    }

    protected static int parseCea708AccessibilityChannel(List<Descriptor> list) {
        for (int i = 0; i < list.size(); i++) {
            Descriptor descriptor = list.get(i);
            if ("urn:scte:dash:cc:cea-708:2015".equals(descriptor.schemeIdUri) && descriptor.value != null) {
                Matcher matcher = CEA_708_ACCESSIBILITY_PATTERN.matcher(descriptor.value);
                if (matcher.matches()) {
                    return Integer.parseInt(matcher.group(1));
                }
                Log.w(TAG, "Unable to parse CEA-708 service block number from: " + descriptor.value);
            }
        }
        return -1;
    }

    protected static String parseEac3SupplementalProperties(List<Descriptor> list) {
        for (int i = 0; i < list.size(); i++) {
            Descriptor descriptor = list.get(i);
            String str = descriptor.schemeIdUri;
            if ("tag:dolby.com,2018:dash:EC3_ExtensionType:2018".equals(str) && "JOC".equals(descriptor.value)) {
                return MimeTypes.AUDIO_E_AC3_JOC;
            }
            if ("tag:dolby.com,2014:dash:DolbyDigitalPlusExtensionType:2014".equals(str) && "ec+3".equals(descriptor.value)) {
                return MimeTypes.AUDIO_E_AC3_JOC;
            }
        }
        return MimeTypes.AUDIO_E_AC3;
    }

    protected static float parseFrameRate(XmlPullParser xmlPullParser, float f) {
        String attributeValue = xmlPullParser.getAttributeValue(null, "frameRate");
        if (attributeValue == null) {
            return f;
        }
        Matcher matcher = FRAME_RATE_PATTERN.matcher(attributeValue);
        if (!matcher.matches()) {
            return f;
        }
        int parseInt = Integer.parseInt(matcher.group(1));
        return !TextUtils.isEmpty(matcher.group(2)) ? parseInt / Integer.parseInt(r2) : parseInt;
    }

    protected static long parseDuration(XmlPullParser xmlPullParser, String str, long j) {
        String attributeValue = xmlPullParser.getAttributeValue(null, str);
        return attributeValue == null ? j : Util.parseXsDuration(attributeValue);
    }

    protected static long parseDateTime(XmlPullParser xmlPullParser, String str, long j) throws ParserException {
        String attributeValue = xmlPullParser.getAttributeValue(null, str);
        return attributeValue == null ? j : Util.parseXsDateTime(attributeValue);
    }

    protected static String parseText(XmlPullParser xmlPullParser, String str) throws XmlPullParserException, IOException {
        String str2 = "";
        do {
            xmlPullParser.next();
            if (xmlPullParser.getEventType() == 4) {
                str2 = xmlPullParser.getText();
            } else {
                maybeSkipTag(xmlPullParser);
            }
        } while (!XmlPullParserUtil.isEndTag(xmlPullParser, str));
        return str2;
    }

    protected static int parseInt(XmlPullParser xmlPullParser, String str, int i) {
        String attributeValue = xmlPullParser.getAttributeValue(null, str);
        return attributeValue == null ? i : Integer.parseInt(attributeValue);
    }

    protected static long parseLong(XmlPullParser xmlPullParser, String str, long j) {
        String attributeValue = xmlPullParser.getAttributeValue(null, str);
        return attributeValue == null ? j : Long.parseLong(attributeValue);
    }

    protected static String parseString(XmlPullParser xmlPullParser, String str, String str2) {
        String attributeValue = xmlPullParser.getAttributeValue(null, str);
        return attributeValue == null ? str2 : attributeValue;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    protected static int parseDolbyChannelConfiguration(XmlPullParser xmlPullParser) {
        char c;
        String lowerInvariant = Util.toLowerInvariant(xmlPullParser.getAttributeValue(null, "value"));
        if (lowerInvariant == null) {
            return -1;
        }
        lowerInvariant.hashCode();
        switch (lowerInvariant.hashCode()) {
            case 1596796:
                if (lowerInvariant.equals("4000")) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case 2937391:
                if (lowerInvariant.equals("a000")) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case 3094035:
                if (lowerInvariant.equals("f801")) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case 3133436:
                if (lowerInvariant.equals("fa01")) {
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
        }
        return -1;
    }

    protected static long parseLastSegmentNumberSupplementalProperty(List<Descriptor> list) {
        for (int i = 0; i < list.size(); i++) {
            Descriptor descriptor = list.get(i);
            if ("http://dashif.org/guidelines/last-segment-number".equalsIgnoreCase(descriptor.schemeIdUri)) {
                return Long.parseLong(descriptor.value);
            }
        }
        return -1L;
    }

    protected static final class RepresentationInfo {
        public final String baseUrl;
        public final ArrayList<DrmInitData.SchemeData> drmSchemeDatas;
        public final String drmSchemeType;
        public final Format format;
        public final ArrayList<Descriptor> inbandEventStreams;
        public final long revisionId;
        public final SegmentBase segmentBase;

        public RepresentationInfo(Format format, String str, SegmentBase segmentBase, String str2, ArrayList<DrmInitData.SchemeData> arrayList, ArrayList<Descriptor> arrayList2, long j) {
            this.format = format;
            this.baseUrl = str;
            this.segmentBase = segmentBase;
            this.drmSchemeType = str2;
            this.drmSchemeDatas = arrayList;
            this.inbandEventStreams = arrayList2;
            this.revisionId = j;
        }
    }
}