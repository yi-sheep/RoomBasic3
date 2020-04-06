# RoomBaslc3
演示视频

<video src="https://yi-sheep.github.io/RoomBasic2/Res/mp4/1.mp4"  autoplay loop muted>浏览器不支持播放该视频</video>

[无法播放点击](https://yi-sheep.github.io/RoomBasic2/Res/mp4/1.mp4)

Room 是在 SQLite 的基础上提供了一个抽象层，让用户能够在充分利用 SQLite 的强大功能的同时，获享更强健的数据库访问机制。
[官方文档](https://developer.android.google.cn/jetpack/androidx/releases/room?hl=zh_cn)

还是一样的从上一个教程继续，为了避免太乱，我将上一个教程结束的项目从新复制出了一个，这一期还是从上一期结束哪里开始，要是你还没看前面的教程，请移步[RoomBaslc2](https://github.com/yi-sheep/RoomBasic2)先看了再回来看这一期。

### 开始
这一期来将一下数据库版本的迁移，不影响已有的数据。

先来看看上一期结束时，数据表的结构和数据内容。

<img src="https://yi-sheep.github.io/RoomBasic3/Res/image/1.png"/>

我们要添加字段，在word类中添加，还记得word类是什么吗？是Entity数据表的实体类。
在这个类中添加
```java
// 添加一个新的字段
@ColumnInfo(name = "foo_data")
private boolean foo;
// 添加了新的字段需要添加对应的get/set方法，然Room会报错
public boolean isFoo() {
    return foo;
}
public void setFoo(boolean foo) {
    this.foo = foo;
}
```

然后我们需要在database的类中进行版本的修改和定义迁移规则。
这段代码中有之前的代码，前后对比，看看改了什么，仔细阅读
```java
// entities这个指定实体，version这个指定版本，exportSchema指定导出模式
@Database(entities = {Word.class},version = 2,exportSchema = false) // 当对数据表实体做了新的更改就需要改变这里的版本
public abstract class WordDatabase extends RoomDatabase {
    private static WordDatabase INSTANCE;
    static synchronized WordDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),WordDatabase.class,"Word_database")
//                    .fallbackToDestructiveMigration() // 这是破坏性的迁移，不保留原有的数据
                    .addMigrations(MIGRATION_1_2) // 添加迁移规则
                    .build();
        }
        return INSTANCE;
    }

    public abstract WordDao getWordDao();

    // 定义迁移规则,增加一个字段
    static final Migration MIGRATION_1_2 = new Migration(1,2) { // 这里传入的是从那一个版本迁移到那一个版本
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // 执行SQL语句进行迁移
            // 向表中添加一个字段，不影响之前的数据
            database.execSQL("ALTER TABLE word ADD COLUMN foo_data INTEGER NOT NULL DEFAULT 1");
        }
    };
}
```

添加一个字段我们知道了，那么删除一个字段怎么弄呢。
先把word类中刚刚添加的字段删掉。
```java
//          删掉
//    // 添加一个新的字段
//    @ColumnInfo(name = "foo_data")
//    private boolean foo;
//
//    // 添加了新的字段需要添加对应的get/set方法，然Room会报错
//    public boolean isFoo() {
//        return foo;
//    }
//
//    public void setFoo(boolean foo) {
//        this.foo = foo;
//    }
```

在sqlite里面不支持直接对一个表中的字段进行删除，我们就只能麻烦点分成四步来完成这个操作的迁移规则。在database中添加规则。

1、创建一个新的表，这个表中的字段全是原始表中的字段，你要删除的字段不要就行了。

2、将原始表中你保留下来的字段数据查询出来，插入新的表中。

3、删除原始表。

4、修改新的表名为原始表名。

```java
// entities这个指定实体，version这个指定版本，exportSchema指定导出模式
@Database(entities = {Word.class},version = 3,exportSchema = false) // 当对数据表实体做了新的更改就需要改变这里的版本
public abstract class WordDatabase extends RoomDatabase {
    private static WordDatabase INSTANCE;
    static synchronized WordDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),WordDatabase.class,"Word_database")
//                    .fallbackToDestructiveMigration() // 这是破坏性的迁移，不保留原有的数据
                    .addMigrations(MIGRATION_2_3) // 添加迁移规则
                    .build();
        }
        return INSTANCE;
    }

    public abstract WordDao getWordDao();

    // 定义迁移规则,增加一个字段
    ...

    // 删除一个字段，因为SQLite中不能直接删除一个字段，所以这里要麻烦点
    static final Migration MIGRATION_2_3 = new Migration(2,3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // 创建一个新的表，名字可以随便(word_temp)，表中的字段就是你不删除的字段
            database.execSQL("CREATE TABLE word_temp (_id INTEGER PRIMARY KEY NOT NULL,english_word TEXT," +
                    "chinese_meaning TEXT)");
            // 向上面创建的的表插入数据，数据从最初的表中查询，新的表有那个几个字段就查询那几个字段的数据，顺序也要一样
            database.execSQL("INSERT INTO word_temp(_id,english_word,chinese_meaning)" +
                    "SELECT _id,english_word,chinese_meaning FROM word");
            // 删除最初的表
            database.execSQL("DROP TABLE word");
            // 将新的表，名字改为最初那个表的名字
            database.execSQL("ALTER TABLE word_temp RENAME TO word");
        }
    };
}
```

还是要仔细阅读，我们修改了开始的数据库版本号(version)，将添加的规则改成了新定义的 MIGRATION_2_3。

---

感谢B站大佬longway777的[视频教程](https://www.bilibili.com/video/BV1b4411171L)

如果侵权，请联系qq:1766816333
立即删除

---