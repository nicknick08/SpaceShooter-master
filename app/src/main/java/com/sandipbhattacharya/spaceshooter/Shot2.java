package com.sandipbhattacharya.spaceshooter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Shot2 {
    Bitmap shot;
    Context context;
    int shx, shy;

    public Shot2(Context context, int shx, int shy) {
        this.context = context;
        shot = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.shot2);
        this.shx = shx;
        this.shy = shy;
    }
    public Bitmap getShot(){
        return shot;
    }
    public int getShotWidth() {
        return shot.getWidth();
    }
    public int getShotHeight() {
        return shot.getHeight();
    }
}
