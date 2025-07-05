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

    Long userId;

    Long hotelId;

    String hotelName;

    @Column(name = "booked_rooms_num")
    Integer bookedRoomsNum;

    @Column(name = "booking_status")
    String bookingStatus;


    @Column(name = "start_date", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date startDate;

    @Column(name = "end_date", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date endDate;

    @Override
    public String toString() {
        return "Booking{" +
                "bookingId=" + bookingId +
                ", userId=" + userId +
                ", hotelId=" + hotelId +
                ", hotelName='" + hotelName + '\'' +
                ", bookedRoomsNum=" + bookedRoomsNum +
                ", bookingStatus='" + bookingStatus + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }

}
