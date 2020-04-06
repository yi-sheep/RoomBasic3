# RoomBaslc3
演示视频

<video src="https://yi-sheep.github.io/RoomBasic3/Res/mp4/1.mp4"  autoplay loop muted>浏览器不支持播放该视频</video>

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

这个时候数据表的结构和数据应该是这样的

<img src="https://yi-sheep.github.io/RoomBasic3/Res/image/2.png"/>

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

这个时候数据表的结构和数据应该是这样的

<img src="https://yi-sheep.github.io/RoomBasic3/Res/image/3.png"/>

上面已经将关于数据库的版本迁移实现了，现在就来看看怎么在UI上实现呢，增加一个字段，使用这个字段保存当前单词是否，需要隐藏中文意思。
添加字段:

```java
@ColumnInfo(name = "chinese_invisible")
private boolean chineseInvisible;
public boolean isChineseInvisible() {
    return chineseInvisible;
}
public void setChineseInvisible(boolean chineseInvisible) {
    this.chineseInvisible = chineseInvisible;
}
```

具体意思上面也有说到，下一步就该进行数据库版本的迁移了，注意每一次修改都有将版本号加一。

```java
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
    ...

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
```

然后是再写两个itemView的布局，改之前的会有点混乱。
默认布局

<img src="https://yi-sheep.github.io/RoomBasic3/Res/image/4.png"/>

卡片布局

<img src="https://yi-sheep.github.io/RoomBasic3/Res/image/5.png"/>

布局的代码可以去上面的仓库中找。
下一步就是修改适配器了，仔细对比改前后和改后的区别，弄清楚意思。

```java
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    List<Word> mWords = new ArrayList<>(); // 用于获取数据
    boolean useCardView; // 用于判断用户选择使用卡片item还是默认item
    WordViewModel mWordViewModel; // 用于更新数据

    /**
     * 传入数据对象
     * @param words
     */
    public void setWords(List<Word> words) {
        mWords = words;
    }

    /**
     * 在实例化当前适配器的时候传入一个布尔值和一个ViewModel
     * true表示使用卡片item
     * false表示使用默认item
     * @param useCardView
     */
    public MyAdapter(boolean useCardView,WordViewModel wordViewModel) {
        this.useCardView = useCardView;
        this.mWordViewModel = wordViewModel;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 实现父类RecyclerView.Adapter中的抽象方法，在这个方法中初始化item，然后返回这个itemView
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView;
        // 这里判断用户想要使用哪种item
        if (useCardView) {
            itemView = inflater.inflate(R.layout.item_c_layout_2, parent, false);
        } else {
            itemView = inflater.inflate(R.layout.item_layout_2, parent, false);
        }
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // 这也是实现的父类的抽象方法，在这个方法中，做数据的绑定
        Word word = mWords.get(position); // 通过position(当前是第几itemView)获取到word中的对应数据
        holder.textViewNumber.setText(String.valueOf(position + 1)); // 设置布局中显示序号的textView,因为position是从0开始的，所以需要加1
        holder.textViewEnglish.setText(word.getWord()); // 设置布局中显示单词的textView,通过获取到的word中对应数据对象获取到单词
        holder.textViewChinese.setText(word.getChineseMeaning()); // 设置显示中文意思的textView

        holder.mSwitch.setOnCheckedChangeListener(null); // 这一句能够解决itemView被回收后状态没有保存
        // 判断当前单词是否隐藏中文意思
        if (word.isChineseInvisible()) {
            holder.textViewChinese.setVisibility(View.GONE);
            holder.mSwitch.setChecked(true); // 当修改这个的时候会调用switch的点击事件，因为前面我们将点击事件设置为null了，所以不会出现调用下面的点击事件的情况
        } else {
            holder.textViewChinese.setVisibility(View.VISIBLE);
            holder.mSwitch.setChecked(false);
        }

        // 给item设置点击事件
        holder.itemView.setOnClickListener(v -> {
            // 这里定义一个URI，使用的百度翻译的地址，通过分析前面一部分是固定的，想要翻译的单词跟在后面
            // 比如要翻译hello,uri就是 https://fanyi.baidu.com/#en/zh/hello
            Uri uri = Uri.parse("https://fanyi.baidu.com/#en/zh/" + holder.textViewEnglish.getText());
            Intent intent = new Intent(Intent.ACTION_VIEW); // 定义一个隐式意图
            intent.setData(uri); // 传递URI
            holder.itemView.getContext().startActivity(intent); // 启动意图
        });

        holder.mSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                holder.textViewChinese.setVisibility(View.GONE);
                word.setChineseInvisible(true);
                mWordViewModel.updateWord(word);
            } else {
                holder.textViewChinese.setVisibility(View.VISIBLE);
                word.setChineseInvisible(false);
                mWordViewModel.updateWord(word);
            }
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
        Switch mSwitch;
        public ViewHolder(View itemView) {
            super(itemView);
            textViewNumber = itemView.findViewById(R.id.textViewNumber);
            textViewEnglish = itemView.findViewById(R.id.textViewEnglish);
            textViewChinese = itemView.findViewById(R.id.textViewChinese);
            mSwitch = itemView.findViewById(R.id.switch2);
        }
    }
}

```

完成到这里，或许点击switch的时候会有点卡顿，原因是适配器中做的更新的操作，MainActivity中也做了更新操作，出现多余的情况，我们在MainActivity中做一个判断就行。

```java
注意看这串代码原来在哪里。不是新加入的
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
```
还有一个地方，就是在适配器中给构造方法加了一个参数，需要在MainActivity里面，传入
```java
mAdapter = new MyAdapter(true,mViewModel); // 初始化第一个适配器，使用的是卡片item
mAdapter2 = new MyAdapter(false,mViewModel); // 初始化第二个适配器，使用的是默认item
```

---

感谢B站大佬longway777的[视频教程1](https://www.bilibili.com/video/BV1b4411171L)[视频教程2](https://www.bilibili.com/video/BV16J411N7bL)

如果侵权，请联系qq:1766816333
立即删除

---