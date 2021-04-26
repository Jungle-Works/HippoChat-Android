package com.hippo.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import com.hippo.utils.filepicker.activity.AudioPickActivity;

import java.io.IOException;

/**
 * Created by gurmail on 17/01/19.
 *
 * @author gurmail
 */
public class CommonMediaPlayer {

    private static CommonMediaPlayer instance;
    private static MediaPlayer mediaPlayer;

    private CommonMediaPlayer() {

    }
    public static CommonMediaPlayer getInstance() {
        if(instance == null) {
            synchronized (CommonMediaPlayer.class) {
                if(instance == null){
                    instance = new CommonMediaPlayer();
                }
            }
        }
        return instance;
    }

    public void playMediaPlayer(Context context, String fileUrl, final MediaPlayerStatus mediaPlayerStatus) {
        Uri myUri = Uri.parse(fileUrl);
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        try {
            mediaPlayer.setDataSource(context, myUri);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mediaPlayer = mp;
                    mediaPlayer.start();
                    mediaPlayerStatus.onPlaying();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mediaPlayerStatus.onCompletion(mp);
            }
        });
        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                mediaPlayerStatus.onError(mp, what, extra);
                return true;
            }
        });

    }

    public void stopMedia() {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.reset();
                mediaPlayer.release();
                mediaPlayer = null;
            }
        } catch (Exception e) {

        }
    }

    public interface MediaPlayerStatus {
        void onPlaying();
        void onCompletion(MediaPlayer mp);
        void onError(MediaPlayer mp, int what, int extra);
    }

}
