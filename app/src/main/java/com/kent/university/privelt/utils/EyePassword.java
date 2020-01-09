package com.kent.university.privelt.utils;

import android.annotation.SuppressLint;
import android.media.Image;
import android.text.InputType;
import android.util.Pair;
import android.view.MotionEvent;
import android.widget.EditText;
import android.widget.ImageView;

public class EyePassword {

    @SuppressLint("ClickableViewAccessibility")
    public static void configureEye(Pair<ImageView, EditText>... imageViews) {
        for (Pair<ImageView, EditText> pair : imageViews)
            pair.first.setOnTouchListener((v, event) -> showEye(event, pair.second));
    }

    private static boolean showEye(MotionEvent event, EditText editText) {
        if (editText.getText().toString().isEmpty())
            return false;

        switch (event.getAction() ) {
            case MotionEvent.ACTION_DOWN:
                editText.setInputType(InputType.TYPE_CLASS_TEXT);
                break;
            case MotionEvent.ACTION_UP:
                editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                break;
        }
        return true;
    }
}
