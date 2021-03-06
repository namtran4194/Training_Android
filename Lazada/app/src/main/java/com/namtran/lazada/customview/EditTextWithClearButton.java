package com.namtran.lazada.customview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.namtran.lazada.R;

/**
 * Created by namtr on 01/05/2017.
 */

public class EditTextWithClearButton extends TextInputEditText {
    private Drawable cross, nonCross;
    private boolean visible = false;

    public EditTextWithClearButton(Context context) {
        super(context);
        init();
    }

    public EditTextWithClearButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public EditTextWithClearButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        cross = ContextCompat.getDrawable(getContext(), R.drawable.ic_close_black_24dp).mutate();
        nonCross = ContextCompat.getDrawable(getContext(), android.R.drawable.screen_background_light_transparent).mutate();

        setup();
    }

    // gán hình vào bên phải của TextInputEditText
    private void setup() {
        Drawable[] drawables = getCompoundDrawables();
        Drawable drawable = visible ? cross : nonCross;
        setCompoundDrawablesWithIntrinsicBounds(drawables[0], drawables[1], drawable, drawables[3]);
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        visible = !(lengthAfter == 0);
        setup();
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // xóa EditText khi nhấn vào dấu "x"
        if (event.getAction() == MotionEvent.ACTION_UP && event.getX() >= getRight() - getCompoundDrawables()[2].getBounds().width()) {
            setText("");
        }
        return super.onTouchEvent(event);
    }
}
