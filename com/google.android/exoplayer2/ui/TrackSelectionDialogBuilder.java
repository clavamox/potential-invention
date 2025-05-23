package com.google.android.exoplayer2.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionUtil;
import com.google.android.exoplayer2.util.Assertions;
import java.util.Collections;
import java.util.List;

/* loaded from: classes.dex */
public final class TrackSelectionDialogBuilder {
    private boolean allowAdaptiveSelections;
    private boolean allowMultipleOverrides;
    private final DialogCallback callback;
    private final Context context;
    private boolean isDisabled;
    private final MappingTrackSelector.MappedTrackInfo mappedTrackInfo;
    private List<DefaultTrackSelector.SelectionOverride> overrides;
    private final int rendererIndex;
    private boolean showDisableOption;
    private final CharSequence title;
    private TrackNameProvider trackNameProvider;

    public interface DialogCallback {
        void onTracksSelected(boolean z, List<DefaultTrackSelector.SelectionOverride> list);
    }

    public TrackSelectionDialogBuilder(Context context, CharSequence charSequence, MappingTrackSelector.MappedTrackInfo mappedTrackInfo, int i, DialogCallback dialogCallback) {
        this.context = context;
        this.title = charSequence;
        this.mappedTrackInfo = mappedTrackInfo;
        this.rendererIndex = i;
        this.callback = dialogCallback;
        this.overrides = Collections.emptyList();
    }

    public TrackSelectionDialogBuilder(Context context, CharSequence charSequence, final DefaultTrackSelector defaultTrackSelector, final int i) {
        this.context = context;
        this.title = charSequence;
        MappingTrackSelector.MappedTrackInfo mappedTrackInfo = (MappingTrackSelector.MappedTrackInfo) Assertions.checkNotNull(defaultTrackSelector.getCurrentMappedTrackInfo());
        this.mappedTrackInfo = mappedTrackInfo;
        this.rendererIndex = i;
        final TrackGroupArray trackGroups = mappedTrackInfo.getTrackGroups(i);
        final DefaultTrackSelector.Parameters parameters = defaultTrackSelector.getParameters();
        this.isDisabled = parameters.getRendererDisabled(i);
        DefaultTrackSelector.SelectionOverride selectionOverride = parameters.getSelectionOverride(i, trackGroups);
        this.overrides = selectionOverride == null ? Collections.emptyList() : Collections.singletonList(selectionOverride);
        this.callback = new DialogCallback() { // from class: com.google.android.exoplayer2.ui.TrackSelectionDialogBuilder$$ExternalSyntheticLambda1
            @Override // com.google.android.exoplayer2.ui.TrackSelectionDialogBuilder.DialogCallback
            public final void onTracksSelected(boolean z, List list) {
                DefaultTrackSelector.this.setParameters(TrackSelectionUtil.updateParametersWithOverride(parameters, i, trackGroups, z, r6.isEmpty() ? null : (DefaultTrackSelector.SelectionOverride) list.get(0)));
            }
        };
    }

    public TrackSelectionDialogBuilder setIsDisabled(boolean z) {
        this.isDisabled = z;
        return this;
    }

    public TrackSelectionDialogBuilder setOverride(DefaultTrackSelector.SelectionOverride selectionOverride) {
        return setOverrides(selectionOverride == null ? Collections.emptyList() : Collections.singletonList(selectionOverride));
    }

    public TrackSelectionDialogBuilder setOverrides(List<DefaultTrackSelector.SelectionOverride> list) {
        this.overrides = list;
        return this;
    }

    public TrackSelectionDialogBuilder setAllowAdaptiveSelections(boolean z) {
        this.allowAdaptiveSelections = z;
        return this;
    }

    public TrackSelectionDialogBuilder setAllowMultipleOverrides(boolean z) {
        this.allowMultipleOverrides = z;
        return this;
    }

    public TrackSelectionDialogBuilder setShowDisableOption(boolean z) {
        this.showDisableOption = z;
        return this;
    }

    public TrackSelectionDialogBuilder setTrackNameProvider(TrackNameProvider trackNameProvider) {
        this.trackNameProvider = trackNameProvider;
        return this;
    }

    public AlertDialog build() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
        View inflate = LayoutInflater.from(builder.getContext()).inflate(R.layout.exo_track_selection_dialog, (ViewGroup) null);
        final TrackSelectionView trackSelectionView = (TrackSelectionView) inflate.findViewById(R.id.exo_track_selection_view);
        trackSelectionView.setAllowMultipleOverrides(this.allowMultipleOverrides);
        trackSelectionView.setAllowAdaptiveSelections(this.allowAdaptiveSelections);
        trackSelectionView.setShowDisableOption(this.showDisableOption);
        TrackNameProvider trackNameProvider = this.trackNameProvider;
        if (trackNameProvider != null) {
            trackSelectionView.setTrackNameProvider(trackNameProvider);
        }
        trackSelectionView.init(this.mappedTrackInfo, this.rendererIndex, this.isDisabled, this.overrides, null);
        return builder.setTitle(this.title).setView(inflate).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() { // from class: com.google.android.exoplayer2.ui.TrackSelectionDialogBuilder$$ExternalSyntheticLambda0
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                TrackSelectionDialogBuilder.this.m55xff2904f1(trackSelectionView, dialogInterface, i);
            }
        }).setNegativeButton(android.R.string.cancel, (DialogInterface.OnClickListener) null).create();
    }

    /* renamed from: lambda$build$1$com-google-android-exoplayer2-ui-TrackSelectionDialogBuilder, reason: not valid java name */
    /* synthetic */ void m55xff2904f1(TrackSelectionView trackSelectionView, DialogInterface dialogInterface, int i) {
        this.callback.onTracksSelected(trackSelectionView.getIsDisabled(), trackSelectionView.getOverrides());
    }
}