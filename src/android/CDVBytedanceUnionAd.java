package cordova.plugins.bytedanceunionad;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTRewardVideoAd;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CDVBytedanceUnionAd extends CordovaPlugin {

    TTRewardVideoAd mttRewardVideoAd;

    public CallbackContext splashAdCallbackContext;

    public CallbackContext rewardedVideoAdCallbackContext;

    private InterstitialAdFragment interstitialFragment;

    private BannerAdFragment bannerFragment;

    private RelativeLayout bottomView, contentView;

    private static final int BOTTOM_VIEW_ID = 0x1;


    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);

        final Activity activity = cordova.getActivity();
        Context context = activity.getApplicationContext();

        String appId = webView.getPreferences().getString("CDVBytedanceUnionAdAppId", "");

        TTAdManagerHolder.init(activity, appId);
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

        if (args.length() < 0) {
            Toast.makeText(cordova.getActivity(), "ssss", Toast.LENGTH_LONG).show();
            return true;
        }
        JSONObject jsonObject = args.getJSONObject(0);

        final Activity activity = cordova.getActivity();

        // Rewarded Video AD
        if ("showRewardedVideoAd".equals(action)) {
            this.rewardedVideoAdCallbackContext = callbackContext;

            String userId = args.length() <= 1 ? "" : jsonObject.getString("userId");
            String slotId = jsonObject.getString("slotId");

            showRewardedVideoAd(slotId, userId);

            return true;
        }
        // Splash AD
        else if ("showSplashAd".equals(action)) {
            this.splashAdCallbackContext = callbackContext;

            String slotId = jsonObject.getString("slotId");

            SplashActivity.plugin = this;

            Intent intent = new Intent();
            intent.setClass(activity, SplashActivity.class);
            intent.putExtra("slotId", slotId);

            activity.startActivity(intent);

            return true;
        }

        // Interstitial AD
        else if ("showInterstitialAd".equals(action)) {

            String slotId = jsonObject.getString("slotId");
            int width = jsonObject.getInt("width");
            int height = jsonObject.getInt("height");

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    FragmentManager fm = activity.getFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    interstitialFragment = InterstitialAdFragment.newInstance(slotId, width, height);
                    interstitialFragment.setCallbackContext(callbackContext);
                    ft.add(interstitialFragment, InterstitialAdFragment.class.getSimpleName());
                    ft.commitAllowingStateLoss();
                }
            });

            return true;
        }

        // Banner AD
        else if ("showBannerAd".equals(action)) {

            final String slotId = jsonObject.getString("slotId");
            final int width = jsonObject.getInt("width");
            final int height = jsonObject.getInt("height");
            final String align = jsonObject.optString("align");
            final int interval = jsonObject.getInt("interval");

            activity.runOnUiThread(new Runnable() {
                public void run() {
                    bottomView = new RelativeLayout(activity);
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.MATCH_PARENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT
                    );
                    if (align.equalsIgnoreCase("top")) {
                        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                    } else {
                        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                    }
                    params.addRule(RelativeLayout.CENTER_HORIZONTAL);
                    bottomView.setLayoutParams(params);
                    bottomView.setId(BOTTOM_VIEW_ID);

                    contentView = new RelativeLayout(activity);
                    contentView.addView(bottomView);
                    activity.addContentView(contentView, new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.MATCH_PARENT,
                            RelativeLayout.LayoutParams.MATCH_PARENT));

                    FragmentManager fm = activity.getFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    bannerFragment = BannerAdFragment.newInstance(slotId, width, height, interval);
                    bannerFragment.setCallbackContext(callbackContext);
                    ft.replace(BOTTOM_VIEW_ID, bannerFragment);
                    ft.commitAllowingStateLoss();
                }
            });

            return true;
        }
        else if (action.equals("hideBannerAd")) {

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (bannerFragment != null) {
                        sendPluginResult(bannerFragment.callbackContext, "close", false);
                        FragmentManager fm = activity.getFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();
                        ft.remove(bannerFragment);
                        ft.commitAllowingStateLoss();
                    }
                    ViewGroup group = activity.findViewById(android.R.id.content);
                    if (group != null) {
                        group.removeView(contentView);
                    }
                    sendPluginResult(callbackContext, "close", false);
                }
            });
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
                        public void onRewardVerify(boolean rewardVerify, int rewardAmount, String rewardName, int code, String msg) {
                            if (rewardVerify) {
                                sendPluginResult(rewardedVideoAdCallbackContext, "verify:valid", true);
                            } else {
                                sendPluginResult(rewardedVideoAdCallbackContext, "verify:invalid", code, msg, true);
                            }
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
