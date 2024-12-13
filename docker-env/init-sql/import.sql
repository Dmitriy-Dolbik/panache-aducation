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
