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
    this.name = (name);
    this.description = (description);
    this.locationName = locationName;
    this.startTime = (startTime);
    this.endTime = endTime;
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
 
}
