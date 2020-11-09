package top.shiftregister.safeout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Sync.startService(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu m) {
        getMenuInflater().inflate(R.menu.main_menu, m);
        return true;
    }

    public void openAccountSettings() {
        showUnfinished();
    }

    public void showAbout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("关于本应用");
        builder.setMessage("\n作者：武科大计算机19级\n");
        builder.setPositiveButton("确定", null);
        builder.show();
    }

    public void showUnfinished() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("抱歉");
        builder.setMessage("此功能尚未完成！");
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

    public void pubTest(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("确定", null);
        if (Sync.pub("Message from mqtt on Android")) {
            builder.setTitle("成功");
            builder.setMessage("发送订阅内容成功！");
        } else {
            builder.setTitle("失败");
            builder.setMessage("发送订阅内容失败！");
        }
        builder.show();
    }
}