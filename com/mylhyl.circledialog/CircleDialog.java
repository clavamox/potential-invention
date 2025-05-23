package com.mylhyl.circledialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.view.View;
import android.widget.BaseAdapter;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import com.mylhyl.circledialog.callback.CircleItemViewBinder;
import com.mylhyl.circledialog.callback.ConfigButton;
import com.mylhyl.circledialog.callback.ConfigDialog;
import com.mylhyl.circledialog.callback.ConfigInput;
import com.mylhyl.circledialog.callback.ConfigItems;
import com.mylhyl.circledialog.callback.ConfigLottie;
import com.mylhyl.circledialog.callback.ConfigProgress;
import com.mylhyl.circledialog.callback.ConfigSubTitle;
import com.mylhyl.circledialog.callback.ConfigText;
import com.mylhyl.circledialog.callback.ConfigTitle;
import com.mylhyl.circledialog.internal.CircleParams;
import com.mylhyl.circledialog.params.AdParams;
import com.mylhyl.circledialog.params.ButtonParams;
import com.mylhyl.circledialog.params.CloseParams;
import com.mylhyl.circledialog.params.DialogParams;
import com.mylhyl.circledialog.params.InputParams;
import com.mylhyl.circledialog.params.ItemsParams;
import com.mylhyl.circledialog.params.LottieParams;
import com.mylhyl.circledialog.params.ProgressParams;
import com.mylhyl.circledialog.params.SubTitleParams;
import com.mylhyl.circledialog.params.TextParams;
import com.mylhyl.circledialog.params.TitleParams;
import com.mylhyl.circledialog.res.values.CircleColor;
import com.mylhyl.circledialog.view.listener.CountDownTimerObserver;
import com.mylhyl.circledialog.view.listener.OnAdItemClickListener;
import com.mylhyl.circledialog.view.listener.OnAdPageChangeListener;
import com.mylhyl.circledialog.view.listener.OnBindBodyViewCallback;
import com.mylhyl.circledialog.view.listener.OnCreateBodyViewListener;
import com.mylhyl.circledialog.view.listener.OnCreateButtonListener;
import com.mylhyl.circledialog.view.listener.OnCreateInputListener;
import com.mylhyl.circledialog.view.listener.OnCreateLottieListener;
import com.mylhyl.circledialog.view.listener.OnCreateProgressListener;
import com.mylhyl.circledialog.view.listener.OnCreateTextListener;
import com.mylhyl.circledialog.view.listener.OnCreateTitleListener;
import com.mylhyl.circledialog.view.listener.OnInputClickListener;
import com.mylhyl.circledialog.view.listener.OnInputCounterChangeListener;
import com.mylhyl.circledialog.view.listener.OnLvItemClickListener;
import com.mylhyl.circledialog.view.listener.OnRvItemClickListener;
import com.mylhyl.circledialog.view.listener.OnShowListener;
import java.util.List;

/* loaded from: classes.dex */
public final class CircleDialog {
    private BaseCircleDialog mBaseCircleDialog;

    private CircleDialog() {
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void refresh() {
        if (check()) {
            return;
        }
        this.mBaseCircleDialog.refreshView();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dismiss() {
        if (check()) {
            return;
        }
        this.mBaseCircleDialog.dialogDismiss();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void show(FragmentManager fragmentManager) {
        this.mBaseCircleDialog.show(fragmentManager);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public BaseCircleDialog create(CircleParams circleParams) {
        BaseCircleDialog newAbsCircleDialog = BaseCircleDialog.newAbsCircleDialog(circleParams);
        this.mBaseCircleDialog = newAbsCircleDialog;
        return newAbsCircleDialog;
    }

    private boolean check() {
        Dialog dialog;
        BaseCircleDialog baseCircleDialog = this.mBaseCircleDialog;
        return baseCircleDialog == null || (dialog = baseCircleDialog.getDialog()) == null || !dialog.isShowing();
    }

    public static final class Builder {
        private CircleDialog mCircleDialog;
        private CircleParams mCircleParams;

        public Builder() {
            CircleParams circleParams = new CircleParams();
            this.mCircleParams = circleParams;
            circleParams.dialogParams = new DialogParams();
        }

        public Builder setManualClose(boolean z) {
            this.mCircleParams.dialogParams.manualClose = z;
            return this;
        }

        public Builder setGravity(int i) {
            this.mCircleParams.dialogParams.gravity = i;
            return this;
        }

        public Builder setCanceledOnTouchOutside(boolean z) {
            this.mCircleParams.dialogParams.canceledOnTouchOutside = z;
            return this;
        }

        public Builder setCancelable(boolean z) {
            this.mCircleParams.dialogParams.cancelable = z;
            return this;
        }

        public Builder setWidth(float f) {
            this.mCircleParams.dialogParams.width = f;
            return this;
        }

        public Builder setMaxHeight(float f) {
            this.mCircleParams.dialogParams.maxHeight = f;
            return this;
        }

        public Builder setRadius(int i) {
            this.mCircleParams.dialogParams.radius = i;
            return this;
        }

        public Builder setYoff(int i) {
            this.mCircleParams.dialogParams.yOff = i;
            return this;
        }

        public Builder bottomFull() {
            this.mCircleParams.dialogParams.gravity = 80;
            this.mCircleParams.dialogParams.radius = 0;
            this.mCircleParams.dialogParams.width = 1.0f;
            this.mCircleParams.dialogParams.yOff = 0;
            return this;
        }

        public Builder setTypeface(Typeface typeface) {
            this.mCircleParams.dialogParams.typeface = typeface;
            return this;
        }

        public Builder setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
            this.mCircleParams.circleListeners.dismissListener = onDismissListener;
            return this;
        }

        public Builder setOnCancelListener(DialogInterface.OnCancelListener onCancelListener) {
            this.mCircleParams.circleListeners.cancelListener = onCancelListener;
            return this;
        }

        public Builder setOnShowListener(OnShowListener onShowListener) {
            this.mCircleParams.circleListeners.showListener = onShowListener;
            return this;
        }

        public Builder setOnKeyListener(DialogInterface.OnKeyListener onKeyListener) {
            this.mCircleParams.circleListeners.keyListener = onKeyListener;
            return this;
        }

        public Builder configDialog(ConfigDialog configDialog) {
            configDialog.onConfig(this.mCircleParams.dialogParams);
            return this;
        }

        public Builder setTitle(String str) {
            newTitleParams();
            this.mCircleParams.titleParams.text = str;
            return this;
        }

        public Builder setTitleIcon(int i) {
            newTitleParams();
            this.mCircleParams.titleParams.icon = i;
            return this;
        }

        public Builder setTitleColor(int i) {
            newTitleParams();
            this.mCircleParams.titleParams.textColor = i;
            return this;
        }

        public Builder configTitle(ConfigTitle configTitle) {
            newTitleParams();
            configTitle.onConfig(this.mCircleParams.titleParams);
            return this;
        }

        public Builder setOnCreateTitleListener(OnCreateTitleListener onCreateTitleListener) {
            this.mCircleParams.circleListeners.createTitleListener = onCreateTitleListener;
            return this;
        }

        public Builder setSubTitle(String str) {
            newSubTitleParams();
            this.mCircleParams.subTitleParams.text = str;
            return this;
        }

        public Builder setSubTitleColor(int i) {
            newSubTitleParams();
            this.mCircleParams.subTitleParams.textColor = i;
            return this;
        }

        public Builder configSubTitle(ConfigSubTitle configSubTitle) {
            newSubTitleParams();
            configSubTitle.onConfig(this.mCircleParams.subTitleParams);
            return this;
        }

        public Builder setText(String str) {
            newTextParams();
            this.mCircleParams.textParams.text = str;
            return this;
        }

        public Builder setTextColor(int i) {
            newTextParams();
            this.mCircleParams.textParams.textColor = i;
            return this;
        }

        public Builder configText(ConfigText configText) {
            newTextParams();
            configText.onConfig(this.mCircleParams.textParams);
            return this;
        }

        public Builder setOnCreateTextListener(OnCreateTextListener onCreateTextListener) {
            this.mCircleParams.circleListeners.createTextListener = onCreateTextListener;
            return this;
        }

        public Builder setItems(Object obj, OnLvItemClickListener onLvItemClickListener) {
            newItemsParams();
            this.mCircleParams.itemListViewType = true;
            this.mCircleParams.itemsParams.items = obj;
            this.mCircleParams.circleListeners.itemListener = onLvItemClickListener;
            return this;
        }

        public Builder setItems(BaseAdapter baseAdapter, OnLvItemClickListener onLvItemClickListener) {
            newItemsParams();
            this.mCircleParams.itemListViewType = true;
            this.mCircleParams.itemsParams.adapter = baseAdapter;
            this.mCircleParams.circleListeners.itemListener = onLvItemClickListener;
            return this;
        }

        public Builder setItems(Object obj, OnRvItemClickListener onRvItemClickListener) {
            newItemsParams();
            this.mCircleParams.itemListViewType = false;
            this.mCircleParams.itemsParams.items = obj;
            this.mCircleParams.circleListeners.rvItemListener = onRvItemClickListener;
            return this;
        }

        public Builder setItems(Object obj, RecyclerView.LayoutManager layoutManager, OnRvItemClickListener onRvItemClickListener) {
            newItemsParams();
            this.mCircleParams.itemListViewType = false;
            ItemsParams itemsParams = this.mCircleParams.itemsParams;
            itemsParams.items = obj;
            itemsParams.layoutManager = layoutManager;
            this.mCircleParams.circleListeners.rvItemListener = onRvItemClickListener;
            return this;
        }

        public Builder setItems(RecyclerView.Adapter adapter, RecyclerView.LayoutManager layoutManager) {
            newItemsParams();
            this.mCircleParams.itemListViewType = false;
            ItemsParams itemsParams = this.mCircleParams.itemsParams;
            itemsParams.layoutManager = layoutManager;
            itemsParams.adapterRv = adapter;
            return this;
        }

        public Builder setItems(RecyclerView.Adapter adapter, RecyclerView.LayoutManager layoutManager, RecyclerView.ItemDecoration itemDecoration) {
            newItemsParams();
            this.mCircleParams.itemListViewType = false;
            ItemsParams itemsParams = this.mCircleParams.itemsParams;
            itemsParams.layoutManager = layoutManager;
            itemsParams.itemDecoration = itemDecoration;
            itemsParams.adapterRv = adapter;
            return this;
        }

        public Builder setItemsViewBinder(CircleItemViewBinder circleItemViewBinder) {
            newItemsParams();
            this.mCircleParams.itemsParams.viewBinder = circleItemViewBinder;
            return this;
        }

        public Builder configItems(ConfigItems configItems) {
            newItemsParams();
            configItems.onConfig(this.mCircleParams.itemsParams);
            return this;
        }

        public Builder setProgressText(String str) {
            return setProgressText(str, "");
        }

        public Builder setProgressText(String str, String str2) {
            newProgressParams();
            this.mCircleParams.progressParams.text = str;
            this.mCircleParams.progressParams.timeoutText = str2;
            return this;
        }

        public Builder setProgressStyle(int i) {
            newProgressParams();
            this.mCircleParams.progressParams.style = i;
            return this;
        }

        public Builder setProgressColor(int i) {
            newProgressParams();
            this.mCircleParams.progressParams.indeterminateColor = i;
            return this;
        }

        public Builder setProgress(int i, int i2) {
            newProgressParams();
            ProgressParams progressParams = this.mCircleParams.progressParams;
            progressParams.max = i;
            progressParams.progress = i2;
            return this;
        }

        public Builder setProgressDrawable(int i) {
            newProgressParams();
            this.mCircleParams.progressParams.progressDrawableId = i;
            return this;
        }

        public Builder setProgressHeight(int i) {
            newProgressParams();
            this.mCircleParams.progressParams.progressHeight = i;
            return this;
        }

        public Builder configProgress(ConfigProgress configProgress) {
            newProgressParams();
            configProgress.onConfig(this.mCircleParams.progressParams);
            return this;
        }

        public Builder setOnCreateProgressListener(OnCreateProgressListener onCreateProgressListener) {
            this.mCircleParams.circleListeners.createProgressListener = onCreateProgressListener;
            return this;
        }

        public Builder setBodyView(int i, OnCreateBodyViewListener onCreateBodyViewListener) {
            this.mCircleParams.bodyViewId = i;
            this.mCircleParams.circleListeners.createBodyViewListener = onCreateBodyViewListener;
            return this;
        }

        public Builder setBodyView(View view, OnCreateBodyViewListener onCreateBodyViewListener) {
            this.mCircleParams.bodyView = view;
            this.mCircleParams.circleListeners.createBodyViewListener = onCreateBodyViewListener;
            return this;
        }

        public Builder setBodyView(int i) {
            this.mCircleParams.bodyViewId = i;
            return this;
        }

        public Builder setBodyView(View view) {
            this.mCircleParams.bodyView = view;
            return this;
        }

        public Builder setPositiveBody(String str, OnBindBodyViewCallback onBindBodyViewCallback) {
            newPositiveParams();
            this.mCircleParams.positiveParams.text = str;
            this.mCircleParams.circleListeners.bindBodyViewCallback = onBindBodyViewCallback;
            return this;
        }

        public Builder setInputText(String str) {
            newInputParams();
            this.mCircleParams.inputParams.text = str;
            return this;
        }

        public Builder setInputShowKeyboard(boolean z) {
            newInputParams();
            this.mCircleParams.inputParams.showSoftKeyboard = z;
            return this;
        }

        public Builder setInputText(String str, String str2) {
            newInputParams();
            this.mCircleParams.inputParams.text = str;
            this.mCircleParams.inputParams.hintText = str2;
            return this;
        }

        public Builder setInputHint(String str) {
            newInputParams();
            this.mCircleParams.inputParams.hintText = str;
            return this;
        }

        public Builder setInputHeight(int i) {
            newInputParams();
            this.mCircleParams.inputParams.inputHeight = i;
            return this;
        }

        public Builder setInputCounter(int i) {
            return setInputCounter(i, null);
        }

        public Builder setInputCounter(int i, OnInputCounterChangeListener onInputCounterChangeListener) {
            newInputParams();
            this.mCircleParams.inputParams.maxLen = i;
            this.mCircleParams.circleListeners.inputCounterChangeListener = onInputCounterChangeListener;
            return this;
        }

        public Builder setInputCounterColor(int i) {
            newInputParams();
            this.mCircleParams.inputParams.counterColor = i;
            return this;
        }

        public Builder setInputEmoji(boolean z) {
            newInputParams();
            this.mCircleParams.inputParams.isEmojiInput = z;
            return this;
        }

        public Builder setPositiveInput(String str, OnInputClickListener onInputClickListener) {
            newPositiveParams();
            this.mCircleParams.positiveParams.text = str;
            this.mCircleParams.circleListeners.inputListener = onInputClickListener;
            return this;
        }

        public Builder configInput(ConfigInput configInput) {
            newInputParams();
            configInput.onConfig(this.mCircleParams.inputParams);
            return this;
        }

        public Builder setOnCreateInputListener(OnCreateInputListener onCreateInputListener) {
            this.mCircleParams.circleListeners.createInputListener = onCreateInputListener;
            return this;
        }

        public Builder setLottieAnimation(String str) {
            newLottieParams();
            this.mCircleParams.lottieParams.animationFileName = str;
            return this;
        }

        public Builder setLottieAnimation(int i) {
            newLottieParams();
            this.mCircleParams.lottieParams.animationResId = i;
            return this;
        }

        public Builder playLottieAnimation() {
            newLottieParams();
            this.mCircleParams.lottieParams.autoPlay = true;
            return this;
        }

        public Builder setLottieLoop(boolean z) {
            newLottieParams();
            this.mCircleParams.lottieParams.loop = z;
            return this;
        }

        public Builder setLottieText(String str) {
            newLottieParams();
            this.mCircleParams.lottieParams.text = str;
            return this;
        }

        public Builder setLottieSize(int i, int i2) {
            newLottieParams();
            this.mCircleParams.lottieParams.lottieWidth = i;
            this.mCircleParams.lottieParams.lottieHeight = i2;
            return this;
        }

        public Builder configLottie(ConfigLottie configLottie) {
            newLottieParams();
            configLottie.onConfig(this.mCircleParams.lottieParams);
            return this;
        }

        public Builder setOnCreateLottieListener(OnCreateLottieListener onCreateLottieListener) {
            this.mCircleParams.circleListeners.createLottieListener = onCreateLottieListener;
            return this;
        }

        public Builder setCloseResId(int i) {
            setCloseResId(i, 0);
            return this;
        }

        public Builder setCloseResId(int i, int i2) {
            newCloseParams();
            this.mCircleParams.closeParams.closeResId = i;
            this.mCircleParams.closeParams.closeSize = i2;
            return this;
        }

        public Builder setClosePadding(int[] iArr) {
            newCloseParams();
            this.mCircleParams.closeParams.closePadding = iArr;
            return this;
        }

        public Builder setCloseGravity(int i) {
            newCloseParams();
            this.mCircleParams.closeParams.closeGravity = i;
            return this;
        }

        public Builder setCloseConnector(int i, int i2) {
            newCloseParams();
            this.mCircleParams.closeParams.connectorWidth = i;
            this.mCircleParams.closeParams.connectorHeight = i2;
            return this;
        }

        public Builder setCloseConnector(int i, int i2, int i3) {
            newCloseParams();
            this.mCircleParams.closeParams.connectorWidth = i;
            this.mCircleParams.closeParams.connectorHeight = i2;
            this.mCircleParams.closeParams.connectorColor = i3;
            return this;
        }

        public Builder setAdResId(int i, OnAdItemClickListener onAdItemClickListener) {
            newAdParams();
            return setAdResId(new int[]{i}, onAdItemClickListener);
        }

        public Builder setAdResId(int[] iArr, OnAdItemClickListener onAdItemClickListener) {
            newAdParams();
            this.mCircleParams.adParams.resIds = iArr;
            this.mCircleParams.circleListeners.adItemClickListener = onAdItemClickListener;
            return this;
        }

        public Builder setAdUrl(String str, OnAdItemClickListener onAdItemClickListener) {
            return setAdUrl(new String[]{str}, onAdItemClickListener);
        }

        public Builder setAdUrl(String[] strArr, OnAdItemClickListener onAdItemClickListener) {
            newAdParams();
            this.mCircleParams.adParams.urls = strArr;
            this.mCircleParams.circleListeners.adItemClickListener = onAdItemClickListener;
            return this;
        }

        public Builder setAdUrl(List<String> list, OnAdItemClickListener onAdItemClickListener) {
            return setAdUrl((String[]) list.toArray(new String[list.size()]), onAdItemClickListener);
        }

        public Builder setAdIndicator(boolean z) {
            newAdParams();
            this.mCircleParams.adParams.isShowIndicator = z;
            return this;
        }

        public Builder setAdIndicatorPoint(int i) {
            newAdParams();
            this.mCircleParams.adParams.pointDrawableResId = i;
            return this;
        }

        public Builder setAdPageChangeListener(OnAdPageChangeListener onAdPageChangeListener) {
            this.mCircleParams.circleListeners.adPageChangeListener = onAdPageChangeListener;
            return this;
        }

        public Builder setPositive(String str, View.OnClickListener onClickListener) {
            newPositiveParams();
            this.mCircleParams.positiveParams.text = str;
            this.mCircleParams.circleListeners.clickPositiveListener = onClickListener;
            return this;
        }

        public Builder setPositiveTime(long j, long j2, CountDownTimerObserver countDownTimerObserver) {
            return setPositiveTime(j, j2, "", countDownTimerObserver);
        }

        public Builder setPositiveTime(long j, long j2, String str, CountDownTimerObserver countDownTimerObserver) {
            newPositiveParams();
            ButtonParams buttonParams = this.mCircleParams.positiveParams;
            buttonParams.countDownTime = j;
            buttonParams.countDownInterval = j2;
            buttonParams.countDownText = str;
            this.mCircleParams.circleListeners.countDownTimerObserver = countDownTimerObserver;
            return this;
        }

        public Builder setPositiveDisable(boolean z) {
            newPositiveParams();
            this.mCircleParams.positiveParams.disable = z;
            return this;
        }

        public Builder configPositive(ConfigButton configButton) {
            newPositiveParams();
            configButton.onConfig(this.mCircleParams.positiveParams);
            return this;
        }

        public Builder setNegative(String str, View.OnClickListener onClickListener) {
            newNegativeParams();
            this.mCircleParams.negativeParams.text = str;
            this.mCircleParams.circleListeners.clickNegativeListener = onClickListener;
            return this;
        }

        public Builder configNegative(ConfigButton configButton) {
            newNegativeParams();
            configButton.onConfig(this.mCircleParams.negativeParams);
            return this;
        }

        public Builder setNeutral(String str, View.OnClickListener onClickListener) {
            newNeutralParams();
            this.mCircleParams.neutralParams.text = str;
            this.mCircleParams.circleListeners.clickNeutralListener = onClickListener;
            return this;
        }

        public Builder configNeutral(ConfigButton configButton) {
            newNeutralParams();
            configButton.onConfig(this.mCircleParams.neutralParams);
            return this;
        }

        public Builder setOnCreateButtonListener(OnCreateButtonListener onCreateButtonListener) {
            this.mCircleParams.circleListeners.createButtonListener = onCreateButtonListener;
            return this;
        }

        public BaseCircleDialog show(FragmentManager fragmentManager) {
            BaseCircleDialog create = create();
            this.mCircleDialog.show(fragmentManager);
            return create;
        }

        public BaseCircleDialog create() {
            if (this.mCircleDialog == null) {
                this.mCircleDialog = new CircleDialog();
            }
            return this.mCircleDialog.create(this.mCircleParams);
        }

        public void refresh() {
            CircleDialog circleDialog = this.mCircleDialog;
            if (circleDialog != null) {
                circleDialog.refresh();
            }
        }

        public void dismiss() {
            CircleDialog circleDialog = this.mCircleDialog;
            if (circleDialog != null) {
                circleDialog.dismiss();
            }
        }

        private void newTitleParams() {
            if (this.mCircleParams.titleParams == null) {
                this.mCircleParams.titleParams = new TitleParams();
            }
        }

        private void newSubTitleParams() {
            if (this.mCircleParams.subTitleParams == null) {
                this.mCircleParams.subTitleParams = new SubTitleParams();
            }
        }

        private void newTextParams() {
            if (this.mCircleParams.textParams == null) {
                this.mCircleParams.textParams = new TextParams();
            }
        }

        private void newItemsParams() {
            if (this.mCircleParams.itemsParams == null) {
                this.mCircleParams.itemsParams = new ItemsParams();
            }
        }

        private void newProgressParams() {
            if (this.mCircleParams.progressParams == null) {
                this.mCircleParams.progressParams = new ProgressParams();
            }
        }

        private void newInputParams() {
            if (this.mCircleParams.inputParams == null) {
                this.mCircleParams.inputParams = new InputParams();
            }
        }

        private void newPositiveParams() {
            if (this.mCircleParams.positiveParams == null) {
                this.mCircleParams.positiveParams = new ButtonParams();
            }
        }

        private void newLottieParams() {
            if (this.mCircleParams.lottieParams == null) {
                this.mCircleParams.lottieParams = new LottieParams();
            }
        }

        private void newNegativeParams() {
            if (this.mCircleParams.negativeParams == null) {
                this.mCircleParams.negativeParams = new ButtonParams();
                this.mCircleParams.negativeParams.textColor = CircleColor.FOOTER_BUTTON_TEXT_NEGATIVE;
            }
        }

        private void newNeutralParams() {
            if (this.mCircleParams.neutralParams == null) {
                this.mCircleParams.neutralParams = new ButtonParams();
            }
        }

        private void newCloseParams() {
            if (this.mCircleParams.closeParams == null) {
                this.mCircleParams.closeParams = new CloseParams();
            }
        }

        private void newAdParams() {
            if (this.mCircleParams.adParams == null) {
                this.mCircleParams.adParams = new AdParams();
            }
        }
    }
}