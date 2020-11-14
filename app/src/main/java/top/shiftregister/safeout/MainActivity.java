package top.shiftregister.safeout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import static top.shiftregister.safeout.Tools.*;

public class MainActivity extends AppCompatActivity {

    public static String path;
    public static Account account;
    public static ListView mainListView;
    public static MyListAdapter customAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initListView();
        Sync.startService(this);
        Sync.setMainActivityContext(this);
        this.getExternalFilesDir(null);
        path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/top.shiftregister.safeout";

        try {
            File dir = new File(path);
            if (!dir.exists()) {
                println("文件夹不存在，准备尝试创建");
            }
            if (!dir.mkdir()) {
                println("创建文件夹失败");
            }
            File file = new File(path + "/database.kin");
            if (file.createNewFile()) {
                println("创建数据库文件成功");
                // 写入初始信息
                PrintWriter output = new PrintWriter(new FileWriter(path + "/database.kin"));
                output.write("0\n");
                output.flush();
                output.close();
                println("对数据文件写入初始化信息成功");
            } else {
                println("数据库文件已存在");
            }
        } catch (IOException e) {
            println("初始化文件系统失败");
        }
        try {
            BufferedReader input = new BufferedReader(new FileReader(path + "/database.kin"));
            String buf = input.readLine();
            if (buf == null) {
                input.close();
                // 写入初始信息
                PrintWriter output = new PrintWriter(new FileWriter(path + "/database.kin"));
                output.write("0\n");
                output.flush();
                output.close();
                println("对数据文件写入初始化信息成功");
                account = new Account();
                return;
            }
            if (buf.split("\n")[0].equals("0")) {
                account = new Account();
            } else {
                buf = input.readLine();
                if (buf == null) {
                    input.close();
                    // 写入初始信息
                    PrintWriter output = new PrintWriter(new FileWriter(path + "/database.kin"));
                    output.write("0\n");
                    output.flush();
                    output.close();
                    println("对数据文件写入初始化信息成功");
                    account = new Account();
                } else {
                    account = new Account(buf.split("\n")[0]);
                    input.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        int a = View.VISIBLE, b = View.INVISIBLE;
        if (account.isLogin()) {
            int t = a;
            a = b;
            b = t;
        }
        findViewById(R.id.logout_layout).setVisibility(a);
        findViewById(R.id.data_layout).setVisibility(b);

        if (account.isLogin()) {
            findViewById(R.id.logout_layout).setVisibility(View.INVISIBLE);
            findViewById(R.id.data_layout).setVisibility(View.VISIBLE);
            sendDataRequest();
        }
    }

    protected void onResume() {
        super.onResume();
        try {
            BufferedReader input = new BufferedReader(new FileReader(path + "/database.kin"));
            if (input.readLine().split("\n")[0].equals("0")) {
                account = new Account();
            } else {
                account = new Account(input.readLine().split("\n")[0]);
            }
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        int a = View.VISIBLE, b = View.INVISIBLE;
        if (account.isLogin()) {
            int t = a;
            a = b;
            b = t;
        }
        findViewById(R.id.logout_layout).setVisibility(a);
        findViewById(R.id.data_layout).setVisibility(b);

        if (account.isLogin()) {
            findViewById(R.id.logout_layout).setVisibility(View.INVISIBLE);
            findViewById(R.id.data_layout).setVisibility(View.VISIBLE);
            sendDataRequest();
        }
    }

    void initListView() {
        mainListView = findViewById(R.id.main_list_view);
        customAdapter = new MyListAdapter(MainActivity.this);
        mainListView.setAdapter(customAdapter);
    }

    void sendDataRequest() {
        if (!account.isLogin()) {
            println("加载数据终止，因为没有登录");
            return;
        }
        Sync.pub("{\"cmd_id\": \"ServerAndAndroid_" + account.getTouristId() + "\", \"tourist_id\":\"" + account.getTouristId() + "\"}");
        println("已发送Mqtt请求");
    }

    void loadData(String dataString) {
        // 处理JSON String
        try {
            JSONObject jsonObject = new JSONObject(dataString);
            String outputString = jsonObject.getString("output");
            outputString = MyJsonTools.removeBackSlash(outputString);
            JSONArray dataArray = new JSONArray(outputString);
            println("总共有" + dataArray.length() + "条数据");
            // 渲染列表
            println("开始渲染列表");
            String date, machineId;
            boolean granted;
            initListView();
            for (int i = 0; i < dataArray.length(); i++) {
                println("正在渲染第" + i + "个项目");
                date = ((JSONObject) dataArray.get(i)).getString("time") + "      对应机器ID：";
                machineId = ((JSONObject) dataArray.get(i)).getString("machine_id");
                granted = ((JSONObject) dataArray.get(i)).getString("status").equals("granted");
                println(date + machineId + granted);
                customAdapter.addItem(new ListViewItem(granted, date, machineId));
            }
            findViewById(R.id.loading_lay_out).setVisibility(View.INVISIBLE);
            findViewById(R.id.list_view_father).setVisibility(View.VISIBLE);
            findViewById(R.id.main_list_view).setVisibility(View.VISIBLE);
        } catch (JSONException e) {
            println("无效的cmdret，内容为：" + dataString);
            println("本次 loadData 调用将不会更新数据列表");
        }
    }

    public void gotoLogin(View v) {
        openAccountSettings();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu m) {
        getMenuInflater().inflate(R.menu.main_menu, m);
        return true;
    }

    public void openAccountSettings() {
        startActivity(new Intent(this, AccountSettings.class));
    }

    public void showAbout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("关于本应用");
        builder.setMessage("\n作者：武科大计算机19级\n");
        builder.setPositiveButton("确定", null);
        builder.show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.account_settings:
                openAccountSettings();
                break;
            case R.id.about:
                showAbout();
                break;
            default:
                Toast.makeText(this, "Error happened", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }
}