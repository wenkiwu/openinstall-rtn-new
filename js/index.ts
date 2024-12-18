import NativeOpenInstall from './NativeOpenInstall'
import {
    EmitterSubscription,
    Platform,
    DeviceEventEmitter,
} from 'react-native';

const WakeUpEventName: string = 'OPENINSTALL_WAKEUP_EVENT'

export default class RTNOpenInstall {
    static serialEnabled(enabled: boolean) {
        if (Platform.OS === 'android') {
            NativeOpenInstall?.serialEnabled(enabled)
        } else {
            // ignore, platform unsupport
        }
    }

    static clipBoardEnabled(enabled: boolean) {
        if (Platform.OS === 'android') {
            NativeOpenInstall?.clipBoardEnabled(enabled)
        } else {
            // ignore, platform unsupport
        }
    }


    static init(options?: Object) {
        if (Platform.OS == 'android') {
            if (options == null) {
                options = {}
            }
            NativeOpenInstall?.init(options)
        } else {

        }
    }

    static getInstall(seconds?: number): Promise<Object> {
        if (seconds == null) {
            seconds = 10
        }
        if (NativeOpenInstall == null) {
            return Promise.reject("import NativeOpenInstall failed")
        }
        return NativeOpenInstall.getInstall(seconds)
    }

    static addWakeUpListener(callback : Function, alwaysCallback?: boolean) : EmitterSubscription {
        NativeOpenInstall?.registerWakeUp(alwaysCallback ?? false)
        return DeviceEventEmitter.addListener(
            WakeUpEventName,
            result => {
                callback(result)
            }
        )
    }

    static reportRegister() {
        NativeOpenInstall?.reportRegister()
    }

    static reportEffectPoint(effectID: string, effectValue: number, extraMap?: Object) {
        if (extraMap == null) {
            extraMap = {}
        }
        NativeOpenInstall?.reportEffectPoint(effectID, effectValue, extraMap)
    }


    static reportShare(shareCode: string, sharePlatform: string): Promise<Object> {
        if (NativeOpenInstall == null) {
            return Promise.reject("import NativeOpenInstall failed")
        }
        return NativeOpenInstall.reportShare(shareCode, sharePlatform)
    }

}