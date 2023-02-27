package com.example.workshop2.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.workshop2.model.RSVP;
import com.example.workshop2.service.RSVPService;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;

@RestController
@RequestMapping(path = "/api/rsvp", produces = MediaType.APPLICATION_JSON_VALUE)
public class RSVPRestController {
    
    @Autowired
    RSVPService rsvpSvc;

    // READ 
    @GetMapping()
    public ResponseEntity<String> getAllCustomer(@RequestParam(required = false) String name) {
        // Query the database for rsvps 
        List<RSVP> rsvps = rsvpSvc.getAllRSVP(name);

        // Build the result and return json object
        JsonArrayBuilder arrBuilder = Json.createArrayBuilder();
        for(RSVP c : rsvps)
            arrBuilder.add(c.toJSON());
        JsonArray result = arrBuilder.build();
        System.out.println("" + result.toString());
        if (rsvps.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{'error_code': " + HttpStatus.NOT_FOUND + "'}");
        }
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(result.toString());
    }
    // READ
    @GetMapping(path = "/count")
    public ResponseEntity<String> countRSVP() {
        JsonObject resp;
        Integer totalCntRsvps = rsvpSvc.getTotalRSVP();

        resp = Json.createObjectBuilder()
                .add("total_cnt",totalCntRsvps)
                .build();
        
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(resp.toString());
    }
    // CREATE 
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> postRSVP(@RequestBody String json) {
        RSVP rsvp = null;
        RSVP rsvpResult = null;
        JsonObject resp;
        try {
            rsvp = RSVP.create(json);
        } catch (Exception e) {
            e.printStackTrace();
            resp = Json.createObjectBuilder()
                    .add("error", e.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(resp.toString());
        }

        rsvpResult = rsvpSvc.insertRSVP(rsvp);
        resp = Json.createObjectBuilder()
                .add("rsvpId", rsvpResult.getId())
                .build();

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(resp.toString());
    }
    // UPDATE 
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> putRSVP(@RequestBody String json) {
        RSVP rsvp = null;
        boolean rsvpResult = false;
        JsonObject resp;
        try {
            rsvp = RSVP.create(json);
        } catch (Exception e) {
            e.printStackTrace();
            resp = Json.createObjectBuilder()
                    .add("error", e.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(resp.toString());
        }

        rsvpResult = rsvpSvc.updateRSVP(rsvp);
        resp = Json.createObjectBuilder()
                .add("updated", rsvpResult)
                .build();

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(resp.toString());
    }
}
