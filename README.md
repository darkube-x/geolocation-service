# geolocation-service

A simple JavaScript module to manage geolocation services with start, stop, and event listener capabilities.

### `start(config: object): void`

Starts the geolocation service with the specified configuration.

- **Parameters:**
  - `config` (object): Configuration options for the geolocation service. This will be converted to JSON internally.
- **Throws:** Error if the `config` object cannot be serialized to JSON.

### `stop(): void`

Stops the geolocation service.

### `addLocationListener(listener: (event: LocationEvent) => void): EventSubscription`

Adds a listener for geolocation updates.

- **Parameters:**
  - `listener` (function): Callback function invoked with a `LocationEvent` object when location updates occur.
- **Returns:** An `EventSubscription` that can be used to remove the listener.

### `addStatusListener(listener: (event: ChangeEvent) => void): EventSubscription`

Adds a listener for status changes or completion events.

- **Parameters:**
  - `listener` (function): Callback function invoked with a `ChangeEvent` object when the status changes or the service completes.
- **Returns:** An `EventSubscription` that can be used to remove the listener.

<br>
<br>

## Usage Example

```js
import GeoLocation from './GeoLocation';

// Start the geolocation service with configuration
GeoLocation.start(
    { interval: 60000 } // in milliseconds
);

// Add a location listener
const locationSubscription = GeoLocation.addLocationListener({ location } => {
  console.log('New location:', location);
});

// Add a status listener
const statusSubscription = GeoLocation.addStatusListener({ status } => {
  console.log('Status changed:', status);
});

// To stop the service
GeoLocation.stop();

// To remove listeners when no longer needed
locationSubscription.remove();
statusSubscription.remove();
