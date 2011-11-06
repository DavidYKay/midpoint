package com.tapink.midpoint.calendar;

import java.sql.Date;

import android.os.Parcel;
import android.os.Parcelable;

public class Event implements Parcelable {

  private long databaseId = 1;
  private String name;
  private String description;
  private String locationName;
  private Date startTime;
  private Date endTime;
  
  public Event(long databaseId, String name, String description,
      String locationName, Date startTime, Date endTime) {
    super();
    this.databaseId = databaseId;
    this.name = (name);
    this.description = (description);
    this.locationName = locationName;
    this.startTime = (startTime);
    this.endTime = endTime;
  }
  
  public long getDatabaseId() {
    return databaseId;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public Date getStartTime() {
    return startTime;
  }
  
  ////////////////////////////////////////
  // Object
  ////////////////////////////////////////
  
  public String toString() {
    return String.format(
        "(%d) %s AT %s: %s",
        databaseId,
        name,
        locationName,
        description
        );
  }
  
  ////////////////////////////////////////
  // Parcelable
  ////////////////////////////////////////

  public int describeContents() {
    return 0;
  }

  public void writeToParcel(Parcel out, int flags) {
    out.writeLong(databaseId);
    out.writeString(name);
    out.writeString(description);
    out.writeString(locationName);
    out.writeLong(startTime.getTime());
    out.writeLong(endTime.getTime());
  }

  public static final Parcelable.Creator<Event> CREATOR
      = new Parcelable.Creator<Event>() {
        public Event createFromParcel(Parcel in) {
          return new Event(in);
        }

        public Event[] newArray(int size) {
          return new Event[size];
        }
      };

  private Event(Parcel in) {
    this(
     in.readLong(),
     in.readString(),
     in.readString(),
     in.readString(),
     new Date(in.readLong()),
     new Date(in.readLong())
    );
      //throw new IllegalStateException("Failed to load Event from Parcelled data!");
  }
 
}
