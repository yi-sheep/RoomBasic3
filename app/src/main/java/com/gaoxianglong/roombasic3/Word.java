package com.gaoxianglong.roombasic3;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity // 使用注解指定这是一个数据表的实体
public class Word {
    @PrimaryKey(autoGenerate = true) // 根据下面的成员变量自动生成数据库列
    @ColumnInfo(name = "_id") // 指定在生成列时的列名称
    private int id;
    @ColumnInfo(name = "english_word") // 指定在生成列时的列名称
    private String word;
    @ColumnInfo(name = "chinese_meaning") // 指定在生成列时的列名称
    private String chineseMeaning;

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

    /**
     * 构造方法，方便创建对象
     */
    public Word() {}

    /**
     * 构造方法，方便创建对象时再添加数据
     * @param word
     * @param chineseMeaning
     */
    public Word(String word, String chineseMeaning) {
        this.word = word;
        this.chineseMeaning = chineseMeaning;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getChineseMeaning() {
        return chineseMeaning;
    }

    public void setChineseMeaning(String chineseMeaning) {
        this.chineseMeaning = chineseMeaning;
    }
}
