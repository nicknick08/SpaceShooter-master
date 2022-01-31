package com.sandipbhattacharya.spaceshooter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.Random;

public class EnemyBat {
    Context context;
    Bitmap enemyBat;
    int ex, ey;
    int enemyVelocity;
    Random random;

    public EnemyBat(Context context){
        this.context = context;
        enemyBat = BitmapFactory.decodeResource(context.getResources(), R.drawable.bat);
        random = new Random();
        ex = 200 + random.nextInt(400);
        ey = 0;
        enemyVelocity = 14 + random.nextInt(10);
    }


    public Bitmap getEnemyBat(){
        return enemyBat;
    }

    int getEnemyBatWidth(){
        return enemyBat.getWidth();
    }

    int getEnemyBatHeight(){
        return enemyBat.getHeight();
    }
}