package com.example.workshop2.repository;

import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import com.example.workshop2.model.RSVP;
import com.example.workshop2.model.RSVPTotalCntMapper;

@Repository
public class RSVPRepository {
    public static final String SQL_SELECT_ALL_RSVP = "select id, name, email, phone, DATE_FORMAT(confirmation_date, \"%d/%m/%Y\") as confirmation_date , comments from rsvp";

    public static final String SQL_SEARCH_RSVP_BY_NAME = "select id, name, email, phone, DATE_FORMAT(confirmation_date, \"%d/%m/%Y\") as confirmation_date , comments from rsvp where name = ?";

    public static final String SQL_INSERT_RSVP = "insert into rsvp(name, email, phone, confirmation_date, comments) values (?,?,?,?,?)";

    public static final String SQL_UPDATE_RSVP_BY_EMAIL = "update rsvp set name = ?, phone = ? , confirmation_date = ?, comments = ? where email = ?";

    public static final String SQL_TOTAL_CNT_RSVP = "select count(*) as total from rsvp";

    public static final String SQL_SEARCH_RSVP_BY_EMAiL = "select id, name, email, phone, DATE_FORMAT(confirmation_date, \"%d/%m/%Y\") as confirmation_date , comments from rsvp where email = ?";

    @Autowired
    // java database connectivity for relational database 
    private JdbcTemplate jdbcTemplate;
    // string q - params is the name input 
    public List<RSVP> getAllRSVP(String name) {
        // prevent inheritance
        final List<RSVP> rsvps = new LinkedList<>();
        SqlRowSet rs = null;
        // perform the query
        System.out.println("name>" + name);
        // if no name we get the list of all the rsvp
        if (name == null) {
            rs = jdbcTemplate.queryForRowSet(SQL_SELECT_ALL_RSVP);
        // else we get the details of the person who rsvp 
        } else {
            rs = jdbcTemplate.queryForRowSet(SQL_SEARCH_RSVP_BY_NAME, name);
        }

        while (rs.next()) {
            rsvps.add(RSVP.create(rs));
        }
        return rsvps;
    }
    // search the list of rsvp by email 
    public RSVP searchRSVPByEmail(String email) {
        // prevent inheritance
        final List<RSVP> rsvps = new LinkedList<>();
        // perform the query
        final SqlRowSet rs = jdbcTemplate.queryForRowSet(SQL_SEARCH_RSVP_BY_EMAiL, email);

        while (rs.next()) {
            rsvps.add(RSVP.create(rs));
        }
        return rsvps.get(0);
    }
    // add a new rsvp into the table if exist overwrite the rsvp with the new data by updating
    public RSVP insertRSVP(final RSVP rsvp) {
        // hold keys generated during database insertions
        KeyHolder keyholder = new GeneratedKeyHolder();
        try {
            jdbcTemplate.update(conn -> {
                PreparedStatement ps = conn.prepareStatement(SQL_INSERT_RSVP,
                        Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, rsvp.getName());
                ps.setString(2, rsvp.getEmail());
                ps.setString(3, rsvp.getPhone());
                System.out.println("Confirmation date > " + rsvp.getConfirmationDate());
                ps.setTimestamp(4, new Timestamp(rsvp.getConfirmationDate().toDateTime().getMillis()));
                ps.setString(5, rsvp.getComments());
                return ps;
            }, keyholder);
            BigInteger primaryKeyVal = (BigInteger) keyholder.getKey();
            rsvp.setId(primaryKeyVal.intValue());

        } catch (DataIntegrityViolationException e) {
            // email is unique hence if the email exist we will set the value to it
            RSVP existingRSVP = searchRSVPByEmail(rsvp.getEmail());
            existingRSVP.setComments(rsvp.getComments());
            existingRSVP.setName(rsvp.getName());
            existingRSVP.setPhone(rsvp.getPhone());
            existingRSVP.setConfirmationDate(rsvp.getConfirmationDate());
            if (updateRSVP(existingRSVP))
                rsvp.setId(existingRSVP.getId());
        }

        return rsvp;
    }

    public boolean updateRSVP(final RSVP rsvp) {
        return jdbcTemplate.update(SQL_UPDATE_RSVP_BY_EMAIL,
                rsvp.getName(),
                rsvp.getPhone(),
                new Timestamp(rsvp.getConfirmationDate().toDateTime().getMillis()),
                rsvp.getComments(),
                rsvp.getEmail()) > 0;
    }

    public Integer getTotalRSVP() {
        // perform the query
        List<RSVP> rsvps = jdbcTemplate.query(SQL_TOTAL_CNT_RSVP, new RSVPTotalCntMapper(),
                new Object[] {});

        return rsvps.get(0).getTotalCnt();
    }
}
