package com.wy.ui.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.wy.ui.MarginDecoration;
import com.xzf.camera.R;

/* loaded from: classes.dex */
public class HelpWindow extends PopupWindow implements View.OnClickListener {
    private Context context;
    private OnItemClickListener mListener;
    View mPopView;
    private RecyclerView recyclerView;

    public interface OnItemClickListener {
        void setOnItemClick(View view);
    }

    public HelpWindow(Context context) {
        super(context);
        this.context = context;
        initView(context);
        setPopupWindow();
        setOnDismissListener(new PopupWindow.OnDismissListener() { // from class: com.wy.ui.view.HelpWindow.1
            @Override // android.widget.PopupWindow.OnDismissListener
            public void onDismiss() {
                HelpWindow.this.setBackGround(1.0f);
            }
        });
    }

    @Override // android.widget.PopupWindow
    public void showAtLocation(View view, int i, int i2, int i3) {
        super.showAtLocation(view, i, i2, i3);
        setBackGround(0.7f);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setBackGround(float f) {
        WindowManager.LayoutParams attributes = ((Activity) this.context).getWindow().getAttributes();
        attributes.alpha = f;
        ((Activity) this.context).getWindow().addFlags(2);
        ((Activity) this.context).getWindow().setAttributes(attributes);
    }

    private void initView(Context context) {
        View inflate = LayoutInflater.from(context).inflate(R.layout.window_help, (ViewGroup) null);
        this.mPopView = inflate;
        RecyclerView recyclerView = (RecyclerView) inflate.findViewById(R.id.recyclerView);
        this.recyclerView = recyclerView;
        recyclerView.addItemDecoration(new MarginDecoration(context));
        this.recyclerView.setHasFixedSize(true);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(context));
        this.recyclerView.setAdapter(new HelpAdapter(new String[]{context.getString(R.string.help_item_step1), context.getString(R.string.help_item_step2), context.getString(R.string.help_item_step3), context.getString(R.string.help_item_step4)}));
    }

    private void setPopupWindow() {
        setContentView(this.mPopView);
        setWidth(-2);
        setHeight(-2);
        setFocusable(true);
        setBackgroundDrawable(new ColorDrawable(0));
        this.mPopView.setOnTouchListener(new View.OnTouchListener() { // from class: com.wy.ui.view.HelpWindow.2
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        OnItemClickListener onItemClickListener = this.mListener;
        if (onItemClickListener != null) {
            onItemClickListener.setOnItemClick(view);
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mListener = onItemClickListener;
    }

    public class HelpAdapter extends RecyclerView.Adapter {
        String[] messages;

        public HelpAdapter(String[] strArr) {
            this.messages = strArr;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.window_help_item, viewGroup, false));
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            ((ViewHolder) viewHolder).tv_message.setText(this.messages[i]);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return this.messages.length;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            private TextView tv_message;

            public ViewHolder(View view) {
                super(view);
                this.tv_message = (TextView) view.findViewById(R.id.tv_message);
            }
        }
    }
}