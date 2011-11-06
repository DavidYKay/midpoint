package com.tapink.midpoint.calendar;

import java.sql.Date;

public class Event {

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
    this.setName(name);
    this.setDescription(description);
    this.locationName = locationName;
    this.startTime = startTime;
    this.endTime = endTime;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }
 
}
