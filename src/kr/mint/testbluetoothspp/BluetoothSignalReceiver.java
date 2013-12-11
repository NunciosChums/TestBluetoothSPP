package kr.mint.testbluetoothspp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BluetoothSignalReceiver extends BroadcastReceiver
{
   
   @Override
   public void onReceive(Context context, Intent intent)
   {
      Log.i("BluetoothSignalReceiver.java | onReceive", "|action : " + intent.getAction() + "| signal : " + intent.getStringExtra("signal") + "|");
   }
   
}
