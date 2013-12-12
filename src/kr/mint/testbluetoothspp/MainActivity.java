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
      
//      mHandler = new Handler(new Callback()
//      {
//         @Override
//         public boolean handleMessage(final Message msg)
//         {
//            Log.i("MainActivity.java | handleMessage", "|" + msg.what + "|");
//            runOnUiThread(new Runnable()
//            {
//               @Override
//               public void run()
//               {
//                  try
//                  {
//                     // TODO 화면에 표시
//                     byte[] readBuf = (byte[]) msg.obj;
//                     _text1.setText("|" + bytes2String(readBuf, msg.arg1) + "|");
//                  }
//                  catch (Exception e)
//                  {
//                     e.printStackTrace();
//                  }
//               }
//            });
//            return false;
//         }
//      });
      
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
   }
   
   
   private String bytes2String(byte[] b, int count)
   {
      StringBuilder ret = new StringBuilder();
      for (int i = 0; i < count; i++)
      {
         String myInt = Integer.toHexString((int) (b[i] & 0xFF));
         ret.append("0x" + myInt);
      }
      return ret.toString();
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
