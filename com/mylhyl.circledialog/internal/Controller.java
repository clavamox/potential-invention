package com.mylhyl.circledialog.internal;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.EditText;
import com.mylhyl.circledialog.CircleViewHolder;
import com.mylhyl.circledialog.params.DialogParams;
import com.mylhyl.circledialog.view.BuildViewAdImpl;
import com.mylhyl.circledialog.view.BuildViewConfirmImpl;
import com.mylhyl.circledialog.view.BuildViewCustomBodyImpl;
import com.mylhyl.circledialog.view.BuildViewInputImpl;
import com.mylhyl.circledialog.view.BuildViewItemsListViewImpl;
import com.mylhyl.circledialog.view.BuildViewItemsRecyclerViewImpl;
import com.mylhyl.circledialog.view.BuildViewLottieImpl;
import com.mylhyl.circledialog.view.BuildViewProgressImpl;
import com.mylhyl.circledialog.view.listener.AdView;
import com.mylhyl.circledialog.view.listener.ButtonView;
import com.mylhyl.circledialog.view.listener.InputView;
import com.mylhyl.circledialog.view.listener.ItemsView;
import com.mylhyl.circledialog.view.listener.OnAdItemClickListener;
import com.mylhyl.circledialog.view.listener.OnRvItemClickListener;

/* loaded from: classes.dex */
public final class Controller {
    private CircleViewHolder mCircleViewHolder;
    private Context mContext;
    private BuildView mCreateView;
    private OnDialogInternalListener mOnDialogListener;
    private CircleParams mParams;
    public static final boolean SDK_LOLLIPOP = true;
    public static final boolean SDK_JELLY_BEAN = true;

    public interface OnDialogInternalListener {
        void dialogDismiss();
    }

    public Controller(Context context, CircleParams circleParams, OnDialogInternalListener onDialogInternalListener) {
        this.mContext = context;
        this.mParams = circleParams;
        this.mOnDialogListener = onDialogInternalListener;
    }

    public static int dp2px(Context context, float f) {
        return (int) (TypedValue.applyDimension(1, f, context.getResources().getDisplayMetrics()) + 0.5f);
    }

    public CircleViewHolder getViewHolder() {
        return this.mCircleViewHolder;
    }

    public View createView() {
        if (this.mParams.lottieParams != null) {
            BuildViewLottieImpl buildViewLottieImpl = new BuildViewLottieImpl(this.mContext, this.mParams);
            this.mCreateView = buildViewLottieImpl;
            buildViewLottieImpl.buildBodyView();
        } else if (this.mParams.bodyViewId != 0 || this.mParams.bodyView != null) {
            BuildViewCustomBodyImpl buildViewCustomBodyImpl = new BuildViewCustomBodyImpl(this.mContext, this.mParams);
            this.mCreateView = buildViewCustomBodyImpl;
            buildViewCustomBodyImpl.buildBodyView();
        } else if (this.mParams.adParams != null) {
            BuildViewAdImpl buildViewAdImpl = new BuildViewAdImpl(this.mContext, this.mParams);
            this.mCreateView = buildViewAdImpl;
            buildViewAdImpl.buildBodyView();
            ((AdView) this.mCreateView.getBodyView()).regOnImageClickListener(new OnAdItemClickListener() { // from class: com.mylhyl.circledialog.internal.Controller.1
                @Override // com.mylhyl.circledialog.view.listener.OnAdItemClickListener
                public boolean onItemClick(View view, int i) {
                    if (Controller.this.mParams.circleListeners.adItemClickListener != null && Controller.this.mParams.circleListeners.adItemClickListener.onItemClick(view, i) && !Controller.this.mParams.dialogParams.manualClose) {
                        Controller.this.mOnDialogListener.dialogDismiss();
                    }
                    return false;
                }
            });
        } else if (this.mParams.itemsParams != null) {
            DialogParams dialogParams = this.mParams.dialogParams;
            if (dialogParams.gravity == 0) {
                dialogParams.gravity = 80;
            }
            if (dialogParams.gravity == 80 && dialogParams.yOff == -1) {
                dialogParams.yOff = 20;
            }
            if (this.mParams.itemListViewType) {
                BuildViewItemsListViewImpl buildViewItemsListViewImpl = new BuildViewItemsListViewImpl(this.mContext, this.mParams);
                this.mCreateView = buildViewItemsListViewImpl;
                buildViewItemsListViewImpl.buildBodyView();
                ((ItemsView) this.mCreateView.getBodyView()).regOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: com.mylhyl.circledialog.internal.Controller.2
                    @Override // android.widget.AdapterView.OnItemClickListener
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                        if (Controller.this.mParams.circleListeners.itemListener == null || !Controller.this.mParams.circleListeners.itemListener.onItemClick(adapterView, view, i, j) || Controller.this.mParams.dialogParams.manualClose) {
                            return;
                        }
                        Controller.this.mOnDialogListener.dialogDismiss();
                    }
                });
            } else {
                BuildViewItemsRecyclerViewImpl buildViewItemsRecyclerViewImpl = new BuildViewItemsRecyclerViewImpl(this.mContext, this.mParams);
                this.mCreateView = buildViewItemsRecyclerViewImpl;
                buildViewItemsRecyclerViewImpl.buildBodyView();
                ((ItemsView) this.mCreateView.getBodyView()).regOnItemClickListener(new OnRvItemClickListener() { // from class: com.mylhyl.circledialog.internal.Controller.3
                    @Override // com.mylhyl.circledialog.view.listener.OnRvItemClickListener
                    public boolean onItemClick(View view, int i) {
                        if (Controller.this.mParams.circleListeners.rvItemListener != null && Controller.this.mParams.circleListeners.rvItemListener.onItemClick(view, i) && !Controller.this.mParams.dialogParams.manualClose) {
                            Controller.this.mOnDialogListener.dialogDismiss();
                        }
                        return false;
                    }
                });
            }
        } else if (this.mParams.progressParams != null) {
            BuildViewProgressImpl buildViewProgressImpl = new BuildViewProgressImpl(this.mContext, this.mParams);
            this.mCreateView = buildViewProgressImpl;
            buildViewProgressImpl.buildBodyView();
        } else if (this.mParams.inputParams != null) {
            BuildViewInputImpl buildViewInputImpl = new BuildViewInputImpl(this.mContext, this.mParams);
            this.mCreateView = buildViewInputImpl;
            buildViewInputImpl.buildBodyView();
        } else {
            BuildViewConfirmImpl buildViewConfirmImpl = new BuildViewConfirmImpl(this.mContext, this.mParams);
            this.mCreateView = buildViewConfirmImpl;
            buildViewConfirmImpl.buildBodyView();
        }
        if (this.mParams.closeParams != null) {
            this.mCreateView.buildCloseImgView().regOnCloseClickListener(new View.OnClickListener() { // from class: com.mylhyl.circledialog.internal.Controller.4
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    if (Controller.this.mParams.dialogParams.manualClose) {
                        return;
                    }
                    Controller.this.mOnDialogListener.dialogDismiss();
                }
            });
        }
        ButtonView buildButton = this.mCreateView.buildButton();
        regNegativeListener(buildButton);
        regNeutralListener(buildButton);
        if (this.mParams.inputParams != null) {
            regPositiveInputListener(buildButton, (InputView) this.mCreateView.getBodyView());
        } else if (this.mParams.bodyViewId != 0 || this.mParams.bodyView != null) {
            regPositiveBodyListener(buildButton);
        } else {
            regPositiveListener(buildButton);
        }
        return getView();
    }

    public void refreshView() {
        getView().post(new Runnable() { // from class: com.mylhyl.circledialog.internal.Controller.5
            @Override // java.lang.Runnable
            public void run() {
                Animation loadAnimation;
                Controller.this.mCreateView.refreshTitle();
                Controller.this.mCreateView.refreshContent();
                Controller.this.mCreateView.refreshButton();
                if (Controller.this.mParams.dialogParams.refreshAnimation == 0 || Controller.this.getView() == null || (loadAnimation = AnimationUtils.loadAnimation(Controller.this.mContext, Controller.this.mParams.dialogParams.refreshAnimation)) == null) {
                    return;
                }
                Controller.this.getView().startAnimation(loadAnimation);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public View getView() {
        if (this.mCreateView == null) {
            return null;
        }
        CircleViewHolder circleViewHolder = new CircleViewHolder(this.mCreateView.getRootView());
        this.mCircleViewHolder = circleViewHolder;
        return circleViewHolder.getDialogView();
    }

    private void regNegativeListener(final ButtonView buttonView) {
        buttonView.regNegativeListener(new View.OnClickListener() { // from class: com.mylhyl.circledialog.internal.Controller.6
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                buttonView.timerCancel();
                if (Controller.this.mParams.circleListeners.clickNegativeListener != null) {
                    Controller.this.mParams.circleListeners.clickNegativeListener.onClick(view);
                }
                if (Controller.this.mParams.dialogParams.manualClose) {
                    return;
                }
                Controller.this.mOnDialogListener.dialogDismiss();
            }
        });
    }

    private void regNeutralListener(ButtonView buttonView) {
        buttonView.regNeutralListener(new View.OnClickListener() { // from class: com.mylhyl.circledialog.internal.Controller.7
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (Controller.this.mParams.circleListeners.clickNeutralListener != null) {
                    Controller.this.mParams.circleListeners.clickNeutralListener.onClick(view);
                }
                if (Controller.this.mParams.dialogParams.manualClose) {
                    return;
                }
                Controller.this.mOnDialogListener.dialogDismiss();
            }
        });
    }

    private void regPositiveListener(final ButtonView buttonView) {
        buttonView.regPositiveListener(new View.OnClickListener() { // from class: com.mylhyl.circledialog.internal.Controller.8
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                buttonView.timerRestart();
                if (Controller.this.mParams.circleListeners.clickPositiveListener != null) {
                    Controller.this.mParams.circleListeners.clickPositiveListener.onClick(view);
                }
                if (Controller.this.mParams.dialogParams.manualClose) {
                    return;
                }
                Controller.this.mOnDialogListener.dialogDismiss();
            }
        });
    }

    private void regPositiveInputListener(final ButtonView buttonView, final InputView inputView) {
        buttonView.regPositiveListener(new View.OnClickListener() { // from class: com.mylhyl.circledialog.internal.Controller.9
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                buttonView.timerRestart();
                EditText input = inputView.getInput();
                String obj = input.getText().toString();
                if (Controller.this.mParams.circleListeners.inputListener == null || !Controller.this.mParams.circleListeners.inputListener.onClick(obj, input) || Controller.this.mParams.dialogParams.manualClose) {
                    return;
                }
                Controller.this.mOnDialogListener.dialogDismiss();
            }
        });
    }

    private void regPositiveBodyListener(ButtonView buttonView) {
        buttonView.regPositiveListener(new View.OnClickListener() { // from class: com.mylhyl.circledialog.internal.Controller.10
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (Controller.this.mParams.circleListeners.bindBodyViewCallback == null || !Controller.this.mParams.circleListeners.bindBodyViewCallback.onBindBodyView(Controller.this.mCircleViewHolder) || Controller.this.mParams.dialogParams.manualClose) {
                    return;
                }
                Controller.this.mOnDialogListener.dialogDismiss();
            }
        });
    }
}