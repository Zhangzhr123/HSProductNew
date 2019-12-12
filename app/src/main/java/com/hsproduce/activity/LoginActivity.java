package com.hsproduce.activity;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.*;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.*;
import com.google.gson.reflect.TypeToken;
import com.hsproduce.App;
import com.hsproduce.R;
import com.hsproduce.bean.Result;
import com.hsproduce.bean.UpdateVersion;
import com.hsproduce.util.HttpUtil;
import com.hsproduce.util.PathUtil;
import com.hsproduce.util.StringUtil;
import com.hsproduce.util.TaskUtil;

import java.io.*;
import java.util.*;

import static android.support.constraint.Constraints.TAG;

public class LoginActivity extends BaseActivity {

    //用户名  密码
    private TextView tv_code, tv_password;
    //班组下拉列表
    private Spinner sp_shift;
    //声明一个long类型变量：用于存放上一点击“返回键”的时刻
    private long mExitTime = 0;
    //点击图片更换ip地址
    private ImageView ip;
    //下拉列表及适配器
    private List<String> shiftlist = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    //IP
    public static String IP = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //更新版本
        findViewById(R.id.title).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                new 版本更新Task().execute();
                return false;
            }
        });
        //显示版本信息
        String 当前版本 = "";
        try {
            当前版本 = "版本：" + getPackageManager().getPackageInfo("com.hsproduce", 0).versionName;
            System.out.println("当前版本:"+当前版本);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("login", e.getMessage());
        }

        // 获取设置的IP地址
        String text = get();
        if (!TextUtils.isEmpty(text)) {
            App.ip = text;
            Toast.makeText(this, "读取成功", Toast.LENGTH_LONG).show();
            new ShiftTask().execute();
        }

        initView();
    }

    public void initView() {
        //用户名
        tv_code = (TextView) findViewById(R.id.code);
        //密码
        tv_password = (TextView) findViewById(R.id.password);
        //班组
        sp_shift = findViewById(R.id.shift);
        //ip地址
        ip = (ImageView) findViewById(R.id.ip);

        //自行输入IP
        ip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText et = new EditText(LoginActivity.this);
                et.setHint(App.ip);
                new AlertDialog.Builder(LoginActivity.this).setTitle("请输入IP地址")
                        .setView(et)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //et.setText(App.ip);
                                //按下确定键后的事件
                                IP = et.getText().toString();
                                if (IP.equals("") || IP == null) {
                                    IP = App.ip;
                                }
                                App.ip = IP;
                                //数据持久化
                                File file = getDir("myIP", Context.MODE_PRIVATE);
                                if (file != null) {
                                    deleteFile(file);
                                    save(IP);
                                } else {
                                    save(IP);
                                }
                                new ShiftTask().execute();//查询班组
                                Toast.makeText(getApplicationContext(), et.getText().toString(), Toast.LENGTH_LONG).show();
                            }
                        }).setNegativeButton("取消", null).show();
            }
        });

        //下拉列表
        //创建一个数组适配器
        //new ShiftTask().execute();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, shiftlist);
        sp_shift.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String shift = parent.getItemAtPosition(position).toString();
                //根据ID获取内容
                if (shift.equals(getResources().getString(R.string.team1))) {
                    shift = "1";
                } else if (shift.equals(getResources().getString(R.string.team2))) {
                    shift = "2";
                } else if (shift.equals(getResources().getString(R.string.team3))) {
                    shift = "3";
                } else {
                    shift = "15";//丁班
                }
                App.shift = shift;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


    }

    public void login(View v) {
        String username = tv_code.getText().toString().trim();
        String password = tv_password.getText().toString().trim();
        App.username = username;
        String param = "UserName=" + username + "&PWD=" + password;
        new MyTask().execute(param);
    }

    //班组
    class ShiftTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strs) {
            String result = HttpUtil.sendGet(PathUtil.GET_SHIFT, null);
            return result;
        }

        //事后执行
        @Override
        protected void onPostExecute(String s) {
            if (StringUtil.isNullOrBlank(s)) {
                Toast.makeText(LoginActivity.this, "网络连接异常", Toast.LENGTH_LONG).show();
            } else {
                Map<String, Object> res = App.gson.fromJson(s, new TypeToken<Map<String, Object>>() {
                }.getType());
                List<Map<String, String>> map = (List<Map<String, String>>) res.get("data");
                if (res.get("code").equals("200")) {
                    for (int i = 0; i < map.size(); i++) {
                        shiftlist.add(map.get(i).get("name"));
                    }
                    sp_shift.setAdapter(adapter);
                } else {
                    Toast.makeText(LoginActivity.this, "获取失败", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    //登录
    class MyTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strs) {
            String result = HttpUtil.sendGet(PathUtil.LOGIN, strs[0]);
            //String result = HttpUtil.sendPost(PathUtil.LOGIN, strs[0], HttpUtil.formContent);
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            if (StringUtil.isNullOrBlank(s)) {
                Toast.makeText(LoginActivity.this, "网络连接异常", Toast.LENGTH_LONG).show();
            } else {
                Map<String, String> res = App.gson.fromJson(s, new TypeToken<HashMap<String, String>>() {
                }.getType());
                if (res.get("code").equals("200")) {

                    Intent intent = new Intent(LoginActivity.this, FunctionActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(LoginActivity.this, "用户名或密码错误", Toast.LENGTH_LONG).show();
                }

            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 保存到文件
     *
     * @param value
     */
    private void save(String value) {
        BufferedWriter writer = null;
        try {

            FileOutputStream out = openFileOutput("myIP", Context.MODE_PRIVATE);
            writer = new BufferedWriter(new OutputStreamWriter(out));
            writer.write(value);

        } catch (FileNotFoundException e) {
            Log.e(TAG, "save: 找不到文件", e);
        } catch (IOException e) {
            Log.e(TAG, "save:IO 异常", e);
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                Log.e(TAG, "save: 无法关闭 BufferedWriter", e);
            }
        }
    }

    /**
     * 从文件中获取数据
     *
     * @return
     */
    private String get() {

        BufferedReader reader = null;
        StringBuilder content = new StringBuilder();
        try {
            FileInputStream in = openFileInput("myIP");
            reader = new BufferedReader(new InputStreamReader(in));
            String temp;
            while ((temp = reader.readLine()) != null) {
                content.append(temp);
            }
        } catch (FileNotFoundException e) {
            Log.e(TAG, "get: FileNotFoundException", e);
        } catch (IOException e) {
            Log.e(TAG, "get: IOException", e);
        }
        return content.toString();
    }

    public static void deleteFile(File file) {
        if (file.exists()) {                          //判断文件是否存在
            if (file.isFile()) {                      //判断是否是文件
                boolean isSucess = file.delete();
                Log.i("TAG:", "文件删除状态--->" + isSucess);
            } else if (file.isDirectory()) {           //判断是否是文件夹
                File files[] = file.listFiles();    //声明目录下所有文件
                for (int i = 0; i < files.length; i++) {   //遍历目录下所有文件
                    deleteFile(files[i]);           //把每个文件迭代删除
                }
                boolean isSucess = file.delete();
                Log.i("TAG:", "文件夹删除状态--->" + isSucess);
            }
        }
    }

    //键盘监听
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.e("key", keyCode + "  ");
        //两次返回键时间间隔超过两秒 退出登录
        if (keyCode == 4) {
            if (System.currentTimeMillis() - mExitTime > 2000) {
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                //并记录下本次点击“返回键”的时刻，以便下次进行判断
                mExitTime = System.currentTimeMillis();
            } else {
                System.exit(0);//注销操作
            }
        }
        return true;
    }

    //版本更新
    class 版本更新Task extends AsyncTask<Void, Void, UpdateVersion> {
        @Override
        protected UpdateVersion doInBackground(Void... voids) {
            UpdateVersion 终端版本 = null;
            // 获取服务器最新版本
            try {
                String result = HttpUtil.sendGet(PathUtil.获取最新版本, "");
                Result<UpdateVersion> res = new Result<>();
                res = TaskUtil.handle(res, result, new TypeToken<Result<UpdateVersion>>() {
                }.getType());
                if (res.isFlag()) {
                    终端版本 = res.getData();
                }
            } catch (Exception e) {
                return 终端版本;
            }
            return 终端版本;
        }

        @Override
        protected void onPostExecute(UpdateVersion 终端版本) {
            try {
                // 获取当前软件版本
                int 当前版本 = LoginActivity.this.getPackageManager().getPackageInfo("com.hsproduce", 0).versionCode;
                if (null != 终端版本) {
                    int 最新版本 = Integer.valueOf(终端版本.getItemid());
                    // 版本判断
                    if (最新版本 > 当前版本) {
                        download(PathUtil.文件下载 + 终端版本.getDownloadPath());
                    } else {
                        Toast.makeText(LoginActivity.this, "已经是最新版本", Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (Exception e) {

            }

        }
    }

    //根据地址下载
    public void download(String url) {
        final DownloadManager dManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);
// 设置下载路径和文件名
        request.setDestinationInExternalPublicDir("download", "update.apk");
        request.setDescription("软件新版本下载");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setMimeType("application/vnd.android.package-archive");
// 设置为可被媒体扫描器找到
        request.allowScanningByMediaScanner();
// 设置为可见和可管理
        request.setVisibleInDownloadsUi(true);
// 获取此次下载的ID
        final long refernece = dManager.enqueue(request);
// 注册广播接收器，当下载完成时自动安装
        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        BroadcastReceiver receiver = new BroadcastReceiver() {

            public void onReceive(Context context, Intent intent) {
                long myDwonloadID = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (refernece == myDwonloadID) {
                    Intent install = new Intent(Intent.ACTION_VIEW);
                    Uri downloadFileUri = dManager.getUriForDownloadedFile(refernece);
                    install.setDataAndType(downloadFileUri, "application/vnd.android.package-archive");
                    install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(install);
                }
            }
        };
        registerReceiver(receiver, filter);
    }

}
