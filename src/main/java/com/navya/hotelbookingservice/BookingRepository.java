package com.navya.hotelbookingservice;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


public interface BookingRepository extends MongoRepository<Booking, String> {

    Optional<Booking> findBookingByBookingId(Long bookingId);
}
