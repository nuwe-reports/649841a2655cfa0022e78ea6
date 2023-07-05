package com.example.demo;

import com.example.demo.entities.Appointment;
import com.example.demo.entities.Doctor;
import com.example.demo.entities.Patient;
import com.example.demo.entities.Room;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import javax.persistence.EntityExistsException;
import javax.persistence.PersistenceException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@TestInstance(Lifecycle.PER_CLASS)
class EntityUnitTest {

    @Autowired
    private TestEntityManager entityManager;

    private Doctor doctor;

    private Patient patient;

    private Room room;

    private Appointment appointmentOne;
    private Appointment appointmentTwo;
    private Appointment appointmentTree;

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");

    @AfterEach
    void tearDown() {
        // Delete all entities persisted before execute the next test
        entityManager.clear();
    }

    // ###--- APPOINTMENT TESTS ---###
    @Test
    void shouldCreateAppointmentPerConstructor() {
        // given
        LocalDateTime STARTS_AT = LocalDateTime.now();
        LocalDateTime FINISHES_AT = LocalDateTime.now();

        doctor = new Doctor("Héctor", "Cortez", 31, "h.cortez@email.com");
        patient = new Patient("Francisco", "Orieta", 18, "f.orieta@email.com");
        room = new Room("Dentist");

        appointmentOne = new Appointment(patient, doctor, room, STARTS_AT, FINISHES_AT);

        // when
        appointmentOne = entityManager.persist(appointmentOne);

        // then
        assertThat(appointmentOne).isInstanceOf(Appointment.class);
        assertThat(appointmentOne).hasNoNullFieldsOrProperties();

        assertThat(appointmentOne.getPatient()).hasNoNullFieldsOrProperties();
        assertThat(appointmentOne.getDoctor()).hasNoNullFieldsOrProperties();
        assertThat(appointmentOne.getRoom()).hasNoNullFieldsOrProperties();

        assertNotNull(appointmentOne.getStartsAt());
        assertNotNull(appointmentOne.getFinishesAt());
    }

    @Test
    void shouldCreateAppointmentPerEmptyConstructor() {
        // given
        appointmentOne = new Appointment();

        // when
        appointmentOne = entityManager.persist(appointmentOne);

        // then
        assertThat(appointmentOne).isInstanceOf(Appointment.class);
        assertThat(appointmentOne).hasAllNullFieldsOrPropertiesExcept("id");
    }

    @Test
    void shouldObtainAllAppointmentValuesFromGetters() {
        // given
        LocalDateTime STARTS_AT = LocalDateTime.parse("17:10 04/07/2023", formatter);
        LocalDateTime FINISHES_AT = LocalDateTime.parse("17:40 04/07/2023", formatter);

        doctor = new Doctor("Héctor", "Cortez", 31, "h.cortez@email.com");
        patient = new Patient("Francisco", "Orieta", 18, "f.orieta@email.com");
        room = new Room("Dentist");

        appointmentOne = new Appointment(patient, doctor, room, STARTS_AT, FINISHES_AT);

        // when
        appointmentOne = entityManager.persist(appointmentOne);

        // then
        assertThat(appointmentOne.getId()).isNotNull();
        assertThat(appointmentOne)
                .hasFieldOrPropertyWithValue("patient", patient)
                .hasFieldOrPropertyWithValue("doctor", doctor)
                .hasFieldOrPropertyWithValue("room", room)
                .hasFieldOrPropertyWithValue("startsAt", STARTS_AT)
                .hasFieldOrPropertyWithValue("finishesAt", FINISHES_AT);
    }

    @Test
    void shouldSetAppointmentAttributesValues() {
        // given
        LocalDateTime STARTS_AT = LocalDateTime.parse("17:10 04/07/2023", formatter);
        LocalDateTime FINISHES_AT = LocalDateTime.parse("17:40 04/07/2023", formatter);

        doctor = new Doctor("Héctor", "Cortez", 31, "h.cortez@email.com");
        patient = new Patient("Francisco", "Orieta", 18, "f.orieta@email.com");
        room = new Room("Dentist");

        appointmentOne = new Appointment(patient, doctor, room, STARTS_AT, FINISHES_AT);
        appointmentOne = entityManager.persist(appointmentOne);

        // when
        LocalDateTime NEW_STARTS_AT = LocalDateTime.of(2023, 7, 6, 10, 0);
        LocalDateTime NEW_FINISHES_AT = LocalDateTime.of(2023, 7, 6, 10, 30);
        Doctor NEW_DOCTOR = new Doctor("Lucila", "Orieta", 25, "l.orieta@email.com");
        Patient NEW_PATIENT = new Patient("Simona", "Cortez", 5, "s.cortez@email.com");
        Room NEW_ROOM = new Room("Dermatology");
        long NEW_ID = 42L;

        NEW_DOCTOR = entityManager.persist(NEW_DOCTOR);
        NEW_PATIENT = entityManager.persist(NEW_PATIENT);
        NEW_ROOM = entityManager.persist(NEW_ROOM);

        appointmentOne.setId(NEW_ID);
        appointmentOne.setPatient(NEW_PATIENT);
        appointmentOne.setDoctor(NEW_DOCTOR);
        appointmentOne.setRoom(NEW_ROOM);
        appointmentOne.setStartsAt(NEW_STARTS_AT);
        appointmentOne.setFinishesAt(NEW_FINISHES_AT);

        appointmentOne = entityManager.merge(appointmentOne);

        // then
        assertThat(appointmentOne).hasNoNullFieldsOrProperties()
                .hasFieldOrPropertyWithValue("id", NEW_ID)
                .hasFieldOrPropertyWithValue("patient", NEW_PATIENT)
                .hasFieldOrPropertyWithValue("doctor", NEW_DOCTOR)
                .hasFieldOrPropertyWithValue("room", NEW_ROOM)
                .hasFieldOrPropertyWithValue("startsAt", NEW_STARTS_AT)
                .hasFieldOrPropertyWithValue("finishesAt", NEW_FINISHES_AT);
    }

    @Test
    void shouldCreateAppointmentPerConstructorWithNullValues() {
        // given
        appointmentTree = new Appointment(null, null, null, null, null);

        // when
        appointmentTree = entityManager.persist(appointmentTree);

        // then
        assertThat(appointmentTree).hasAllNullFieldsOrPropertiesExcept("id");

        assertNull(appointmentTree.getPatient());
        assertNull(appointmentTree.getDoctor());
        assertNull(appointmentTree.getRoom());
        assertNull(appointmentTree.getStartsAt());
        assertNull(appointmentTree.getFinishesAt());
    }

    @Test
    void shouldNotOverlapsForDifferentAppointments() {
        // given
        // Appointment One Dates and Room
        LocalDateTime START_DATE_ONE = LocalDateTime.parse("17:10 04/07/2023", formatter);
        LocalDateTime FINISH_DATE_ONE = LocalDateTime.parse("17:40 04/07/2023", formatter);
        Room ROOM_ONE = new Room("Dentist");

        // Appointment Two Dates and Room
        LocalDateTime START_DATE_TWO = LocalDateTime.parse("17:10 10/07/2023", formatter);
        LocalDateTime FINISH_DATE_TWO = LocalDateTime.parse("17:40 10/07/2023", formatter);
        Room ROOM_TWO = new Room("Dermatology");

        appointmentOne = new Appointment
                (null, null, ROOM_ONE, START_DATE_ONE, FINISH_DATE_ONE);

        appointmentTwo = new Appointment
                (null, null, ROOM_TWO, START_DATE_TWO, FINISH_DATE_TWO);

        // when
        boolean overlaps = appointmentOne.overlaps(appointmentTwo);

        // then
        assertFalse(overlaps);
    }

    @Test
    void shouldNotOverlapsForSameAppointmentRoomDifferentDates() {
        // given
        Room SAME_ROOM = new Room("Dentist");

        // Appointment One Dates
        LocalDateTime START_DATE_ONE = LocalDateTime.parse("17:10 04/07/2023", formatter);
        LocalDateTime FINISH_DATE_ONE = LocalDateTime.parse("17:40 04/07/2023", formatter);

        // Appointment Two dates
        LocalDateTime START_DATE_TWO = LocalDateTime.parse("17:10 10/07/2023", formatter);
        LocalDateTime FINISH_DATE_TWO = LocalDateTime.parse("17:40 10/07/2023", formatter);

        appointmentOne = new Appointment
                (null, null, SAME_ROOM, START_DATE_ONE, FINISH_DATE_ONE);

        appointmentTwo = new Appointment
                (null, null, SAME_ROOM, START_DATE_TWO, FINISH_DATE_TWO);

        // when
        boolean overlaps = appointmentOne.overlaps(appointmentTwo);

        // then
        assertFalse(overlaps);
    }

    @Test
    void shouldNotOverlapsForSameAppointmentDateDifferentRooms() {
        // given
        LocalDateTime SAME_START_DATE = LocalDateTime.parse("17:10 04/07/2023", formatter);
        LocalDateTime SAME_FINISH_DATE = LocalDateTime.parse("17:40 04/07/2023", formatter);

        Room ROOM_ONE = new Room("Dentist");
        appointmentOne = new Appointment
                (null, null, ROOM_ONE, SAME_START_DATE, SAME_FINISH_DATE);

        Room ROOM_TWO = new Room("Oncology");
        appointmentTwo = new Appointment
                (null, null, ROOM_TWO, SAME_START_DATE, SAME_FINISH_DATE);

        // when
        boolean overlaps = appointmentOne.overlaps(appointmentTwo);

        // then
        assertFalse(overlaps);
    }

    @Test
    void shouldOverlapsForSameAppointmentRoomAndStartDate() {
        // given
        Room SAME_ROOM = new Room("Dentist");
        LocalDateTime SAME_START_DATE = LocalDateTime.parse("17:10 04/07/2023", formatter);

        LocalDateTime FINISH_DATE_ONE = LocalDateTime.parse("17:40 04/07/2023", formatter);
        LocalDateTime FINISH_DATE_TWO = LocalDateTime.parse("18:00 04/07/2023", formatter);

        appointmentOne = new Appointment
                (null, null, SAME_ROOM, SAME_START_DATE, FINISH_DATE_ONE);

        appointmentTwo = new Appointment
                (null, null, SAME_ROOM, SAME_START_DATE, FINISH_DATE_TWO);

        // when
        boolean overlaps = appointmentOne.overlaps(appointmentTwo);

        // then
        assertTrue(overlaps);
    }

    @Test
    void shouldOverlapsForSameAppointmentRoomAndFinishDate() {
        // given
        Room SAME_ROOM = new Room("Dentist");
        LocalDateTime SAME_FINISH_DATE = LocalDateTime.parse("11:00 04/07/2023", formatter);

        LocalDateTime START_DATE_ONE = LocalDateTime.parse("10:20 04/07/2023", formatter);
        LocalDateTime START_DATE_TWO = LocalDateTime.parse("10:30 04/07/2023", formatter);

        appointmentOne = new Appointment
                (null, null, SAME_ROOM, START_DATE_ONE, SAME_FINISH_DATE);

        appointmentTwo = new Appointment
                (null, null, SAME_ROOM, START_DATE_TWO, SAME_FINISH_DATE);

        // when
        boolean overlaps = appointmentOne.overlaps(appointmentTwo);

        // then
        assertTrue(overlaps);
    }

    @Test
    void shouldOverlaps_When_StartDateA_IsMinorThan_FinishDateB_And_FinishDateB_IsMinoThan_FinishDateA() {
        // given
        Room SAME_ROOM = new Room("Example");

        // Appointment One dates
        LocalDateTime START_DATE_ONE = LocalDateTime.parse("09:10 04/07/2023", formatter);
        LocalDateTime FINISH_DATE_ONE = LocalDateTime.parse("09:40 04/07/2023", formatter);

        // Appointment Two dates
        LocalDateTime START_DATE_TWO = LocalDateTime.parse("09:00 04/07/2023", formatter);
        LocalDateTime FINISH_DATE_TWO = LocalDateTime.parse("09:30 04/07/2023", formatter);

        appointmentOne = new Appointment(null, null, SAME_ROOM, START_DATE_ONE, FINISH_DATE_ONE);
        appointmentTwo = new Appointment(null, null, SAME_ROOM, START_DATE_TWO, FINISH_DATE_TWO);

        // when
        boolean overlaps = appointmentOne.overlaps(appointmentTwo);

        // then
        assertTrue(overlaps);
    }

    @Test
    void shouldOverlaps_When_StartDateB_IsMinorThan_StartDateA_And_FinishDateA_IsMinoThan_FinishDateB() {
        // given
        Room SAME_ROOM = new Room("Example");

        // Appointment One dates
        LocalDateTime START_DATE_ONE = LocalDateTime.parse("08:00 04/07/2023", formatter);
        LocalDateTime FINISH_DATE_ONE = LocalDateTime.parse("08:30 04/07/2023", formatter);

        // Appointment Two dates
        LocalDateTime START_DATE_TWO = LocalDateTime.parse("08:15 04/07/2023", formatter);
        LocalDateTime FINISH_DATE_TWO = LocalDateTime.parse("08:45 04/07/2023", formatter);

        appointmentOne = new Appointment(null, null, SAME_ROOM, START_DATE_ONE, FINISH_DATE_ONE);
        appointmentTwo = new Appointment(null, null, SAME_ROOM, START_DATE_TWO, FINISH_DATE_TWO);

        // when
        boolean overlaps = appointmentOne.overlaps(appointmentTwo);

        // then
        assertTrue(overlaps);
    }


    // ###--- DOCTOR TESTS ---###
    @Test
    void shouldObtainDoctorsEmptyStringValues() {
        // given
        String EMPTY_FIRST_NAME = "";
        String EMPTY_LAST_NAME = "";
        String EMPTY_EMAIL = "";

        doctor = new Doctor(EMPTY_FIRST_NAME, EMPTY_LAST_NAME, 0, EMPTY_EMAIL);

        // when
        doctor = entityManager.persist(doctor);

        // then
        assertThat(doctor.getFirstName()).isEmpty();
        assertThat(doctor.getLastName()).isEmpty();
        assertThat(doctor.getEmail()).isEmpty();
    }

    @Test
    void shouldCreateDoctorPerConstructorWithNullValues() {
        // given
        doctor = new Doctor(null, null, 0, null);

        // when
        doctor = entityManager.persist(doctor);

        // then
        assertThat(doctor).isInstanceOf(Doctor.class);
        assertThat(doctor).hasAllNullFieldsOrPropertiesExcept("id", "age");

        assertNull(doctor.getFirstName());
        assertNull(doctor.getLastName());
        assertNull(doctor.getEmail());
    }

    @Test
    void shouldSetDoctorsAttributesValues() {
        // given
        doctor = new Doctor("Héctor", "Cortez", 31, "h.cortez@email.com");
        doctor = entityManager.persist(doctor);

        // when
        String FIRST_NAME = "Lucila";
        String LAST_NAME = "Orieta";
        String EMAIL = "l.orieta@email.com";
        int AGE = 25;
        long ID = 2L;

        doctor.setId(ID);
        doctor.setFirstName(FIRST_NAME);
        doctor.setLastName(LAST_NAME);
        doctor.setAge(AGE);
        doctor.setEmail(EMAIL);

        doctor = entityManager.merge(doctor);

        // then
        assertThat(doctor).hasNoNullFieldsOrProperties()
                .hasFieldOrPropertyWithValue("id", ID)
                .hasFieldOrPropertyWithValue("firstName", FIRST_NAME)
                .hasFieldOrPropertyWithValue("lastName", LAST_NAME)
                .hasFieldOrPropertyWithValue("age", AGE)
                .hasFieldOrPropertyWithValue("email", EMAIL);
    }

    @Test
    void shouldObtainAllDoctorsValuesFromGetters() {
        // given
        String FIRST_NAME = "Héctor";
        String LAST_NAME = "Cortez";
        String EMAIL = "h.cortez@gmail.com";
        int AGE = 31;
        long ID = 1L;

        doctor = new Doctor(FIRST_NAME, LAST_NAME, AGE, EMAIL);

        // when
        doctor = entityManager.persist(doctor);

        // then
        assertThat(doctor.getId()).isNotNull();
        assertThat(doctor.getId()).isGreaterThan(0L);
        assertThat(doctor)
                .hasFieldOrPropertyWithValue("firstName", FIRST_NAME)
                .hasFieldOrPropertyWithValue("lastName", LAST_NAME)
                .hasFieldOrPropertyWithValue("age", AGE)
                .hasFieldOrPropertyWithValue("email", EMAIL);
    }

    @Test
    void shouldCreateDoctorPerEmptyConstructor() {
        // given
        doctor = new Doctor();

        // when
        doctor = entityManager.persist(doctor);

        // then
        assertThat(doctor).isInstanceOf(Doctor.class);
        assertThat(doctor).hasAllNullFieldsOrPropertiesExcept("id", "age");
    }

    @Test
    void shouldCreateDoctorPerConstructor() {
        // given
        String FIRST_NAME = "Héctor";
        String LAST_NAME = "Cortez";
        String EMAIL = "h.cortez@gmail.com";
        int AGE = 31;

        doctor = new Doctor(FIRST_NAME, LAST_NAME, AGE, EMAIL);

        // when
        doctor = entityManager.persist(doctor);

        // then
        assertThat(doctor).isInstanceOf(Doctor.class);
        assertThat(doctor).hasNoNullFieldsOrProperties();

        assertEquals(doctor.getFirstName(), FIRST_NAME);
        assertEquals(doctor.getLastName(), LAST_NAME);
        assertEquals(doctor.getEmail(), EMAIL);
        assertEquals(doctor.getAge(), AGE);
    }


    // ###--- PATIENT TESTS ---###
    @Test
    void shouldCreatePatientPerConstructor() {
        // given
        String FIRST_NAME = "Héctor";
        String LAST_NAME = "Cortez";
        String EMAIL = "h.cortez@gmail.com";
        int AGE = 31;

        patient = new Patient(FIRST_NAME, LAST_NAME, AGE, EMAIL);

        // when
        patient = entityManager.persist(patient);

        // then
        assertThat(patient).isInstanceOf(Patient.class);
        assertThat(patient).hasNoNullFieldsOrProperties();

        assertEquals(patient.getFirstName(), FIRST_NAME);
        assertEquals(patient.getLastName(), LAST_NAME);
        assertEquals(patient.getEmail(), EMAIL);
        assertEquals(patient.getAge(), AGE);
    }

    @Test
    void shouldCreatePatientPerEmptyConstructor() {
        // given
        patient = new Patient();

        // when
        patient = entityManager.persist(patient);

        // then
        assertThat(patient).isInstanceOf(Patient.class);
        assertThat(patient).hasAllNullFieldsOrPropertiesExcept("id", "age");
    }

    @Test
    void shouldObtainAllPatientValuesFromGetters() {
        // given
        String FIRST_NAME = "Héctor";
        String LAST_NAME = "Cortez";
        String EMAIL = "h.cortez@gmail.com";
        int AGE = 31;

        patient = new Patient(FIRST_NAME, LAST_NAME, AGE, EMAIL);

        // when
        patient = entityManager.persist(patient);

        // then
        assertThat(patient.getId()).isNotNull();
        assertThat(patient)
                .hasFieldOrPropertyWithValue("firstName", FIRST_NAME)
                .hasFieldOrPropertyWithValue("lastName", LAST_NAME)
                .hasFieldOrPropertyWithValue("age", AGE)
                .hasFieldOrPropertyWithValue("email", EMAIL);
    }

    @Test
    void shouldSetPatientAttributesValues() {
        // given
        patient = new Patient("Héctor", "Cortez", 31, "h.cortez@email.com");
        patient = entityManager.persist(patient);

        // when
        String FIRST_NAME = "Lucila";
        String LAST_NAME = "Orieta";
        String EMAIL = "l.orieta@email.com";
        int AGE = 25;
        long ID = 2L;

        patient.setId(ID);
        patient.setFirstName(FIRST_NAME);
        patient.setLastName(LAST_NAME);
        patient.setAge(AGE);
        patient.setEmail(EMAIL);

        patient = entityManager.merge(patient);

        // then
        assertThat(patient).hasNoNullFieldsOrProperties()
                .hasFieldOrPropertyWithValue("id", ID)
                .hasFieldOrPropertyWithValue("firstName", FIRST_NAME)
                .hasFieldOrPropertyWithValue("lastName", LAST_NAME)
                .hasFieldOrPropertyWithValue("age", AGE)
                .hasFieldOrPropertyWithValue("email", EMAIL);
    }

    @Test
    void shouldObtainPatientEmptyStringValues() {
        // given
        String EMPTY_FIRST_NAME = "";
        String EMPTY_LAST_NAME = "";
        String EMPTY_EMAIL = "";

        patient = new Patient(EMPTY_FIRST_NAME, EMPTY_LAST_NAME, 0, EMPTY_EMAIL);

        // when
        patient = entityManager.persist(patient);

        // then
        assertThat(patient.getFirstName()).isEmpty();
        assertThat(patient.getLastName()).isEmpty();
        assertThat(patient.getEmail()).isEmpty();
    }

    @Test
    void shouldCreatePatientPerConstructorWithNullValues() {
        // given
        patient = new Patient(null, null, 0, null);

        // when
        patient = entityManager.persist(patient);

        // then
        assertThat(patient).isInstanceOf(Patient.class);
        assertThat(patient).hasAllNullFieldsOrPropertiesExcept("id", "age");

        assertNull(patient.getFirstName());
        assertNull(patient.getLastName());
        assertNull(patient.getEmail());
    }


    // ###--- ROOM TESTS ---###
    @Test
    void shouldCreateRoomPerConstructor() {
        // given
        String ROOM_NAME = "Dermatology";

        room = new Room(ROOM_NAME);

        // when
        room = entityManager.persist(room);

        // then
        assertThat(room).isInstanceOf(Room.class);
        assertThat(room).hasNoNullFieldsOrProperties();

        assertEquals(room.getRoomName(), ROOM_NAME);
    }

    @Test
    void shouldThrowForCreateRoomPerEmptyConstructor() {
        // given
        String ERROR_MESSAGE = "ids for this class must be manually assigned before calling " +
                "save(): com.example.demo.entities.Room";
        room = new Room();

        // when
        // then
        assertThatThrownBy(() -> entityManager.persist(room))
                .isInstanceOf(PersistenceException.class)
                .hasMessageContaining(ERROR_MESSAGE);
    }

    @Test
    void shouldObtainRoomNameValueFromGetter() {
        // given
        String ROOM_NAME = "Pediatrics";
        room = new Room(ROOM_NAME);

        // when
        room = entityManager.persist(room);

        // then
        assertThat(room.getRoomName()).isEqualTo(ROOM_NAME);
    }

    @Test
    void shouldObtainRoomEmptyName() {
        // given
        String EMPTY_ROOM_NAME = "";

        room = new Room(EMPTY_ROOM_NAME);

        // when
        room = entityManager.persist(room);

        // then
        assertThat(room.getRoomName()).isEmpty();
    }

    @Test
    void shouldThrowForCreateRoomPerConstructorWithNullValue() {
        // given
        String ERROR_MESSAGE = "ids for this class must be manually assigned before calling " +
                "save(): com.example.demo.entities.Room";
        room = new Room(null);

        // when
        // then
        assertThatThrownBy(() -> entityManager.persist(room))
                .isInstanceOf(PersistenceException.class)
                .hasMessageContaining(ERROR_MESSAGE);
    }

    @Test
    void shouldThrowForRoomIdDuplicity() {
        // given
        String ROOM_NAME = "Oncology";
        String ERROR_MESSAGE = "A different object with the same identifier value was already " +
                "associated with the session : [com.example.demo.entities.Room#Oncology]";

        room = new Room(ROOM_NAME);
        Room r2 = new Room(ROOM_NAME);

        // when
        entityManager.persist(room);

        // and when
        // then
        assertThatThrownBy(() -> entityManager.persist(r2))
                .isInstanceOf(EntityExistsException.class)
                .hasMessageContaining(ERROR_MESSAGE);
    }
}
