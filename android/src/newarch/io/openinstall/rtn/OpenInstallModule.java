package io.openinstall.rtn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import java.util.Map;
import java.util.HashMap;
import java.lang.Math;

public class OpenInstallModule extends NativeOpenInstallSpec {

    public static String NAME = "RTNOpenInstall";

    private OpenInstallModuleImpl moduleImpl;

    OpenInstallModule(ReactApplicationContext context) {
        super(context);
        this.moduleImpl = new OpenInstallModuleImpl(context);
    }

    @Override
    public @Nonnull String getName() {
        return NAME;
    }

    @Override
    public void serialEnabled(boolean enabled) {
        moduleImpl.serialEnabled(enabled);
    }

    @Override
    public void clipBoardEnabled(boolean enabled) {
        moduleImpl.clipBoardEnabled(enabled);
    }

    @Override
    public void init(ReadableMap options) {
        moduleImpl.config(options);
        moduleImpl.init();
    }

    @Override
    public void registerWakeUp(boolean alwaysCallback) {
        moduleImpl.registerWakeUp(alwaysCallback);
    }

    @Override
    public void getInstall(double seconds, Promise promise) {
        int timeout = (int) Math.round(seconds);
        moduleImpl.getInstall(timeout, promise);
    }

    @Override
    public void reportRegister() {
        moduleImpl.reportRegister();
    }

    @Override
    public void reportEffectPoint(String id, double value, ReadableMap extra) {
        int valueInt = (int) value; // ts 转 java : number -> double
        moduleImpl.reportEffectPoint(id, valueInt, extra);
    }

    @Override
    public void reportShare(String shareCode, String sharePlatform, Promise promise) {
        moduleImpl.reportShare(shareCode, sharePlatform, promise);
    }

}