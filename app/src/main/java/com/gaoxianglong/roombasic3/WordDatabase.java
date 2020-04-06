package com.gaoxianglong.roombasic3;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

// entities这个指定实体，version这个指定版本，exportSchema指定导出模式
@Database(entities = {Word.class},version = 4,exportSchema = false) // 当对数据表实体做了新的更改就需要改变这里的版本
public abstract class WordDatabase extends RoomDatabase {
    private static WordDatabase INSTANCE;
    static synchronized WordDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),WordDatabase.class,"Word_database")
//                    .fallbackToDestructiveMigration() // 这是破坏性的迁移，不保留原有的数据
                    .addMigrations(MIGRATION_3_4) // 添加迁移规则
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

    // 定义迁移规则,增加一个字段
    static final Migration MIGRATION_3_4 = new Migration(3,4) { // 这里传入的是从那一个版本迁移到那一个版本
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // 执行SQL语句进行迁移
            // 向表中添加一个字段，不影响之前的数据
            database.execSQL("ALTER TABLE word ADD COLUMN chinese_invisible INTEGER NOT NULL DEFAULT 0");
        }
    };
}
