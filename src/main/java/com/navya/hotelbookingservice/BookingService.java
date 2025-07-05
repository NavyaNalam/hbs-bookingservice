package com.navya.hotelbookingservice;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BookingService {

    private static final Logger logger = LoggerFactory.getLogger(BookingService.class);

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    WebClient webClientBuilder;

    @Autowired
    private HotelInventoryService hotelInventoryService;

    public List<Booking> findAll() {
        return bookingRepository.findAll();
    }

    public Object saveBooking(Long hotelId, Booking booking) {
        logger.info("Inside saveBooking of BookingService");

        //Get the Hotel from the Booking
        Optional<Hotel> hotel = hotelRepository.findByHotelName(booking.getHotelName());
        if (hotel.isEmpty()) {
            logger.debug("Hotel Inventory doesnt exist for Hotel Name: " + booking.getHotelName());
            return ResponseEntity.status(404).body("Hotel Inventory doesnt exist for Hotel Name: " + booking.getHotelName() + " not found");
        } else {
            int bookedRooms = booking.getBookedRoomsNum();
            int numOfRoomsAvailable = hotel.get().getNumOfRoomsAvailable();

            // Check to see if there are available rooms
            if (numOfRoomsAvailable <= 0 || bookedRooms > numOfRoomsAvailable) {
                return false;
            } else {

                // update the hotel number of rooms available
                int numOfHotelRoomRemaining = numOfRoomsAvailable - bookedRooms;
                int updatedRow;
                updatedRow = hotelInventoryService.updateNumOfRoomsAvailable(hotelId, numOfHotelRoomRemaining);

                int i = updatedRow;

                if (i == 0) {
                    return false;
                }

                booking.setBookingStatus("booked");

                //create the booking
                bookingRepository.save(booking);

                return true;
            }

        }
    }

    public Booking findBookingById(Long id)
    {
        return bookingRepository.findBookingById(id);
    }

}





