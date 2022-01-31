package com.sandipbhattacharya.spaceshooter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.SoundPool;
import android.os.Handler;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Random;

public class SpaceShooter extends View {
    Context context;
    Bitmap background, lifeImage;
    Handler handler;
    private static SoundPool soundPool;
    private static int explosionSound;
    private static int shootingSound;
    long UPDATE_MILLIS = 30;
    static int screenWidth, screenHeight;
    int []batMove = {0,30,60,90,120,150,180,210,250,280,310,330,360,330,310,280,250,210,180,150,120,90,60,30,0};
    int []batShot = {1,0,0,1,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
    int points = 0;
    int life = 3;
    int batIndex =0;
    Paint scorePaint;
    int TEXT_SIZE = 80;
    boolean paused = false;
    OurSpaceship ourSpaceship;
    EnemySpaceship enemySpaceship;
    EnemyBat enemyBat;
    Random random;
    ArrayList<Shot> enemyShots, ourShots;
    ArrayList<Shot1> enemyShots1;
    ArrayList<Shot2> enemyShots2;
    Explosion explosion;
    ArrayList<Explosion> explosions;
    boolean enemyShotAction = false;
    int enemyBatShotAction = 0;
    final Runnable runnable = new Runnable() {
        @Override
        public void run() {
           invalidate();
        }
    };



    public SpaceShooter(Context context) {
        super(context);
//        soundPool = new SoundPool(2,Audio)
        this.context = context;
        Display display = ((Activity) getContext()).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;
        random = new Random();
        enemyShots = new ArrayList<>();
        enemyShots1 = new ArrayList<>();
        enemyShots2 = new ArrayList<>();
        ourShots = new ArrayList<>();
        explosions = new ArrayList<>();
        ourSpaceship = new OurSpaceship(context);
        enemySpaceship = new EnemySpaceship(context);
        enemyBat = new EnemyBat(context);
        handler = new Handler();
        background = BitmapFactory.decodeResource(context.getResources(), R.drawable.background);
        lifeImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.life);
        scorePaint = new Paint();
        scorePaint.setColor(Color.RED);
        scorePaint.setTextSize(TEXT_SIZE);
        scorePaint.setTextAlign(Paint.Align.LEFT);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // Draw background, Points and life on Canvas
        canvas.drawBitmap(background, 0, 0, null);
        canvas.drawText("Pt: " + points, 0, TEXT_SIZE, scorePaint);
        for(int i=life; i>=1; i--){
            canvas.drawBitmap(lifeImage, screenWidth - lifeImage.getWidth() * i, 0, null);
        }
        // When life becomes 0, stop game and launch GameOver Activity with points
        if(life == 0){
            paused = true;
            handler = null;
            Intent intent = new Intent(context, GameOver.class);
            intent.putExtra("points", points);
            context.startActivity(intent);
            ((Activity) context).finish();
        }
        // Move enemySpaceship
        enemySpaceship.ex += enemySpaceship.enemyVelocity;
        // If enemySpaceship collides with right wall, reverse enemyVelocity
        if(enemySpaceship.ex + enemySpaceship.getEnemySpaceshipWidth() >= screenWidth){
            enemySpaceship.enemyVelocity *= -1;
        }
        // If enemySpaceship collides with left wall, again reverse enemyVelocity
        if(enemySpaceship.ex <=0){
            enemySpaceship.enemyVelocity *= -1;
        }
        // Till enemyShotAction is false, enemy should fire shots from random travelled distance
        if(enemyShotAction == false){
            if(enemySpaceship.ex >= 200 + random.nextInt(400)){
                Shot enemyShot = new Shot(context, enemySpaceship.ex + enemySpaceship.getEnemySpaceshipWidth() / 2, enemySpaceship.ey );
                enemyShots.add(enemyShot);
                // We're making enemyShotAction to true so that enemy can take a short at a time
                enemyShotAction = true;
            }
            if(enemySpaceship.ex >= 400 + random.nextInt(800)){
                Shot enemyShot = new Shot(context, enemySpaceship.ex + enemySpaceship.getEnemySpaceshipWidth() / 2, enemySpaceship.ey );
                enemyShots.add(enemyShot);
                // We're making enemyShotAction to true so that enemy can take a short at a time
                enemyShotAction = true;
            }
            else{
                Shot enemyShot = new Shot(context, enemySpaceship.ex + enemySpaceship.getEnemySpaceshipWidth() / 2, enemySpaceship.ey );
                enemyShots.add(enemyShot);
                // We're making enemyShotAction to true so that enemy can take a short at a time
                enemyShotAction = true;
            }
        }
        // Draw the enemy Spaceship
        canvas.drawBitmap(enemySpaceship.getEnemySpaceship(), enemySpaceship.ex, enemySpaceship.ey, null);



        // Draw our spaceship between the left and right edge of the screen
        if(ourSpaceship.ox > screenWidth - ourSpaceship.getOurSpaceshipWidth()){
            ourSpaceship.ox = screenWidth - ourSpaceship.getOurSpaceshipWidth();
        }else if(ourSpaceship.ox < 0){
            ourSpaceship.ox = 0;
        }
        // Draw our Spaceship
        canvas.drawBitmap(ourSpaceship.getOurSpaceship(), ourSpaceship.ox, ourSpaceship.oy, null);
        // Draw the enemy shot downwards our spaceship and if it's being hit, decrement life, remove
        // the shot object from enemyShots ArrayList and show an explosion.
        // Else if, it goes away through the bottom edge of the screen also remove
        // the shot object from enemyShots.
        // When there is no enemyShots no the screen, change enemyShotAction to false, so that enemy
        // can shot.
        for(int i=0; i < enemyShots.size(); i++){
            enemyShots.get(i).shy += 15;
//            enemyShots.get(i).shx += 10;
            canvas.drawBitmap(enemyShots.get(i).getShot(), enemyShots.get(i).shx, enemyShots.get(i).shy, null);
            if((enemyShots.get(i).shx >= ourSpaceship.ox)
                && enemyShots.get(i).shx <= ourSpaceship.ox + ourSpaceship.getOurSpaceshipWidth()
                && enemyShots.get(i).shy >= ourSpaceship.oy
                && enemyShots.get(i).shy <= screenHeight){
                life--;
                enemyShots.remove(i);
                explosion = new Explosion(context, ourSpaceship.ox, ourSpaceship.oy);
                explosions.add(explosion);
            }else if(enemyShots.get(i).shy >= screenHeight){
                enemyShots.remove(i);
            }
            if(enemyShots.size() < 1){
                enemyShotAction = false;
            }
        }

        // Draw the enemy Bat
        canvas.drawBitmap(enemyBat.getEnemyBat(), enemyBat.ex, enemyBat.ey, null);

        // Move enemyBat
        enemyBat.ex += enemyBat.enemyVelocity;
        // Move enemyBat as V movement
        enemyBat.ey=batMove[batIndex++% batMove.length];

        // If enemyBat collides with right wall, reverse enemyVelocity
        if(enemyBat.ex + enemyBat.getEnemyBatWidth() >= screenWidth){
            enemyBat.enemyVelocity *= -1;
        }
        // If enemyBat collides with left wall, again reverse enemyVelocity
        if(enemyBat.ex <=0){
            enemyBat.enemyVelocity *= -1;
        }

        //making the enemyBat shooting
        enemyBatShotAction=batShot[batIndex++% batShot.length];
        if(enemyBatShotAction==1){
            Shot1 enemyShot1 = new Shot1(context, enemyBat.ex + enemyBat.getEnemyBatWidth() / 2, enemyBat.ey );

            enemyShots1.add(enemyShot1);
            Shot2 enemyShot2 = new Shot2(context, enemyBat.ex + enemyBat.getEnemyBatWidth() / 2, enemyBat.ey );
            enemyShots2.add(enemyShot2);

        }


        // Draw the enemy shot downwards and right our spaceship and if it's being hit, decrement life, remove
        // the shot object from enemyShots ArrayList and show an explosion.
        // Else if, it goes away through the bottom edge of the screen also remove
        // the shot object from enemyShots.
        // When there is no enemyShots no the screen, change enemyShotAction to false, so that enemy
        // can shot.
        for(int i=0; i < enemyShots1.size(); i++){
            enemyShots1.get(i).shy += 15;
            enemyShots1.get(i).shx += 5;
            canvas.drawBitmap(enemyShots1.get(i).getShot(), enemyShots1.get(i).shx, enemyShots1.get(i).shy, null);
            if((enemyShots1.get(i).shx >= ourSpaceship.ox)
                    && enemyShots1.get(i).shx <= ourSpaceship.ox + ourSpaceship.getOurSpaceshipWidth()
                    && enemyShots1.get(i).shy >= ourSpaceship.oy
                    && enemyShots1.get(i).shy <= screenHeight){
                life--;
                enemyShots1.remove(i);
                explosion = new Explosion(context, ourSpaceship.ox, ourSpaceship.oy);
                explosions.add(explosion);
            }else if(enemyShots1.get(i).shy >= screenHeight){
                enemyShots1.remove(i);
            }
            if(enemyShots1.size() < 1){
                enemyShotAction = false;
            }
        }
        // Draw the enemy shot downwards and left our spaceship and if it's being hit, decrement life, remove
        // the shot object from enemyShots ArrayList and show an explosion.
        // Else if, it goes away through the bottom edge of the screen also remove
        // the shot object from enemyShots.
        // When there is no enemyShots no the screen, change enemyShotAction to false, so that enemy
        // can shot.
        for(int i=0; i < enemyShots2.size(); i++){
            enemyShots2.get(i).shy += 15;
            enemyShots2.get(i).shx -= 5;
            canvas.drawBitmap(enemyShots2.get(i).getShot(), enemyShots2.get(i).shx, enemyShots2.get(i).shy, null);
            if((enemyShots2.get(i).shx >= ourSpaceship.ox)
                    && enemyShots2.get(i).shx <= ourSpaceship.ox + ourSpaceship.getOurSpaceshipWidth()
                    && enemyShots2.get(i).shy >= ourSpaceship.oy
                    && enemyShots2.get(i).shy <= screenHeight){
                life--;
                enemyShots2.remove(i);
                explosion = new Explosion(context, ourSpaceship.ox, ourSpaceship.oy-150);
                explosions.add(explosion);
            }else if(enemyShots2.get(i).shy >= screenHeight){
                enemyShots2.remove(i);
            }
            if(enemyShots2.size() < 1){
                enemyShotAction = false;
            }
        }
//        makeing the point increase with time
        points++;

        // Draw our spaceship shots towards the enemy. If there is a collision between our shot and enemy
        // spaceship, increment points, remove the shot from ourShots and create a new Explosion object.
        // Else if, our shot goes away through the top edge of the screen also remove
        // the shot object from enemyShots ArrayList.
        for(int i=0; i < ourShots.size(); i++){
            ourShots.get(i).shy -= 15;
            canvas.drawBitmap(ourShots.get(i).getShot(), ourShots.get(i).shx, ourShots.get(i).shy, null);
            if((ourShots.get(i).shx >= enemySpaceship.ex)
               && ourShots.get(i).shx <= enemySpaceship.ex + enemySpaceship.getEnemySpaceshipWidth()
               && ourShots.get(i).shy <= enemySpaceship.getEnemySpaceshipWidth()
               && ourShots.get(i).shy >= enemySpaceship.ey){
                points++;
                ourShots.remove(i);
                explosion = new Explosion(context, enemySpaceship.ex, enemySpaceship.ey);
                explosions.add(explosion);
            }else if(ourShots.get(i).shy <=0){
                ourShots.remove(i);
            }
        }
        // Do the explosion
        for(int i=0; i < explosions.size(); i++){
            canvas.drawBitmap(explosions.get(i).getExplosion(explosions.get(i).explosionFrame), explosions.get(i).eX, explosions.get(i).eY, null);
            explosions.get(i).explosionFrame++;
            if(explosions.get(i).explosionFrame > 8){
                explosions.remove(i);
            }
        }
        // If not paused, we’ll call the postDelayed() method on handler object which will cause the
        // run method inside Runnable to be executed after 30 milliseconds, that is the value inside
        // UPDATE_MILLIS.
        if(!paused)
            handler.postDelayed(runnable, UPDATE_MILLIS);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int touchX = (int)event.getX();
//         When event.getAction() is MotionEvent.ACTION_UP, if ourShots arraylist size < 1,
        // create a new Shot.
        // This way we restrict ourselves of making just one shot at a time, on the screen.
        if(event.getAction() == MotionEvent.ACTION_UP){
            if(ourShots.size() < 1){
                Shot ourShot = new Shot(context, ourSpaceship.ox + ourSpaceship.getOurSpaceshipWidth() / 2, ourSpaceship.oy);
                ourShots.add(ourShot);
            }
        }
        // When event.getAction() is MotionEvent.ACTION_DOWN, control ourSpaceship
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            ourSpaceship.ox = touchX;
        }
        // When event.getAction() is MotionEvent.ACTION_MOVE, control ourSpaceship
        // along with the touch.
        if(event.getAction() == MotionEvent.ACTION_MOVE){
            ourSpaceship.ox = touchX;
        }
        // Returning true in an onTouchEvent() tells Android system that you already handled
        // the touch event and no further handling is required.
        return true;
    }
}
