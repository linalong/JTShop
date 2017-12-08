package com.heizi.jtshop.block.home;

import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.heizi.jtshop.R;
import com.heizi.jtshop.block.my.ModelCoupon;
import com.heizi.jtshop.fragment.BaseFragment;
import com.heizi.mylibrary.callback.IResponseCallback;
import com.heizi.mylibrary.model.DataSourceModel;
import com.heizi.mylibrary.model.ErrorModel;
import com.heizi.mylibrary.retrofit2.ParseObjectProtocol;
import com.heizi.mylibrary.retrofit2.ParseStringProtocol;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.InjectView;

/**
 * Created by leo on 17/10/13.
 */

public class FragmentHongbao extends BaseFragment {
    @InjectView(R.id.iv_hongbao)
    ImageView iv_hongbao;

    @InjectView(R.id.tv_des)
    TextView tv_des;

    private MediaPlayer media1, media2, media3;
    private boolean playBeep;
    private static final float BEEP_VOLUME = 0.10f;
    private boolean vibrate;

    private int max = 20;//随机范围
    private int current = 0;//当前次数
    private int random = 1;//随机数


    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_hongbao;
    }

    @Override
    protected void initView(View v) {
        super.initView(v);
        playBeep = true;
//        AudioManager audioService = (AudioManager) getActivity().getSystemService(getActivity().AUDIO_SERVICE);
//        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
//            playBeep = false;
//        }
        vibrate = true;
        iv_hongbao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playSound2();
                current++;
                if (current == random) {
                    showHongbao();
                }
                Log.d("===========", playBeep + "");
            }
        });
    }

    /**
     * 判断是否有红包
     */
    private void getExsit() {
        ParseStringProtocol parseStringProtocol = new ParseStringProtocol(getActivity(), SERVER_URL_SHOP + EXSITHONGBAO);
        Map<String, String> map = new HashMap<>();
        map.put("token", userModel.getToken());
        parseStringProtocol.getData(map, new IResponseCallback<DataSourceModel<String>>() {
            @Override
            public void onSuccess(DataSourceModel<String> data) {
                if (data.json != null && data.json.equals("1")) {
                    playSound1();
                    current = 0;
                    iv_hongbao.setImageDrawable(getResources().getDrawable(R.mipmap.iv_hongbaog));
                    iv_hongbao.setEnabled(true);
                    random = (int) (Math.random() * max) + 1;
                    tv_des.setText("获得一次抢红包机会,快来抢红包吧!");

                } else {
                    iv_hongbao.setImageDrawable(getResources().getDrawable(R.mipmap.iv_wuhongbao));
                    iv_hongbao.setEnabled(false);
                    tv_des.setText("暂无红包活动");
                }
            }

            @Override
            public void onFailure(ErrorModel errorModel) {
                iv_hongbao.setImageDrawable(getResources().getDrawable(R.mipmap.iv_hongbaok));
                iv_hongbao.setEnabled(false);
            }

            @Override
            public void onStart() {

            }
        });
    }

    private void showHongbao() {
        ParseObjectProtocol<ModelCoupon> parseStringProtocol = new ParseObjectProtocol(getActivity(), SERVER_URL_SHOP + GETHONGBAO, ModelCoupon.class);
        Map<String, String> map = new HashMap<>();
        map.put("token", userModel.getToken());
        parseStringProtocol.getData(map, new IResponseCallback<DataSourceModel<ModelCoupon>>() {
            @Override
            public void onSuccess(DataSourceModel<ModelCoupon> data) {
                if (data.temp != null) {
                    ModelCoupon modelCoupon = data.temp;
                    playSound3();
                    iv_hongbao.setImageDrawable(getResources().getDrawable(R.mipmap.iv_hongbaok));
                    iv_hongbao.setEnabled(false);
                    tv_des.setText("恭喜获得" + modelCoupon.getVoucher_title() + " 金额:" + modelCoupon.getVoucher_price());

                } else {
                    iv_hongbao.setImageDrawable(getResources().getDrawable(R.mipmap.iv_hongbaok));
                    iv_hongbao.setEnabled(false);
                    tv_des.setText("手慢了,下次努力吧!");
                }
            }

            @Override
            public void onFailure(ErrorModel errorModel) {
                iv_hongbao.setImageDrawable(getResources().getDrawable(R.mipmap.iv_hongbaok));
                iv_hongbao.setEnabled(false);
            }

            @Override
            public void onStart() {

            }
        });
    }


    private static final long VIBRATE_DURATION = 200L;

    private void playSound1() {
        if (playBeep) {
            // The volume on STREAM_SYSTEM is not adjustable, and users found it
            // too loud,
            // so we now play on the music stream.
            getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);
            MediaPlayer media1 = new MediaPlayer();
            media1.setAudioStreamType(AudioManager.STREAM_MUSIC);
            media1.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.hongbao_gq);
            try {
                media1.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
                file.close();
                media1.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                media1.prepare();
                media1.start();
            } catch (IOException e) {
                media1 = null;
            }
        }
    }

    private void playSound2() {
        if (playBeep) {
            // The volume on STREAM_SYSTEM is not adjustable, and users found it
            // too loud,
            // so we now play on the music stream.
            getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);
            MediaPlayer media2 = new MediaPlayer();
            media2.setOnCompletionListener(beepListener);
            AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.packet_received);
            try {
                media2.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
                file.close();
                media2.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                media2.prepare();
                media2.start();
            } catch (IOException e) {
                media2 = null;
            }
        }
//        if (vibrate) {
//            Vibrator vibrator = (Vibrator) getActivity().getSystemService(getActivity().VIBRATOR_SERVICE);
//            vibrator.vibrate(VIBRATE_DURATION);
//        }
    }

    private void playSound3() {
        if (playBeep) {
            // The volume on STREAM_SYSTEM is not adjustable, and users found it
            // too loud,
            // so we now play on the music stream.
            getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);
            MediaPlayer media3 = new MediaPlayer();
            media3.setAudioStreamType(AudioManager.STREAM_MUSIC);
            media3.setOnCompletionListener(beepListener);
            AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.hongbao_gx);
            try {
                media3.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
                file.close();
                media3.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                media3.prepare();
                media3.start();
                if (vibrate) {
                    Vibrator vibrator = (Vibrator) getActivity().getSystemService(getActivity().VIBRATOR_SERVICE);
                    vibrator.vibrate(VIBRATE_DURATION);
                }
            } catch (IOException e) {
                media3 = null;
            }
        }

    }

    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final MediaPlayer.OnCompletionListener beepListener = new MediaPlayer.OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.reset();
            mediaPlayer.release();
        }
    };


    Handler handler = new Handler();

    public void reload() {
        iv_hongbao.setImageDrawable(getResources().getDrawable(R.mipmap.iv_hongbaosousuo));
        tv_des.setText("红包活动获取中");
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isAdded()) {
                    getExsit();
                }
            }
        }, 2000);

    }


}
