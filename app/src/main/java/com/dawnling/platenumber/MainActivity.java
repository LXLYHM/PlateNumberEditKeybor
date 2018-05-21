package com.dawnling.platenumber;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.dawnling.platenumber.utils.CarKeyboardUtil;

/**
 * 车牌号输入框  键盘
 * http://my.csdn.net/lxlyhm
 * https://github.com/LXLYHM
 * http://www.jianshu.com/u/8fd63a0d4c4c
 * Created by dawnling on 2018-5-20.
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener{

    private EditText etPlateNumber;
    private CarKeyboardUtil keyboardUtil;
    private ImageView imgIcon;//新能源icon 车牌号8位显示  默认隐藏
    private Button btnSubmit;
    private RelativeLayout rlPlateNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etPlateNumber = findViewById(R.id.etPlateNumber);
        keyboardUtil = new CarKeyboardUtil(this, etPlateNumber);
        imgIcon = findViewById(R.id.imgIcon);
        btnSubmit = findViewById(R.id.btnSubmit);
        rlPlateNumber = findViewById(R.id.rlPlateNumber);
        btnSubmit.setEnabled(false);
        etPlateNumber.setOnTouchListener(this);

        etPlateNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString();
                checkViewState(text);
                if (s.length() == 8){
                    rlPlateNumber.setBackgroundResource(R.drawable.btn_round_blue);
                    imgIcon.setVisibility(View.VISIBLE);
                }else{
                    rlPlateNumber.setBackgroundResource(R.drawable.btn_round_green);
                    imgIcon.setVisibility(View.GONE);
                }
                if (text.contains("港") || text.contains("澳") || text.contains("学") ){
                    etPlateNumber.setFilters(new InputFilter[]{new InputFilter.LengthFilter(7)});
                }else{
                    etPlateNumber.setFilters(new InputFilter[]{new InputFilter.LengthFilter(8)});
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        btnSubmit.setOnClickListener(this);
    }

    /**
     * 检查状态
     */
    private void checkViewState(String s) {
        if (s.length() >= 7) {
            btnSubmit.setEnabled(true);
            return;
        }
        btnSubmit.setEnabled(false);
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        switch (view.getId()) {
            case R.id.etPlateNumber:
                keyboardUtil.hideSystemKeyBroad();
                keyboardUtil.hideSoftInputMethod();
                if (!keyboardUtil.isShow())
                    keyboardUtil.showKeyboard();
                break;
            default:
                if (keyboardUtil.isShow())
                    keyboardUtil.hideKeyboard();
                break;
        }

        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (keyboardUtil.isShow()) {
            keyboardUtil.hideKeyboard();
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnSubmit://添加车牌
                etPlateNumber.setText("");
                btnSubmit.setEnabled(false);
                break;
        }
    }
}
