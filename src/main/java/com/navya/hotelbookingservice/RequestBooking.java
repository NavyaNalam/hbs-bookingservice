package com.navya.hotelbookingservice;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class RequestBooking {
    String userId;
    String hotelName;
    Integer numOfRooms;
    Date checkInDate;
    Date checkOutDate;

}



