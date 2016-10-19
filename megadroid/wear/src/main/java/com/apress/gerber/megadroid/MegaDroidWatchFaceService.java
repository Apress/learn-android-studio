package com.apress.gerber.megadroid;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.text.format.Time;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceHolder;

import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * Created by Clifton
 * Copyright 2/2/2015.
 */
public class MegaDroidWatchFaceService extends CanvasWatchFaceService {
    private static final String TAG = "MegaDroidWatchSvc";

    @Override
    public Engine onCreateEngine() {
        // create and return the watch face engine
        return new MegaDroidEngine(this);
    }

    private static final long INTERACTIVE_UPDATE_RATE_MS = TimeUnit.SECONDS.toMillis(1);
    private static final int MSG_UPDATE_TIME = 0;

    /* implement service callback methods */
    private class MegaDroidEngine extends CanvasWatchFaceService.Engine {

        private final Service service;
        private Bitmap backgroundBitmap;
        private Bitmap logo;
        private Bitmap character;
        private Bitmap minuteHand;
        private Bitmap hourHand;
        private Time time;
        private Paint secondPaint;
        private Bitmap backgroundScaledBitmap;

        /** Handler to update the time once a second in interactive mode. */
        final Handler mUpdateTimeHandler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                switch (message.what) {
                    case MSG_UPDATE_TIME:
                        if (Log.isLoggable(TAG, Log.VERBOSE)) {
                            Log.v(TAG, "updating time");
                        }
                        invalidate();
                        if (shouldTimerBeRunning()) {
                            long timeMs = System.currentTimeMillis();
                            long delayMs = INTERACTIVE_UPDATE_RATE_MS
                                    - (timeMs % INTERACTIVE_UPDATE_RATE_MS);
                            mUpdateTimeHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIME, delayMs);
                        }
                        break;
                }
            }
        };
        private boolean lowBitAmbient;

        private boolean shouldTimerBeRunning() {
            return isVisible() && !isInAmbientMode();
        }

        public MegaDroidEngine(Service service) {
            this.service = service;
        }

        /**
         * initialize your watch face
         */
        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);
            setWatchFaceStyle(new WatchFaceStyle.Builder(service)
                    .setCardPeekMode(WatchFaceStyle.PEEK_MODE_SHORT)
                    .setStatusBarGravity(Gravity.RIGHT | Gravity.TOP)
                    .setHotwordIndicatorGravity(Gravity.LEFT | Gravity.TOP)
                    .setBackgroundVisibility(WatchFaceStyle.BACKGROUND_VISIBILITY_INTERRUPTIVE)
                    .setShowSystemUiTime(false)
                    .build());

            Resources resources = service.getResources();
            Drawable backgroundDrawable = resources.getDrawable(R.drawable.bg);
            this.backgroundBitmap = ((BitmapDrawable) backgroundDrawable).getBitmap();
            this.character = ((BitmapDrawable) resources.getDrawable(R.drawable.character_standing)).getBitmap();
            this.logo = ((BitmapDrawable) resources.getDrawable(R.drawable.megadroid_logo)).getBitmap();
            this.minuteHand = ((BitmapDrawable) resources.getDrawable(R.drawable.minute_hand)).getBitmap();
            this.hourHand = ((BitmapDrawable) resources.getDrawable(R.drawable.hour_hand)).getBitmap();

            this.secondPaint = new Paint();
            secondPaint.setARGB(255, 255, 0, 0);
            secondPaint.setStrokeWidth(2.f);
            secondPaint.setAntiAlias(true);
            secondPaint.setStrokeCap(Paint.Cap.ROUND);

            this.time = new Time();
        }

        @Override
        public void onDestroy() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            super.onDestroy();
        }

        /**
         * called when system properties are changed
         * use this to capture low-bit ambient.
         */
        public void onPropertiesChanged(Bundle properties) {
            super.onPropertiesChanged(properties);
            this.lowBitAmbient = properties.getBoolean(PROPERTY_LOW_BIT_AMBIENT, false);
            if (Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, "onPropertiesChanged: low-bit ambient = " + lowBitAmbient);
            }
        }

        /**
         * This is called by the runtime on every minute tick
         */
        @Override
        public void onTimeTick() {
            super.onTimeTick();
            if (Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, "onTimeTick: ambient = " + isInAmbientMode());
            }
            invalidate();
        }

        /**
         * Called when there's a switched in/out of ambient mode
         */
        @Override
        public void onAmbientModeChanged(boolean inAmbientMode) {
            super.onAmbientModeChanged(inAmbientMode);
            if (Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, "onAmbientModeChanged: " + inAmbientMode);
            }
            if(inAmbientMode) {
                character = ((BitmapDrawable) service.getResources().getDrawable(R.drawable.character_standing_greyscale)).getBitmap();
                logo = ((BitmapDrawable) service.getResources().getDrawable(R.drawable.megadroid_logo_bw)).getBitmap();
                hourHand = ((BitmapDrawable) service.getResources().getDrawable(R.drawable.hour_hand_bw)).getBitmap();
                minuteHand = ((BitmapDrawable) service.getResources().getDrawable(R.drawable.minute_hand_bw)).getBitmap();
            } else {
                character = ((BitmapDrawable) service.getResources().getDrawable(R.drawable.character_standing)).getBitmap();
                logo = ((BitmapDrawable) service.getResources().getDrawable(R.drawable.megadroid_logo)).getBitmap();
                hourHand = ((BitmapDrawable) service.getResources().getDrawable(R.drawable.hour_hand)).getBitmap();
                minuteHand = ((BitmapDrawable) service.getResources().getDrawable(R.drawable.minute_hand)).getBitmap();
            }
            if (lowBitAmbient) {
                boolean antiAlias = !inAmbientMode;
                secondPaint.setAntiAlias(antiAlias);
            }
            invalidate();

            // Whether the timer should be running depends on whether we're in ambient mode (as well
            // as whether we're visible), so we may need to start or stop the timer.
            updateTimer();
        }

        private void updateTimer() {
            if (Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, "updateTimer");
            }
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            if (shouldTimerBeRunning()) {
                mUpdateTimeHandler.sendEmptyMessage(MSG_UPDATE_TIME);
            }
        }

        @Override
        public void onDraw(Canvas canvas, Rect bounds) {
            time.setToNow();

            int width = bounds.width();
            int height = bounds.height();

            // Draw the background, scaled to fit.
            if (backgroundScaledBitmap == null
                    || backgroundScaledBitmap.getWidth() != width
                    || backgroundScaledBitmap.getHeight() != height) {
                backgroundScaledBitmap = Bitmap.createScaledBitmap(backgroundBitmap,
                        width, height, true /* filter */);
            }
            canvas.drawBitmap(backgroundScaledBitmap, 0, 0, null);

            canvas.drawBitmap(character, (width- character.getWidth())/2, ((height- character.getHeight())/2)+ 20, null);
            canvas.drawBitmap(logo, (width- logo.getWidth())/2, (logo.getHeight()*2), null);

            float secRot = time.second / 30f * (float) Math.PI;
            int minutes = time.minute;
            float minRot = minutes / 30f * (float) Math.PI;
            float hrRot = ((time.hour + (minutes / 60f)) / 6f ) * (float) Math.PI;

            // Find the center. Ignore the window insets so that, on round watches with a
            // "chin", the watch face is centered on the entire screen, not just the usable
            // portion.
            float centerX = width / 2f;
            float centerY = height / 2f;

            Matrix matrix = new Matrix();
            int minuteHandX = ((width - minuteHand.getWidth()) / 2) - (minuteHand.getWidth() / 2);
            int minuteHandY = (height - minuteHand.getHeight()) / 2;
            matrix.setTranslate(minuteHandX-20, minuteHandY);
            float degrees = minRot * (float) (180.0 / Math.PI);
            matrix.postRotate(degrees+90, centerX,centerY);
            canvas.drawBitmap(minuteHand, matrix, null);

            matrix = new Matrix();
            int rightArmX = ((width - hourHand.getWidth()) / 2) + (hourHand.getWidth() / 2);
            int rightArmY = (height - hourHand.getHeight()) / 2;
            matrix.setTranslate(rightArmX + 20, rightArmY);
            degrees = hrRot * (float) (180.0 / Math.PI);
            matrix.postRotate(degrees-90, centerX,centerY);
            canvas.drawBitmap(hourHand, matrix, null);

            float secLength = centerX - 20;

            if (!isInAmbientMode()) {
                float secX = (float) Math.sin(secRot) * secLength;
                float secY = (float) -Math.cos(secRot) * secLength;
                canvas.drawLine(centerX, centerY, centerX + secX, centerY + secY, secondPaint);
            }
        }

        /**
         * Called when the watch face becomes visible or invisible
         */
        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            if (Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, "onVisibilityChanged: " + visible);
            }

            if (visible) {
                registerReceiver();

                // Update time zone in case it changed while we weren't visible.
                time.clear(TimeZone.getDefault().getID());
                time.setToNow();
            } else {
                unregisterReceiver();
            }

            // Whether the timer should be running depends on whether we're visible (as well as
            // whether we're in ambient mode), so we may need to start or stop the timer.
            updateTimer();
        }

        final BroadcastReceiver mTimeZoneReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                time.clear(intent.getStringExtra("time-zone"));
                time.setToNow();
            }
        };
        boolean mRegisteredTimeZoneReceiver = false;

        private void registerReceiver() {
            if (mRegisteredTimeZoneReceiver) {
                return;
            }
            mRegisteredTimeZoneReceiver = true;
            IntentFilter filter = new IntentFilter(Intent.ACTION_TIMEZONE_CHANGED);
            service.registerReceiver(mTimeZoneReceiver, filter);
        }

        private void unregisterReceiver() {
            if (!mRegisteredTimeZoneReceiver) {
                return;
            }
            mRegisteredTimeZoneReceiver = false;
            service.unregisterReceiver(mTimeZoneReceiver);
        }

    }
}
