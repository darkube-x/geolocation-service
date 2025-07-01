import { EventSubscription } from 'expo-modules-core';
import GeolocationServiceModule from './GeolocationServiceModule';
import { ChangeEvent, LocationEvent, Location } from './GeolocationService.types';

function start(config: object) {
  try {
    const configString = JSON.stringify(config)
    return GeolocationServiceModule.startService(configString);
  } catch (e) {
    throw new Error("Invalid config: not a valid JSON.");
  }
}

function stop() {
  return GeolocationServiceModule.stopService();
}

function addLocationListener(listener: (event: LocationEvent) => void): EventSubscription {
  return GeolocationServiceModule.addListener('geoLocation', listener);
}

function addStatusListener(listener: (event: ChangeEvent) => void): EventSubscription {
  return GeolocationServiceModule.addListener('onComplete', listener);
}

const GeoLocation = {
  start,
  stop,
  addLocationListener,
  addStatusListener,
}

export { Location };
export default GeoLocation;
