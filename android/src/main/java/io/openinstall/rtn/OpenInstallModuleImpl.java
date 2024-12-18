package io.openinstall.rtn;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import com.fm.openinstall.Configuration;
import com.fm.openinstall.OpenInstall;
import com.fm.openinstall.listener.AppInstallListener;
import com.fm.openinstall.listener.AppInstallRetryAdapter;
import com.fm.openinstall.listener.AppWakeUpAdapter;
import com.fm.openinstall.listener.AppWakeUpListener;
import com.fm.openinstall.listener.ResultCallback;
import com.fm.openinstall.model.AppData;
import com.fm.openinstall.model.Error;

import java.util.Map;
import java.util.HashMap;

// 模块的业务具体实现
public class OpenInstallModuleImpl {

    private static final String TAG = "OpenInstallModule";
    private static final String EVENT = "OPENINSTALL_WAKEUP_EVENT";
    private final ReactApplicationContext context;
    private Intent wakeupIntent = null;
    private WritableMap wakeupDataHolder = null;
    private boolean registerWakeUp = false;
    private boolean initialized = false;
    private Configuration configuration = null;
    private boolean alwaysCallback = false;

    public OpenInstallModuleImpl(final ReactApplicationContext reactContext) {
        context = reactContext;
        OpenInstall.preInit(context);
        reactContext.addActivityEventListener(new ActivityEventListener() {
            @Override
            public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {

            }

            @Override
            public void onNewIntent(Intent intent) {
                Log.d(TAG, "onNewIntent");
                getWakeUp(intent);
            }
        });
    }

    private boolean hasTrue(ReadableMap map, String key) {
        if (map.hasKey(key)) {
            if (map.isNull(key)) return false;
            return map.getBoolean(key);
        }
        return false;
    }


    public void config(ReadableMap readableMap) {
        Configuration.Builder builder = new Configuration.Builder();

        if (hasTrue(readableMap, "adEnabled")) {
            builder.adEnabled(true);
        }
        if (readableMap.hasKey("oaid")) {
            builder.oaid(readableMap.getString("oaid"));
        }
        if (readableMap.hasKey("gaid")) {
            builder.gaid(readableMap.getString("gaid"));
        }
        if (hasTrue(readableMap, "imeiDisabled")) {
            builder.imeiDisabled();
        }
        if (readableMap.hasKey("imei")) {
            builder.imei(readableMap.getString("imei"));
        }
        if (hasTrue(readableMap, "macDisabled")) {
            builder.macDisabled();
        }
        if (readableMap.hasKey("macAddress")) {
            builder.macAddress(readableMap.getString("macAddress"));
        }
        if (readableMap.hasKey("androidId")) {
            builder.androidId(readableMap.getString("androidId"));
        }
        if (readableMap.hasKey("serialNumber")) {
            builder.serialNumber(readableMap.getString("serialNumber"));
        }
        if (hasTrue(readableMap, "simulatorDisabled")) {
            builder.simulatorDisabled();
        }
        if (hasTrue(readableMap, "storageDisabled")) {
            builder.storageDisabled();
        }

        configuration = builder.build();

    }


    public void serialEnabled(boolean enabled) {
        OpenInstall.serialEnabled(enabled);
    }


    public void clipBoardEnabled(boolean enabled) {
        OpenInstall.clipBoardEnabled(enabled);
    }


    public void init() {
        if (context.hasCurrentActivity()) {
            OpenInstall.init(context.getCurrentActivity(), configuration);
        } else {
            Log.w(TAG, "init with context, not activity");
            OpenInstall.init(context, configuration);
        }
        initialized();
    }

    private void initialized() {
        initialized = true;
        if (wakeupIntent != null) {
            OpenInstall.getWakeUp(wakeupIntent, new AppWakeUpAdapter() {
                @Override
                public void onWakeUp(AppData appData) {
                    wakeupIntent = null;
                    if (appData != null) {
                        Log.d(TAG, "getWakeUp : wakeupData = " + appData.toString());
                        WritableMap params = putData2Map(appData);
                        context.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                                .emit(EVENT, params);
                    }
                }
            });
        }
    }

    public void registerWakeUp(boolean alwaysCallback) {
        registerWakeUp = true;
        this.alwaysCallback = alwaysCallback;
        if (wakeupDataHolder != null) {
            // 调用getWakeUp注册前就处理过拉起参数了(onNewIntent)
            context.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                    .emit(EVENT, wakeupDataHolder);
            wakeupDataHolder = null;
        } else {
            Activity currentActivity = context.getCurrentActivity();
            if (currentActivity != null) {
                Intent intent = currentActivity.getIntent();
                getWakeUp(intent);
            }
        }
    }

    // 可能在用户调用初始化之前调用
    private void getWakeUp(Intent intent) {
        if (initialized) {
            if (alwaysCallback) {
                OpenInstall.getWakeUpAlwaysCallback(intent, new AppWakeUpListener() {
                    @Override
                    public void onWakeUpFinish(AppData appData, Error error) {
                        WritableMap params = putData2Map(appData);
                        if (registerWakeUp) {
                            context.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                                    .emit(EVENT, params);
                        } else {
                            wakeupDataHolder = params;
                        }
                    }
                });
            } else {
                OpenInstall.getWakeUp(intent, new AppWakeUpAdapter() {
                    @Override
                    public void onWakeUp(AppData appData) {
                        WritableMap params = putData2Map(appData);
                        if (registerWakeUp) {
                            context.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                                    .emit(EVENT, params);
                        } else {
                            wakeupDataHolder = params;
                        }
                    }
                });
            }
        } else {
            wakeupIntent = intent;
        }
    }

    public void getInstall(int time, final Promise promise) {
        OpenInstall.getInstall(new AppInstallListener() {
            @Override
            public void onInstallFinish(AppData appData, Error error) {
                WritableMap params = putData2Map(appData);
                putError2Map(params, error);
                promise.resolve(params);
            }
        }, time);
    }

    public void getInstallCanRetry(int time, final Promise promise) {
        OpenInstall.getInstallCanRetry(new AppInstallRetryAdapter() {
            @Override
            public void onInstall(AppData appData, boolean retry) {
                WritableMap params = putData2Map(appData);
                params.putBoolean("retry", retry);
                params.putBoolean("shouldRetry", retry);
                promise.resolve(params);
            }
        }, time);
    }

    public void reportRegister() {
        OpenInstall.reportRegister();
    }

    public void reportEffectPoint(String pointId, Integer pointValue, ReadableMap readableMap) {
        if (!TextUtils.isEmpty(pointId) && pointValue >= 0) {
            HashMap<String, String> extraMap = null;
            if (readableMap != null) {
                extraMap = new HashMap<>();
                HashMap<String, Object> map = readableMap.toHashMap();
                for (Map.Entry<String, ?> entry : map.entrySet()) {
                    String name = entry.getKey();
                    Object value = entry.getValue();
                    if (value == null) continue;
                    if (value instanceof String) {
                        extraMap.put(name, (String) value);
                    } else {
                        extraMap.put(name, value.toString());
                    }
                }
            }
            OpenInstall.reportEffectPoint(pointId, pointValue, extraMap);
        } else {
            Log.w(TAG, "reportEffectPoint 调用失败：pointId 不能为空，pointValue 必须大于0");
        }
    }

    @ReactMethod
    public void reportShare(String shareCode, String sharePlatform, final Promise promise) {
        if (TextUtils.isEmpty(shareCode) || TextUtils.isEmpty(sharePlatform)) {
            Log.w(TAG, "reportShare 调用失败：shareCode 和 sharePlatform 不能为空");
            WritableMap params = Arguments.createMap();
            params.putBoolean("shouldRetry", false);
            params.putString("message", "shareCode 和 sharePlatform 不能为空");
            promise.resolve(params);
        } else {
            OpenInstall.reportShare(shareCode, sharePlatform, new ResultCallback<Void>() {
                @Override
                public void onResult(Void v, Error error) {
                    WritableMap params = Arguments.createMap();
                    putError2Map(params, error);
                    promise.resolve(params);
                }
            });
        }
    }

    private WritableMap putData2Map(AppData appData) {
        WritableMap params = Arguments.createMap();
        if (appData != null) {
            params.putString("channel", appData.getChannel());
            params.putString("data", appData.getData());
        }
        return params;
    }

    private WritableMap putError2Map(WritableMap params, Error error) {
        if (params == null) {
            params = Arguments.createMap();
        }
        params.putBoolean("shouldRetry", error != null && error.shouldRetry());
        if (error != null) {
            params.putString("message", error.getErrorMsg());
        }
        return params;
    }


}