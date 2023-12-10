package com.nevexo.proiectseminar.database;

import androidx.room.TypeConverter;

import java.util.Date;

public class DateConverter {

    @TypeConverter
    public Date fromTimeStamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public Long dateToTimeStamp(Date date) {
        return date == null ? null : date.getTime();
    }
}
