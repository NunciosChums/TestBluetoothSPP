package kr.mint.testbluetoothspp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class BluetoothSignalReceiver extends BroadcastReceiver
{
   
   @Override
   public void onReceive(Context $context, Intent $intent)
   {
      Log.i("BluetoothSignalReceiver.java | onReceive", "|action : " + $intent.getAction() + "| signal : " + $intent.getStringExtra("signal") + "|");
      Toast.makeText($context, $intent.getStringExtra("signal"), Toast.LENGTH_SHORT).show();
      
      Intent intent = new Intent($context, MainActivity.class);
      intent.setAction($intent.getAction());
      intent.putExtra("msg", $intent.getStringExtra("signal"));
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      $context.startActivity(intent);
   }
   
}
