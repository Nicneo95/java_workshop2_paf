package com.example.workshop2.model;

import java.io.ByteArrayInputStream;

import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormat;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

// 1. create table rsvp
public class RSVP {
    // 2. list the attribute for the table column 
    private Integer id;
    private String name;
    private String email;
    private String phone;
    private DateTime confirmationDate;
    private String comments;
    private Integer totalCnt;

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public DateTime getConfirmationDate() {
        return confirmationDate;
    }
    public void setConfirmationDate(DateTime confirmationDate) {
        this.confirmationDate = confirmationDate;
    }
    public String getComments() {
        return comments;
    }
    public void setComments(String comments) {
        this.comments = comments;
    }
    public Integer getTotalCnt() {
        return totalCnt;
    }
    public void setTotalCnt(Integer totalCnt) {
        this.totalCnt = totalCnt;
    }
    // convert sqlrowset into RSVP object
    public static RSVP create(SqlRowSet rs) {
        // create a rsvp object and store sqlrowset result into rsvp object
        RSVP r = new RSVP();
        // set the attribute by getting it from rs
        r.setId(rs.getInt("id"));
        r.setName(rs.getString("name"));
        r.setEmail(rs.getString("email"));
        r.setPhone(rs.getString("phone"));
        r.setConfirmationDate(new DateTime(
                DateTimeFormat.forPattern("dd/MM/yyyy")
                        .parseDateTime(rs.getString("confirmation_date"))));
        r.setComments(rs.getString("comments"));
        return r;
    }
    // from the rsvp object we convert into JsonObject
    public JsonObject toJSON() {
        return Json.createObjectBuilder()
                .add("id", getId())
                .add("name", getName())
                .add("email", getEmail())
                .add("phone", getPhone())
                .add("confirmation_date", getConfirmationDate() != null ? getConfirmationDate().toString() : "")
                .add("commments", getComments())
                .build();
    }
    // from JSON string as input we return an RSVP object
    public static RSVP create(String jsonStr) throws Exception {
        JsonReader reader = Json.createReader(
                new ByteArrayInputStream(jsonStr.getBytes()));
        return create(reader.readObject());
    }
    // from JSON object we retun an RSVP object inserting all the fields
    private static RSVP create(JsonObject readObject) {
        final RSVP rsvp = new RSVP();
        rsvp.setName(readObject.getString("name"));
        rsvp.setEmail(readObject.getString("email"));
        rsvp.setPhone(readObject.getString("phone"));
        rsvp.setConfirmationDate(new DateTime(Instant.parse(readObject.getString("confirmation_date"))));
        rsvp.setComments(readObject.getString("comments"));
        return rsvp;
    }
}
