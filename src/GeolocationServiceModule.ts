import { NativeModule, requireNativeModule } from 'expo';

import { GeolocationServiceModuleEvents } from './GeolocationService.types';

declare class GeolocationServiceModule extends NativeModule<GeolocationServiceModuleEvents> {
  startService: (config: string) => void;
  stopService: () => void;
}

export default requireNativeModule<GeolocationServiceModule>('GeolocationService');
