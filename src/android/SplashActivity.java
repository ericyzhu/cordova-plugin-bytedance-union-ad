package cordova.plugins.bytedanceunionad;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTSplashAd;

import androidx.annotation.MainThread;
import io.igirl.app.R;

public class SplashActivity extends Activity {
    @SuppressLint("StaticFieldLeak")
    public static CDVBytedanceUnionAd plugin;

    private TTAdNative mTTAdNative;
    private FrameLayout mSplashContainer;

    private static final int LOAD_TIME_OUT = 3000;

    private boolean forceDestroy;

    private String slotId;

    @SuppressWarnings("RedundantCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.bytedanceunionad_activity_splash);
        mSplashContainer = (FrameLayout) findViewById(R.id.splash_container);
        mTTAdNative = TTAdManagerHolder.get().createAdNative(this);
        getExtraInfo();

        loadSplashAd();
    }

    private void getExtraInfo() {
        Intent intent = getIntent();
        if (intent != null) {
            slotId = intent.getStringExtra("slotId");
            if (!TextUtils.isEmpty(slotId)) {
                return;
            }
        }

        plugin.sendPluginResult(plugin.rewardedVideoAdCallbackContext, "error", -1, "", false);

        finish();
    }

    @Override
    protected void onResume() {
        if (forceDestroy) {
            finish();
        }
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        forceDestroy = true;
    }

    @Override
    protected void onDestroy() {
        plugin.sendPluginResult(plugin.splashAdCallbackContext, "close", false);
        plugin.rewardedVideoAdCallbackContext = null;
        plugin = null;

        mSplashContainer.removeAllViews();

        super.onDestroy();
    }

    /**
     * 加载开屏广告
     */
    private void loadSplashAd() {
        Point displaySize = plugin.getDisplaySize();

        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(slotId)
                .setImageAcceptedSize(displaySize.x, displaySize.y)
                .setOrientation(TTAdConstant.VERTICAL)
                .setSupportDeepLink(true)
                .setAdCount(3)
                .build();

        mTTAdNative.loadSplashAd(adSlot, new TTAdNative.SplashAdListener() {
            @Override
            @MainThread
            public void onError(int code, String message) {
                plugin.sendPluginResult(plugin.rewardedVideoAdCallbackContext, "error", code, message, false);
                plugin.rewardedVideoAdCallbackContext = null;

                finish();
            }

            @Override
            @MainThread
            public void onTimeout() {
                plugin.sendPluginResult(plugin.rewardedVideoAdCallbackContext, "error", 0, "timeout", false);
                plugin.rewardedVideoAdCallbackContext = null;

                finish();
            }

            @Override
            @MainThread
            public void onSplashAdLoad(TTSplashAd ad) {
                if (ad == null) {
                    plugin.sendPluginResult(plugin.rewardedVideoAdCallbackContext, "error", 0, "empty", false);

                    finish();
                    return;
                }

                View view = ad.getSplashView();

                if (view != null && mSplashContainer != null && !SplashActivity.this.isFinishing()) {
                    mSplashContainer.removeAllViews();
                    mSplashContainer.addView(view);
                } else {
                    finish();
                }

                //设置SplashView的交互监听器
                ad.setSplashInteractionListener(new TTSplashAd.AdInteractionListener() {
                    @Override
                    public void onAdClicked(View view, int type) {
                        plugin.sendPluginResult(plugin.splashAdCallbackContext, "click", true);
                    }

                    @Override
                    public void onAdShow(View view, int type) {
                        plugin.sendPluginResult(plugin.splashAdCallbackContext, "show", true);
                    }

                    @Override
                    public void onAdSkip() {
                        plugin.sendPluginResult(plugin.splashAdCallbackContext, "skip", true);

                        finish();
                    }

                    @Override
                    public void onAdTimeOver() {
                        finish();
                    }
                });
            }
        }, LOAD_TIME_OUT);
    }
}
