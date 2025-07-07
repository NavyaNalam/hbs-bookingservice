package com.navya.hotelbookingservice;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.persistence.Column;
import java.util.Date;

@Entity
@Getter
@Setter
@Document(collection = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long bookingId;

    String userId; // Phone Number of the User

    String hotelName;

    Integer numOfRoomsBooked;

    String bookingStatus;

    @JsonFormat(pattern = "yyyy-MM-dd")
    Date startDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    Date endDate;

    Integer totalPrice;



    @Override
    public String toString() {
        return "Booking{" +
                "bookingId=" + bookingId +
                ", userId=" + userId +
                ", hotelName='" + hotelName + '\'' +
                ", numOfRoomsBooked=" + numOfRoomsBooked +
                ", bookingStatus='" + bookingStatus + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }

}
