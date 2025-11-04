package com.example.test;

/**
 * 用户管理器（单例模式）
 * 管理当前登录用户的状态
 */
public class UserManager {
    private static UserManager instance;
    private User currentUser;
    private boolean isLoggedIn;

    private UserManager() {
        this.isLoggedIn = false;
        // 初始化一个默认测试用户
        initDefaultUser();
    }

    /**
     * 获取UserManager单例
     */
    public static UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }

    /**
     * 初始化默认测试用户
     */
    private void initDefaultUser() {
        // 创建一个默认用户用于测试
        this.currentUser = new User(
                "wangbian",
                "王扁",
                "123456",
                "13812341234",
                "u202315588@hust.edu.cn"
        );
        this.isLoggedIn = true;
    }

    /**
     * 用户登录
     */
    public boolean login(String username, String password) {
        // 这里应该调用真实的API进行验证
        // 暂时使用硬编码的验证逻辑
        if (username.equals("user_12345") && password.equals("123456")) {
            this.isLoggedIn = true;
            return true;
        }
        return false;
    }

    /**
     * 用户注册
     */
    public boolean register(String username, String password, String phoneNumber, String email) {
        // 这里应该调用真实的API进行注册
        // 暂时创建一个新用户
        String userId = "user_" + System.currentTimeMillis();
        this.currentUser = new User(userId, username, password, phoneNumber, email);
        this.isLoggedIn = true;
        return true;
    }

    /**
     * 用户登出
     */
    public void logout() {
        this.isLoggedIn = false;
        // 清除购物车
        ShoppingCart.getInstance().clear();
        // 清除订单
        OrderManager.getInstance().clearOrders();
    }

    /**
     * 修改密码
     */
    public boolean changePassword(String oldPassword, String newPassword) {
        if (currentUser == null || !isLoggedIn) {
            return false;
        }

        // 验证旧密码
        if (!currentUser.getPassword().equals(oldPassword)) {
            return false;
        }

        // 更新密码
        currentUser.setPassword(newPassword);
        return true;
    }

    /**
     * 更新用户信息
     */
    public void updateUserInfo(String phoneNumber, String email) {
        if (currentUser != null && isLoggedIn) {
            if (phoneNumber != null && !phoneNumber.isEmpty()) {
                currentUser.setPhoneNumber(phoneNumber);
            }
            if (email != null && !email.isEmpty()) {
                currentUser.setEmail(email);
            }
        }
    }

    /**
     * 获取当前登录用户
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * 检查是否已登录
     */
    public boolean isLoggedIn() {
        return isLoggedIn;
    }
}
