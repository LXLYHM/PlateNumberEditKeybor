package com.dawnling.platenumber.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.support.annotation.IntegerRes;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;

/**
 * Created by dawnling on 2019/3/27.
 */

public class BaseKeyboard extends Keyboard implements KeyboardView.OnKeyboardActionListener{

    private EditText mEditText;

    private View mNextFocusView;

    private KeyStyle mKeyStyle;

    protected Context mContext;

    public BaseKeyboard(Context context, int xmlLayoutResId) {
        super(context, xmlLayoutResId);
        mContext = context;
    }

    public BaseKeyboard(Context context, int xmlLayoutResId, int modeId, int width, int height) {
        super(context, xmlLayoutResId, modeId, width, height);
        mContext = context;
    }

    public BaseKeyboard(Context context, int xmlLayoutResId, int modeId) {
        super(context, xmlLayoutResId, modeId);
        mContext = context;
    }

    public BaseKeyboard(Context context, int layoutTemplateResId, CharSequence characters, int columns, int horizontalPadding) {
        super(context, layoutTemplateResId, characters, columns, horizontalPadding);
        mContext = context;
    }

    public void setEditText(EditText editText) {
        mEditText = editText;
    }

    public void setNextFocusView(View nextFocusView) {
        mNextFocusView = nextFocusView;
    }

    public void setKeyStyle(KeyStyle keyStyle) {
        mKeyStyle = keyStyle;
    }

    public EditText getEditText() {
        return mEditText;
    }

    public View getNextFocusView() {
        return mNextFocusView;
    }

    public KeyStyle getKeyStyle() {
        return mKeyStyle;
    }

    public int getKeyCode(@IntegerRes int redId) {
        return mContext.getResources().getInteger(redId);
    }

    @Override
    public void onPress(int primaryCode) {
    }

    @Override
    public void onRelease(int primaryCode) {
    }

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {}

    @Override
    public void onText(CharSequence text) {
    }

    @Override
    public void swipeLeft() {
    }

    @Override
    public void swipeRight() {
    }

    @Override
    public void swipeDown() {
    }

    @Override
    public void swipeUp() {
    }

    public void hideKeyboard() {
       if(mNextFocusView != null) {
           mNextFocusView.requestFocus();
       }
    }

    public interface KeyStyle {

        public Drawable getKeyBackound(Key key);

        public Float getKeyTextSize(Key key);

        public Integer getKeyTextColor(Key key);

        public CharSequence getKeyLabel(Key key);
    }

    public Padding getPadding() {
        return new Padding(0,0,0,0);
    }

    public static class DefaultKeyStyle implements KeyStyle {

        @Override
        public Drawable getKeyBackound(Key key) {
            return key.iconPreview;
        }

        @Override
        public Float getKeyTextSize(Key key) {
            return null;
        }

        @Override
        public Integer getKeyTextColor(Key key) {
            return null;
        }

        @Override
        public CharSequence getKeyLabel(Key key) {
            return key.label;
        }
    }

    public static class Padding {
        int top;
        int left;
        int bottom;
        int right;

        /**
         * px
         * @param top
         * @param left
         * @param bottom
         * @param right
         */
        public Padding(int top, int left, int bottom, int right) {
            this.top = top;
            this.left = left;
            this.bottom = bottom;
            this.right = right;
        }
    }

    public float convertSpToPixels(Context context, float sp) {
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
        return px;
    }
}
