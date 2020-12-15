package cordova.plugins.bytedanceunionad;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;

import com.bytedance.sdk.openadsdk.TTAdNative;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.json.JSONException;
import org.json.JSONObject;

abstract public class AdFragment extends DialogFragment {

    protected Context mContext;

    protected TTAdNative ttAdNative;

    protected boolean forceDestroy;

    public CallbackContext callbackContext;

    protected String slotId;

    protected int count;

    public void setCallbackContext(CallbackContext callbackContext) {
        this.callbackContext = callbackContext;
    }


    private void sendPluginResult(JSONObject obj, boolean keepCallback) {
        if (callbackContext != null) {
            PluginResult result = new PluginResult(PluginResult.Status.OK, obj);
            result.setKeepCallback(keepCallback);
            callbackContext.sendPluginResult(result);
        }
    }

    public void sendPluginResult(String type, boolean keepCallback) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("type", type);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        sendPluginResult(obj, keepCallback);
    }

    public void sendPluginResult(String type, long code, String message, boolean keepCallback) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("type", type);
            obj.put("code", code);
            obj.put("message", message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        sendPluginResult(obj, keepCallback);
    }

    public Point getDisplaySize() {
        Activity activity = (Activity) mContext;
        WindowManager wm = activity.getWindowManager();
        Point point = new Point();
        wm.getDefaultDisplay().getSize(point);
        return point;
    }


    protected void onBeforeDestroy() {
        sendPluginResult("close", false);
        callbackContext = null;
    }

    public void finish() {
        FragmentManager fm = getFragmentManager();
        if (fm != null) {
            FragmentTransaction ft = fm.beginTransaction();
            ft.remove(this);
            ft.commitAllowingStateLoss();
        }
    }

    protected boolean retrieveData(Bundle arguments) {
        return true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getActivity();

        ttAdNative = TTAdManagerHolder.get().createAdNative(mContext);

        slotId = getArguments().getString("slotId");
        count = getArguments().getInt("count", 1);

        if (!TextUtils.isEmpty(slotId) && count >= 1 && count <= 3 && retrieveData(getArguments())) {
            return;
        }

        sendPluginResult("error", -1, "", false);

        finish();
    }

    @Override
    public void onResume() {
        if (forceDestroy) {
            finish();
        }
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
        forceDestroy = true;
    }

    @Override
    public void onDestroy() {
        onBeforeDestroy();
        super.onDestroy();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        showAd();
    }

    abstract protected void showAd();
}
