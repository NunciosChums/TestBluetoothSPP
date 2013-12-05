package kr.mint.testbluetoothspp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity
{
   private static final int REQUEST_ENABLE_BT = 100;
   private static final int MESSAGE_READ = 200;
   private static final int REQUEST_CONNECT_DEVICE_INSECURE = 300;
   
   private BluetoothAdapter mBTAdapter;
   private Handler mHandler;
   private TextView _text1;
   
   
   @Override
   public void onCreate(Bundle savedInstanceState)
   {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);
      _text1 = (TextView) findViewById(R.id.textView1);
      
      mHandler = new Handler(new Callback()
      {
         @Override
         public boolean handleMessage(final Message msg)
         {
            Log.i("MainActivity.java | handleMessage", "|" + msg.what + "|");
            runOnUiThread(new Runnable()
            {
               @Override
               public void run()
               {
                  try
                  {
                     // TODO 화면에 표시
                     byte[] readBuf = (byte[]) msg.obj;
                     _text1.setText("|" + bytes2String(readBuf, msg.arg1) + "|");
                  }
                  catch (Exception e)
                  {
                     e.printStackTrace();
                  }
               }
            });
            return false;
         }
      });
      
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
         BluetoothDevice device = mBTAdapter.getRemoteDevice(address);
         conn(device);
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
   
   
   private void conn(BluetoothDevice $device)
   {
      ConnectThread thread = new ConnectThread($device);
      thread.start();
   }
   
   
   private void manageConnectedSocket(BluetoothSocket $socket)
   {
      Log.i("MainActivity.java | manageConnectedSocket", "|" + $socket.getRemoteDevice().getName() + "|");
      ConnectedThread thread = new ConnectedThread($socket);
      thread.start();
   }
   
   private class ConnectThread extends Thread
   {
      private final BluetoothSocket mmSocket;
      private final BluetoothDevice mmDevice;
      
      
      public ConnectThread(BluetoothDevice device)
      {
         // Use a temporary object that is later assigned to mmSocket,
         // because mmSocket is final
         BluetoothSocket tmp = null;
         mmDevice = device;
         // Get a BluetoothSocket to connect with the given BluetoothDevice
         try
         {
            UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
            
            // MY_UUID is the app's UUID string, also used by the server code
            tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
         }
         catch (IOException e)
         {
         }
         mmSocket = tmp;
      }
      
      
      public void run()
      {
         // Cancel discovery because it will slow down the connection
         mBTAdapter.cancelDiscovery();
         try
         {
            // Connect the device through the socket. This will block
            // until it succeeds or throws an exception
            mmSocket.connect();
         }
         catch (Exception e1)
         {
            runOnUiThread(new Runnable()
            {
               @Override
               public void run()
               {
                  _text1.setText("connect fail");
               }
            });
            
            e1.printStackTrace();
            // Unable to connect; close the socket and get out
            try
            {
               mmSocket.close();
            }
            catch (Exception e2)
            {
               e2.printStackTrace();
            }
            return;
         }
         // Do work to manage the connection (in a separate thread)
         manageConnectedSocket(mmSocket);
      }
      
      
      /** Will cancel an in-progress connection, and close the socket */
      public void cancel()
      {
         try
         {
            mmSocket.close();
         }
         catch (IOException e)
         {
         }
      }
   }
   
   private class ConnectedThread extends Thread
   {
      private final BluetoothSocket mmSocket;
      private final InputStream mmInStream;
      private final OutputStream mmOutStream;
      
      
      public ConnectedThread(BluetoothSocket socket)
      {
         mmSocket = socket;
         InputStream tmpIn = null;
         OutputStream tmpOut = null;
         // Get the input and output streams, using temp objects because
         // member streams are final
         try
         {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
         }
         catch (IOException e)
         {
         }
         mmInStream = tmpIn;
         mmOutStream = tmpOut;
      }
      
      
      public void run()
      {
         byte[] buffer = new byte[1024]; // buffer store for the stream
         int bytes; // bytes returned from read()
         // Keep listening to the InputStream until an exception occurs
         
         while (true)
         {
            try
            {
               // Read from the InputStream
               bytes = mmInStream.read(buffer);
               // Send the obtained bytes to the UI Activity
               mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer).sendToTarget();
               Log.i("MainActivity.java | run", "|" + bytes + "|");
            }
            catch (IOException e)
            {
               break;
            }
         }
      }
      
      
      /* Call this from the main Activity to send data to the remote device */
      public void write(byte[] bytes)
      {
         try
         {
            mmOutStream.write(bytes);
         }
         catch (IOException e)
         {
         }
      }
      
      
      /* Call this from the main Activity to shutdown the connection */
      public void cancel()
      {
         try
         {
            mmSocket.close();
         }
         catch (IOException e)
         {
         }
      }
   }
}
