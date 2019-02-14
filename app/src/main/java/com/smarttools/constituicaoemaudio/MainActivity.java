package com.smarttools.constituicaoemaudio;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

public class MainActivity extends AppCompatActivity {

    private static final String app_id = "ca-app-pub-8285437888591773~8160252083";
    private static final String interstitial_id = "ca-app-pub-8285437888591773/5478248423";
    private static final String rewarded_id = "ca-app-pub-8285437888591773/5506168583";

    private int adCount = 3;

    private InterstitialAd mInterstitialAd;
    public static RewardedVideoAd mRewardedVideoAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        MobileAds.initialize(this, app_id);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(interstitial_id);
        mInterstitialAd.setAdListener(new AdListener() {

            @Override
            public void onAdClosed() {

                mInterstitialAd.loadAd(new AdRequest.Builder().build());

                super.onAdClosed();
            }
        });
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedVideoAd.setRewardedVideoAdListener(new RewardedVideoAdListener() {

            @Override
            public void onRewardedVideoAdLoaded() {

            }

            @Override
            public void onRewardedVideoAdOpened() {

            }

            @Override
            public void onRewardedVideoStarted() {

            }

            @Override
            public void onRewardedVideoAdClosed() {

                if (mRewardedVideoAd != null) {

                    mRewardedVideoAd.loadAd(rewarded_id, new AdRequest.Builder().build());
                }
            }

            @Override
            public void onRewarded(RewardItem rewardItem) {

                mRewardedVideoAd.destroy(MainActivity.this);
                mRewardedVideoAd = null;
            }

            @Override
            public void onRewardedVideoAdLeftApplication() {

            }

            @Override
            public void onRewardedVideoAdFailedToLoad(int i) {

            }

            @Override
            public void onRewardedVideoCompleted() {

            }
        });
        mRewardedVideoAd.loadAd(rewarded_id, new AdRequest.Builder().build());

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Constituição em Áudio");

        String soundList[] = null;

        try {

            Context ctx = getApplicationContext();

            soundList = ctx.getAssets().list("");

            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            LinearLayout inflaterLayout = (LinearLayout) findViewById(R.id.inflaterLayout);

            int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 0, px);

            for (int a = 0; a < soundList.length; a++) {

                if (!soundList[a].contains(".ogg")) continue;

                String fileName = soundList[a].substring(0, soundList[a].length() - 4);
                String artigos = null;
                String titulo = fileName.substring(0, 8);

                if (fileName.equals("Preambulo")) {

                    titulo = "Preâmbulo";
                    artigos = "Preâmbulo constitucional";
                }
                else {

                    artigos = fileName.substring(9, fileName.length());
                }

                View child = inflater.inflate(R.layout.sound_item, null);

                child.setLayoutParams(params);
                child.setTag(Integer.toString(a));
                ((TextView)child.findViewById(R.id.titulo)).setText(titulo);
                ((TextView)child.findViewById(R.id.artigos)).setText(artigos);

                inflaterLayout.addView(child);
            }
        }
        catch (Exception e) {}


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action1) {

            Intent i = new Intent(MainActivity.this, SobreActivity.class);
            startActivity(i);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100) {

            if (mInterstitialAd.isLoaded()) {

                if (adCount > 2) {

                    mInterstitialAd.show();
                    adCount = 0;
                }
                else {

                    adCount++;
                }
            }
        }
    }

    public void openPlayer(View view) {

        Intent i = new Intent(MainActivity.this, PlayerActivity.class);
        i.putExtra("position", view.getTag().toString());
        startActivityForResult(i, 100);
    }
}
