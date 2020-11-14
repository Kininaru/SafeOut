package top.shiftregister.safeout;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import static top.shiftregister.safeout.Tools.*;

public class AccountSettings extends AppCompatActivity {

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_account);
        this.setTitle("账户设置");

        if (MainActivity.account.isLogin()) {
            findViewById(R.id.login_panel).setVisibility(View.INVISIBLE);
            ((TextView) findViewById(R.id.tourist_id)).setText("你已登录，你的游客id为：" + MainActivity.account.getTouristId());
        } else {
            findViewById(R.id.account_panel).setVisibility(View.INVISIBLE);
        }
    }

    public void logoutBtn(android.view.View v) throws IOException {
        findViewById(R.id.account_panel).setVisibility(View.INVISIBLE);
        findViewById(R.id.login_panel).setVisibility(View.VISIBLE);
        MainActivity.account = new Account();

        PrintWriter output = new PrintWriter(new FileWriter(MainActivity.path + "/database.kin"));
        output.write("0\n");
        output.flush();
        output.close();
    }

    @SuppressLint("SetTextI18n")
    public void login(View v) throws IOException {
        String uid = ((EditText) findViewById(R.id.input_uid)).getText().toString();
        MainActivity.account = new Account(uid);
        findViewById(R.id.account_panel).setVisibility(View.VISIBLE);
        findViewById(R.id.login_panel).setVisibility(View.INVISIBLE);
        ((TextView) findViewById(R.id.tourist_id)).setText("你已登录，你的游客id为：" + MainActivity.account.getTouristId());

        PrintWriter output = new PrintWriter(new FileWriter(MainActivity.path + "/database.kin"));
        output.write("1\n");
        output.write(uid + "\n");
        output.flush();
        output.close();
    }
}
