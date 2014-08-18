package com.naosim.windowmanagersample;

import android.content.Context;
import android.view.View;
import android.view.WindowManager;

/**
* Created by fujitanao on 2014/08/18.
*/
public class WindowOverlay {
    private final View view;
    private final WindowManager wm;
    private final WindowManager.LayoutParams params;
    private boolean isVisible;

    protected final Context context;
    private VisibleListener visibleListener;


    WindowOverlay(Context context, View view, WindowManager.LayoutParams params, VisibleListener l) {
        this.context = context;
        this.wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        this.view = view;
        this.params = params;
        this.visibleListener = l;
    }

    public void show() {
        if(isVisible) return;

        wm.addView(view, params);
        if(visibleListener != null) visibleListener.onShow(this);
        isVisible = true;
    }

    public void dismiss() {
        if(!isVisible) return;
        
        wm.removeView(view);
        if(visibleListener != null) visibleListener.onDismiss(this);
        isVisible = false;
    }

    public interface VisibleListener {
        void onShow(WindowOverlay wo);
        void onDismiss(WindowOverlay wo);
    }

    public static class Builder {
        private final Context context;
        private View view;
        private WindowManager.LayoutParams params;
        private VisibleListener visibleListener;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setView(View view) {
            this.view = view;
            return this;
        }

        public Builder setLayoutParams(WindowManager.LayoutParams params) {
            this.params = params;
            return this;
        }

        public Builder setVisibleListener(VisibleListener l) {
            this.visibleListener = l;
            return this;
        }

        public WindowOverlay create() {
            return new WindowOverlay(context, view, params, visibleListener);
        }

        public WindowOverlay show() {
            WindowOverlay result = create();
            result.show();
            return result;
        }
    }
}
