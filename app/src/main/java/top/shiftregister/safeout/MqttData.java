package top.shiftregister.safeout;

import android.os.Build;

import androidx.annotation.RequiresApi;

public class MqttData {
    public static final String host = "tcp://124.70.155.130:1883";
    public static final String userName = "user1";
    public static final String pswd = "helloworld";
    public static final String subTopic = "KininaruTest";
    public static final String pubTopic = "KininaruTest";
//    public static final String clientId = Build.getSerial();
    public static final String clientId = "0";
}
