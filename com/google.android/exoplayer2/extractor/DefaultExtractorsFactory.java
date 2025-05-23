package com.google.android.exoplayer2.extractor;

import com.google.android.exoplayer2.extractor.amr.AmrExtractor;
import com.google.android.exoplayer2.extractor.flac.FlacExtractor;
import com.google.android.exoplayer2.extractor.flv.FlvExtractor;
import com.google.android.exoplayer2.extractor.mkv.MatroskaExtractor;
import com.google.android.exoplayer2.extractor.mp3.Mp3Extractor;
import com.google.android.exoplayer2.extractor.mp4.FragmentedMp4Extractor;
import com.google.android.exoplayer2.extractor.mp4.Mp4Extractor;
import com.google.android.exoplayer2.extractor.ogg.OggExtractor;
import com.google.android.exoplayer2.extractor.ts.Ac3Extractor;
import com.google.android.exoplayer2.extractor.ts.Ac4Extractor;
import com.google.android.exoplayer2.extractor.ts.AdtsExtractor;
import com.google.android.exoplayer2.extractor.ts.PsExtractor;
import com.google.android.exoplayer2.extractor.ts.TsExtractor;
import com.google.android.exoplayer2.extractor.wav.WavExtractor;
import java.lang.reflect.Constructor;

/* loaded from: classes.dex */
public final class DefaultExtractorsFactory implements ExtractorsFactory {
    private static final Constructor<? extends Extractor> FLAC_EXTENSION_EXTRACTOR_CONSTRUCTOR;
    private int adtsFlags;
    private int amrFlags;
    private boolean constantBitrateSeekingEnabled;
    private int fragmentedMp4Flags;
    private int matroskaFlags;
    private int mp3Flags;
    private int mp4Flags;
    private int tsFlags;
    private int tsMode = 1;

    static {
        Constructor<? extends Extractor> constructor = null;
        try {
            if (Boolean.TRUE.equals(Class.forName("com.google.android.exoplayer2.ext.flac.FlacLibrary").getMethod("isAvailable", new Class[0]).invoke(null, new Object[0]))) {
                constructor = Class.forName("com.google.android.exoplayer2.ext.flac.FlacExtractor").asSubclass(Extractor.class).getConstructor(new Class[0]);
            }
        } catch (ClassNotFoundException unused) {
        } catch (Exception e) {
            throw new RuntimeException("Error instantiating FLAC extension", e);
        }
        FLAC_EXTENSION_EXTRACTOR_CONSTRUCTOR = constructor;
    }

    public synchronized DefaultExtractorsFactory setConstantBitrateSeekingEnabled(boolean z) {
        this.constantBitrateSeekingEnabled = z;
        return this;
    }

    public synchronized DefaultExtractorsFactory setAdtsExtractorFlags(int i) {
        this.adtsFlags = i;
        return this;
    }

    public synchronized DefaultExtractorsFactory setAmrExtractorFlags(int i) {
        this.amrFlags = i;
        return this;
    }

    public synchronized DefaultExtractorsFactory setMatroskaExtractorFlags(int i) {
        this.matroskaFlags = i;
        return this;
    }

    public synchronized DefaultExtractorsFactory setMp4ExtractorFlags(int i) {
        this.mp4Flags = i;
        return this;
    }

    public synchronized DefaultExtractorsFactory setFragmentedMp4ExtractorFlags(int i) {
        this.fragmentedMp4Flags = i;
        return this;
    }

    public synchronized DefaultExtractorsFactory setMp3ExtractorFlags(int i) {
        this.mp3Flags = i;
        return this;
    }

    public synchronized DefaultExtractorsFactory setTsExtractorMode(int i) {
        this.tsMode = i;
        return this;
    }

    public synchronized DefaultExtractorsFactory setTsExtractorFlags(int i) {
        this.tsFlags = i;
        return this;
    }

    @Override // com.google.android.exoplayer2.extractor.ExtractorsFactory
    public synchronized Extractor[] createExtractors() {
        Extractor[] extractorArr;
        extractorArr = new Extractor[14];
        extractorArr[0] = new MatroskaExtractor(this.matroskaFlags);
        int i = 1;
        extractorArr[1] = new FragmentedMp4Extractor(this.fragmentedMp4Flags);
        extractorArr[2] = new Mp4Extractor(this.mp4Flags);
        extractorArr[3] = new Mp3Extractor(this.mp3Flags | (this.constantBitrateSeekingEnabled ? 1 : 0));
        extractorArr[4] = new AdtsExtractor(this.adtsFlags | (this.constantBitrateSeekingEnabled ? 1 : 0));
        extractorArr[5] = new Ac3Extractor();
        extractorArr[6] = new TsExtractor(this.tsMode, this.tsFlags);
        extractorArr[7] = new FlvExtractor();
        extractorArr[8] = new OggExtractor();
        extractorArr[9] = new PsExtractor();
        extractorArr[10] = new WavExtractor();
        int i2 = this.amrFlags;
        if (!this.constantBitrateSeekingEnabled) {
            i = 0;
        }
        extractorArr[11] = new AmrExtractor(i | i2);
        extractorArr[12] = new Ac4Extractor();
        Constructor<? extends Extractor> constructor = FLAC_EXTENSION_EXTRACTOR_CONSTRUCTOR;
        if (constructor != null) {
            try {
                extractorArr[13] = constructor.newInstance(new Object[0]);
            } catch (Exception e) {
                throw new IllegalStateException("Unexpected error creating FLAC extractor", e);
            }
        } else {
            extractorArr[13] = new FlacExtractor();
        }
        return extractorArr;
    }
}