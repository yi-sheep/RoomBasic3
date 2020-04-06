package com.gaoxianglong.roombasic3;

import android.content.Context;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

/**
 * 这个是仓库类，用于获取数据
 */
public class WordRepository {
    private WordDao mWordDao;
    private LiveData<List<Word>> queryAllWordLive;
    public WordRepository(Context context) {
        WordDatabase wordDatabase = WordDatabase.getDatabase(context.getApplicationContext());
        mWordDao = wordDatabase.getWordDao();
        queryAllWordLive = mWordDao.queryAllWordLive();
    }

    public LiveData<List<Word>> getQueryAllWordLive() {
        return queryAllWordLive;
    }

    public void insertWord(Word... words) {
        new InsertAsyncTask(mWordDao).execute(words);
    }
    public void updateWord(Word... words) {
        new UpdateAsyncTask(mWordDao).execute(words);
    }
    public void deleteWord(Word... words) {
        new DeleteAsyncTask(mWordDao).execute(words);
    }
    public void deleteAllWord() {
        new DeleteAllAsyncTask(mWordDao).execute();
    }

    /**
     * 使用AsyncTask完成更新UI
     * 前面的操作都是强制在主线程(UI线程)中更新的数据
     * 在Android中这个操作是很危险的
     * 插入
     */
    static class InsertAsyncTask extends AsyncTask<Word, Void, Void> {
        private WordDao mWordDao;

        public InsertAsyncTask(WordDao wordDao) {
            mWordDao = wordDao;
        }

        @Override
        protected Void doInBackground(Word... words) {
            // 这个回调方法中写更新数据的逻辑
            mWordDao.insertWords(words);
            return null;
        }
    }

    /**
     * 更新
     */
    static class UpdateAsyncTask extends AsyncTask<Word, Void, Void> {
        private WordDao mWordDao;

        public UpdateAsyncTask(WordDao wordDao) {
            mWordDao = wordDao;
        }

        @Override
        protected Void doInBackground(Word... words) {
            mWordDao.updateWords(words);
            return null;
        }
    }

    /**
     * 删除
     */
    static class DeleteAsyncTask extends AsyncTask<Word, Void, Void> {
        private WordDao mWordDao;

        public DeleteAsyncTask(WordDao wordDao) {
            mWordDao = wordDao;
        }

        @Override
        protected Void doInBackground(Word... words) {
            mWordDao.deleteWords(words);
            return null;
        }
    }

    /**
     * 清空
     * 因为这个操作不需要数据库实体对象，所以三个参数都可以是Void
     */
    static class DeleteAllAsyncTask extends AsyncTask<Void, Void, Void> {
        private WordDao mWordDao;

        public DeleteAllAsyncTask(WordDao wordDao) {
            mWordDao = wordDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mWordDao.deleteAllWord();
            return null;
        }
    }
}
