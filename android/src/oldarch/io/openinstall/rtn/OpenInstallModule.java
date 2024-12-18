package io.openinstall.rtn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import java.util.Map;
import java.util.HashMap;
import java.lang.Math;

import java.util.HashMap;
import java.util.Map;

public class OpenInstallModule extends ReactContextBaseJavaModule {

    public static String NAME = "RTNOpenInstall";
    private OpenInstallModuleImpl moduleImpl;

    public OpenInstallModule(final ReactApplicationContext reactContext) {
        super(reactContext);
        this.moduleImpl = new OpenInstallModuleImpl(reactContext);
    }

    @Override
    public String getName() {
        // return "OpeninstallModule";
        return NAME;
    }

    @ReactMethod
    public void serialEnabled(boolean enabled) {
        moduleImpl.serialEnabled(enabled);
    }

    @ReactMethod
    public void clipBoardEnabled(boolean enabled) {
        moduleImpl.clipBoardEnabled(enabled);
    }

    @ReactMethod
    public void init(ReadableMap options) {
        moduleImpl.config(options);
        moduleImpl.init();
    }

    @ReactMethod
    public void registerWakeUp(boolean alwaysCallback) {
        moduleImpl.registerWakeUp(alwaysCallback);
    }

    @ReactMethod
    public void getInstall(double seconds, Promise promise) {
        int timeout = (int) Math.round(seconds);
        moduleImpl.getInstall(timeout, promise);
    }

    @ReactMethod
    public void reportRegister() {
        moduleImpl.reportRegister();
    }

    @ReactMethod
    public void reportEffectPoint(String id, double value, ReadableMap extra) {
        int valueInt = (int) value; // ts 转 java : number -> double
        moduleImpl.reportEffectPoint(id, valueInt, extra);
    }

    @ReactMethod
    public void reportShare(String shareCode, String sharePlatform, Promise promise) {
        moduleImpl.reportShare(shareCode, sharePlatform, promise);
    }

}
