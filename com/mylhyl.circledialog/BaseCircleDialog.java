package com.mylhyl.circledialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.mylhyl.circledialog.internal.CircleParams;
import com.mylhyl.circledialog.internal.Controller;
import com.mylhyl.circledialog.params.DialogParams;
import java.lang.reflect.Field;

/* loaded from: classes.dex */
public final class BaseCircleDialog extends AbsBaseCircleDialog implements DialogInterface.OnShowListener, Controller.OnDialogInternalListener {
    private static final String CIRCLE_DIALOG = "circleDialog";
    private static final String SAVED_PARAMS = "circle:params";
    private Controller mController;
    private CircleParams mParams;

    public static BaseCircleDialog newAbsCircleDialog(CircleParams circleParams) {
        BaseCircleDialog baseCircleDialog = new BaseCircleDialog();
        baseCircleDialog.mParams = circleParams;
        Bundle bundle = new Bundle();
        bundle.putParcelable(SAVED_PARAMS, circleParams);
        baseCircleDialog.setArguments(bundle);
        return baseCircleDialog;
    }

    @Override // com.mylhyl.circledialog.AbsBaseCircleDialog, androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (bundle != null) {
            this.mParams = (CircleParams) bundle.getParcelable(SAVED_PARAMS);
        }
    }

    @Override // com.mylhyl.circledialog.AbsBaseCircleDialog, androidx.fragment.app.DialogFragment, android.content.DialogInterface.OnDismissListener
    public void onDismiss(DialogInterface dialogInterface) {
        super.onDismiss(dialogInterface);
        CircleParams circleParams = this.mParams;
        if (circleParams != null) {
            if (circleParams.circleListeners.dismissListener != null) {
                this.mParams.circleListeners.dismissListener.onDismiss(dialogInterface);
            }
            if (this.mParams.circleListeners.cancelListener != null) {
                this.mParams.circleListeners.cancelListener.onCancel(dialogInterface);
            }
        }
        this.mController = null;
    }

    @Override // com.mylhyl.circledialog.AbsBaseCircleDialog, androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putParcelable(SAVED_PARAMS, this.mParams);
    }

    @Override // com.mylhyl.circledialog.AbsBaseCircleDialog, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        DialogParams dialogParams = this.mParams.dialogParams;
        setGravity(dialogParams.gravity);
        setCanceledOnTouchOutside(dialogParams.canceledOnTouchOutside);
        setCancelable(dialogParams.cancelable);
        setWidth(dialogParams.width);
        setMaxHeight(dialogParams.maxHeight);
        int[] iArr = dialogParams.mPadding;
        if (iArr != null) {
            setPadding(iArr[0], iArr[1], iArr[2], iArr[3]);
        }
        setAnimations(dialogParams.animStyle);
        setDimEnabled(dialogParams.isDimEnabled);
        setDimAmount(dialogParams.dimAmount);
        setRadius(dialogParams.radius);
        setAlpha(dialogParams.alpha);
        setX(dialogParams.xOff);
        setY(dialogParams.yOff);
        CircleParams circleParams = this.mParams;
        if (circleParams != null && circleParams.inputParams != null && this.mParams.inputParams.showSoftKeyboard && this.mController != null) {
            setSoftInputMode();
        }
        setSystemUiVisibility(dialogParams.systemUiVisibility);
        super.onViewCreated(view, bundle);
    }

    @Override // com.mylhyl.circledialog.AbsBaseCircleDialog
    public View createView(Context context, LayoutInflater layoutInflater, ViewGroup viewGroup) {
        Controller controller = new Controller(context, this.mParams, this);
        this.mController = controller;
        return controller.createView();
    }

    public void show(FragmentManager fragmentManager) {
        show(fragmentManager, CIRCLE_DIALOG);
    }

    @Override // androidx.fragment.app.DialogFragment
    public void show(FragmentManager fragmentManager, String str) {
        try {
            Class<? super Object> superclass = getClass().getSuperclass().getSuperclass();
            Field declaredField = superclass.getDeclaredField("mDismissed");
            declaredField.setAccessible(true);
            declaredField.set(this, false);
            Field declaredField2 = superclass.getDeclaredField("mShownByMe");
            declaredField2.setAccessible(true);
            declaredField2.set(this, true);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e2) {
            e2.printStackTrace();
        }
        FragmentTransaction beginTransaction = fragmentManager.beginTransaction();
        if (isVisible() && isAdded()) {
            beginTransaction.remove(this).commitAllowingStateLoss();
        }
        beginTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        beginTransaction.add(this, str);
        beginTransaction.commitAllowingStateLoss();
    }

    @Override // androidx.fragment.app.DialogFragment
    public void dismiss() {
        dialogDismiss();
    }

    @Override // androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        Dialog dialog = getDialog();
        if (dialog == null || this.mParams == null) {
            return;
        }
        dialog.setOnShowListener(this);
        dialog.setOnKeyListener(this.mParams.circleListeners.keyListener);
        if (this.mParams.circleListeners.createBodyViewListener != null) {
            this.mParams.circleListeners.createBodyViewListener.onCreateBodyView(this.mController.getViewHolder());
        }
    }

    @Override // android.content.DialogInterface.OnShowListener
    public void onShow(DialogInterface dialogInterface) {
        CircleParams circleParams = this.mParams;
        if (circleParams == null || circleParams.circleListeners.showListener == null) {
            return;
        }
        this.mParams.circleListeners.showListener.onShow(dialogInterface, this.mController.getViewHolder());
    }

    public void refreshView() {
        Controller controller = this.mController;
        if (controller != null) {
            controller.refreshView();
        }
    }

    @Override // com.mylhyl.circledialog.internal.Controller.OnDialogInternalListener
    public void dialogDismiss() {
        dismissAllowingStateLoss();
    }
}