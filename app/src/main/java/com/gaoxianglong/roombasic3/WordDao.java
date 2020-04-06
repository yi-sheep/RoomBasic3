package com.gaoxianglong.roombasic3;

import android.view.View;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao // Database access object,数据库访问对象
public interface WordDao {
    @Insert // 使用注解的方式声明这是一个插入语句，传入对象数据
    void insertWords(Word... words); // 这里的...表示可以接收多个对象,返回值也可以是int返回插入多少行

    @Update // 声明这是一个更新语句
    void updateWords(Word... words); // 返回值也可以是int返回更新多少行

    @Delete // 声明这是一个删除语句
    void deleteWords(Word... words); // 返回值也可以是int返回删除多少行

    @Query("SELECT * FROM WORD ORDER BY _ID DESC") // 使用@Query注解可以执行后面跟的SQL语句,使用降序排序
//    List<Word> queryAllWord(); // 查询所有的数据
    LiveData<List<Word>> queryAllWordLive(); // 本身Room就是支持LiveData的

    @Query("DELETE FROM WORD")
    void deleteAllWord(); // 删除整个表
}
