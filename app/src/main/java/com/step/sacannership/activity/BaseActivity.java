package com.step.sacannership.activity;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.step.sacannership.BuildConfig;
import com.step.sacannership.R;
import com.step.sacannership.model.TModel;
import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity {
    public TModel tModel;
    private MediaPlayer mediaPlayer;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(contentView());
        ButterKnife.bind(this);
        setSystemVoice();
        initView();
    }

    public void initToolBar(Toolbar toolbar){
        toolbar.setNavigationIcon(R.mipmap.back);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
    }

    protected abstract int contentView();
    protected abstract void initView();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tModel != null){
            tModel.onDestory();
        }
        releaseMediaPlayer();
    }

    public void scane(EditText editText){
        this.editText = editText;
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setOrientationLocked(true);//是否锁定方向
        integrator.initiateScan();
    }
    private EditText editText;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        try {
            String json = result.getContents();
            if (editText != null){
                editText.setText(json);
                editText.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
            }

        } catch (Exception e) {
            e.printStackTrace();

        }
    }
    public void requestFocus(EditText editText){
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            editText.requestFocus();
            editText.setFocusable(true);
            editText.setCursorVisible(true);
            editText.setText("");
        }, 500);
    }

    private long millions = 0;

    public void setOnclick(EditText editText){
        editText.setOnClickListener(view -> {
            if (System.currentTimeMillis() - millions < 1000){
                editText.setText("");
            }
            millions = System.currentTimeMillis();
        });
    }

    /**
     * 创建mediaplayer
     * */
    public void createMediaPlayer(int id){
        mediaPlayer = MediaPlayer.create(this, id);
        mediaPlayer.setVolume(0.5f, 0.5f);
        mediaPlayer.start();
    }

    /**
     * 释放MediaPlayer
     * */
    public void releaseMediaPlayer(){
        if (mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public void setSystemVoice(){
        if (BuildConfig.DEBUG){
            return;
        }
        AudioManager manager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        int maxVoice = manager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int curVoice = manager.getStreamVolume(AudioManager.STREAM_MUSIC);
        if (curVoice < maxVoice / 2){
            manager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVoice/2, AudioManager.FLAG_SHOW_UI);
        }
    }
    protected boolean isTwice(){
        if (System.currentTimeMillis() - millions < 1000){
            return true;
        }
        millions = System.currentTimeMillis();
        return false;
    }
}
