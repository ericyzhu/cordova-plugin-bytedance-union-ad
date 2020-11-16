package cordova.plugins.bytedanceunionad;

import android.content.Context;
import android.content.pm.ApplicationInfo;

import com.bytedance.sdk.openadsdk.TTAdConfig;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdManager;
import com.bytedance.sdk.openadsdk.TTAdSdk;

public class TTAdManagerHolder {

    private static boolean sInit;

    public static TTAdManager get() {
        if (!sInit) {
            throw new RuntimeException("TTAdSdk is not init, please check.");
        }
        return TTAdSdk.getAdManager();
    }

    public static void init(Context context, String appId) {
        doInit(context, appId);
    }

    private static void doInit(Context context, String appId) {
        if (!sInit) {
            TTAdSdk.init(context, buildConfig(context, appId));
            sInit = true;
        }
    }

    private static TTAdConfig buildConfig(Context context, String appId) {

        ApplicationInfo ai = context.getApplicationInfo();
        boolean debug = (ai.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;

        return new TTAdConfig.Builder()
                .appId(appId)
                .appName("App")
                .useTextureView(true)
                .titleBarTheme(TTAdConstant.TITLE_BAR_THEME_DARK)
                .allowShowNotify(true)
                .allowShowPageWhenScreenLock(true)
                .debug(debug)
                .directDownloadNetworkType(TTAdConstant.NETWORK_STATE_WIFI, TTAdConstant.NETWORK_STATE_3G)
                .supportMultiProcess(true)
                .needClearTaskReset()
                .build();
    }
}
