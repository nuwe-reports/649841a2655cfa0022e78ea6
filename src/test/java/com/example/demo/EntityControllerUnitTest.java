package com.example.demo;

import com.example.demo.controllers.DoctorController;
import com.example.demo.controllers.PatientController;
import com.example.demo.controllers.RoomController;
import com.example.demo.entities.Room;
import com.example.demo.repositories.DoctorRepository;
import com.example.demo.repositories.PatientRepository;
import com.example.demo.repositories.RoomRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/**
 * TODO
 * Implement all the unit test in its corresponding class.
 * Make sure to be as exhaustive as possible. Coverage is checked ;)
 */

@WebMvcTest(DoctorController.class)
class DoctorControllerUnitTest {

    @MockBean
    private DoctorRepository doctorRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void this_is_a_test() {
        // DELETE ME
        assertThat(true).isEqualTo(false);
    }
}


@WebMvcTest(PatientController.class)
class PatientControllerUnitTest {

    @MockBean
    private PatientRepository patientRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void this_is_a_test() {
        // DELETE ME
        assertThat(true).isEqualTo(false);
    }

}

@WebMvcTest(RoomController.class)
class RoomControllerUnitTest {

    @MockBean
    private RoomRepository roomRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateRoom() throws Exception {
        // given
        String POST_ROOM_URI = "/api/room";
        Room room = new Room("Dermatology");

        // when
        // then
        mockMvc.perform(post(POST_ROOM_URI).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(room)))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldGetRoomByRoomName() throws Exception {
        // given
        String GET_ROOM_BY_NAME_URI = "/api/rooms/";
        String ROOM_NAME = "Dermatology";
        Room room = new Room(ROOM_NAME);

        Optional<Room> optionalRoom = Optional.of(room);

        // when
        // then
        assertThat(optionalRoom).isPresent();
        assertThat(optionalRoom.get().getRoomName()).isEqualTo(room.getRoomName());
        assertThat(room.getRoomName()).isEqualTo(ROOM_NAME);

        when(roomRepository.findByRoomName(room.getRoomName())).thenReturn(optionalRoom);
        mockMvc.perform(get(GET_ROOM_BY_NAME_URI + ROOM_NAME))
                .andExpect(status().isOk());
    }

    @Test
    void shouldNotGetAnyRoomByRoomName() throws Exception {
        // given
        String GET_ROOM_BY_NAME_URI = "/api/rooms/";
        String ROOM_NAME = "Dermatology";

        // when
        mockMvc.perform(get(GET_ROOM_BY_NAME_URI + ROOM_NAME))
                // then
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldGetTwoRooms() throws Exception {
        // given
        String GET_ALL_ROOMS_URI = "/api/rooms";
        Room room1 = new Room("Dermatology");
        Room room2 = new Room("Dentist");

        List<Room> rooms = new ArrayList<>();
        rooms.add(room1);
        rooms.add(room2);

        // when
        when(roomRepository.findAll()).thenReturn(rooms);

        // then
        mockMvc.perform(get(GET_ALL_ROOMS_URI))
                .andExpect(status().isOk());
    }

    @Test
    void shouldNotGetRooms() throws Exception {
        // given
        String GET_ALL_ROOMS_URI = "/api/rooms";
        List<Room> EMPTY_ROOM_LIST = new ArrayList<>();

        // when
        when(roomRepository.findAll()).thenReturn(EMPTY_ROOM_LIST);

        // then
        mockMvc.perform(get(GET_ALL_ROOMS_URI))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldDeleteRoomByRoomName() throws Exception {
        // given
        String DELETE_ROOM_BY_NAME_URI = "/api/rooms/";
        String ROOM_NAME = "Dermatology";
        Room room = new Room(ROOM_NAME);

        Optional<Room> optionalRoom = Optional.of(room);

        // when
        when(roomRepository.findByRoomName(ROOM_NAME)).thenReturn(optionalRoom);

        // then
        mockMvc.perform(delete(DELETE_ROOM_BY_NAME_URI + ROOM_NAME))
                .andExpect(status().isOk());
    }

    @Test
    void shouldNotDeleteAnyRoom() throws Exception {
        // given
        String DELETE_ROOM_BY_NAME_URI = "/api/rooms/";
        String ROOM_NAME = "Dermatology";

        Optional<Room> optionalRoom = Optional.empty();

        // when
        when(roomRepository.findByRoomName(ROOM_NAME)).thenReturn(optionalRoom);

        // then
        mockMvc.perform(delete(DELETE_ROOM_BY_NAME_URI + ROOM_NAME))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteAllRooms() throws Exception {
        // given
        String DELETE_ROOM_URI = "/api/rooms";

        // when
        // then
        mockMvc.perform(delete(DELETE_ROOM_URI))
                .andExpect(status().isOk());
    }
}
