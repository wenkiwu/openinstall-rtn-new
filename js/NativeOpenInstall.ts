import type {TurboModule} from 'react-native/Libraries/TurboModule/RCTExport';
import {TurboModuleRegistry} from 'react-native';

export interface Spec extends TurboModule {
  serialEnabled(enabled: boolean): void;
  clipBoardEnabled(enabled: boolean): void;
  init(options: Object): void;
  registerWakeUp(alwaysCallback: boolean): void;
  getInstall(seconds: number): Promise<Object>;
  reportRegister():void;
  reportEffectPoint(id: string, value: number, extra: Object): void;
  reportShare(shareCode: string, sharePlatform: string): Promise<Object>;
}

export default TurboModuleRegistry.get<Spec>(
  'RTNOpenInstall',
) as Spec | null;