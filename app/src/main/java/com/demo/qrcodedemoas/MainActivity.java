package com.demo.qrcodedemoas;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.WriterException;
import com.zxing.activity.CaptureActivity;
import com.zxing.encoding.CodeCreator;
import com.zxing.util.DensityUtil;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_SCAN_CONTENT = 0x30;
    private static final int REQUEST_OPEN_CAMERA = 0x31;
    private Button btnScan;
    private TextView tvScanResult;
    private EditText edtInput;
    private Button btnGenCode;
    private ImageView ivCode;


    private static final String DECODED_CONTENT_KEY = "codedContent";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnScan = (Button) findViewById(R.id.btn_scan);
        tvScanResult = (TextView) findViewById(R.id.tv_scanResult);
        edtInput = (EditText) findViewById(R.id.edt_input);
        btnGenCode = (Button) findViewById(R.id.btn_genCode);
        ivCode = (ImageView) findViewById(R.id.iv_code);

        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 启动包含扫描二维码功能的Activity
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    //申请权限，REQUEST_OPEN_CAMERA是自定义的常量
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA},
                            REQUEST_OPEN_CAMERA);
                } else {
                    //有权限，直接跳转
                    scan();
                }
            }
        });

        btnGenCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = edtInput.getEditableText().toString();
                if (TextUtils.isEmpty(input)) {
                    Toast.makeText(MainActivity.this, "输入的内容为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    // 启动二维码生成工具
                    // 利用qrcode2生成二维码，存在一个bug，中文字符会乱码；而qrcode不会
                    Bitmap bitmap = CodeCreator.createQRCode(input, DensityUtil.dp2px(MainActivity.this, 400));
//                    Bitmap bitmap = CodeCreator.createQRCode(input);
                    ivCode.setImageBitmap(bitmap);
                } catch (WriterException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_OPEN_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //申请成功，可以扫码
                scan();
            } else {
                Toast.makeText(this, "CAMERA PERMISSION DENIED", Toast.LENGTH_SHORT).show();
            }
            return;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_SCAN_CONTENT:
                if (resultCode == RESULT_OK) {
                    // 标识一定是result，这是zxing底层定义好的
//                    String result = data.getStringExtra("result");
                    String result = data.getStringExtra(DECODED_CONTENT_KEY);
                    tvScanResult.setText(result);

                    // 可以对获取到的结果进一步执行操作，比如：访问网络、作为一个参数提交等等
                }
                break;

            default:
                break;
        }
    }

    private void scan() {
        Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
        startActivityForResult(intent, REQUEST_SCAN_CONTENT);
    }
}
