package kr.mint.testbluetoothspp;

public class PreferenceUtil extends BasePreferenceUtil
{
   private static final String LAST_REQUEST_DEVICE_ADDRESS = "LAST_REQUEST_DEVICE_ADDRESS";
   private static final String LAST_CONNECT_DEVICE_ADDRESS = "LAST_CONNECT_DEVICE_ADDRESS";
   
   
   /**
    * 마지막으로 연결 요청한 기기 주소
    * 
    * @param $address
    *           mac address
    */
   public static void putLastRequestDeviceAddress(String $address)
   {
      put(LAST_REQUEST_DEVICE_ADDRESS, $address);
   }
   
   
   /**
    * 마지막으로 연결한 기기 주소
    * 
    * @return mac address
    */
   public static String lastRequestDeviceAddress()
   {
      return get(LAST_REQUEST_DEVICE_ADDRESS);
   }
   
   
   /**
    * 마지막으로 연결한 기기 주소
    * 
    * @param $address
    *           mac address
    */
   public static void putLastConnectedDeviceAddress(String $address)
   {
      put(LAST_CONNECT_DEVICE_ADDRESS, $address);
   }
   
   
   /**
    * 마지막으로 연결 요청한 기기 주소
    * 
    * @return mac address
    */
   public static String lastConnectedDeviceAddress()
   {
      return get(LAST_CONNECT_DEVICE_ADDRESS);
   }
   
}
