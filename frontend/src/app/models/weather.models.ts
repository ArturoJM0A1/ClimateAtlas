/** Estructuras TypeScript que corresponden a los DTO del backend. */
export interface WeatherReading {
  id: number;
  cityId: number;
  temperature: number;
  humidity: number;
  windSpeed: number;
  measuredAt: string;
  queriedAt: string;
}

export interface City {
  id: number;
  name: string;
  state: string;
  latitude: number;
  longitude: number;
  enabled: boolean;
  currentWeather: WeatherReading | null;
}

export interface Statistics {
  totalCities: number;
  enabledCities: number;
  totalReadings: number;
  averageCurrentTemperature: number | null;
}

export interface BatchExecution {
  executionId: number;
  jobName: string;
  status: string;
  startTime: string | null;
  endTime: string | null;
}
