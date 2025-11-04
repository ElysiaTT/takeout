# 外卖应用 (Takeout App)

这是一个基于Android平台开发的外卖点餐应用，提供多种餐厅选择和便捷的购物车管理功能。

## 项目介绍

该应用允许用户浏览不同类型的餐厅，查看菜单，将食物添加到购物车，并进行结算。应用采用了清晰的分类导航和直观的用户界面，为用户提供流畅的点餐体验。

## 主要功能

- **餐厅选择**：提供中餐、意大利餐和汉堡店三种类型的餐厅选择
- **菜单浏览**：按类别浏览不同餐厅的菜单
- **购物车管理**：添加、删除和修改购物车中的商品数量
- **结算功能**：查看订单总额并完成结算
- **导航系统**：底部导航栏提供首页、订单和个人中心入口

## 技术栈

- **开发语言**：Java
- **开发框架**：Android SDK
- **UI组件**：RecyclerView, ConstraintLayout, CardView
- **设计模式**：单例模式（购物车）

## 项目结构

```
app/src/main/
├── java/com/example/test/
│   ├── MainActivity.java            # 通用菜单页面
│   ├── RestaurantListActivity.java  # 餐厅列表主页面
│   ├── ChineseRestaurantActivity.java  # 中餐餐厅页面
│   ├── ItalianRestaurantActivity.java  # 意大利餐厅页面
│   ├── BurgerRestaurantActivity.java   # 汉堡店页面
│   ├── CartActivity.java            # 购物车页面
│   ├── FoodItem.java                # 食物数据模型
│   ├── CartItem.java                # 购物车项目模型
│   ├── ShoppingCart.java            # 购物车管理类（单例）
│   ├── FoodItemAdapter.java         # 食物列表适配器
│   └── CartAdapter.java             # 购物车列表适配器
└── res/
    ├── layout/                      # 布局文件
    └── values/                      # 资源文件
```

## 数据模型

### FoodItem
表示菜单中的单个食物项，包含以下属性：
- 名称 (name)
- 描述 (description)
- 价格 (price)
- 分类 (category)
- 图片URL (imageUrl)

### CartItem
表示购物车中的单个项目，包含以下属性：
- 食物项 (foodItem)
- 数量 (quantity)
- 总价计算方法 (getTotalPrice())

### ShoppingCart
采用单例模式实现的购物车管理类，提供以下功能：
- 添加商品
- 移除商品
- 更新商品数量
- 获取购物车中的所有项目

## 使用说明

1. 启动应用后，在餐厅列表页面选择想要点餐的餐厅类型
2. 在餐厅菜单页面，浏览不同类别的食物
3. 点击食物项将其添加到购物车
4. 点击底部的购物车按钮查看购物车内容
5. 在购物车页面可以修改商品数量或移除商品
6. 点击结算按钮完成订单

## 开发环境

- Android Studio
- Android SDK 最新版本
- Java 开发工具包 (JDK)

## 安装指南

1. 克隆或下载此项目到本地
2. 使用Android Studio打开项目
3. 等待Gradle同步完成
4. 连接Android设备或启动模拟器
5. 点击运行按钮安装并启动应用

## 未来功能规划

- 实现用户登录和注册功能
- 添加支付系统集成
- 实现订单历史记录查询
- 增加餐厅评分和评价功能
- 添加实时订单跟踪功能

## 许可证

此项目仅供学习和演示目的使用。