package cordova.plugins.bytedanceunionad;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTRewardVideoAd;
import com.bytedance.sdk.openadsdk.TTSplashAd;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class CDVBytedanceUnionAd extends CordovaPlugin {

    TTSplashAd mttSplashAd;

    TTRewardVideoAd mttRewardVideoAd;

    private FrameLayout mSplashContainer;

    public CallbackContext splashAdCallbackContext;

    public CallbackContext rewardedVideoAdCallbackContext;

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);

        String appId;

        final Activity activity = cordova.getActivity();
        Context context = activity.getApplicationContext();

        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            appId = Objects.requireNonNull(ai.metaData.get("CDVBytedanceUnionAdAppId")).toString();
        } catch (PackageManager.NameNotFoundException exception) {
            appId = "";
        }

        TTAdManagerHolder.init(activity, appId);
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

        if (args.length() < 0) {
            Toast.makeText(cordova.getActivity(), "ssss", Toast.LENGTH_LONG).show();
            return true;
        }
        JSONObject jsonObject = args.getJSONObject(0);

        if ("showRewardedVideoAd".equals(action)) {
            this.rewardedVideoAdCallbackContext = callbackContext;

            String userId = args.length() <= 1 ? "" : jsonObject.getString("userId");
            String slotId = jsonObject.getString("slotId");

            showRewardedVideoAd(slotId, userId);

            return true;
        }

        else if ("showSplashAd".equals(action)) {
            this.splashAdCallbackContext = callbackContext;

            final Activity activity = cordova.getActivity();

            String slotId = jsonObject.getString("slotId");

            SplashActivity.plugin = this;

            Intent intent = new Intent();
            intent.setClass(activity, SplashActivity.class);
            intent.putExtra("slotId", slotId);

            activity.startActivity(intent);

            return true;
        }

        return false;
    }

    private void sendPluginResult(CallbackContext callbackContext, JSONObject obj, boolean keepCallback) {
        if (callbackContext != null) {
            PluginResult result = new PluginResult(PluginResult.Status.OK, obj);
            result.setKeepCallback(keepCallback);
            callbackContext.sendPluginResult(result);
        }
    }

    public void sendPluginResult(CallbackContext callbackContext, String type, boolean keepCallback) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("type", type);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        sendPluginResult(callbackContext, obj, keepCallback);
    }

    public void sendPluginResult(CallbackContext callbackContext, String type, long code, String message, boolean keepCallback) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("type", type);
            obj.put("code", code);
            obj.put("message", message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        sendPluginResult(callbackContext, obj, keepCallback);
    }

    public Point getDisplaySize() {
        WindowManager wm = cordova.getActivity().getWindowManager();
        Point point = new Point();
        wm.getDefaultDisplay().getSize(point);
        return point;
    }


    public void showRewardedVideoAd(String slotId, String userId) {
        final Activity activity = cordova.getActivity();

        TTAdNative mTTAdNative = TTAdManagerHolder.get().createAdNative(activity);

        Point displaySize = getDisplaySize();

        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(slotId)
                .setSupportDeepLink(true)
                .setAdCount(3)
                .setExpressViewAcceptedSize(displaySize.x, displaySize.y)
                .setImageAcceptedSize(displaySize.x, displaySize.y)
                .setUserID(userId)
                .setOrientation(TTAdConstant.VERTICAL)
                .build();

        mTTAdNative.loadRewardVideoAd(adSlot, new TTAdNative.RewardVideoAdListener() {
            @Override
            public void onError(int code, String message) {
                sendPluginResult(rewardedVideoAdCallbackContext, "error", code, message, false);
                rewardedVideoAdCallbackContext = null;
            }

            @Override
            public void onRewardVideoCached() {
            }

            @Override
            public void onRewardVideoAdLoad(TTRewardVideoAd ad) {

                activity.runOnUiThread(() -> {
                    mttRewardVideoAd = ad;
                    mttRewardVideoAd.showRewardVideoAd(activity);

                    mttRewardVideoAd.setShowDownLoadBar(true);

                    mttRewardVideoAd.setRewardAdInteractionListener(new TTRewardVideoAd.RewardAdInteractionListener() {
                        @Override
                        public void onAdShow() {
                            sendPluginResult(rewardedVideoAdCallbackContext, "show", true);
                        }

                        @Override
                        public void onAdVideoBarClick() {
                            sendPluginResult(rewardedVideoAdCallbackContext, "click", true);
                        }

                        @Override
                        public void onAdClose() {
                            sendPluginResult(rewardedVideoAdCallbackContext, "close", false);
                            rewardedVideoAdCallbackContext = null;
                        }

                        @Override
                        public void onVideoComplete() {
                            sendPluginResult(rewardedVideoAdCallbackContext, "play:finish", true);
                        }

                        @Override
                        public void onVideoError() {
                            sendPluginResult(rewardedVideoAdCallbackContext, "play:error", 0, "", true);
                        }

                        @Override
                        public void onRewardVerify(boolean rewardVerify, int rewardAmount, String rewardName) {
                            sendPluginResult(rewardedVideoAdCallbackContext, rewardVerify ? "verify:valid" : "verify:invalid", true);
                        }

                        @Override
                        public void onSkippedVideo() {
                            sendPluginResult(rewardedVideoAdCallbackContext, "skip", true);
                        }
                    });
                });
            }
        });
    }
}
