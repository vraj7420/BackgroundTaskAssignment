package com.example.backgroundtask.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataHelperCityList extends SQLiteOpenHelper {
   public  static int id=1;
    public DataHelperCityList(Context context) {
        super(context,"AddCityListNewOne.db",null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create Table CityList(cityId NUMBER,latitude NUMBER,longitude NUMBER,cityName TEXT,weatherDescription TEXT,temperature NUMBER,humidity NUMBER,windSpeed TEXT)");



    }

    public  Cursor getCityData(){
        SQLiteDatabase Db=this.getWritableDatabase();
        return Db.rawQuery("Select * From CityList",null);
    }


  public Cursor getDateCityFromID(int id)
    {
        SQLiteDatabase Db=this.getWritableDatabase();
        return Db.rawQuery("select * from CityList Where cityID=?",new String[]{String.valueOf(id)});
    }

    public Boolean insertData(String cityName,double latitude,double longitude,String weatherDescription,double temperature,int humidity,String windSpeed){
        SQLiteDatabase DB=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put("CityID",id);
        id=id+1;
        contentValues.put("cityName",cityName);
        contentValues.put("weatherDescription",weatherDescription);
        contentValues.put("latitude",latitude);
        contentValues.put("longitude",longitude);
        contentValues.put("temperature",temperature);
        contentValues.put("humidity",humidity);
        contentValues.put("windSpeed",windSpeed);
        long result=DB.insert("CityList",null,contentValues);
        return result != -1;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public boolean deleteCity(int id)
    {          SQLiteDatabase db=this.getWritableDatabase();
          return db.delete("CityList","CityID" + "=?", new String[]{String.valueOf(id)}) > 0;
    }
}
