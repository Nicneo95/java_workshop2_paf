-- this is to create a table called rsvp for the workshop
-- we can do this using mysql workbench
CREATE TABLE rsvp
(
    id int auto_increment not null,
    name varchar(64) not null,
    email varchar(64),
    phone varchar(30),
    confirmation_date datetime,
    comments longtext,
    primary key(id)
);