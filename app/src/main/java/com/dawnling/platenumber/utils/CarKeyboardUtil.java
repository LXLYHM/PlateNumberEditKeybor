package com.dawnling.platenumber.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView.OnKeyboardActionListener;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dawnling.platenumber.R;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class CarKeyboardUtil {

    private final View keyBoardLayout;
    private Context mContext;
    private Activity mActivity;
    private BaseKeyboardView mKeyboardView;
    private EditText mEdit;
    /**
     * 省份简称键盘
     */
    private Keyboard provinceKeyboard;
    /**
     * 数字与大写字母键盘
     */
    private BaseKeyboard haveChineseKeyboar;

    /**
     * 软键盘切换判断
     */
    private boolean isChange = true;
    /**
     * 判定是否是中文的正则表达式 [\\u4e00-\\u9fa5]判断一个中文 [\\u4e00-\\u9fa5]+多个中文
     */
    private String reg = "[\\u4e00-\\u9fa5]";
    private final TextView tvCansel;

    public CarKeyboardUtil(Activity activity, ViewGroup rootView, EditText edit) {
        mActivity = activity;
        mContext = activity;
        mEdit = edit;
        init();

        provinceKeyboard = new Keyboard(mContext, R.xml.provice);//第一位 省份简称
        haveChineseKeyboar = new BaseKeyboard(mContext, R.xml.qwerty_have_chinese);//最后一位 数字+英文+港澳学  不允许o

        LayoutInflater inflater = LayoutInflater.from(mContext);
        keyBoardLayout = inflater.inflate(R.layout.layout_keybord, null);
        tvCansel = keyBoardLayout.findViewById(R.id.tvCansel);

        keyBoardLayout.setBackgroundColor(mActivity.getResources().getColor(R.color.colorwhite));
        initLayoutHeight((LinearLayout) keyBoardLayout);
        this.layoutView = keyBoardLayout;

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);//与父容器的左侧对齐
        keyBoardLayout.setLayoutParams(lp);//设置布局参数
        rootView.addView(keyBoardLayout);

        mKeyboardView = keyBoardLayout
                .findViewById(R.id.keyboard_view);
        mKeyboardView.setKeyboard(provinceKeyboard);
        mKeyboardView.setEnabled(true);
        mKeyboardView.setPreviewEnabled(false);
        mKeyboardView.setOnKeyboardActionListener(listener);

        mEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int len = s.toString().length();
                if(len >= 1 && len < 2){//第2位 允许o 不允许数字 港澳学
                    keyStyle(new int[]{28207, 28595, 23398, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57});
                }else if (len >= 2 && len <=5){//第3-6位 数字+英文不允许o 不允许港 澳 学
                    keyStyle(new int[]{28207, 28595, 23398, 79});
                }else {//第7-8位 数字+英文+港澳学  不允许o
                    keyStyle(new int[]{79});
                }
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });
    }

    //设置键盘灰色
    private void keyStyle(final int[] keyArray) {
        haveChineseKeyboar.setKeyStyle(new BaseKeyboard.KeyStyle() {
            @Override
            public Drawable getKeyBackound(Keyboard.Key key) {//设置按钮背景色 点击颜色
                if(key.iconPreview != null) {
                    for (int i = 0; i < keyArray.length; i++){
                        if (key.codes[0] == keyArray[i]) return key.iconPreview;
                    }
                    return ContextCompat.getDrawable(mContext, R.drawable.key_o_bg);
                } else  return ContextCompat.getDrawable(mContext, R.drawable.key_o_bg);
            }

            @Override
            public Float getKeyTextSize(Keyboard.Key key) {//设置字体大小
//                if(key.codes[0] == mContext.getResources().getInteger(R.integer.action_o)) {
//                    return convertSpToPixels(mContext, 15f);
//                }
                return convertSpToPixels(mContext, 15f);
            }

            @Override
            public Integer getKeyTextColor(Keyboard.Key key) {//设置字体颜色
//                if(key.codes[0] == mContext.getResources().getInteger(R.integer.action_o)) {
//                    return Color.WHITE;
//                }
                return null;
            }

            @Override
            public CharSequence getKeyLabel(Keyboard.Key key) {return null; }
        });
    }

    /**
     * 字体转换成sp
     */
    public float convertSpToPixels(Context context, float sp) {
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
        return px;
    }

    private View layoutView;
    class finishListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            hideKeyboard();
        }
    }

    public void initLayoutHeight(LinearLayout layoutView) {
        LinearLayout.LayoutParams keyboard_layoutlLayoutParams = (LinearLayout.LayoutParams) layoutView
                .getLayoutParams();
        tvCansel.setOnClickListener(new finishListener());
        if (keyboard_layoutlLayoutParams == null) {
            int height = (int) (mActivity.getResources().getDisplayMetrics().heightPixels * SIZE.KEYBOARY_H);
            layoutView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        } else {
            keyboard_layoutlLayoutParams.height = (int) (mActivity.getResources().getDisplayMetrics().heightPixels * SIZE.KEYBOARY_H);
        }

        LinearLayout.LayoutParams TopLayoutParams = (LinearLayout.LayoutParams) tvCansel
                .getLayoutParams();

        if (TopLayoutParams == null) {
            int height = (int) (mActivity.getResources().getDisplayMetrics().heightPixels * SIZE.KEYBOARY_T_H);
            tvCansel.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, height));
        } else {
            TopLayoutParams.height = (int) (mActivity.getResources().getDisplayMetrics().heightPixels * SIZE.KEYBOARY_T_H);
        }
    }

    private OnKeyboardActionListener listener = new OnKeyboardActionListener() {
        @Override
        public void swipeUp() {}

        @Override
        public void swipeRight() {}

        @Override
        public void swipeLeft() {}

        @Override
        public void swipeDown() {}

        @Override
        public void onText(CharSequence text) {}

        @Override
        public void onRelease(int primaryCode) {}

        @Override
        public void onPress(int primaryCode) {}

        @Override
        public void onKey(int primaryCode, int[] keyCodes) {
            Editable editable = mEdit.getText();
            int start = mEdit.getSelectionStart();
            if (primaryCode == -3) {// 回退
                if (editable != null && editable.length() > 0) {
                    //没有输入内容时软键盘重置为省份简称软键盘
                    if (editable.length() == 1)  changeKeyboard(false);
                    if (start > 0) editable.delete(start - 1, start);
                }
            } else {
                if(start == 1){//第2位 允许o 不允许数字 港澳学
                    if (primaryCode == 28207 || primaryCode == 28595 || primaryCode == 23398 || //港澳学
                            primaryCode == 48 || primaryCode == 49 || primaryCode == 50 || primaryCode == 51 ||
                            primaryCode == 52 || primaryCode == 53 || primaryCode == 54 || primaryCode == 55 ||
                            primaryCode == 56 || primaryCode == 57 ){ // 0-9数字
//                        keyStyle(new int[]{28207, 28595, 23398, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57});
                        return;
                    }
                }else if (start >= 2 && start <=5){//第3-6位 数字+英文不允许o 不允许港 澳 学
                    if (primaryCode == 28207 || primaryCode == 28595 || primaryCode == 23398 || //港澳学
                            primaryCode == 79 ){ //O字母
//                        keyStyle(new int[]{28207, 28595, 23398, 79});
                        return;
                    }
                }else {//第7-8位 数字+英文+港澳学  不允许o
                    if (primaryCode == 79){ //O字母
//                        keyStyle(new int[]{79});
                        return;
                    }
                }
                editable.insert(start, Character.toString((char) primaryCode)); //输入内容
                // 判断第一个字符是否是中文,是，则自动切换到数字软键盘
                if (mEdit.getText().toString().matches(reg)) {
                    changeKeyboard(true);
                }
            }
        }
    };

    /**
     * 指定切换软键盘 isnumber false表示要切换为省份简称软键盘 true表示要切换为数字软键盘
     */
    public void changeKeyboard(boolean isnumber) {
        if (isnumber)  mKeyboardView.setKeyboard(haveChineseKeyboar);
        else   mKeyboardView.setKeyboard(provinceKeyboard);
        isChange = !isChange;
    }

    /**
     * 软键盘展示状态
     */
    public boolean isShow() {
        return mKeyboardView.getVisibility() == View.VISIBLE;
    }

    /**
     * 软键盘展示
     */
    public void showKeyboard() {
        startAnimation();
    }

    /**
     * 软键盘隐藏
     */
    public void hideKeyboard() {
        stopAnimation();
    }

    /**
     * 禁掉系统软键盘
     */
    public void hideSoftInputMethod() {
        mActivity.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        int currentVersion = android.os.Build.VERSION.SDK_INT;
        String methodName = null;
        if (currentVersion >= 16) {// 4.2
            methodName = "setShowSoftInputOnFocus";
        } else if (currentVersion >= 14) {// 4.0
            methodName = "setSoftInputShownOnFocus";
        }
        if (methodName == null) {
            mEdit.setInputType(InputType.TYPE_NULL);
        } else {
            Class<EditText> cls = EditText.class;
            Method setShowSoftInputOnFocus;
            try {
                setShowSoftInputOnFocus = cls.getMethod(methodName,
                        boolean.class);
                setShowSoftInputOnFocus.setAccessible(true);
                setShowSoftInputOnFocus.invoke(mEdit, false);
            } catch (NoSuchMethodException e) {
                mEdit.setInputType(InputType.TYPE_NULL);
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 隐藏系统软键盘
     */
    public void hideSystemKeyBroad() {
        ((InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(mActivity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private Animation openAnimation, closeAnimation;
    private void init() {
        openAnimation = AnimationUtils.loadAnimation(mActivity, R.anim.anim_entry_from_bottom);
        closeAnimation = AnimationUtils.loadAnimation(mActivity, R.anim.anim_leave_from_bottom);
    }

    private void startAnimation() {
        tvCansel.setVisibility(View.VISIBLE);
        mKeyboardView.setVisibility(View.VISIBLE);
        if (openAnimation != null) {
            this.layoutView.startAnimation(openAnimation);
        }
    }

    public void stopAnimation() {
        if (closeAnimation != null) {
            this.layoutView.startAnimation(closeAnimation);
            closeAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation arg0) {}
                @Override
                public void onAnimationRepeat(Animation arg0) {}
                @Override
                public void onAnimationEnd(Animation arg0) {
                    tvCansel.setVisibility(View.GONE);
                    mKeyboardView.setVisibility(View.GONE);
                }
            });
        }
    }

}
