package com.example.demo.controllers;

import com.example.demo.entities.Appointment;
import com.example.demo.repositories.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        // checking if other classes that Appointment depends on are null
        if (Objects.isNull(appointment.getPatient()))
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        if (Objects.isNull(appointment.getDoctor()))
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        if (Objects.isNull(appointment.getRoom()))
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        // checking if dates are null
        if (Objects.isNull(appointment.getStartsAt()) || Objects.isNull(appointment.getFinishesAt()))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        // checks if there is a null attribute for Patient
        if (appointment.getPatient().getFirstName().trim().isEmpty())
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (appointment.getPatient().getLastName().trim().isEmpty())
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (appointment.getPatient().getEmail().trim().isEmpty())
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (appointment.getPatient().getAge() < 0)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        // checks if there is a null attribute for Doctor
        if (appointment.getDoctor().getFirstName().trim().isEmpty())
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (appointment.getDoctor().getLastName().trim().isEmpty())
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (appointment.getDoctor().getEmail().trim().isEmpty())
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (appointment.getDoctor().getAge() < 18)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        // checks if there is a null attribute for Room
        if (appointment.getRoom().getRoomName().trim().isEmpty())
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        // checks if both dates, start and finish are the same
        if (appointment.getStartsAt().isEqual(appointment.getFinishesAt()))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        // checks if the end date is before start date
        if (appointment.getFinishesAt().isBefore(appointment.getStartsAt()))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        // checking conflicts between appointments
        List<Appointment> appointmentsFromDb = appointmentRepository.findAll();
        Optional<Appointment> appointmentOptional = appointmentsFromDb.stream()
                .filter(appointmentDb -> appointmentDb.overlaps(appointment))
                .findFirst();
        if (appointmentOptional.isPresent())
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

}
