package kr.mint.testbluetoothspp;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity
{
   private static final int REQUEST_ENABLE_BT = 100;
   private static final int REQUEST_CONNECT_DEVICE_INSECURE = 200;
   
   private BluetoothAdapter mBTAdapter;
   private TextView _text1;
   private BTService _btService;
   
   
   @Override
   public void onCreate(Bundle savedInstanceState)
   {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);
      _text1 = (TextView) findViewById(R.id.textView1);
      ContextUtil.CONTEXT = getApplicationContext();
      
      _btService = new BTService(getApplicationContext());
      
      mBTAdapter = BluetoothAdapter.getDefaultAdapter();
      if (mBTAdapter == null)
      {
         // device does not support Bluetooth
         Toast.makeText(getApplicationContext(), "device does not support Bluetooth", Toast.LENGTH_LONG).show();
         _text1.setText("device does not support Bluetooth");
      }
      else
      {
         if (!mBTAdapter.isEnabled())
         {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
         }
      }
      
      checkIntent(getIntent());
   }
   
   
   @Override
   protected void onNewIntent(Intent intent)
   {
      super.onNewIntent(intent);
      checkIntent(intent);
   }
   
   
   @Override
   protected void onActivityResult(int requestCode, int resultCode, Intent data)
   {
      super.onActivityResult(requestCode, resultCode, data);
      
      Log.i("MainActivity.java | onActivityResult", "|" + requestCode + "|" + resultCode + "(ok = " + RESULT_OK + ")|" + data);
      if (resultCode != RESULT_OK)
         return;
      
      if (requestCode == REQUEST_ENABLE_BT)
      {
         discovery();
      }
      else if (requestCode == REQUEST_CONNECT_DEVICE_INSECURE)
      {
         String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
         Log.i("MainActivity.java | onActivityResult", "|" + address + "|");
         BluetoothDevice device = mBTAdapter.getRemoteDevice(address);
         _btService.connect(device);
      }
   }
   
   
   private void checkIntent(Intent $intent)
   {
      Log.i("MainActivity.java | checkIntent", "|" + $intent.getAction() + "|");
      if ("kr.mint.bluetooth.receive".equals($intent.getAction()))
      {
         Log.i("MainActivity.java | checkIntent", "|" + $intent.getStringExtra("msg") + "|");
         _text1.setText($intent.getStringExtra("msg"));
      }
   }
   
   
   public void onBtnClick(View v)
   {
      discovery();
   }
   
   
   private void discovery()
   {
      Intent serverIntent = new Intent(this, DeviceListActivity.class);
      startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_INSECURE);
   }
}
