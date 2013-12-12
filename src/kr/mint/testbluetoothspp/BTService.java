package kr.mint.testbluetoothspp;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.UUID;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

public class BTService extends Service
{
   private BluetoothAdapter mAdapter;
   private Context _context;
   
   
   @Override
   public IBinder onBind(Intent intent)
   {
      return null;
   }
   
   
   public BTService(Context $context)
   {
      super();
      _context = $context;
      mAdapter = BluetoothAdapter.getDefaultAdapter();
   }
   
   
   /**
    * 주소로 연결하기
    * 
    * @param $address
    *           mac address
    */
   public void connect(String $address)
   {
      BluetoothDevice device = mAdapter.getRemoteDevice($address);
      connect(device);
   }
   
   
   public void connect(BluetoothDevice $device)
   {
      ConnectThread thread = new ConnectThread($device);
      thread.start();
   }
   
   
   private void manageConnectedSocket(BluetoothSocket $socket)
   {
      Log.i("BTService.java | manageConnectedSocket", "|" + $socket.getRemoteDevice().getName() + "|" + $socket.getRemoteDevice().getAddress());
      PreferenceUtil.putLastRequestDeviceAddress($socket.getRemoteDevice().getAddress());
      mAdapter.cancelDiscovery();
      ConnectedThread thread = new ConnectedThread($socket);
      thread.start();
   }
   
   private class ConnectThread extends Thread
   {
      private final BluetoothSocket mmSocket;
      
      
      public ConnectThread(BluetoothDevice device)
      {
         // Use a temporary object that is later assigned to mmSocket, because mmSocket is final
         BluetoothSocket tmp = null;
//         mmDevice = device;
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
         mAdapter.cancelDiscovery();
         try
         {
            // Connect the device through the socket. This will block until it succeeds or throws an exception
            mmSocket.connect();
         }
         catch (Exception e1)
         {
            Log.e("BTService.java | run", "|" + "connect fail" + "|");
            
            e1.printStackTrace();
            // Unable to connect; close the socket and get out
            try
            {
               if (mmSocket.isConnected())
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
      
      
//      private final OutputStream mmOutStream;
      
      public ConnectedThread(BluetoothSocket socket)
      {
         mmSocket = socket;
         InputStream tmpIn = null;
//         OutputStream tmpOut = null;
         // Get the input and output streams, using temp objects because member streams are final
         try
         {
            tmpIn = socket.getInputStream();
//            tmpOut = socket.getOutputStream();
         }
         catch (IOException e)
         {
         }
         mmInStream = tmpIn;
//         mmOutStream = tmpOut;
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
//               mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer).sendToTarget();
               Log.i("BTService.java | run", "|" + bytes2String(buffer, bytes) + "|");
               
               Intent intent = new Intent("kr.lge.smarthealthcareapps.bluetooth.receive");
               intent.putExtra("signal", bytes2String(buffer, bytes));
               _context.sendBroadcast(intent);
            }
            catch (Exception e)
            {
               e.printStackTrace();
               break;
            }
         }
      }
      
      
      private String bytes2String(byte[] b, int count)
      {
         ArrayList<String> result = new ArrayList<String>();
         for (int i = 0; i < count; i++)
         {
            String myInt = Integer.toHexString((int) (b[i] & 0xFF));
            result.add("0x" + myInt);
         }
         return TextUtils.join("-", result);
      }
      
      
      /* Call this from the main Activity to send data to the remote device */
//      public void write(byte[] bytes)
//      {
//         try
//         {
//            mmOutStream.write(bytes);
//         }
//         catch (IOException e)
//         {
//         }
//      }
      
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
