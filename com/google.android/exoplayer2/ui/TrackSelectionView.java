package com.google.android.exoplayer2.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Pair;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.util.Assertions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.checkerframework.checker.nullness.qual.RequiresNonNull;

/* loaded from: classes.dex */
public class TrackSelectionView extends LinearLayout {
    private boolean allowAdaptiveSelections;
    private boolean allowMultipleOverrides;
    private final ComponentListener componentListener;
    private final CheckedTextView defaultView;
    private final CheckedTextView disableView;
    private final LayoutInflater inflater;
    private boolean isDisabled;
    private TrackSelectionListener listener;
    private MappingTrackSelector.MappedTrackInfo mappedTrackInfo;
    private final SparseArray<DefaultTrackSelector.SelectionOverride> overrides;
    private int rendererIndex;
    private final int selectableItemBackgroundResourceId;
    private TrackGroupArray trackGroups;
    private TrackNameProvider trackNameProvider;
    private CheckedTextView[][] trackViews;

    public interface TrackSelectionListener {
        void onTrackSelectionChanged(boolean z, List<DefaultTrackSelector.SelectionOverride> list);
    }

    public TrackSelectionView(Context context) {
        this(context, null);
    }

    public TrackSelectionView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public TrackSelectionView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        setOrientation(1);
        this.overrides = new SparseArray<>();
        setSaveFromParentEnabled(false);
        TypedArray obtainStyledAttributes = context.getTheme().obtainStyledAttributes(new int[]{android.R.attr.selectableItemBackground});
        int resourceId = obtainStyledAttributes.getResourceId(0, 0);
        this.selectableItemBackgroundResourceId = resourceId;
        obtainStyledAttributes.recycle();
        LayoutInflater from = LayoutInflater.from(context);
        this.inflater = from;
        ComponentListener componentListener = new ComponentListener();
        this.componentListener = componentListener;
        this.trackNameProvider = new DefaultTrackNameProvider(getResources());
        this.trackGroups = TrackGroupArray.EMPTY;
        CheckedTextView checkedTextView = (CheckedTextView) from.inflate(android.R.layout.simple_list_item_single_choice, (ViewGroup) this, false);
        this.disableView = checkedTextView;
        checkedTextView.setBackgroundResource(resourceId);
        checkedTextView.setText(R.string.exo_track_selection_none);
        checkedTextView.setEnabled(false);
        checkedTextView.setFocusable(true);
        checkedTextView.setOnClickListener(componentListener);
        checkedTextView.setVisibility(8);
        addView(checkedTextView);
        addView(from.inflate(R.layout.exo_list_divider, (ViewGroup) this, false));
        CheckedTextView checkedTextView2 = (CheckedTextView) from.inflate(android.R.layout.simple_list_item_single_choice, (ViewGroup) this, false);
        this.defaultView = checkedTextView2;
        checkedTextView2.setBackgroundResource(resourceId);
        checkedTextView2.setText(R.string.exo_track_selection_auto);
        checkedTextView2.setEnabled(false);
        checkedTextView2.setFocusable(true);
        checkedTextView2.setOnClickListener(componentListener);
        addView(checkedTextView2);
    }

    public void setAllowAdaptiveSelections(boolean z) {
        if (this.allowAdaptiveSelections != z) {
            this.allowAdaptiveSelections = z;
            updateViews();
        }
    }

    public void setAllowMultipleOverrides(boolean z) {
        if (this.allowMultipleOverrides != z) {
            this.allowMultipleOverrides = z;
            if (!z && this.overrides.size() > 1) {
                for (int size = this.overrides.size() - 1; size > 0; size--) {
                    this.overrides.remove(size);
                }
            }
            updateViews();
        }
    }

    public void setShowDisableOption(boolean z) {
        this.disableView.setVisibility(z ? 0 : 8);
    }

    public void setTrackNameProvider(TrackNameProvider trackNameProvider) {
        this.trackNameProvider = (TrackNameProvider) Assertions.checkNotNull(trackNameProvider);
        updateViews();
    }

    public void init(MappingTrackSelector.MappedTrackInfo mappedTrackInfo, int i, boolean z, List<DefaultTrackSelector.SelectionOverride> list, TrackSelectionListener trackSelectionListener) {
        this.mappedTrackInfo = mappedTrackInfo;
        this.rendererIndex = i;
        this.isDisabled = z;
        this.listener = trackSelectionListener;
        int size = this.allowMultipleOverrides ? list.size() : Math.min(list.size(), 1);
        for (int i2 = 0; i2 < size; i2++) {
            DefaultTrackSelector.SelectionOverride selectionOverride = list.get(i2);
            this.overrides.put(selectionOverride.groupIndex, selectionOverride);
        }
        updateViews();
    }

    public boolean getIsDisabled() {
        return this.isDisabled;
    }

    public List<DefaultTrackSelector.SelectionOverride> getOverrides() {
        ArrayList arrayList = new ArrayList(this.overrides.size());
        for (int i = 0; i < this.overrides.size(); i++) {
            arrayList.add(this.overrides.valueAt(i));
        }
        return arrayList;
    }

    private void updateViews() {
        for (int childCount = getChildCount() - 1; childCount >= 3; childCount--) {
            removeViewAt(childCount);
        }
        if (this.mappedTrackInfo == null) {
            this.disableView.setEnabled(false);
            this.defaultView.setEnabled(false);
            return;
        }
        this.disableView.setEnabled(true);
        this.defaultView.setEnabled(true);
        TrackGroupArray trackGroups = this.mappedTrackInfo.getTrackGroups(this.rendererIndex);
        this.trackGroups = trackGroups;
        this.trackViews = new CheckedTextView[trackGroups.length][];
        boolean shouldEnableMultiGroupSelection = shouldEnableMultiGroupSelection();
        for (int i = 0; i < this.trackGroups.length; i++) {
            TrackGroup trackGroup = this.trackGroups.get(i);
            boolean shouldEnableAdaptiveSelection = shouldEnableAdaptiveSelection(i);
            this.trackViews[i] = new CheckedTextView[trackGroup.length];
            for (int i2 = 0; i2 < trackGroup.length; i2++) {
                if (i2 == 0) {
                    addView(this.inflater.inflate(R.layout.exo_list_divider, (ViewGroup) this, false));
                }
                CheckedTextView checkedTextView = (CheckedTextView) this.inflater.inflate((shouldEnableAdaptiveSelection || shouldEnableMultiGroupSelection) ? android.R.layout.simple_list_item_multiple_choice : android.R.layout.simple_list_item_single_choice, (ViewGroup) this, false);
                checkedTextView.setBackgroundResource(this.selectableItemBackgroundResourceId);
                checkedTextView.setText(this.trackNameProvider.getTrackName(trackGroup.getFormat(i2)));
                if (this.mappedTrackInfo.getTrackSupport(this.rendererIndex, i, i2) == 4) {
                    checkedTextView.setFocusable(true);
                    checkedTextView.setTag(Pair.create(Integer.valueOf(i), Integer.valueOf(i2)));
                    checkedTextView.setOnClickListener(this.componentListener);
                } else {
                    checkedTextView.setFocusable(false);
                    checkedTextView.setEnabled(false);
                }
                this.trackViews[i][i2] = checkedTextView;
                addView(checkedTextView);
            }
        }
        updateViewStates();
    }

    private void updateViewStates() {
        this.disableView.setChecked(this.isDisabled);
        this.defaultView.setChecked(!this.isDisabled && this.overrides.size() == 0);
        for (int i = 0; i < this.trackViews.length; i++) {
            DefaultTrackSelector.SelectionOverride selectionOverride = this.overrides.get(i);
            int i2 = 0;
            while (true) {
                CheckedTextView[] checkedTextViewArr = this.trackViews[i];
                if (i2 < checkedTextViewArr.length) {
                    checkedTextViewArr[i2].setChecked(selectionOverride != null && selectionOverride.containsTrack(i2));
                    i2++;
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onClick(View view) {
        if (view == this.disableView) {
            onDisableViewClicked();
        } else if (view == this.defaultView) {
            onDefaultViewClicked();
        } else {
            onTrackViewClicked(view);
        }
        updateViewStates();
        TrackSelectionListener trackSelectionListener = this.listener;
        if (trackSelectionListener != null) {
            trackSelectionListener.onTrackSelectionChanged(getIsDisabled(), getOverrides());
        }
    }

    private void onDisableViewClicked() {
        this.isDisabled = true;
        this.overrides.clear();
    }

    private void onDefaultViewClicked() {
        this.isDisabled = false;
        this.overrides.clear();
    }

    private void onTrackViewClicked(View view) {
        this.isDisabled = false;
        Pair pair = (Pair) view.getTag();
        int intValue = ((Integer) pair.first).intValue();
        int intValue2 = ((Integer) pair.second).intValue();
        DefaultTrackSelector.SelectionOverride selectionOverride = this.overrides.get(intValue);
        Assertions.checkNotNull(this.mappedTrackInfo);
        if (selectionOverride == null) {
            if (!this.allowMultipleOverrides && this.overrides.size() > 0) {
                this.overrides.clear();
            }
            this.overrides.put(intValue, new DefaultTrackSelector.SelectionOverride(intValue, intValue2));
            return;
        }
        int i = selectionOverride.length;
        int[] iArr = selectionOverride.tracks;
        boolean isChecked = ((CheckedTextView) view).isChecked();
        boolean shouldEnableAdaptiveSelection = shouldEnableAdaptiveSelection(intValue);
        boolean z = shouldEnableAdaptiveSelection || shouldEnableMultiGroupSelection();
        if (isChecked && z) {
            if (i == 1) {
                this.overrides.remove(intValue);
                return;
            } else {
                this.overrides.put(intValue, new DefaultTrackSelector.SelectionOverride(intValue, getTracksRemoving(iArr, intValue2)));
                return;
            }
        }
        if (isChecked) {
            return;
        }
        if (shouldEnableAdaptiveSelection) {
            this.overrides.put(intValue, new DefaultTrackSelector.SelectionOverride(intValue, getTracksAdding(iArr, intValue2)));
        } else {
            this.overrides.put(intValue, new DefaultTrackSelector.SelectionOverride(intValue, intValue2));
        }
    }

    @RequiresNonNull({"mappedTrackInfo"})
    private boolean shouldEnableAdaptiveSelection(int i) {
        return this.allowAdaptiveSelections && this.trackGroups.get(i).length > 1 && this.mappedTrackInfo.getAdaptiveSupport(this.rendererIndex, i, false) != 0;
    }

    private boolean shouldEnableMultiGroupSelection() {
        return this.allowMultipleOverrides && this.trackGroups.length > 1;
    }

    private static int[] getTracksAdding(int[] iArr, int i) {
        int[] copyOf = Arrays.copyOf(iArr, iArr.length + 1);
        copyOf[copyOf.length - 1] = i;
        return copyOf;
    }

    private static int[] getTracksRemoving(int[] iArr, int i) {
        int[] iArr2 = new int[iArr.length - 1];
        int i2 = 0;
        for (int i3 : iArr) {
            if (i3 != i) {
                iArr2[i2] = i3;
                i2++;
            }
        }
        return iArr2;
    }

    private class ComponentListener implements View.OnClickListener {
        private ComponentListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            TrackSelectionView.this.onClick(view);
        }
    }
}