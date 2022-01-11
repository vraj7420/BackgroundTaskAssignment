package com.example.backgroundtask.background;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.MenuItem;

import com.example.backgroundtask.R;

public class BatteryReceiver  extends BroadcastReceiver {
    MenuItem menuItemBatteryPercentage;
    MenuItem menuItemBatteryPercentageText;

    public BatteryReceiver(MenuItem menuItemBatteryPercentage,MenuItem menuItemBatteryPercentageText) {
        this.menuItemBatteryPercentage = menuItemBatteryPercentage;
        this.menuItemBatteryPercentageText=menuItemBatteryPercentageText;
    }

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {
        int percentage=intent.getIntExtra("level",0);
        Log.d("battery",String.valueOf(percentage));
        menuItemBatteryPercentage.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
       menuItemBatteryPercentage.setIcon(R.drawable.ic_battery_full);
        menuItemBatteryPercentage.setTitle(percentage +"%");
        menuItemBatteryPercentageText.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menuItemBatteryPercentageText.setTitle(percentage +"%");
    }
}
