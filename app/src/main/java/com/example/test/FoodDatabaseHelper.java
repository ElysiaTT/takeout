package com.example.test;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class FoodDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "takeout.db";
    private static final int DATABASE_VERSION = 11; // 再次增加版本号，强制重建数据库以加载新增的饮品
    private Context mContext;

    // 表名
    public static final String TABLE_BURGER_FOOD = "burger_food";
    public static final String TABLE_CHINESE_FOOD = "chinese_food";
    public static final String TABLE_DONGYI_FOOD = "dongyi_food";
    public static final String TABLE_ITALIAN_FOOD = "italian_food";

    // 列名
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_PRICE = "price";
    public static final String COLUMN_CATEGORY = "category";
    public static final String COLUMN_IMAGE_URL = "image_url";

    // 创建表的SQL语句
    private String getCreateTableSQL(String tableName) {
        return "CREATE TABLE " + tableName + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT NOT NULL, " +
                COLUMN_DESCRIPTION + " TEXT NOT NULL, " +
                COLUMN_PRICE + " REAL NOT NULL, " +
                COLUMN_CATEGORY + " TEXT NOT NULL, " +
                COLUMN_IMAGE_URL + " TEXT DEFAULT '')";
    }

    public FoodDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 创建所有餐厅的菜品表
        db.execSQL(getCreateTableSQL(TABLE_BURGER_FOOD));
        db.execSQL(getCreateTableSQL(TABLE_CHINESE_FOOD));
        db.execSQL(getCreateTableSQL(TABLE_DONGYI_FOOD));
        db.execSQL(getCreateTableSQL(TABLE_ITALIAN_FOOD));

        // 从JSON文件初始化菜品数据
        initializeFoodFromJsonInternal(db, TABLE_CHINESE_FOOD, "foods/chinese_foods.json");
        initializeFoodFromJsonInternal(db, TABLE_BURGER_FOOD, "foods/burger_foods.json");
        initializeFoodFromJsonInternal(db, TABLE_DONGYI_FOOD, "foods/dongyi_foods.json");
        initializeFoodFromJsonInternal(db, TABLE_ITALIAN_FOOD, "foods/italian_foods.json");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 升级数据库时删除旧表并创建新表
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BURGER_FOOD);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHINESE_FOOD);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DONGYI_FOOD);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITALIAN_FOOD);
        onCreate(db);
    }
    
    // 保留原始方法名称但重定向到内部方法
    private void initializeFoodFromJson(SQLiteDatabase db, String tableName, String jsonFilePath) {
        initializeFoodFromJsonInternal(db, tableName, jsonFilePath);
    }

    // 获取指定餐厅的所有菜品
    public List<FoodItem> getAllFoodItems(String tableName) {
        List<FoodItem> foodItems = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;
        
        try {
            db = this.getReadableDatabase();
            if (db == null) {
                Log.e("FoodDatabase", "Database is null for table: " + tableName);
                return foodItems;
            }
            
            cursor = db.query(tableName, null, null, null, null, null, null);
            Log.d("FoodDatabase", "Querying table: " + tableName + ", cursor count: " + (cursor != null ? cursor.getCount() : 0));

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
                    String description = cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION));
                    double price = cursor.getDouble(cursor.getColumnIndex(COLUMN_PRICE));
                    String category = cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY));
                    String imageUrl = cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE_URL));

                    FoodItem foodItem = new FoodItem(name, description, price, category);
                    foodItem.setImageUrl(imageUrl);
                    foodItems.add(foodItem);
                } while (cursor.moveToNext());
                Log.d("FoodDatabase", "Retrieved " + foodItems.size() + " items from " + tableName);
            } else {
                Log.d("FoodDatabase", "No data found in table: " + tableName);
            }
        } catch (Exception e) {
            Log.e("FoodDatabase", "Error in getAllFoodItems: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            // 移除db.close()调用，只关闭光标
        }
        return foodItems;
    }

    // 根据分类获取菜品
    public List<FoodItem> getFoodItemsByCategory(String tableName, String category) {
        List<FoodItem> foodItems = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;
        
        try {
            db = this.getReadableDatabase();
            if (db == null) {
                Log.e("FoodDatabase", "Database is null for table: " + tableName + ", category: " + category);
                return foodItems;
            }

            String selection = COLUMN_CATEGORY + " = ?";
            String[] selectionArgs = { category };

            cursor = db.query(tableName, null, selection, selectionArgs, null, null, null);
            Log.d("FoodDatabase", "Querying table: " + tableName + ", category: " + category + ", cursor count: " + (cursor != null ? cursor.getCount() : 0));

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
                    String description = cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION));
                    double price = cursor.getDouble(cursor.getColumnIndex(COLUMN_PRICE));
                    String imageUrl = cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE_URL));

                    FoodItem foodItem = new FoodItem(name, description, price, category);
                    foodItem.setImageUrl(imageUrl);
                    foodItems.add(foodItem);
                } while (cursor.moveToNext());
                Log.d("FoodDatabase", "Retrieved " + foodItems.size() + " items from " + tableName + " for category: " + category);
            } else {
                Log.d("FoodDatabase", "No data found in table: " + tableName + " for category: " + category);
            }
        } catch (Exception e) {
            Log.e("FoodDatabase", "Error in getFoodItemsByCategory: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            // 移除db.close()调用，只关闭光标
        }
        return foodItems;
    }

    // 添加菜品
    public long addFoodItem(String tableName, FoodItem foodItem) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, foodItem.getName());
        values.put(COLUMN_DESCRIPTION, foodItem.getDescription());
        values.put(COLUMN_PRICE, foodItem.getPrice());
        values.put(COLUMN_CATEGORY, foodItem.getCategory());
        values.put(COLUMN_IMAGE_URL, foodItem.getImageUrl());

        long id = db.insert(tableName, null, values);
        // 移除db.close()调用
        return id;
    }

    // 删除菜品
    public void deleteFoodItem(String tableName, String foodName) {
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = COLUMN_NAME + " = ?";
        String[] whereArgs = { foodName };
        db.delete(tableName, whereClause, whereArgs);
        // 移除db.close()调用
    }

    // 初始化汉堡店菜品数据
    // 从JSON文件初始化菜品数据
    // 从JSON初始化数据的公共方法，可在运行时调用以刷新数据
    public void refreshDataFromJson(String tableName, String jsonFilePath) {
        Log.d("FoodDatabase", "refreshDataFromJson: 开始刷新数据 - 表: " + tableName + ", 文件: " + jsonFilePath);
        
        SQLiteDatabase db = null;
        try {
            // 1. 安全地获取数据库连接
            db = this.getWritableDatabase();
            Log.d("FoodDatabase", "成功获取数据库连接");
            
            // 2. 使用事务进行数据清理和重建
            db.beginTransaction();
            Log.d("FoodDatabase", "事务开始");
            
            // 3. 删除表并重建
            Log.d("FoodDatabase", "删除并重建表" + tableName);
            db.execSQL("DROP TABLE IF EXISTS " + tableName);
            db.execSQL(getCreateTableSQL(tableName));
            Log.d("FoodDatabase", "表" + tableName + "已删除并重建");
            
            // 4. 从JSON重新加载数据
            Log.d("FoodDatabase", "准备从JSON文件重新加载数据");
            initializeFoodFromJsonInternal(db, tableName, jsonFilePath);
            
            // 5. 提交事务
            db.setTransactionSuccessful();
            Log.d("FoodDatabase", "事务提交成功");
            
            // 6. 验证数据是否正确加载
            Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + tableName, null);
            if (cursor.moveToFirst()) {
                int count = cursor.getInt(0);
                Log.d("FoodDatabase", "验证数据: 表" + tableName + "中共有" + count + "条记录");
            }
            cursor.close();
            
        } catch (Exception e) {
            Log.e("FoodDatabase", "刷新数据时出错: " + e.getMessage(), e);
        } finally {
            // 结束事务
            if (db != null && db.inTransaction()) {
                db.endTransaction();
                Log.d("FoodDatabase", "事务结束");
            }
            // 关闭数据库连接
            if (db != null && db.isOpen()) {
                db.close();
                Log.d("FoodDatabase", "数据库连接已关闭");
            }
        }
        Log.d("FoodDatabase", "refreshDataFromJson: 数据刷新操作完成");
    }
    
    // 公开方法，直接从JSON加载数据并更新到数据库，不依赖缓存
    public List<FoodItem> loadFoodFromJsonDirectly(String tableName, String jsonFilePath) {
        Log.d("FoodDatabase", "loadFoodFromJsonDirectly: 直接从JSON加载数据 - " + jsonFilePath + " (时间戳: " + System.currentTimeMillis() + ")");
        
        SQLiteDatabase db = null;
        List<FoodItem> loadedItems = new ArrayList<>();
        
        try {
            // 获取数据库连接
            db = this.getWritableDatabase();
            if (db == null) {
                Log.e("FoodDatabase", "无法获取数据库连接");
                return loadedItems;
            }
            
            // 开始事务
            db.beginTransaction();
            
            try {
                // 清除现有数据
                db.delete(tableName, null, null);
                Log.d("FoodDatabase", "已清除表" + tableName + "中的所有数据");
                
                // 直接从JSON加载数据
                loadedItems = loadJsonAndInsertToDatabase(db, tableName, jsonFilePath);
                
                // 标记事务成功
                db.setTransactionSuccessful();
                Log.d("FoodDatabase", "事务成功，加载了" + loadedItems.size() + "个菜品");
                
            } finally {
                // 结束事务
                db.endTransaction();
            }
            
        } catch (Exception e) {
            Log.e("FoodDatabase", "数据库操作错误: " + e.getMessage(), e);
        } finally {
            // 关闭数据库连接
            if (db != null) {
                db.close();
            }
        }
        
        return loadedItems;
    }
    
    // 内部方法，执行实际的JSON数据加载
    private void initializeFoodFromJsonInternal(SQLiteDatabase db, String tableName, String jsonFilePath) {
        Log.d("FoodDatabase", "initializeFoodFromJsonInternal: 开始从JSON加载数据 - " + jsonFilePath + " (时间戳: " + System.currentTimeMillis() + ")");
        try {
            // 使用新的方法加载数据
            loadJsonAndInsertToDatabase(db, tableName, jsonFilePath);
        } catch (Exception e) {
            Log.e("FoodDatabase", "数据加载错误: " + e.getMessage(), e);
            // 如果JSON解析失败，使用备用硬编码数据
            Log.d("FoodDatabase", "使用备用硬编码数据初始化表: " + tableName);
            if (tableName.equals(TABLE_BURGER_FOOD)) {
                initializeBurgerFood(db);
            } else if (tableName.equals(TABLE_CHINESE_FOOD)) {
                initializeChineseFood(db);
            } else if (tableName.equals(TABLE_DONGYI_FOOD)) {
                initializeDongyiFood(db);
            } else if (tableName.equals(TABLE_ITALIAN_FOOD)) {
                initializeItalianFood(db);
            }
        }
    }
    
    // 内部方法，执行实际的JSON数据加载并返回加载的项目
    private List<FoodItem> loadJsonAndInsertToDatabase(SQLiteDatabase db, String tableName, String jsonFilePath) {
        Log.d("FoodDatabase", "loadJsonAndInsertToDatabase: 开始从JSON加载数据 - " + jsonFilePath + " (时间戳: " + System.currentTimeMillis() + ")");
        
        List<FoodItem> loadedItems = new ArrayList<>();
        
        try {
            AssetManager assetManager = mContext.getAssets();
            Log.d("FoodDatabase", "尝试打开JSON文件: " + jsonFilePath);
            
            StringBuilder stringBuilder = new StringBuilder();
            
            // 每次都创建新的输入流，确保不使用缓存
            try (InputStreamReader isr = new InputStreamReader(
                    assetManager.open(jsonFilePath), "UTF-8")) {
                try (BufferedReader reader = new BufferedReader(isr)) {
                    String line;
                    Log.d("FoodDatabase", "开始读取文件内容");
                    while ((line = reader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    Log.d("FoodDatabase", "文件内容读取完成");
                }
            }
            
            String json = stringBuilder.toString();
            Log.d("FoodDatabase", "JSON字符串长度: " + json.length() + " 字符");
            // 为了调试，记录JSON内容和武汉热干面相关的部分
            Log.d("FoodDatabase", "JSON预览: " + json.substring(0, Math.min(200, json.length())));
            
            // 检查JSON中是否包含武汉热干面相关内容
            if (json.contains("武汉热干面")) {
                int startIndex = json.indexOf("武汉热干面") - 20; // 向前查找更多上下文
                if (startIndex < 0) startIndex = 0;
                int endIndex = json.indexOf("武汉热干面") + 60; // 向后查找更多上下文
                if (endIndex > json.length()) endIndex = json.length();
                Log.d("FoodDatabase", "武汉热干面相关JSON片段: " + json.substring(startIndex, endIndex));
            }
            
            // 解析JSON
            JSONArray jsonArray = new JSONArray(json);
            Log.d("FoodDatabase", "JSON数组长度: " + jsonArray.length());
            int totalItems = jsonArray.length();
            int addedCount = 0;
            
            for (int i = 0; i < totalItems; i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String name = jsonObject.getString("name");
                String description = jsonObject.getString("description");
                double price = jsonObject.getDouble("price");
                String category = jsonObject.getString("category");
                
                // 记录价格信息，特别是武汉热干面
                Log.d("FoodDatabase", "读取菜品: " + name + ", 价格: " + price + ", 分类: " + category);
                
                FoodItem foodItem = new FoodItem(name, description, price, category);
                // 如果JSON中有image_url字段，则设置
                if (jsonObject.has("image_url")) {
                    foodItem.setImageUrl(jsonObject.getString("image_url"));
                }
                
                // 插入数据库
                long id = addFoodItem(db, tableName, foodItem);
                if (id > 0) {
                    addedCount++;
                    // 添加到返回列表
                    loadedItems.add(foodItem);
                }
            }
            
            Log.d("FoodDatabase", "从JSON加载完成，总共" + totalItems + "个项目，成功添加" + addedCount + "个项目到" + tableName);
            
        } catch (Exception e) {
            Log.e("FoodDatabase", "从JSON加载数据时出错: " + e.getMessage(), e);
            // 不再抛出异常，因为已经在调用方法中处理
        }
        
        return loadedItems;
    }

    // 备用硬编码数据初始化方法（当JSON读取失败时使用）
    private void initializeBurgerFood(SQLiteDatabase db) {
        try {
            addBurgerFoodItem(db, new FoodItem("经典芝士牛肉汉堡 🏆", "100%纯牛肉饼，搭配融化的车打芝士和新鲜蔬菜。", 32.00, "经典汉堡"));
            addBurgerFoodItem(db, new FoodItem("双层牛肉汉堡", "双倍牛肉饼，双倍满足感！", 45.00, "经典汉堡"));
            addBurgerFoodItem(db, new FoodItem("培根汉堡", "酥脆培根配多汁牛肉饼，绝配！", 38.00, "经典汉堡"));
            addBurgerFoodItem(db, new FoodItem("鸡肉汉堡", "炸鸡排配生菜番茄，清爽美味。", 28.00, "经典汉堡"));

            addBurgerFoodItem(db, new FoodItem("闪电特级汉堡", "三层牛肉饼！芝士、培根、洋葱圈全都有！", 68.00, "特制汉堡"));
            addBurgerFoodItem(db, new FoodItem("墨西哥辣堡", "墨西哥辣椒、芝士、莎莎酱，火辣过瘾！🌶️🌶️", 42.00, "特制汉堡"));
            addBurgerFoodItem(db, new FoodItem("蘑菇瑞士汉堡", "蘑菇配瑞士芝士，口感浓郁。", 48.00, "特制汉堡"));
            addBurgerFoodItem(db, new FoodItem("BBQ汉堡", "BBQ酱配洋葱圈，美式风味十足。", 45.00, "特制汉堡"));
            addBurgerFoodItem(db, new FoodItem("素食汉堡", "植物肉饼，健康环保不失美味。", 38.00, "特制汉堡"));

            addBurgerFoodItem(db, new FoodItem("炸鸡桶", "外皮酥脆，鸡肉鲜嫩多汁。6块装。", 48.00, "小食拼盘"));
            addBurgerFoodItem(db, new FoodItem("鸡块拼盘", "金黄酥脆的鸡块，配多种酱料。10块装。", 32.00, "小食拼盘"));
            addBurgerFoodItem(db, new FoodItem("薯条（大份）", "超大份金黄薯条，外酥内软。", 18.00, "小食拼盘"));
            addBurgerFoodItem(db, new FoodItem("洋葱圈", "香脆洋葱圈，停不下来的美味。", 22.00, "小食拼盘"));
            addBurgerFoodItem(db, new FoodItem("鸡翅拼盘", "烤鸡翅6只，配蜂蜜芥末酱。", 35.00, "小食拼盘"));
            addBurgerFoodItem(db, new FoodItem("芝士薯条", "薯条上淋满浓郁芝士酱。", 25.00, "小食拼盘"));

            addBurgerFoodItem(db, new FoodItem("巧克力奶昔 🏆", "冰爽绵密，浓郁巧克力风味。", 22.00, "奶昔冰沙"));
            addBurgerFoodItem(db, new FoodItem("香草奶昔", "经典香草口味，清甜顺滑。", 20.00, "奶昔冰沙"));
            addBurgerFoodItem(db, new FoodItem("草莓奶昔", "新鲜草莓制作，果香浓郁。", 22.00, "奶昔冰沙"));
            addBurgerFoodItem(db, new FoodItem("奥利奥奶昔", "奥利奥饼干碎配冰淇淋，香甜可口。", 25.00, "奶昔冰沙"));
            addBurgerFoodItem(db, new FoodItem("芒果冰沙", "热带芒果风味，清凉解暑。", 20.00, "奶昔冰沙"));
            addBurgerFoodItem(db, new FoodItem("可乐（大杯）", "冰镇可口可乐，畅爽无比。", 12.00, "奶昔冰沙"));
        } catch (Exception e) {
            Log.e("FoodDatabase", "Error initializing burger food: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void addBurgerFoodItem(SQLiteDatabase db, FoodItem item) {
        addFoodItem(db, TABLE_BURGER_FOOD, item);
    }

    // 初始化中餐菜品数据
    private void initializeChineseFood(SQLiteDatabase db) {
        try {
            addChineseFoodItem(db, new FoodItem("北京烤鸭", "果木挂炉烤制，外皮酥香，肉质鲜嫩。搭配全套饼酱。", 128.00, "招牌推荐"));
            addChineseFoodItem(db, new FoodItem("小笼汤包", "皮薄馅大，汤汁饱满，请小心烫口。", 25.00, "招牌推荐"));
            addChineseFoodItem(db, new FoodItem("东坡肉", "肥而不腻，入口即化的经典杭州菜。", 68.00, "招牌推荐"));

            addChineseFoodItem(db, new FoodItem("麻婆豆腐", "传统川味，麻辣鲜香，下饭神器。🌶️", 32.00, "川湘风味"));
            addChineseFoodItem(db, new FoodItem("水煮鱼", "鲜嫩鱼片，麻辣鲜香，配菜丰富。🌶️🌶️", 88.00, "川湘风味"));
            addChineseFoodItem(db, new FoodItem("剁椒鱼头", "湘菜名品，鲜辣开胃，鱼肉细嫩。🌶️🌶️", 98.00, "川湘风味"));
            addChineseFoodItem(db, new FoodItem("宫保鸡丁", "酸甜微辣，鸡肉嫩滑，花生酥脆。", 38.00, "川湘风味"));

            addChineseFoodItem(db, new FoodItem("扬州炒饭", "粒粒分明，配料丰富，色香味俱全。", 28.00, "主食"));
            addChineseFoodItem(db, new FoodItem("担担面", "四川特色面食，麻辣鲜香。🌶️", 22.00, "主食"));
            addChineseFoodItem(db, new FoodItem("馄饨", "皮薄馅嫩，汤清味美。", 20.00, "主食"));
            addChineseFoodItem(db, new FoodItem("葱油拌面", "简单美味，葱香浓郁。", 18.00, "主食"));

            addChineseFoodItem(db, new FoodItem("酸辣汤", "酸辣开胃，配料丰富。", 25.00, "汤羹"));
            addChineseFoodItem(db, new FoodItem("西湖牛肉羹", "鲜嫩滑润，营养丰富。", 32.00, "汤羹"));
            addChineseFoodItem(db, new FoodItem("银耳莲子羹", "清甜滋润，养生佳品。", 18.00, "汤羹"));

            addChineseFoodItem(db, new FoodItem("酸梅汤", "消暑解渴，酸甜可口。", 12.00, "饮品"));
            addChineseFoodItem(db, new FoodItem("豆浆", "现磨豆浆，营养健康。", 8.00, "饮品"));
            addChineseFoodItem(db, new FoodItem("菊花茶", "清热降火，清香怡人。", 10.00, "饮品"));
        } catch (Exception e) {
            Log.e("FoodDatabase", "Error initializing chinese food: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void addChineseFoodItem(SQLiteDatabase db, FoodItem item) {
        addFoodItem(db, TABLE_CHINESE_FOOD, item);
    }

    // 初始化东一餐厅菜品数据
    private void initializeDongyiFood(SQLiteDatabase db) {
        try {
            addDongyiFoodItem(db, new FoodItem("川味担担面", "经典川味面食，芝麻酱香浓郁，花生碎增添口感，微辣开胃。", 4.00, "面食系列"));
            addDongyiFoodItem(db, new FoodItem("武汉热干面", "武汉特色早餐，芝麻酱拌面，劲道爽滑，配上榨菜丁和葱花。", 2.50, "面食系列"));
            addDongyiFoodItem(db, new FoodItem("炸酱面", "老北京风味，肉酱浓香，配黄瓜丝和豆芽，咸香适口。", 4.00, "面食系列"));
            addDongyiFoodItem(db, new FoodItem("香辣牛肉卤面", "精选牛肉块，卤汁浓郁，香辣入味，配软烂牛肉和青菜。", 12.00, "面食系列"));
            addDongyiFoodItem(db, new FoodItem("牛肉拉面", "手工拉制，面条劲道，牛肉汤底浓香，配炖煮牛肉片。", 6.00, "面食系列"));

            addDongyiFoodItem(db, new FoodItem("叉烧套餐", "广式叉烧，色泽红亮，甜咸适中，配米饭和时蔬，营养均衡。", 13.00, "套餐系列"));
            addDongyiFoodItem(db, new FoodItem("烤鸡套餐", "整只烤鸡腿，外焦里嫩，香气扑鼻，配米饭、青菜和例汤。", 12.00, "套餐系列"));
            addDongyiFoodItem(db, new FoodItem("鸭腿套餐", "卤制鸭腿，肉质鲜嫩，咸香入味，搭配米饭和时令蔬菜。", 10.00, "套餐系列"));
            addDongyiFoodItem(db, new FoodItem("鸡腿套餐", "香煎鸡腿，皮脆肉嫩，汁水丰富，配米饭、蔬菜和汤品。", 12.00, "套餐系列"));
            addDongyiFoodItem(db, new FoodItem("鹅腿套餐", "卤鹅腿，肉质紧实，香味浓郁，搭配米饭和小菜，饱腹感强。", 11.00, "套餐系列"));
            addDongyiFoodItem(db, new FoodItem("孜然肉片套餐", "孜然羊肉片，香辣可口，配洋葱青椒，附米饭和蔬菜。", 10.00, "套餐系列"));
            addDongyiFoodItem(db, new FoodItem("鸡排套餐", "炸鸡排，外酥里嫩，金黄诱人，配米饭、沙拉和玉米浓汤。", 10.00, "套餐系列"));
            addDongyiFoodItem(db, new FoodItem("红烧肉套餐", "家常红烧肉，肥而不腻，入口即化，色泽红亮，配米饭和青菜。", 15.00, "套餐系列"));

            addDongyiFoodItem(db, new FoodItem("酥饼", "传统武汉酥饼，层层酥脆，内馅咸香，刚出炉最好吃。", 2.00, "蒸炸系列"));
            addDongyiFoodItem(db, new FoodItem("面窝", "武汉特色小吃，外酥内软，中空造型，配豆浆最佳。", 2.00, "蒸炸系列"));
            addDongyiFoodItem(db, new FoodItem("牛肉馅饼", "现做现卖，牛肉馅料丰富，外皮金黄酥脆，肉汁饱满。", 4.00, "蒸炸系列"));
            addDongyiFoodItem(db, new FoodItem("蒸饺", "手工蒸饺，皮薄馅大，鲜香多汁，蘸醋更美味。", 5.00, "蒸炸系列"));
            addDongyiFoodItem(db, new FoodItem("油条", "传统早餐，炸至金黄，外酥内软，配豆浆或粥都好吃。", 2.00, "蒸炸系列"));
            addDongyiFoodItem(db, new FoodItem("酱肉包", "精选猪肉馅，酱香浓郁，皮薄馅多，热气腾腾。", 1.00, "蒸炸系列"));
            addDongyiFoodItem(db, new FoodItem("粉条肉沫包", "粉条配肉沫，口感丰富，咸鲜适口，物美价廉。", 1.00, "蒸炸系列"));
            addDongyiFoodItem(db, new FoodItem("韭菜鸡蛋包", "素馅包子，韭菜鸡蛋，鲜香扑鼻，清淡营养。", 0.80, "蒸炸系列"));
            addDongyiFoodItem(db, new FoodItem("虾仁包", "鲜虾仁馅，Q弹美味，皮软馅鲜，配料讲究。", 1.20, "蒸炸系列"));
            addDongyiFoodItem(db, new FoodItem("蒸鸡蛋", "嫩滑蒸蛋，入口即化，营养丰富，老少皆宜。", 0.80, "蒸炸系列"));
        } catch (Exception e) {
            Log.e("FoodDatabase", "Error initializing dongyi food: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void addDongyiFoodItem(SQLiteDatabase db, FoodItem item) {
        addFoodItem(db, TABLE_DONGYI_FOOD, item);
    }

    // 初始化意大利餐厅菜品数据
    private void initializeItalianFood(SQLiteDatabase db) {
        try {
            addItalianFoodItem(db, new FoodItem("玛格丽特披萨 🏆", "意式薄底，圣马扎诺番茄酱与水牛马苏里拉奶酪的经典组合。", 68.00, "披萨"));
            addItalianFoodItem(db, new FoodItem("四季披萨", "四种口味的完美结合：火腿、蘑菇、朝鲜蓟、橄榄。", 78.00, "披萨"));
            addItalianFoodItem(db, new FoodItem("海鲜披萨", "鲜虾、青口贝、鱿鱼等新鲜海鲜，海洋的味道。", 88.00, "披萨"));
            addItalianFoodItem(db, new FoodItem("意式辣肠披萨", "经典辣肠片配番茄酱和马苏里拉奶酪。🌶️", 75.00, "披萨"));

            addItalianFoodItem(db, new FoodItem("海鲜意面", "新鲜海鲜与番茄汁翻炒，意面充分吸收汤汁精华。", 85.00, "意面"));
            addItalianFoodItem(db, new FoodItem("肉酱意面", "经典博洛尼亚肉酱，慢火熬制3小时。", 58.00, "意面"));
            addItalianFoodItem(db, new FoodItem("奶油培根意面", "意式培根与奶油的完美融合，口感丰富。", 65.00, "意面"));
            addItalianFoodItem(db, new FoodItem("松露野菇意面", "黑松露与多种野生蘑菇，奢华美味。", 128.00, "意面"));

            addItalianFoodItem(db, new FoodItem("凯撒沙拉", "新鲜罗马生菜，凯撒酱汁，帕玛森芝士碎。", 38.00, "前菜沙拉"));
            addItalianFoodItem(db, new FoodItem("意式火腿拼盘", "帕尔马火腿、意式萨拉米、橄榄、芝士。", 88.00, "前菜沙拉"));
            addItalianFoodItem(db, new FoodItem("卡布里沙拉", "番茄、水牛芝士、罗勒，意大利国旗色。", 45.00, "前菜沙拉"));
            addItalianFoodItem(db, new FoodItem("烤蔬菜拼盘", "时令蔬菜橄榄油烤制，健康美味。", 42.00, "前菜沙拉"));

            addItalianFoodItem(db, new FoodItem("提拉米苏 🏆", "马斯卡彭奶酪与咖啡酒手指饼干的完美融合。", 38.00, "甜品"));
            addItalianFoodItem(db, new FoodItem("意式奶冻", "奶香浓郁，口感细腻，配水果酱。", 32.00, "甜品"));
            addItalianFoodItem(db, new FoodItem("西西里卷", "酥脆外壳，香浓奶油馅料。", 35.00, "甜品"));

            addItalianFoodItem(db, new FoodItem("基安蒂红葡萄酒", "托斯卡纳经典，适合搭配披萨和意面。", 188.00, "葡萄酒"));
            addItalianFoodItem(db, new FoodItem("普罗塞克起泡酒", "清爽怡人，适合开胃。", 158.00, "葡萄酒"));
            addItalianFoodItem(db, new FoodItem("意式柠檬酒", "餐后酒，清新解腻。", 58.00, "葡萄酒"));
        } catch (Exception e) {
            Log.e("FoodDatabase", "Error initializing italian food: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void addItalianFoodItem(SQLiteDatabase db, FoodItem item) {
        addFoodItem(db, TABLE_ITALIAN_FOOD, item);
    }

    private long addFoodItem(SQLiteDatabase db, String tableName, FoodItem item) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, item.getName());
        values.put(COLUMN_DESCRIPTION, item.getDescription());
        values.put(COLUMN_PRICE, item.getPrice());
        values.put(COLUMN_CATEGORY, item.getCategory());
        values.put(COLUMN_IMAGE_URL, item.getImageUrl());

        return db.insert(tableName, null, values);
    }

    // 搜索菜品：在所有餐厅的菜品中搜索
    public List<FoodItem> searchFoodItems(String keyword) {
        List<FoodItem> foodItems = new ArrayList<>();

        if (keyword == null || keyword.trim().isEmpty()) {
            return foodItems;
        }

        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = this.getReadableDatabase();

            // 在所有餐厅表中搜索
            String[] tables = {TABLE_CHINESE_FOOD, TABLE_ITALIAN_FOOD, TABLE_BURGER_FOOD, TABLE_DONGYI_FOOD};

            for (String tableName : tables) {
                // 搜索菜品名称或描述中包含关键词的项目
                String selection = COLUMN_NAME + " LIKE ? OR " + COLUMN_DESCRIPTION + " LIKE ?";
                String searchPattern = "%" + keyword + "%";
                String[] selectionArgs = {searchPattern, searchPattern};

                cursor = db.query(tableName, null, selection, selectionArgs, null, null, null);
                Log.d("FoodDatabase", "Searching in table: " + tableName + ", found: " + (cursor != null ? cursor.getCount() : 0));

                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
                        String description = cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION));
                        double price = cursor.getDouble(cursor.getColumnIndex(COLUMN_PRICE));
                        String category = cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY));
                        String imageUrl = cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE_URL));

                        FoodItem foodItem = new FoodItem(name, description, price, category);
                        foodItem.setImageUrl(imageUrl);
                        // 设置餐厅名称，便于区分
                        foodItem.setRestaurantName(getRestaurantName(tableName));
                        foodItems.add(foodItem);
                    } while (cursor.moveToNext());
                }

                if (cursor != null) {
                    cursor.close();
                    cursor = null;
                }
            }

            Log.d("FoodDatabase", "Total search results: " + foodItems.size());

        } catch (Exception e) {
            Log.e("FoodDatabase", "Error searching food items: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return foodItems;
    }

    // 根据表名获取餐厅名称
    private String getRestaurantName(String tableName) {
        switch (tableName) {
            case TABLE_CHINESE_FOOD:
                return "中华小馆";
            case TABLE_ITALIAN_FOOD:
                return "意式厨房";
            case TABLE_BURGER_FOOD:
                return "闪电汉堡";
            case TABLE_DONGYI_FOOD:
                return "东一食堂";
            default:
                return "未知餐厅";
        }
    }
}