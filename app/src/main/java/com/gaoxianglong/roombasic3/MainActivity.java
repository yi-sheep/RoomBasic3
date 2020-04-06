package com.gaoxianglong.roombasic3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Switch;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{


    WordViewModel mViewModel; // ViewModel
    RecyclerView mRecyclerView;
    MyAdapter mAdapter,mAdapter2; // 定义两个适配器变量
    Switch mSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mViewModel = ViewModelProviders.of(this).get(WordViewModel.class);

        mAdapter = new MyAdapter(true,mViewModel); // 初始化第一个适配器，使用的是卡片item
        mAdapter2 = new MyAdapter(false,mViewModel); // 初始化第二个适配器，使用的是默认item

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this)); // 给recyclerView设置布局样式
        mRecyclerView.setAdapter(mAdapter); // 设置视频器，这里我们先设置成卡片item的适配器
        mSwitch = findViewById(R.id.switch1);
        mSwitch.setChecked(true); // 因为我们设置的默认适配器是卡片item的所以需要改一下，自己领悟一下这里
        // Switch的点击事件
        mSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // 判断当前switch处于开状态还是关状态
            if (isChecked) {
                // 开状态,设置卡片item适配器
                mRecyclerView.setAdapter(mAdapter);
            } else {
                // 关状态,设置默认item适配器
                mRecyclerView.setAdapter(mAdapter2);
            }
        });

        mViewModel.getQueryAllWordLive().observe(this,words -> {
            int temp = mAdapter.getItemCount();
            int temp2 = mAdapter2.getItemCount();
            // 在这里liveData监听到数据发生变化后
            // 将两个适配器的数据都有设置好，再通过调用notifyDataSetChanged()方法告诉recyclerView数据发生了变化你需要刷新界面

            mAdapter.setWords(words);
            mAdapter2.setWords(words);
            if (temp!=words.size()) {
                mAdapter.notifyDataSetChanged();
                mAdapter2.notifyDataSetChanged();
            }
        });
        findViewById(R.id.button).setOnClickListener(this);
        findViewById(R.id.button2).setOnClickListener(this);
        findViewById(R.id.button3).setOnClickListener(this);
        findViewById(R.id.button4).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button:
                Word word1 = new Word("Hello!","你好！"); // 创建两个对象传入不同的数据
                Word word2 = new Word("Word.","世界。");
                mViewModel.insertWord(word1,word2);
                break;
            case R.id.button2:
                Word word3 = new Word("Hi!","你好！"); // 创建一个对象，传入要更新成的数据
                word3.setId(1); // 设置id，根据id修改数据，要修改那一个数据就设置那一个id
                mViewModel.updateWord(word3);
                break;
            case R.id.button3:
                mViewModel.deleteAllWord();
                break;
            case R.id.button4:
                Word word4 = new Word(); // 这创建一个空对象就可以了
                word4.setId(2); // 根据id删除
                mViewModel.deleteWord(word4);
                break;
        }
    }
}
