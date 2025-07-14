package com.navya.hotelbookingservice;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


public interface BookingRepository extends MongoRepository<Booking, String> {

    Optional<Booking> findBookingByBookingId(String bookingId);
    Optional<List<Booking>> findBookingByUserId(String userId);
}
