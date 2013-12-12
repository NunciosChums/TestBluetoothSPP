package kr.mint.testbluetoothspp;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ScheduledExecutorService;

import android.content.Context;
import android.util.Log;

public class ReConnectService
{
   private static ReConnectService _instance;
   private Context _context;
   private Timer _timer;
   private ScheduledExecutorService _scheduledExecutorService;
   
   
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
            Log.i("ReConnectService.java | run", "|==" + "연결 시도 중" + "|");
            BTService btService = new BTService(_context);
            btService.connect(PreferenceUtil.lastConnectedDeviceAddress());
         }
      };
      _timer = new Timer();
      _timer.schedule(task, 5000, 10000);// 매 분마다 다시 연결한다 
      
//      _scheduledExecutorService = Executors.newScheduledThreadPool(1);
//      _scheduledExecutorService.scheduleWithFixedDelay(new Runnable()
//      {
//         @Override
//         public void run()
//         {
//            Log.i("ReConnectService.java | run", "|==" + "연결 시도 중" + "|" + PreferenceUtil.lastConnectedDeviceAddress());
//            BTService btService = new BTService(_context);
//            btService.connect(PreferenceUtil.lastConnectedDeviceAddress());
//         }
//      }, 5, 10, TimeUnit.SECONDS);
   }
   
   
   /**
    * 자동 연결요청 취소
    */
   public void stopReconnect()
   {
      Log.i("ReConnectService.java | stopReconnect", "|==" + "연결 시도 중지" + "|");
      
      if (_timer != null)
      {
         _timer.cancel();
      }
      
      if (_scheduledExecutorService != null)
         _scheduledExecutorService.shutdown();
   }
}
