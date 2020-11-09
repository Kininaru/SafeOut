package top.shiftregister.safeout;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.Nullable;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import static top.shiftregister.safeout.Tools.*;

public class Sync extends Service {
    static MqttAndroidClient client;
    private MqttConnectOptions connectOptions;
    String TAG = Sync.class.getSimpleName();
    MqttCallback mqttCallback = new MqttCallback() {

        @Override
        public void connectionLost(Throwable cause) {
            println(TAG + '\n' + "连接断开, 尝试重连");
            doClientConnection();
        }

        @Override
        public void messageArrived(String topic, MqttMessage message) {
            print("Mqtt收到消息: ");
            println(message.toString());
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {

        }
    };

    private IMqttActionListener listener = new IMqttActionListener() {
        @Override
        public void onSuccess(IMqttToken asyncActionToken) {
            try {
                client.subscribe(MqttData.pubTopic, 2);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
            println("IMqttActionListener: 连接失败");
            doClientConnection();
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    void init() {
        client = new MqttAndroidClient(this, MqttData.host, MqttData.clientId);
        client.setCallback(mqttCallback);
        connectOptions = new MqttConnectOptions();
        connectOptions.setCleanSession(true);
        connectOptions.setConnectionTimeout(10);
        connectOptions.setKeepAliveInterval(20);
        connectOptions.setUserName(MqttData.userName);
        connectOptions.setPassword(MqttData.pswd.toCharArray());
        boolean doConnect = true;
        String message = "{\"terminal_uid\":\"" + MqttData.clientId + "\"}";
        int qos = 2;
        boolean retained = false;
        if ((!message.equals("")) || (!MqttData.pubTopic.equals(""))) {
            connectOptions.setWill(MqttData.pubTopic, message.getBytes(), qos, retained);
        }
        if (doConnect) {
            doClientConnection();
        }
    }

    private void doClientConnection() {
        if (!client.isConnected() && isConnectIsNormal()) {
            try {
                client.connect(connectOptions, null, listener);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isConnectIsNormal() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info != null && info.isAvailable()) {
            String name = info.getTypeName();
            println("当前网络名称: " + name);
            return true;
        } else {
            println("没有网络");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doClientConnection();
                }
            }, 3000);
            return false;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        init();
        return super.onStartCommand(intent, flags, startId);
    }

    public static void startService(Context mContext) {
        mContext.startService(new Intent(mContext, Sync.class));
    }

    // 发布订阅内容
    public static boolean pub(String message) {
        int qos = 2;
        boolean retained = false;
        try {
            client.publish(MqttData.subTopic, message.getBytes(), qos, retained);
            return true;
        } catch (MqttException e) {
            println("Mqtt发布消息错误! ");
            return false;
        }
    }
}
