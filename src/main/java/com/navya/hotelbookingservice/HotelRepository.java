package com.navya.hotelbookingservice;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;


public interface HotelRepository extends MongoRepository<Hotel, String>
{
    Optional<Hotel> findByHotelName(String hotelName);

    Optional<Hotel> findById(long id);
}
