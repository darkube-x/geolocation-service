import * as Geolocation from 'geolocation-service';
import { useEffect } from 'react';
import { Button, View } from 'react-native';

export default function App() {

  useEffect(() => {
    const subscription = Geolocation.addStatusListener(({ status }) => {
      console.log(status);
    });
    const locationsubscription = Geolocation.addLocationListener(({ location }) => {
      console.log('Location update:', location);
    });

    return () => {
      subscription.remove();
      locationsubscription.remove();
    }
  }, []);

  return (
    <View style={{ flex: 1, alignItems: 'center', justifyContent: 'center' }}>
      <Button title={`Start`} onPress={() => Geolocation.start({})} />
      <Button title={`Stop`} onPress={() => Geolocation.stop()} />
    </View>
  );
}
