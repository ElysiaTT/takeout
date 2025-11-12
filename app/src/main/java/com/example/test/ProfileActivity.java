package com.example.test;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

/**
 * "我的"页面
 */
public class ProfileActivity extends AppCompatActivity {

    private TextView usernameText;
    private TextView userIdText;
    private TextView phoneText;
    private TextView emailText;
    private TextView cacheSize;
    private LinearLayout changePasswordLayout;
    private LinearLayout orderHistoryLayout;
    private LinearLayout clearCacheLayout;

    private UserManager userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // 设置标题
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("我的");
        }

        userManager = UserManager.getInstance();

        initViews();
        loadUserInfo();
        setupClickListeners();
    }

    /**
     * 初始化视图
     */
    private void initViews() {
        usernameText = findViewById(R.id.usernameText);
        userIdText = findViewById(R.id.userIdText);
        phoneText = findViewById(R.id.phoneText);
        emailText = findViewById(R.id.emailText);
        cacheSize = findViewById(R.id.cacheSize);
        changePasswordLayout = findViewById(R.id.changePasswordLayout);
        orderHistoryLayout = findViewById(R.id.orderHistoryLayout);
        clearCacheLayout = findViewById(R.id.clearCacheLayout);
    }

    /**
     * 加载用户信息
     */
    private void loadUserInfo() {
        User currentUser = userManager.getCurrentUser();
        if (currentUser != null) {
            usernameText.setText(currentUser.getUsername());
            userIdText.setText("ID: " + currentUser.getUserId());
            phoneText.setText(currentUser.getMaskedPhoneNumber());
            emailText.setText(currentUser.getMaskedEmail());
        }

        // 计算缓存大小
        updateCacheSize();
    }

    /**
     * 设置点击监听器
     */
    private void setupClickListeners() {
        // 修改密码
        changePasswordLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, ChangePasswordActivity.class);
                startActivity(intent);
            }
        });

        // 订单历史
        orderHistoryLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, OrderHistoryActivity.class);
                startActivity(intent);
            }
        });



        // 清除缓存
        clearCacheLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showClearCacheDialog();
            }
        });

        // 退出登录
        findViewById(R.id.logoutButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogoutDialog();
            }
        });
    }

    /**
     * 更新缓存大小显示
     */
    private void updateCacheSize() {
        try {
            File cacheDir = getCacheDir();
            long size = getDirSize(cacheDir);
            double mb = size / (1024.0 * 1024.0);
            cacheSize.setText(String.format("%.1f MB", mb));
        } catch (Exception e) {
            cacheSize.setText("0.0 MB");
        }
    }

    /**
     * 计算目录大小
     */
    private long getDirSize(File dir) {
        if (dir == null || !dir.exists()) {
            return 0;
        }

        long size = 0;
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    size += getDirSize(file);
                } else {
                    size += file.length();
                }
            }
        }
        return size;
    }

    /**
     * 显示清除缓存确认对话框
     */
    private void showClearCacheDialog() {
        new AlertDialog.Builder(this)
                .setTitle("清除缓存")
                .setMessage("确定要清除应用缓存吗？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        clearCache();
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    /**
     * 清除缓存
     */
    private void clearCache() {
        try {
            File cacheDir = getCacheDir();
            deleteDir(cacheDir);
            updateCacheSize();
            Toast.makeText(this, "缓存已清除", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "清除缓存失败", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 删除目录
     */
    private boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            if (children != null) {
                for (String child : children) {
                    boolean success = deleteDir(new File(dir, child));
                    if (!success) {
                        return false;
                    }
                }
            }
        }
        return dir != null && dir.delete();
    }

    /**
     * 显示退出登录确认对话框
     */
    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("退出登录")
                .setMessage("确定要退出登录吗？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        logout();
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    /**
     * 退出登录
     */
    private void logout() {
        // 清除用户状态
        userManager.logout();

        Toast.makeText(this, "已退出登录", Toast.LENGTH_SHORT).show();

        // 返回主页
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 页面重新显示时刷新用户信息
        loadUserInfo();
    }
}
