package com.tapink.midpoint.calendar;

import android.os.Parcel;
import android.os.Parcelable;

public class Attendee implements Parcelable {

  private long databaseId;
  private String name;
  private String email;
  private String address;
  
  public Attendee(long databaseId, String name, String email) {
    this(databaseId, name, email, null);
  }
  
  public Attendee(long databaseId, String name, String email, String address) {
    super();
    
    this.databaseId = databaseId;
    this.name = name;
    this.email = email;
    this.address = address;
  }
  
  public void setAddress(String address) {
    this.address = address;
  }
  
  public String getAddress() {
    return address;
  }

  public String getEmail() {
    return email;
  }

  public String getName() {
    return name;
  }

  public long getDatabaseId() {
    return databaseId;
  }
  
  ////////////////////////////////////////
  // Object
  ////////////////////////////////////////
  
  public String toString() {
    return String.format(
        "(%d) <%s> %s",
        databaseId,
        email,
        name
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
    out.writeString(email);
    out.writeString(address);
  }

  public static final Parcelable.Creator<Attendee> CREATOR
      = new Parcelable.Creator<Attendee>() {
        public Attendee createFromParcel(Parcel in) {
          return new Attendee(in);
        }

        public Attendee[] newArray(int size) {
          return new Attendee[size];
        }
      };

  private Attendee(Parcel in) {
    this(
     in.readLong(),
     in.readString(),
     in.readString(),
     in.readString()
    );
  }
  
}
