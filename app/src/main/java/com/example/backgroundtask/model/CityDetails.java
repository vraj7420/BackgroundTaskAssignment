package com.example.backgroundtask.model;

public class CityDetails {
    String cityName;
    double temperature;
    int humidity;
    double latitude;
    double longitude;
    String weatherDescription;
    String windSpeed;

    int id;

    public CityDetails(int id,String cityName, double temperature, int humidity, double latitude, double longitude, String weatherDescription, String windSpeed) {
        this.id=id;
        this.cityName = cityName;
        this.temperature = temperature;
        this.humidity = humidity;
        this.latitude = latitude;
        this.longitude = longitude;
        this.weatherDescription = weatherDescription;
        this.windSpeed = windSpeed;
    }

    public String getCityName() {
        return cityName;
    }

    public double getTemperature() {
        return temperature;
    }

    public int getHumidity() {
        return humidity;
    }

    public int getId() {
        return id;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getWeatherDescription() {
        return weatherDescription;
    }

    public String getWindSpeed() {
        return windSpeed;
    }
}
