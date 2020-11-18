package cordova.plugins.bytedanceunionad;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdDislike;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTNativeExpressAd;

import java.util.List;

public class BannerAdFragment extends AdFragment {

    private int width;

    private int height;

    private int interval = 30;

    ViewGroup bannerContainer;

    public static BannerAdFragment newInstance(String slotId, int width, int height, int interval) {
        return newInstance(slotId, width, height, interval, 1);
    }

    public static BannerAdFragment newInstance(String slotId, int width, int height, int interval, int count) {
        Bundle bundle = new Bundle();
        bundle.putString("slotId", slotId);
        bundle.putInt("width", width);
        bundle.putInt("height", height);
        bundle.putInt("interval", interval);
        bundle.putInt("count", count);

        BannerAdFragment fragment = new BannerAdFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    protected boolean retrieveData(Bundle arguments) {
        width = arguments.getInt("width", 0);
        height = arguments.getInt("height", 0);
        interval = arguments.getInt("interval", 0);

        return width > 0 && height > 0;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(mContext, android.R.style.Theme_Translucent_NoTitleBar);
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.gravity = Gravity.BOTTOM;
        }

        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME) {
                    return true;
                }
                return false;
            }
        });

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FrameLayout frameLayout = new FrameLayout(mContext);
        frameLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        bannerContainer = new FrameLayout(mContext);
        frameLayout.addView(bannerContainer, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return frameLayout;
    }

    @Override
    protected void showAd() {
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(slotId)
                .setAdCount(count)
                .setExpressViewAcceptedSize(width, height)
                .build();

        ttAdNative.loadBannerExpressAd(adSlot, new TTAdNative.NativeExpressAdListener() {
            @Override
            public void onError(int code, String message) {
                sendPluginResult("error", code, message, true);
                finish();
            }

            @Override
            public void onNativeExpressAdLoad(List<TTNativeExpressAd> ads) {
                if (ads == null || ads.size() == 0) {
                    return;
                }

                TTNativeExpressAd ad = ads.get(0);

                if (interval > 0) {
                    ad.setSlideIntervalTime(interval * 1000);
                }

                ad.setExpressInteractionListener(new TTNativeExpressAd.ExpressAdInteractionListener() {
                    @Override
                    public void onAdClicked(View view, int type) {
                        sendPluginResult("click", true);
                    }

                    @Override
                    public void onAdShow(View view, int type) {
                        sendPluginResult("show", true);
                    }

                    @Override
                    public void onRenderFail(View view, String msg, int code) {
                        sendPluginResult("error", code, msg, true);
                        finish();
                    }

                    @Override
                    public void onRenderSuccess(View view, float width, float height) {
                        bannerContainer.removeAllViews();
                        bannerContainer.addView(view);
                    }
                });

                ad.setDislikeCallback((Activity) mContext, new TTAdDislike.DislikeInteractionCallback() {
                    @Override
                    public void onSelected(int position, String value) {
                        bannerContainer.removeAllViews();
                    }

                    @Override
                    public void onCancel() {
                    }

                    @Override
                    public void onRefuse() {
                    }
                });

                ad.render();
            }
        });
    }
}
