package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem;

import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.DTOs.ContactInfoDTO;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.DTOs.HotelRequestDTO;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.DTOs.HotelResponseDTO;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.entities.ContactInfo;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.entities.Hotel;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.entities.Inventory;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.entities.Room;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.repositories.HotelRepository;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.repositories.InventoryRepository;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.repositories.RoomRepository;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.services.HotelService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HotelServiceTests {

    @Mock
    private HotelRepository hotelRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private RoomRepository roomRepository;

    @InjectMocks
    private HotelService hotelService;

    private Hotel mumbaiHotel;
    private Hotel goaHotel;
    private Room deluxeRoom;
    private Room suiteRoom;
    private Inventory deluxeInventoryDayOne;
    private Inventory deluxeInventoryDayTwo;
    private Inventory suiteInventoryDayOne;
    private HotelRequestDTO hotelRequestDTO;
    private HotelResponseDTO mumbaiHotelResponseDTO;

    @BeforeEach
    void setUp() {
        ContactInfo mumbaiContactInfo = ContactInfo.builder()
                .id(1L)
                .completeAddress("Marine Drive")
                .location("South Mumbai")
                .email("mumbai@roomly.dev")
                .phoneNumber("+919999999999")
                .build();

        mumbaiHotel = Hotel.builder()
                .id(1L)
                .hotelName("Roomly Mumbai")
                .city("Mumbai")
                .active(true)
                .contactInfo(mumbaiContactInfo)
                .photos(new ArrayList<>(List.of("mumbai-1.jpg", "mumbai-2.jpg")))
                .amenities(new ArrayList<>(List.of("Wifi", "Breakfast", "Pool")))
                .build();
        mumbaiContactInfo.setHotel(mumbaiHotel);

        ContactInfo goaContactInfo = ContactInfo.builder()
                .id(2L)
                .completeAddress("Baga Beach Road")
                .location("North Goa")
                .email("goa@roomly.dev")
                .phoneNumber("+918888888888")
                .build();

        goaHotel = Hotel.builder()
                .id(2L)
                .hotelName("Roomly Goa")
                .city("Goa")
                .active(true)
                .contactInfo(goaContactInfo)
                .photos(new ArrayList<>(List.of("goa-1.jpg")))
                .amenities(new ArrayList<>(List.of("Wifi", "Beach View")))
                .build();
        goaContactInfo.setHotel(goaHotel);

        deluxeRoom = Room.builder()
                .id(10L)
                .hotel(mumbaiHotel)
                .type("Deluxe")
                .basePrice(new BigDecimal("4500.00"))
                .totalCount(8)
                .capacity(2)
                .amenities(new ArrayList<>(List.of("Wifi", "King Bed")))
                .photos(new ArrayList<>(List.of("deluxe-1.jpg")))
                .build();

        suiteRoom = Room.builder()
                .id(11L)
                .hotel(mumbaiHotel)
                .type("Suite")
                .basePrice(new BigDecimal("7500.00"))
                .totalCount(4)
                .capacity(4)
                .amenities(new ArrayList<>(List.of("Wifi", "Living Area")))
                .photos(new ArrayList<>(List.of("suite-1.jpg")))
                .build();

        deluxeInventoryDayOne = Inventory.builder()
                .id(100L)
                .hotel(mumbaiHotel)
                .room(deluxeRoom)
                .date(LocalDate.of(2026, 5, 20))
                .bookedCount(2)
                .totalCount(8)
                .surgeFactor(new BigDecimal("1.20"))
                .closed(false)
                .build();

        deluxeInventoryDayTwo = Inventory.builder()
                .id(101L)
                .hotel(mumbaiHotel)
                .room(deluxeRoom)
                .date(LocalDate.of(2026, 5, 21))
                .bookedCount(3)
                .totalCount(8)
                .surgeFactor(new BigDecimal("1.15"))
                .closed(false)
                .build();

        suiteInventoryDayOne = Inventory.builder()
                .id(102L)
                .hotel(mumbaiHotel)
                .room(suiteRoom)
                .date(LocalDate.of(2026, 5, 20))
                .bookedCount(1)
                .totalCount(4)
                .surgeFactor(new BigDecimal("1.30"))
                .closed(false)
                .build();

        deluxeRoom.setInventories(new ArrayList<>(List.of(deluxeInventoryDayOne, deluxeInventoryDayTwo)));
        suiteRoom.setInventories(new ArrayList<>(List.of(suiteInventoryDayOne)));
        mumbaiHotel.setRooms(new ArrayList<>(List.of(deluxeRoom, suiteRoom)));
        mumbaiHotel.setInventories(new ArrayList<>(List.of(
                deluxeInventoryDayOne,
                deluxeInventoryDayTwo,
                suiteInventoryDayOne
        )));

        hotelRequestDTO = HotelRequestDTO.builder()
                .hotelName("Roomly Mumbai")
                .city("Mumbai")
                .contactInfo(ContactInfoDTO.builder()
                        .completeAddress("Marine Drive")
                        .location("South Mumbai")
                        .email("mumbai@roomly.dev")
                        .phoneNumber("+919999999999")
                        .build())
                .photos(new ArrayList<>(List.of("mumbai-1.jpg", "mumbai-2.jpg")))
                .amenities(new ArrayList<>(List.of("Wifi", "Breakfast", "Pool")))
                .build();

        mumbaiHotelResponseDTO = HotelResponseDTO.builder()
                .id(1L)
                .hotelName("Roomly Mumbai")
                .city("Mumbai")
                .active(true)
                .contactInfo(ContactInfoDTO.builder()
                        .completeAddress("Marine Drive")
                        .location("South Mumbai")
                        .email("mumbai@roomly.dev")
                        .phoneNumber("+919999999999")
                        .build())
                .photos(new ArrayList<>(List.of("mumbai-1.jpg", "mumbai-2.jpg")))
                .amenities(new ArrayList<>(List.of("Wifi", "Breakfast", "Pool")))
                .build();
    }

    @Test
    void findAll_ReturnHotels() {
        HotelResponseDTO goaHotelResponseDTO = HotelResponseDTO.builder()
                .id(2L)
                .hotelName("Roomly Goa")
                .city("Goa")
                .active(true)
                .build();

        when(hotelRepository.findAll()).thenReturn(List.of(mumbaiHotel, goaHotel));
        when(modelMapper.map(mumbaiHotel, HotelResponseDTO.class)).thenReturn(mumbaiHotelResponseDTO);
        when(modelMapper.map(goaHotel, HotelResponseDTO.class)).thenReturn(goaHotelResponseDTO);

        List<HotelResponseDTO> hotels = hotelService.findAll();

        assertThat(hotels).containsExactly(mumbaiHotelResponseDTO, goaHotelResponseDTO);
        verify(hotelRepository).findAll();
    }

    @Test
    void createHotel_WhenContactInfoExists_SavesHotelWithBidirectionalContactInfo() {
        Hotel mappedHotel = Hotel.builder()
                .hotelName("Roomly Mumbai")
                .city("Mumbai")
                .contactInfo(ContactInfo.builder().email("mumbai@roomly.dev").build())
                .build();

        when(modelMapper.map(hotelRequestDTO, Hotel.class)).thenReturn(mappedHotel);
        when(hotelRepository.save(mappedHotel)).thenReturn(mumbaiHotel);
        when(modelMapper.map(mumbaiHotel, HotelResponseDTO.class)).thenReturn(mumbaiHotelResponseDTO);

        HotelResponseDTO createdHotel = hotelService.createHotel(hotelRequestDTO);

        assertThat(mappedHotel.getContactInfo().getHotel()).isSameAs(mappedHotel);
        assertThat(createdHotel).isSameAs(mumbaiHotelResponseDTO);
        verify(hotelRepository).save(mappedHotel);
    }

    @Test
    void createHotel_WhenContactInfoIsNull_SavesHotel() {
        Hotel mappedHotel = Hotel.builder()
                .hotelName("Roomly Mumbai")
                .city("Mumbai")
                .contactInfo(null)
                .build();

        when(modelMapper.map(hotelRequestDTO, Hotel.class)).thenReturn(mappedHotel);
        when(hotelRepository.save(mappedHotel)).thenReturn(mappedHotel);
        when(modelMapper.map(mappedHotel, HotelResponseDTO.class)).thenReturn(mumbaiHotelResponseDTO);

        HotelResponseDTO createdHotel = hotelService.createHotel(hotelRequestDTO);

        assertThat(createdHotel).isSameAs(mumbaiHotelResponseDTO);
        verify(hotelRepository).save(mappedHotel);
    }

    @Test
    void findById_ReturnHotel() {
        when(hotelRepository.findHotelById(1L)).thenReturn(mumbaiHotel);
        when(modelMapper.map(mumbaiHotel, HotelResponseDTO.class)).thenReturn(mumbaiHotelResponseDTO);

        HotelResponseDTO hotel = hotelService.findById(1L);

        assertThat(hotel).isSameAs(mumbaiHotelResponseDTO);
        verify(hotelRepository).findHotelById(1L);
    }

    @Test
    void showHotelDetails_ReturnMappedRoomsForHotel() {
        HotelResponseDTO deluxeRoomResponse = HotelResponseDTO.builder().id(10L).hotelName("Deluxe").build();
        HotelResponseDTO suiteRoomResponse = HotelResponseDTO.builder().id(11L).hotelName("Suite").build();

        when(roomRepository.findAllRoomsByHotelId(1L)).thenReturn(List.of(deluxeRoom, suiteRoom));
        when(modelMapper.map(deluxeRoom, HotelResponseDTO.class)).thenReturn(deluxeRoomResponse);
        when(modelMapper.map(suiteRoom, HotelResponseDTO.class)).thenReturn(suiteRoomResponse);

        List<HotelResponseDTO> hotelDetails = hotelService.showHotelDetails(
                1L,
                LocalDate.of(2026, 5, 20),
                LocalDate.of(2026, 5, 23),
                1,
                3
        );

        assertThat(hotelDetails).containsExactly(deluxeRoomResponse, suiteRoomResponse);
        verify(roomRepository).findAllRoomsByHotelId(1L);
    }

    @Test
    void updateHotelByHotelId_WhenHotelFieldsProvided_UpdatesAndReturnsResponse() {
        when(hotelRepository.findHotelById(1L)).thenReturn(mumbaiHotel);
        when(hotelRepository.save(mumbaiHotel)).thenReturn(mumbaiHotel);
        when(modelMapper.map(mumbaiHotel, HotelResponseDTO.class)).thenReturn(mumbaiHotelResponseDTO);

        ResponseEntity<HotelResponseDTO> response = hotelService.updateHotelByHotelId(1L, Map.of(
                "hotelName", "Roomly Bandra",
                "city", "Mumbai",
                "active", false,
                "unknownField", "ignored"
        ));

        assertThat(mumbaiHotel.getHotelName()).isEqualTo("Roomly Bandra");
        assertThat(mumbaiHotel.getCity()).isEqualTo("Mumbai");
        assertThat(mumbaiHotel.getActive()).isFalse();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isSameAs(mumbaiHotelResponseDTO);
        verify(hotelRepository).save(mumbaiHotel);
    }

    @Test
    void updateHotelByHotelId_WhenContactInfoPatchProvided_UpdatesNestedContactInfo() {
        when(hotelRepository.findHotelById(1L)).thenReturn(mumbaiHotel);
        when(hotelRepository.save(mumbaiHotel)).thenReturn(mumbaiHotel);
        when(modelMapper.map(mumbaiHotel, HotelResponseDTO.class)).thenReturn(mumbaiHotelResponseDTO);

        hotelService.updateHotelByHotelId(1L, Map.of(
                "contactInfo", Map.of(
                        "email", "updated@roomly.dev",
                        "phoneNumber", "+917777777777",
                        "unknownField", "ignored"
                )
        ));

        assertThat(mumbaiHotel.getContactInfo().getEmail()).isEqualTo("updated@roomly.dev");
        assertThat(mumbaiHotel.getContactInfo().getPhoneNumber()).isEqualTo("+917777777777");
        assertThat(mumbaiHotel.getContactInfo().getHotel()).isSameAs(mumbaiHotel);
        verify(hotelRepository).save(mumbaiHotel);
    }

    @Test
    void updateHotelByHotelId_WhenContactInfoIsMissing_CreatesContactInfo() {
        mumbaiHotel.setContactInfo(null);

        when(hotelRepository.findHotelById(1L)).thenReturn(mumbaiHotel);
        when(hotelRepository.save(mumbaiHotel)).thenReturn(mumbaiHotel);
        when(modelMapper.map(mumbaiHotel, HotelResponseDTO.class)).thenReturn(mumbaiHotelResponseDTO);

        hotelService.updateHotelByHotelId(1L, Map.of(
                "contactInfo", Map.of(
                        "completeAddress", "New Address",
                        "location", "Bandra"
                )
        ));

        assertThat(mumbaiHotel.getContactInfo()).isNotNull();
        assertThat(mumbaiHotel.getContactInfo().getCompleteAddress()).isEqualTo("New Address");
        assertThat(mumbaiHotel.getContactInfo().getLocation()).isEqualTo("Bandra");
        assertThat(mumbaiHotel.getContactInfo().getHotel()).isSameAs(mumbaiHotel);
        verify(hotelRepository).save(mumbaiHotel);
    }

    @Test
    void deleteHotelByHotelId_DeleteResolvedHotel() {
        when(hotelRepository.findHotelById(1L)).thenReturn(mumbaiHotel);

        hotelService.deleteHotelByHotelId(1L);

        verify(hotelRepository).findHotelById(1L);
        verify(hotelRepository).delete(mumbaiHotel);
        verify(hotelRepository, never()).deleteById(any());
    }
}
