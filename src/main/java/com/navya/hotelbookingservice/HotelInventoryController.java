package com.navya.hotelbookingservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/hotel/inventory/")
public class HotelInventoryController
{
    private static final Logger logger = LoggerFactory.getLogger(HotelInventoryController.class);

    @Autowired
    HotelRepository hotelInfoRepository;

    @Autowired
    TokenService tokenService;

    @Autowired
    HotelInventoryService hotelService;

    @PostMapping("add")
    public ResponseEntity<?> add(@RequestBody Hotel hotel,
                                          @RequestHeader("traceparent") String traceId) throws JsonProcessingException {

        Optional<Hotel> existingHotel = hotelInfoRepository.findByHotelName(hotel.getHotelName());
        if (existingHotel.isPresent()) {
            logger.info("Hotel already exists with ID: " + hotel.getHotelName());
            return ResponseEntity.status(409).body("Hotel already exists with ID: " + hotel.getHotelName());
        }
        else{
            logger.info("Adding new hotel with ID: " + hotel.getHotelName());
            hotelInfoRepository.save(hotel);

        }
        logger.info("Hotel Details saved: " + hotel.toString());

        return ResponseEntity.status(HttpStatus.CREATED).body("Hotel Added Successfully with ID: " + hotel.getHotelName());
    }

    @GetMapping("fetch")
    public ResponseEntity<?> fetchHotel(@RequestParam String HotelName,
                                        @RequestHeader("Authorization") String token)
    {
        String id = null;
        try
        {
            id =  tokenService.validateToken(token);
            if( id == null)
            {
                logger.info("Token validation failed: Token is null");
                return ResponseEntity.status(401).body("Invalid token");
            }
        }
        catch (WebClientResponseException e)
        {
            logger.info("Token validation failed: " + e.getMessage());
            return ResponseEntity.status(401).body("Invalid token");
        }

        Optional<Hotel> hotelFetched = hotelService.findHotelByName(HotelName);
        logger.info("Phone number from token: " + id);
        return ResponseEntity.ok("Hotel Details fetched Successfully: " + hotelFetched.get().toString());
    }

    @GetMapping("fetchAllHotels")
    public ResponseEntity<?> fetchAllHotels(@RequestParam String HotelName,
                                        @RequestHeader("Authorization") String token)
    {
        String id = null;
        try
        {
            id =  tokenService.validateToken(token);
        }
        catch (WebClientResponseException e)
        {
            logger.info("Token validation failed: " + e.getMessage());
            return ResponseEntity.status(401).body("Invalid token");
        }

        Optional<Hotel> hotelFetched = hotelService.findHotelByName(HotelName);
        logger.info("Phone number from token: " + id);
/*        if(!id.equals(hotelFetched.get().getId()))
        {
            logger.info("Token Validation Mismatch: " + id + " vs " + hotelFetched.get().getId());
            return ResponseEntity.status(401).body("Invalid token or phone number mismatch");
        }*/

        List<Hotel> allHotels = hotelService.getAllHotels();

        return ResponseEntity.ok("Hotel Details fetched Successfully: " + allHotels);
    }


    @DeleteMapping("delete")
    public ResponseEntity<?> deleteHotel(@RequestParam String hotelName,  @RequestHeader("Authorization") String token) {
        String id = null;
        String role = null;
        try
        {
            id =  tokenService.validateToken(token);
            if(id.isEmpty()){
                logger.info("Token validation failed: Token is empty");
                return ResponseEntity.status(401).body("Invalid token");
            }
            else{
                logger.info("Token validation successful for phone: " + id);
                role = tokenService.getRoleFromToken(token);
            }
        }
        catch (WebClientResponseException e)
        {
            logger.info("Token validation failed: " + e.getMessage());
            return ResponseEntity.status(401).body("Invalid token");
        }

        if(role.equals("ADMIN")){
            logger.info("User is an ADMIN, proceeding with deletion");
            logger.debug("Deleting inventory with Hotel Name: " + hotelName);
            Optional<Hotel> existingHotel = hotelInfoRepository.findById(hotelName);
            if (existingHotel.isPresent()) {
                logger.debug("Deleting Hotel Inventory with name: " + hotelName);
                hotelInfoRepository.delete(existingHotel.get());
                return ResponseEntity.status(HttpStatus.OK).body("Hotel Inventory deleted successfully");
            } else {
                logger.debug("Hotel with Name " + hotelName + " does not exist");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Hotel with name " + hotelName + " does not exist");
            }
        } else {
            logger.info("User is not an ADMIN, cannot delete hotel inventory");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only ADMIN can delete hotel inventory");
        }

    }

    @PutMapping("/update-num-of-rooms/{name}")
    public ResponseEntity<Integer> updateNumOfRooms(@PathVariable("name") String name, @RequestBody int newRoomsNum) {
        logger.info("Updating number of rooms for hotel: " + name + " to " + newRoomsNum);

        int rowUpdated = hotelService.updateNumOfRoomsAvailable(name, newRoomsNum);
        if (rowUpdated > 0) {
            return ResponseEntity.ok(rowUpdated);
        }
        return ResponseEntity.badRequest().body(0);
    }


}
