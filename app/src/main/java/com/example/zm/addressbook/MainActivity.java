package com.example.zm.addressbook;

import android.Manifest;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    TextView tv_btn;
    TextView tv_content;
    private static final int MY_PERMISSIONS_REQUEST_ADDRESSBOOK = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_btn = findViewById(R.id.tv_btn);
        tv_content = findViewById(R.id.tv_content);
        tv_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    ArrayList<String> permissionList = new ArrayList<String>();
                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                        permissionList.add(Manifest.permission.READ_CONTACTS);
                    }
                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                        permissionList.add(Manifest.permission.WRITE_CONTACTS);
                    }
                    if (permissionList.size() > 0) {
                        String requestPermissions[] = permissionList.toArray(new String[permissionList.size()]);
                        requestPermissions(requestPermissions, MY_PERMISSIONS_REQUEST_ADDRESSBOOK);
                    } else {
                        try {
                            String addressbook = ContactUtil.getContactInfo(MainActivity.this);
                            tv_content.setText(addressbook);
//                            addConnetion();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    try {
                        String addressbook = ContactUtil.getContactInfo(MainActivity.this);
                        tv_content.setText(addressbook);
//                        addConnetion();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_ADDRESSBOOK) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                try {
                    String addressbook = ContactUtil.getContactInfo(MainActivity.this);
                    tv_content.setText(addressbook);
//                    addConnetion();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(MainActivity.this, "需要打开通讯录权限！", Toast.LENGTH_LONG).show();
            }
        }
    }


    /**
     * 添加信息到通讯录
     */
    public void addConnetion() {
        //2 获取数据
        String name = "官方客服";
        String phone = "4000088888";

        //2.1 定义uri   raw:原始的
        Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
        Uri dataUri = Uri.parse("content://com.android.contacts/data");

        //2.2 先查询一下raw_contacts表中一共有几条数据 行数+1 就是contact_id的值
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        int count = cursor.getCount();
        int contact_id = count + 1; // 代表当前联系人的id

        //3 把数据插入到联系人数据库，由于联系人的数据库也是通过内容提供者暴露出来，所以我们直接通过内容解析者去操作数据库
        ContentValues values = new ContentValues();
        values.put("contact_id", contact_id);
        getContentResolver().insert(uri, values);

        //4 把name,phone,email插入到data表
        ContentValues nameValues = new ContentValues();
        nameValues.put("data1", name); // 把数据插入到data1列
        nameValues.put("raw_contact_id", contact_id); // 告诉数据库我们插入的数据属于哪条联系人
        nameValues.put("mimetype", "vnd.android.cursor.item/name"); // 告诉数据库插入的数据的数据类型
        getContentResolver().insert(dataUri, nameValues);

        //5 把phone 插入到data表
        ContentValues phoneValues = new ContentValues();
        phoneValues.put("data1", phone); // 把数据插入到data1列
        phoneValues.put("raw_contact_id", contact_id); // 告诉数据库我们插入的数据属于哪条联系人
        phoneValues.put("mimetype", "vnd.android.cursor.item/phone_v2"); // 告诉数据库插入的数据的数据类型
        getContentResolver().insert(dataUri, phoneValues);

    }

}
