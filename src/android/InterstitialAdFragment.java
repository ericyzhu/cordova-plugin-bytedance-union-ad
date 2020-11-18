package cordova.plugins.bytedanceunionad;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Point;
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
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTNativeExpressAd;

import java.util.List;

public class InterstitialAdFragment extends AdFragment {

    private int width;

    private int height;

    public static InterstitialAdFragment newInstance(String slotId, int width, int height) {
        return newInstance(slotId, width, height, 1);
    }

    public static InterstitialAdFragment newInstance(String slotId, int width, int height, int count) {
        Bundle bundle = new Bundle();
        bundle.putString("slotId", slotId);
        bundle.putInt("width", width);
        bundle.putInt("height", height);
        bundle.putInt("count", count);

        InterstitialAdFragment fragment = new InterstitialAdFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    protected boolean retrieveData(Bundle arguments) {
        width = arguments.getInt("width", 0);
        height = arguments.getInt("height", 0);

        return width > 0 && height > 0;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(mContext, android.R.style.Theme_Translucent);
        Window window = dialog.getWindow();
        if (window != null) {
            Point size = getDisplaySize();
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.windowAnimations = android.R.style.Animation_Dialog;
            lp.width = size.x;
            lp.height = size.y;
            lp.gravity = Gravity.CENTER;
            lp.x = 0;
            lp.y = 0;
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
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
        frameLayout.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        return frameLayout;
    }

    @Override
    protected void showAd() {
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(slotId)
                .setAdCount(count)
                .setExpressViewAcceptedSize(width, height)
                .build();

        ttAdNative.loadInteractionExpressAd(adSlot, new TTAdNative.NativeExpressAdListener() {
            @Override
            public void onError(int code, String message) {
                sendPluginResult("error", code, message, true);
                finish();
            }

            @Override
            public void onNativeExpressAdLoad(List<TTNativeExpressAd> ads) {
                if (ads == null || ads.size() == 0) {
                    finish();
                    return;
                }

                TTNativeExpressAd ad = ads.get(0);

                ad.setCanInterruptVideoPlay(true);

                ad.setExpressInteractionListener(new TTNativeExpressAd.AdInteractionListener() {
                    @Override
                    public void onAdDismiss() {
                        finish();
                    }

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
                        ad.showInteractionExpressAd((Activity) mContext);
                    }
                });

                ad.render();
            }
        });
    }
}
