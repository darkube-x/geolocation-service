export type Location = {
  latitude: number;
  longitude: number;
  heading: number;
  timestamp: number;
  accuracy: number;
};

export type ChangeEvent = {
  status: string;
};

export type LocationEvent = {
  location: Location;
};

export type GeolocationServiceModuleEvents = {
  onComplete: (params: ChangeEvent) => void;
  geoLocation: (params: LocationEvent) => void;
};
