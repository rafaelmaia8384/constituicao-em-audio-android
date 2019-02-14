package com.smarttools.constituicaoemaudio;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.jean.jcplayer.model.JcAudio;
import com.example.jean.jcplayer.view.JcPlayerView;

import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity {

    private JcPlayerView jcplayerView;
    private CheckBox checkManterAudio;
    private MaterialDialog dialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        jcplayerView = (JcPlayerView) findViewById(R.id.jcPlayer);

        ArrayList<JcAudio> jcAudios = new ArrayList<>();
        String soundList[] = null;

        int id = Integer.parseInt(getIntent().getStringExtra("position"));

        try {

            Context ctx = getApplicationContext();
            soundList = ctx.getAssets().list("");

            for (int a = 0; a < soundList.length; a++) {

                if (soundList[a].length() < 13) continue;

                String fileName = soundList[a].substring(0, soundList[a].length() - 4);

                jcAudios.add(JcAudio.createFromAssets(fileName, soundList[a]));
            }

            jcplayerView.initPlaylist(jcAudios, null);
            jcplayerView.playAudio(jcAudios.get(id));
        }
        catch (Exception e) {

            finish();
        }

        checkManterAudio = (CheckBox)findViewById(R.id.checkManterAudio);

        checkManterAudio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {

                    if (MainActivity.mRewardedVideoAd != null && MainActivity.mRewardedVideoAd.isLoaded()) {

                        dialog = new MaterialDialog.Builder(PlayerActivity.this)
                                .title("Manter áudio")
                                .content("Para ativar esta opção, veja um anúncio de poucos segundos.")
                                .positiveText("Ver anúncio")
                                .negativeText("Agora não")
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull final MaterialDialog dialog, @NonNull DialogAction which) {

                                        jcplayerView.pause();

                                        MainActivity.mRewardedVideoAd.show();
                                    }
                                })
                                .onNegative(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                        checkManterAudio.setChecked(false);
                                    }
                                })
                                .cancelable(false)
                                .show();
                    }
                }
            }
        });
    }

    @Override
    protected void onPause() {

        super.onPause();

        if (!checkManterAudio.isChecked()) {

            jcplayerView.pause();
        }
    }

    @Override
    protected void onResume() {

        super.onResume();

        if (dialog != null) {

            dialog.dismiss();
        }

        if (MainActivity.mRewardedVideoAd != null) {

            checkManterAudio.setChecked(false);

            if (!MainActivity.mRewardedVideoAd.isLoaded()) {

                checkManterAudio.setVisibility(View.GONE);
            }
        }
        else {

            checkManterAudio.setChecked(true);
        }
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

        jcplayerView.kill();
    }
}
