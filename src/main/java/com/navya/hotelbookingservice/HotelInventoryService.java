package com.navya.hotelbookingservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HotelInventoryService {

    private static final Logger logger = LoggerFactory.getLogger(HotelInventoryService.class);

    @Autowired
    HotelRepository hotelRepo;

    public List<Hotel> getAllHotels() {
        return hotelRepo.findAll();
    }

    public Optional<Hotel> findHotelByName(String name) {
        logger.info("Inside findHotelById of HotelService");
        return hotelRepo.findByHotelName(name);
    }

    public Hotel saveHotel(Hotel hotel) {
        logger.info("Inside saveHotel of HotelService");
        return hotelRepo.save(hotel);
    }

    public void delete(String id) {
        logger.info("Inside delete of HotelService");
        hotelRepo.deleteById(id);
    }

    public int updateNumOfRoomsAvailable(String hotelName, int numOfRoom)  {
                Optional<Hotel> hotel = hotelRepo.findByHotelName(hotelName);
                if(hotel.isPresent()){
                    Hotel existingHotel = hotel.get();
                    existingHotel.setNumOfRoomsAvailable(numOfRoom);
                    hotelRepo.save(existingHotel);
                    logger.info("Updated number of available rooms for hotel: " + hotelName);
                    return 1;
                }else {
                    logger.error("Hotel not found with name: " + hotelName);
                    return 0;
                }
        }



}
