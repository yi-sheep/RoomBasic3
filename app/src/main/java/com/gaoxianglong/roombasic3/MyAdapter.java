package com.gaoxianglong.roombasic3;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    List<Word> mWords = new ArrayList<>(); // 用于获取数据
    boolean useCardView; // 用于判断用户选择使用卡片item还是默认item

    /**
     * 传入数据对象
     * @param words
     */
    public void setWords(List<Word> words) {
        mWords = words;
    }

    /**
     * 在实例化当前适配器的时候传入一个布尔值
     * true表示使用卡片item
     * false表示使用默认item
     * @param useCardView
     */
    public MyAdapter(boolean useCardView) {
        this.useCardView = useCardView;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 实现父类RecyclerView.Adapter中的抽象方法，在这个方法中初始化item，然后返回这个itemView
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView;
        // 这里判断用户想要使用哪种item
        if (useCardView) {
            itemView = inflater.inflate(R.layout.item_c_layout, parent, false);
        } else {
            itemView = inflater.inflate(R.layout.item_layout, parent, false);
        }
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // 这也是实现的父类的抽象方法，在这个方法中，做数据的绑定
        Word word = mWords.get(position); // 通过position(当前是第几itemView)获取到word中的对应数据
        holder.textViewNumber.setText(String.valueOf(position - 1)); // 设置布局中显示序号的textView,因为position是从0开始的，所以需要加1
        holder.textViewEnglish.setText(word.getWord()); // 设置布局中显示单词的textView,通过获取到的word中对应数据对象获取到单词
        holder.textViewChinese.setText(word.getChineseMeaning()); // 设置显示中文意思的textView
        // 给item设置点击事件
        holder.itemView.setOnClickListener(v -> {
            // 这里定义一个URI，使用的百度翻译的地址，通过分析前面一部分是固定的，想要翻译的单词跟在后面
            // 比如要翻译hello,uri就是 https://fanyi.baidu.com/#en/zh/hello
            Uri uri = Uri.parse("https://fanyi.baidu.com/#en/zh/" + holder.textViewEnglish.getText());
            Intent intent = new Intent(Intent.ACTION_VIEW); // 定义一个隐式意图
            intent.setData(uri); // 传递URI
            holder.itemView.getContext().startActivity(intent); // 启动意图
        });
    }

    @Override
    public int getItemCount() {
        // 实现父类的抽象方法，这个方法返回数据的长度
        return mWords.size();
    }

    /**
     * 这是自定义的ViewHolder
     * 用来获取itemView中的控件
     * 方便在适配器中直接对控件做操作
     * 必须要有一个构造函数并接受一个View
     */
    static public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewNumber,textViewEnglish, textViewChinese;
        public ViewHolder(View itemView) {
            super(itemView);
            textViewNumber = itemView.findViewById(R.id.textViewNumber);
            textViewEnglish = itemView.findViewById(R.id.textViewEnglish);
            textViewChinese = itemView.findViewById(R.id.textViewChinese);
        }
    }
}
