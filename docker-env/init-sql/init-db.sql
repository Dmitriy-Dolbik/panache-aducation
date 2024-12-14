create table Person (
        id BIGSERIAL primary key,
        name varchar(255),
        birth date,
        status smallint check (status between 0 and 1)
    );

create table Dog (
        id BIGSERIAL primary key,
        name varchar(255),
        race varchar(255),
        weight float(53),
        owner_id bigint REFERENCES Person(id)
    );

INSERT INTO person (birth, name, status) VALUES ('1995-09-12', 'Emily Brown', 0);
INSERT INTO person (birth, name, status) VALUES ('1990-01-15', 'John Smith', 1);
INSERT INTO person (birth, name, status) VALUES ('1985-03-22', 'Jessica Taylor', 0);
INSERT INTO person (birth, name, status) VALUES ('2000-07-30', 'Michael Johnson', 1);
INSERT INTO person (birth, name, status) VALUES ('1992-11-05', 'Sarah Williams', 0);
INSERT INTO person (birth, name, status) VALUES ('1988-12-19', 'David Brown', 1);
INSERT INTO person (birth, name, status) VALUES ('1994-04-14', 'Emma Wilson', 0);
INSERT INTO person (birth, name, status) VALUES ('1993-06-29', 'Daniel Jones', 1);
INSERT INTO person (birth, name, status) VALUES ('1987-09-09', 'Olivia Garcia', 0);
INSERT INTO person (birth, name, status) VALUES ('1996-10-10', 'James Martinez', 1);
INSERT INTO person (birth, name, status) VALUES ('1989-08-20', 'Sophia Rodriguez', 0);
INSERT INTO person (birth, name, status) VALUES ('1991-02-28', 'William Hernandez', 1);
INSERT INTO person (birth, name, status) VALUES ('1997-05-18', 'Ava Lopez', 0);
INSERT INTO person (birth, name, status) VALUES ('1986-01-25', 'Benjamin Lee', 1);
INSERT INTO person (birth, name, status) VALUES ('1995-03-12', 'Mia Gonzalez', 0);
INSERT INTO person (birth, name, status) VALUES ('1994-07-07', 'Lucas Perez', 1);
INSERT INTO person (birth, name, status) VALUES ('1983-02-14', 'Charlotte Wilson', 0);
INSERT INTO person (birth, name, status) VALUES ('1998-11-11', 'Henry Anderson', 1);
INSERT INTO person (birth, name, status) VALUES ('1990-06-30', 'Amelia Thomas', 0);
INSERT INTO person (birth, name, status) VALUES ('1984-04-21', 'Elijah Taylor', 1);
INSERT INTO person (birth, name, status) VALUES ('1999-12-31', 'Harper Moore', 0);
INSERT INTO person (birth, name, status) VALUES ('1992-08-08', 'Jackson Jackson', 1);
INSERT INTO person (birth, name, status) VALUES ('1987-05-15', 'Evelyn Martin', 0);
INSERT INTO person (birth, name, status) VALUES ('1993-09-22', 'Alexander Thompson', 1);
INSERT INTO person (birth, name, status) VALUES ('1996-10-04', 'Abigail White', 0);
INSERT INTO person (birth, name, status) VALUES ('1985-01-18', 'Daniel Harris', 1);
INSERT INTO person (birth, name, status) VALUES ('2001-03-03', 'Ella Clark', 0);
INSERT INTO person (birth, name, status) VALUES ('1994-06-06', 'Matthew Lewis', 1);
INSERT INTO person (birth, name, status) VALUES ('1992-07-25', 'Sofia Walker', 0);
INSERT INTO person (birth, name, status) VALUES ('1989-12-12', 'David Hall', 1);
INSERT INTO person (birth, name, status) VALUES ('1995-04-17', 'Scarlett Allen', 0);
INSERT INTO person (birth, name, status) VALUES ('1998-08-21', 'Joseph Young', 1);
INSERT INTO person (birth, name, status) VALUES ('1986-11-30', 'Grace King', 0);
INSERT INTO person (birth, name, status) VALUES ('1997-02-01', 'Samuel Wright', 1);
INSERT INTO person (birth, name, status) VALUES ('2000-09-09', 'Lily Scott', 0);
INSERT INTO person (birth, name, status) VALUES ('1993-05-05', 'Andrew Green', 1);
INSERT INTO person (birth, name, status) VALUES ('1988-03-14', 'Chloe Adams', 0);
INSERT INTO person (birth, name, status) VALUES ('1994-12-24', 'Isaac Baker', 1);
INSERT INTO person (birth, name, status) VALUES ('1996-06-16', 'Samantha Gonzalez', 0);
INSERT INTO person (birth, name, status) VALUES ('1987-09-30', 'Michael Nelson', 1);
INSERT INTO person (birth, name, status) VALUES ('1995-10-10', 'Madison Carter', 0);
INSERT INTO person (birth, name, status) VALUES ('1992-01-01', 'Christopher Mitchell', 1);
INSERT INTO person (birth, name, status) VALUES ('1989-07-07', 'Avery Perez', 0);
INSERT INTO person (birth, name, status) VALUES ('1998-11-15', 'Ethan Roberts', 1);
INSERT INTO person (birth, name, status) VALUES ('2002-02-28', 'Zoey Turner', 0);

-- Add dogs to persons
INSERT INTO dog (name, owner_id) VALUES ('Dog 1', 1);
INSERT INTO dog (name, owner_id) VALUES ('Dog 2', 1);
INSERT INTO dog (name, owner_id) VALUES ('Dog 3', 1);

INSERT INTO dog (name, owner_id) VALUES ('Dog 1', 2);
INSERT INTO dog (name, owner_id) VALUES ('Dog 2', 2);
INSERT INTO dog (name, owner_id) VALUES ('Dog 3', 2);

INSERT INTO dog (name, owner_id) VALUES ('Dog 1', 3);
INSERT INTO dog (name, owner_id) VALUES ('Dog 2', 3);
INSERT INTO dog (name, owner_id) VALUES ('Dog 3', 3);

-- Repeat for all persons
INSERT INTO dog (name, owner_id) VALUES ('Dog 1', 4);
INSERT INTO dog (name, owner_id) VALUES ('Dog 2', 4);
INSERT INTO dog (name, owner_id) VALUES ('Dog 3', 4);