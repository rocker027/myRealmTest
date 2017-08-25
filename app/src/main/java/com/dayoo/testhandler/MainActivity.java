package com.dayoo.testhandler;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity implements Handler.Callback {
    public static final String TAG = MainActivity.class.getSimpleName();
    public static final int MSG_QUERY = 1;
    public static final int MSG_DISPLAY = 2;
    public static final int MSG_SAVE = 3;
    TextView textView;
    private Handler uiHandler;
    private Handler backgroundHandler;
    private Realm realm;
    private RealmConfiguration realmConfiguration;
    private Button button1;
    private Button button2;
    private Thread backThread;
    private Button button3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        backgroundThredInit();
        findViews();
    }

    //初始化background Thread
    private void backgroundThredInit() {
        backThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                Log.d(TAG, "backgroundHandler start");
                backgroundHandler = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        switch (msg.what) {
                            case MSG_QUERY:
                                Message ui_msg1 = uiHandler.obtainMessage(MSG_QUERY);
                                ui_msg1.sendToTarget();
                                break;
                            case MSG_DISPLAY:
                                break;
                            case MSG_SAVE:
                                Message ui_msg2 = uiHandler.obtainMessage(MSG_SAVE);
                                ui_msg2.sendToTarget();
                                break;
                        }
                    }
                };
                Looper.loop();
            }
        });
        backThread.start();
    }

    //初始化UI元件
    private void findViews() {
        textView = (TextView) findViewById(R.id.textview);
        //按鈕1 查詢資料
        button1 = (Button) findViewById(R.id.btn1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backgroundHandler.sendEmptyMessage(MSG_QUERY);

                // 不使用 backgroundHandler情況下,可以直接call uiHandler
//                Message msg = uiHandler.obtainMessage(MSG_QUERY);
//                msg.sendToTarget();
            }
        });
        //按鈕2 新增資料
        button2 = (Button) findViewById(R.id.btn2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backgroundHandler.sendEmptyMessage(MSG_SAVE);

                // 不使用 backgroundHandler情況下,可以直接call uiHandler
//                Message msg = uiHandler.obtainMessage(MSG_SAVE);
//                msg.sendToTarget();
            }
        });

        //按鈕3 關閉backgroundHandler 未來可以寫在 onDestory()
        button3 = (Button) findViewById(R.id.btn3);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backgroundHandler.getLooper().quit();
                backThread = null;
            }
        });


    }

    // 初始化元件
    private void init() {
        //Realm 初始化
        Realm.init(this);
        realmConfiguration = new RealmConfiguration.Builder()
                .build();
        realm = Realm.getInstance(realmConfiguration);
        // 將uiHandler 綁訂 MainLooper ，於建構式內傳入 MainLooper 以及 Handler.callback
        uiHandler = new Handler(getMainLooper(), this);
    }

    //uiHandler Handler callback
    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_QUERY:
//                User query = realm.where(User.class).equalTo("name", "alex").findFirst();
//                Log.d(TAG, "query name " + query.getName());
                Message message = uiHandler.obtainMessage();
                message.what = MSG_DISPLAY;
//                message.obj = query;
                message.sendToTarget();
//                backgroundHandler.sendMessage(message);
                break;
            case MSG_DISPLAY:
//                User user = (User) msg.obj;
                StringBuilder sb = new StringBuilder();
                RealmResults<User> users = realm.where(User.class).findAll();
                for (User user : users) {
                    sb.append("name:");
                    sb.append(user.getName() + "\n");
                    sb.append("age:");
                    sb.append(user.getAge() + "\n");
                }
                Log.d(TAG, "display " + sb.toString());
                textView.setText(sb.toString());
                break;
            case MSG_SAVE:

                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm r) {
                        Log.d(TAG, "to save");
                        User user = r.createObject(User.class,"alex");

//                        User user = new User();
//                        user.setName("alex");
                        user.setAge(30);
//                        realm.copyToRealmOrUpdate(user);
                    }
                });
                break;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        backgroundHandler.getLooper().quit();
        backThread = null;
        realm.close();
    }
}
