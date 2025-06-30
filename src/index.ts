import { EventSubscription } from 'expo-modules-core';
import GeolocationServiceModule from './GeolocationServiceModule';
import { ChangeEvent, LocationEvent, Location } from './GeolocationService.types';

export function addStatusListener(listener: (event: ChangeEvent) => void): EventSubscription {
  return GeolocationServiceModule.addListener('onComplete', listener);
}

export function addLocationListener(listener: (event: LocationEvent) => void): EventSubscription {
  return GeolocationServiceModule.addListener('geoLocation', listener);
}

export function start(config: object) {
  return GeolocationServiceModule.startService(JSON.stringify(config));
}

export function stop() {
  return GeolocationServiceModule.stopService();
}

export { Location };
