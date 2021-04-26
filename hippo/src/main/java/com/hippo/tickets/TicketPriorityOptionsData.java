package com.hippo.tickets;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TicketPriorityOptionsData {

@SerializedName("statusCode")
@Expose
private Long statusCode;
@SerializedName("message")
@Expose
private String message;
@SerializedName("data")
@Expose
private Data data;

public Long getStatusCode() {
return statusCode;
}

public void setStatusCode(Long statusCode) {
this.statusCode = statusCode;
}

public String getMessage() {
return message;
}

public void setMessage(String message) {
this.message = message;
}

public Data getData() {
return data;
}

public void setData(Data data) {
this.data = data;
}

}