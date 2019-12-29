package com.kent.university.privelt.ui.master_password;


import android.graphics.Color;

import com.kent.university.privelt.R;

enum PasswordStrength {

    WEAK(R.string.weak, 20, Color.LTGRAY),
    FAIR(R.string.fair, 20, Color.YELLOW),
    GOOD(R.string.good, 60, Color.GREEN),
    STRONG(R.string.strong, 80, Color.BLUE),
    VERY_STRONG(R.string.very_strong, 100, Color.WHITE);

    private final int resId;
    private final int color;
    private final int progress;

    PasswordStrength(int resId, int progress, int color) {
        this.resId = resId;
        this.progress = progress;
        this.color = color;
    }

    public int getResId() {
        return resId;
    }

    public int getColor() {
        return color;
    }

    public int getProgress() {
        return progress;
    }
}
