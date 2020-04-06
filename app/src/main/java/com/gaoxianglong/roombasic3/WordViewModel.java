package com.gaoxianglong.roombasic3;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class WordViewModel extends AndroidViewModel {
    private WordDao mWordDao;
    private WordRepository mWordRepository;

    public WordViewModel(@NonNull Application application) {
        super(application);
        mWordRepository = new WordRepository(application);
    }

    public LiveData<List<Word>> getQueryAllWordLive() {
        return mWordRepository.getQueryAllWordLive();
    }

    public void insertWord(Word... words) {
        mWordRepository.insertWord(words);
    }
    public void updateWord(Word... words) {
        mWordRepository.updateWord(words);
    }
    public void deleteWord(Word... words) {
        mWordRepository.deleteWord(words);
    }
    public void deleteAllWord() {
        mWordRepository.deleteAllWord();
    }
}
