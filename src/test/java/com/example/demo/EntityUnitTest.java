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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

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

    @AfterEach
    void tearDown() {
        // Delete all entities persisted before execute the next test
        entityManager.clear();
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
        assertThat(doctor)
                .hasFieldOrPropertyWithValue("id", ID)
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
