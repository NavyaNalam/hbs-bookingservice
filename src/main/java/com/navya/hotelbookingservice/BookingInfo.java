package com.navya.hotelbookingservice;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class BookingInfo {
    private String bookingId;
    private String hotelName;
    private String customerName;
    private int userId;
    private String checkInDate;
    private String checkOutDate;
    private int numberOfGuests;



}
