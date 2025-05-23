package com.google.android.exoplayer2.trackselection;

import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;

/* loaded from: classes.dex */
public final class TrackSelectionUtil {

    public interface AdaptiveTrackSelectionFactory {
        TrackSelection createAdaptiveTrackSelection(TrackSelection.Definition definition);
    }

    private TrackSelectionUtil() {
    }

    public static TrackSelection[] createTrackSelectionsForDefinitions(TrackSelection.Definition[] definitionArr, AdaptiveTrackSelectionFactory adaptiveTrackSelectionFactory) {
        TrackSelection[] trackSelectionArr = new TrackSelection[definitionArr.length];
        boolean z = false;
        for (int i = 0; i < definitionArr.length; i++) {
            TrackSelection.Definition definition = definitionArr[i];
            if (definition != null) {
                if (definition.tracks.length > 1 && !z) {
                    trackSelectionArr[i] = adaptiveTrackSelectionFactory.createAdaptiveTrackSelection(definition);
                    z = true;
                } else {
                    trackSelectionArr[i] = new FixedTrackSelection(definition.group, definition.tracks[0], definition.reason, definition.data);
                }
            }
        }
        return trackSelectionArr;
    }

    public static DefaultTrackSelector.Parameters updateParametersWithOverride(DefaultTrackSelector.Parameters parameters, int i, TrackGroupArray trackGroupArray, boolean z, DefaultTrackSelector.SelectionOverride selectionOverride) {
        DefaultTrackSelector.ParametersBuilder rendererDisabled = parameters.buildUpon().clearSelectionOverrides(i).setRendererDisabled(i, z);
        if (selectionOverride != null) {
            rendererDisabled.setSelectionOverride(i, trackGroupArray, selectionOverride);
        }
        return rendererDisabled.build();
    }
}