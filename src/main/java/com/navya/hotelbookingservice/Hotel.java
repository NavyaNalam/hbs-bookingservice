package com.navya.hotelbookingservice;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "hotels")
@Getter
@Setter
public class Hotel
{
    @Id
    Long id;
    String hotelName;
    Integer totalNumOfRooms;
    Integer numOfRoomsAvailable;
    String location;


    @Override
    public String toString() {
        return "Hotel{" +
                "hotelId='" + id + '\'' +
                ", hotelName='" + hotelName + '\'' +
                ", totalNumOfRooms=" + totalNumOfRooms +
                ", numOfRoomsAvailable=" + numOfRoomsAvailable +
                ", location='" + location +
                '}';
    }
}
