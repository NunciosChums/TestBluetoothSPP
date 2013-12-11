package kr.mint.testbluetoothspp;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class ReConnectService
{
   private static ReConnectService _instance;
   private Context _context;
   private Timer _timer;
   
   
   public static synchronized ReConnectService instance(Context $context)
   {
      if (_instance == null)
         _instance = new ReConnectService($context);
      
      return _instance;
   }
   
   
   private ReConnectService(Context $context)
   {
      super();
      _context = $context;
   }
   
   
   /**
    * 1분 마다 다시 연결요청을 한다
    * 
    * @param $context
    */
   public void autoReconnect()
   {
      TimerTask task = new TimerTask()
      {
         @Override
         public void run()
         {
            Log.i("ReConnectService.java | run", "|" + "연결 시도 중" + "|");
            BTService btService = new BTService(_context);
            btService.connect(PreferenceUtil.lastConnectedDeviceAddress());
         }
      };
      _timer = new Timer();
      _timer.schedule(task, 5000, 60000);// 매 분마다 다시 연결한다 
   }
   
   
   /**
    * 자동 연결요청 취소
    */
   public void stopReconnect()
   {
      if (_timer != null)
      {
         Log.i("ReConnectService.java | stopReconnect", "|" + "연결 시도 중지" + "|");
         _timer.cancel();
      }
   }
}
