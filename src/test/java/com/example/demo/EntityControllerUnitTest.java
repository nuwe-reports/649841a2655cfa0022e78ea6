package com.example.demo;

import com.example.demo.controllers.DoctorController;
import com.example.demo.controllers.PatientController;
import com.example.demo.controllers.RoomController;
import com.example.demo.entities.Doctor;
import com.example.demo.entities.Patient;
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
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.lang.Math.toIntExact;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
    void shouldCreateDoctor() throws Exception {
        // given
        String POST_DOCTOR_URI = "/api/doctor";
        Doctor doctor = new Doctor("Héctor", "Cortez", 31, "h.cortez@email.com");

        // when
        ResultActions resultActions = mockMvc.perform(post(POST_DOCTOR_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(doctor)));

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName", is(doctor.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(doctor.getLastName())))
                .andExpect(jsonPath("$.age", is(doctor.getAge())))
                .andExpect(jsonPath("$.email", is(doctor.getEmail())));
    }

    @Test
    void shouldGetDoctorById() throws Exception {
        // given
        String GET_DOCTOR_BY_ID_URI = "/api/doctors/{id}";
        long DOCTOR_ID = 1L;
        Doctor doctor = new Doctor("Héctor", "Cortez", 31, "h.cortez@email.com");
        doctor.setId(DOCTOR_ID);

        given(doctorRepository.findById(DOCTOR_ID)).willReturn(Optional.of(doctor));

        // when
        ResultActions resultActions = mockMvc.perform(get(GET_DOCTOR_BY_ID_URI, DOCTOR_ID));

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(toIntExact(doctor.getId()))))
                .andExpect(jsonPath("$.firstName", is(doctor.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(doctor.getLastName())))
                .andExpect(jsonPath("$.age", is(doctor.getAge())))
                .andExpect(jsonPath("$.email", is(doctor.getEmail())));
    }

    @Test
    void shouldNotGetAnyDoctor() throws Exception {
        // given
        String GET_DOCTOR_BY_ID_URI = "/api/doctors/{id}";
        long DOCTOR_ID = 1L;

        given(doctorRepository.findById(DOCTOR_ID)).willReturn(Optional.empty());

        // when
        ResultActions resultActions = mockMvc.perform(get(GET_DOCTOR_BY_ID_URI, DOCTOR_ID));

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldGetAllDoctors() throws Exception {
        // given
        String GET_ALL_DOCTORS_URI = "/api/doctors";
        Doctor doctor1 = new Doctor("Héctor", "Cortez", 31, "h.cortez@email.com");
        Doctor doctor2 = new Doctor("Francisco", "Orieta", 18, "f.orieta@email.com");
        doctor1.setId(1L);
        doctor2.setId(2L);

        List<Doctor> doctors = new ArrayList<>();
        doctors.add(doctor1);
        doctors.add(doctor2);

        given(doctorRepository.findAll()).willReturn(doctors);

        // when
        ResultActions resultActions = mockMvc.perform(get(GET_ALL_DOCTORS_URI));

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(doctors.size())));
    }

    @Test
    void shouldNotGetDoctors() throws Exception {
        // given
        String GET_ALL_DOCTORS_URI = "/api/doctors";
        List<Doctor> EMPTY_DOCTOR_LIST = new ArrayList<>();

        given(doctorRepository.findAll()).willReturn(EMPTY_DOCTOR_LIST);

        // when
        ResultActions resultActions = mockMvc.perform(get(GET_ALL_DOCTORS_URI));

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldDeleteDoctorById() throws Exception {
        // given
        String DELETE_DOCTOR_BY_ID_URI = "/api/doctors/{id}";
        long DOCTOR_ID = 3L;
        Doctor doctor = new Doctor("Héctor", "Cortez", 31, "h.cortez@email.com");
        doctor.setId(DOCTOR_ID);

        given(doctorRepository.findById(DOCTOR_ID)).willReturn(Optional.of(doctor));

        // when
        ResultActions resultActions = mockMvc.perform(delete(DELETE_DOCTOR_BY_ID_URI, DOCTOR_ID));

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void shouldNotDeleteAnyDoctor() throws Exception {
        // given
        String DELETE_DOCTOR_BY_ID_URI = "/api/doctors/{id}";
        long DOCTOR_ID = 8L;

        given(doctorRepository.findById(DOCTOR_ID)).willReturn(Optional.empty());

        // when
        ResultActions resultActions = mockMvc.perform(delete(DELETE_DOCTOR_BY_ID_URI, DOCTOR_ID));

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteAllDoctors() throws Exception {
        // given
        String DELETE_ALL_DOCTORS_URI = "/api/doctors";

        // when
        ResultActions resultActions = mockMvc.perform(delete(DELETE_ALL_DOCTORS_URI));

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isOk());
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
    void shouldCreatePatient() throws Exception {
        // given
        String POST_PATIENT_URI = "/api/patient";
        Patient patient = new Patient("Héctor", "Cortez", 31, "h.cortez@email.com");

        // when
        ResultActions resultActions = mockMvc.perform(post(POST_PATIENT_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(patient)));

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName", is(patient.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(patient.getLastName())))
                .andExpect(jsonPath("$.age", is(patient.getAge())))
                .andExpect(jsonPath("$.email", is(patient.getEmail())));
    }

    @Test
    void shouldGetPatientById() throws Exception {
        // given
        String GET_PATIENT_BY_ID_URI = "/api/patients/{id}";
        long PATIENT_ID = 1L;
        Patient patient = new Patient("Héctor", "Cortez", 31, "h.cortez@email.com");
        patient.setId(PATIENT_ID);

        given(patientRepository.findById(PATIENT_ID)).willReturn(Optional.of(patient));

        // when
        ResultActions resultActions = mockMvc.perform(get(GET_PATIENT_BY_ID_URI, PATIENT_ID));

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(toIntExact(patient.getId()))))
                .andExpect(jsonPath("$.firstName", is(patient.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(patient.getLastName())))
                .andExpect(jsonPath("$.age", is(patient.getAge())))
                .andExpect(jsonPath("$.email", is(patient.getEmail())));
    }

    @Test
    void shouldNotGetAnyPatient() throws Exception {
        // given
        String GET_PATIENT_BY_ID_URI = "/api/patients/{id}";
        long PATIENT_ID = 2L;

        given(patientRepository.findById(PATIENT_ID)).willReturn(Optional.empty());

        // when
        ResultActions resultActions = mockMvc.perform(get(GET_PATIENT_BY_ID_URI, PATIENT_ID));

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldGetAllPatients() throws Exception {
        // given
        String GET_ALL_PATIENTS_URI = "/api/patients";
        Patient patient1 = new Patient("Héctor", "Cortez", 31, "h.cortez@email.com");
        Patient patient2 = new Patient("Francisco", "Orieta", 18, "f.orieta@email.com");

        patient1.setId(1L);
        patient2.setId(2L);

        List<Patient> patients = new ArrayList<>();
        patients.add(patient1);
        patients.add(patient2);

        given(patientRepository.findAll()).willReturn(patients);

        // when
        ResultActions resultActions = mockMvc.perform(get(GET_ALL_PATIENTS_URI));

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(patients.size())));
    }

    @Test
    void shouldNotGetPatients() throws Exception {
        // given
        String GET_ALL_PATIENTS_URI = "/api/patients";
        List<Patient> EMPTY_PATIENT_LIST = new ArrayList<>();

        given(patientRepository.findAll()).willReturn(EMPTY_PATIENT_LIST);

        // when
        ResultActions resultActions = mockMvc.perform(get(GET_ALL_PATIENTS_URI));

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldDeletePatientById() throws Exception {
        // given
        String DELETE_PATIENT_BY_ID_URI = "/api/patients/{id}";
        long PATIENT_ID = 3L;
        Patient patient = new Patient("Héctor", "Cortez", 31, "h.cortez@email.com");
        patient.setId(PATIENT_ID);

        given(patientRepository.findById(PATIENT_ID)).willReturn(Optional.of(patient));

        // when
        ResultActions resultActions = mockMvc.perform(delete(DELETE_PATIENT_BY_ID_URI, PATIENT_ID));

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void shouldNotDeleteAnyPatient() throws Exception {
        // given
        String DELETE_PATIENT_BY_ID_URI = "/api/patients/{id}";
        long PATIENT_ID = 8L;

        given(patientRepository.findById(PATIENT_ID)).willReturn(Optional.empty());

        // when
        ResultActions resultActions = mockMvc.perform(delete(DELETE_PATIENT_BY_ID_URI, PATIENT_ID));

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteAllPatients() throws Exception {
        // given
        String DELETE_ALL_PATIENTS_URI = "/api/patients";

        // when
        ResultActions resultActions = mockMvc.perform(delete(DELETE_ALL_PATIENTS_URI));

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isOk());
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
        ResultActions resultActions = mockMvc.perform(post(POST_ROOM_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(room)));

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.roomName", is(room.getRoomName())));
    }

    @Test
    void shouldGetRoomByRoomName() throws Exception {
        // given
        String GET_ROOM_BY_NAME_URI = "/api/rooms/{roomName}";
        String ROOM_NAME = "Dermatology";
        Room room = new Room(ROOM_NAME);

        given(roomRepository.findByRoomName(ROOM_NAME)).willReturn(Optional.of(room));

        // when
        ResultActions resultActions = mockMvc.perform(get(GET_ROOM_BY_NAME_URI, ROOM_NAME));

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roomName", is(room.getRoomName())));
    }

    @Test
    void shouldNotGetAnyRoomByRoomName() throws Exception {
        // given
        String GET_ROOM_BY_NAME_URI = "/api/rooms/{roomName}";
        String ROOM_NAME = "Invalid";

        given(roomRepository.findByRoomName(ROOM_NAME)).willReturn(Optional.empty());

        // when
        ResultActions resultActions = mockMvc.perform(get(GET_ROOM_BY_NAME_URI, ROOM_NAME));

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldGetAllRooms() throws Exception {
        // given
        String GET_ALL_ROOMS_URI = "/api/rooms";
        Room room1 = new Room("Dermatology");
        Room room2 = new Room("Dentist");

        List<Room> rooms = new ArrayList<>();
        rooms.add(room1);
        rooms.add(room2);

        given(roomRepository.findAll()).willReturn(rooms);

        // when
        ResultActions resultActions = mockMvc.perform(get(GET_ALL_ROOMS_URI));

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(rooms.size())));
    }

    @Test
    void shouldNotGetRooms() throws Exception {
        // given
        String GET_ALL_ROOMS_URI = "/api/rooms";
        List<Room> emptyRoomList = new ArrayList<>();

        given(roomRepository.findAll()).willReturn(emptyRoomList);

        // when
        ResultActions resultActions = mockMvc.perform(get(GET_ALL_ROOMS_URI));

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldDeleteRoomByRoomName() throws Exception {
        // given
        String DELETE_ROOM_BY_NAME_URI = "/api/rooms/{roomName}";
        String ROOM_NAME = "Dermatology";
        Room room = new Room(ROOM_NAME);

        given(roomRepository.findByRoomName(ROOM_NAME)).willReturn(Optional.of(room));

        // when
        ResultActions resultActions = mockMvc.perform(delete(DELETE_ROOM_BY_NAME_URI, ROOM_NAME));

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void shouldNotDeleteAnyRoom() throws Exception {
        // given
        String DELETE_ROOM_BY_NAME_URI = "/api/rooms/{roomName}";
        String ROOM_NAME = "Dermatology";

        given(roomRepository.findByRoomName(ROOM_NAME)).willReturn(Optional.empty());

        // when
        ResultActions resultActions = mockMvc.perform(delete(DELETE_ROOM_BY_NAME_URI, ROOM_NAME));

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteAllRooms() throws Exception {
        // given
        String DELETE_ALL_ROOMS_URI = "/api/rooms";

        // when
        ResultActions resultActions = mockMvc.perform(delete(DELETE_ALL_ROOMS_URI));

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isOk());
    }
}
