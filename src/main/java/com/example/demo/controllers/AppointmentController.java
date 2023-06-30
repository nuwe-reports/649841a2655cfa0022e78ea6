package com.example.demo.controllers;

import com.example.demo.entities.Appointment;
import com.example.demo.repositories.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class AppointmentController {

    @Autowired
    AppointmentRepository appointmentRepository;

    @GetMapping("/appointments")
    public ResponseEntity<List<Appointment>> getAllAppointments() {
        List<Appointment> appointments = new ArrayList<>();

        appointmentRepository.findAll().forEach(appointments::add);

        if (appointments.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(appointments, HttpStatus.OK);
    }

    @GetMapping("/appointments/{id}")
    public ResponseEntity<Appointment> getAppointmentById(@PathVariable("id") long id) {
        Optional<Appointment> appointment = appointmentRepository.findById(id);

        if (appointment.isPresent()) {
            return new ResponseEntity<>(appointment.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/appointment")
    public ResponseEntity<List<Appointment>> createAppointment(@RequestBody Appointment appointment) {
        if (areRequiredFieldsNull(appointment))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        if (areConflictBetweenDates(appointment.getStartsAt(), appointment.getFinishesAt()))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        if (isAppointmentsOverlap(appointmentRepository.findAll(), appointment))
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);

        appointmentRepository.save(appointment);

        return new ResponseEntity<>(HttpStatus.OK);
    }


    @DeleteMapping("/appointments/{id}")
    public ResponseEntity<HttpStatus> deleteAppointment(@PathVariable("id") long id) {

        Optional<Appointment> appointment = appointmentRepository.findById(id);

        if (!appointment.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        appointmentRepository.deleteById(id);

        return new ResponseEntity<>(HttpStatus.OK);

    }

    @DeleteMapping("/appointments")
    public ResponseEntity<HttpStatus> deleteAllAppointments() {
        appointmentRepository.deleteAll();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private boolean areRequiredFieldsNull(Appointment appointment) {
        if (Objects.isNull(appointment.getPatient()) ||
                Objects.isNull(appointment.getDoctor()) ||
                Objects.isNull(appointment.getRoom())) {
            return true;
        }

        if (appointment.getPatient().getFirstName().trim().isEmpty() ||
                appointment.getPatient().getLastName().trim().isEmpty() ||
                appointment.getPatient().getEmail().trim().isEmpty() ||
                appointment.getPatient().getAge() < 0) {
            return true;
        }

        if (appointment.getDoctor().getFirstName().trim().isEmpty() ||
                appointment.getDoctor().getLastName().trim().isEmpty() ||
                appointment.getDoctor().getEmail().trim().isEmpty() ||
                appointment.getDoctor().getAge() < 18) {
            return true;
        }

        if (appointment.getRoom().getRoomName().trim().isEmpty()) {
            return true;
        }

        return false;
    }

    private boolean areConflictBetweenDates(LocalDateTime date1, LocalDateTime date2) {
        // checks if both dates are the same or date2 is before date1
        return date1.isEqual(date2) || date2.isBefore(date1);
    }

    private boolean isAppointmentsOverlap(List<Appointment> appointments, Appointment appointment) {
        Optional<Appointment> appointmentOptional = appointments.stream()
                .filter(appointmentDb -> appointmentDb.overlaps(appointment))
                .findFirst();
        return appointmentOptional.isPresent();
    }
}
