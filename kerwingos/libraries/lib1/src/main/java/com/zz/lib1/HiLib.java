package com.zz.lib1;

import android.app.Application;

import com.willy.ratingbar.ScaleRatingBar;

public class HiLib {

    public int getInt() {
        return 10000;
    }

    public int getD() {
        com.willy.ratingbar.BaseRatingBar bar = null;
        return 2000;
    }

    public int noExp() {
        com.willy.ratingbar.ScaleRatingBar scaleRatingBar = new ScaleRatingBar(
                new Application()
        );
        return scaleRatingBar.hashCode();
    }
}
