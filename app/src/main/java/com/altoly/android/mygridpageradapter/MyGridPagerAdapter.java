package com.altoly.android.mygridpageradapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Handler;
import android.os.SystemClock;
import android.support.wearable.view.GridPagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.Random;

/**
 * Created by hitoshi on 10/17/15.
 */
public class MyGridPagerAdapter extends GridPagerAdapter {

    final Context mContext;

    public MyGridPagerAdapter(final Context context) {
        mContext = context.getApplicationContext();
    }

    TextView timeText;
    Button startButton, resetButton;
    long startTime = 0L;
    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;
    boolean isPaused = true; // toggle flag start or pause
    int secs = 0;
    int mins = 0;
    int milliseconds = 0;
    Handler customHandler = new Handler();

    @Override
    public int getRowCount() {
        return 1;
    }

    @Override
    public int getColumnCount(int i) {
        return 2;
    }

    @Override
    public int getCurrentColumnForRow(int row, int currentColumn) {
        int ret = super.getCurrentColumnForRow(row, currentColumn);
        Log.d("test", String.format("getCurColForRow: ret=%d   row=%d   curCol=%d", ret, row, currentColumn));
        return super.getCurrentColumnForRow(row, currentColumn);
    }

    @Override
    public Object instantiateItem(ViewGroup viewGroup, int row, int col) {
        Log.d("test", "instantiateItem: row=" + row + " col=" + col);
        View view;
        if(col == 0){
            view = View.inflate(mContext, R.layout.text_clock, null);
        } else {
            view = View.inflate(mContext, R.layout.stopwatch, null);

            timeText = (TextView) view.findViewById(R.id.textTimer);
            startButton = (Button) view.findViewById(R.id.start_button);
            resetButton = (Button) view.findViewById(R.id.reset_button);

            startButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    if (isPaused) {
//                        startButton.setText(Resources.getSystem().getString(R.string.pause));
                        startButton.setText("Pause");
                        startButton.setTextColor(Color.RED);
                        startTime = SystemClock.uptimeMillis();
                        customHandler.postDelayed(updateTimerThread, 0);
                        isPaused = false;
                    } else {
//                        startButton.setText(Resources.getSystem().getString(R.string.start));
                        startButton.setText("Start");
                        startButton.setTextColor(Color.WHITE);
                        timeText.setTextColor(Color.WHITE);
                        timeSwapBuff += timeInMilliseconds;
                        customHandler.removeCallbacks(updateTimerThread);
                        isPaused = true;
                    }
                }
            });

            resetButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    startTime = 0L;
                    timeSwapBuff = 0L;
                    timeInMilliseconds = 0L;
                    isPaused = true;
                    secs = 0;
                    mins = 0;
                    milliseconds = 0;
//                    startButton.setText(Resources.getSystem().getString(R.string.start));
                    startButton.setText("Start");
                    startButton.setTextColor(Color.WHITE);
                    customHandler.removeCallbacks(updateTimerThread);
//                    timeText.setText(Resources.getSystem().getString(R.string.start_time));
                    timeText.setText("00:00:00");
                }
            });

        }
        viewGroup.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup viewGroup, int i, int i2, Object o) {
        Log.d("test", "destroyItem  row=" + i + " col=" +i2);
        viewGroup.removeView((View)o);
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        Log.d("test", "isViewFromObject");
        return view.equals(o);
    }

    private Runnable updateTimerThread = new Runnable() {

        public void run() {

            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;

            updatedTime = timeSwapBuff + timeInMilliseconds;

            int secs = (int) (updatedTime / 1000);
            int mins = secs / 60;
            secs = secs % 60;
            int milliseconds = (int) (updatedTime % 1000);
            timeText.setText("" + mins + ":"
                    + String.format("%02d", secs) + ":"
                    + String.format("%03d", milliseconds));
            customHandler.postDelayed(this, 0);
        }
    };
}
