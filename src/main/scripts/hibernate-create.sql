    create table student_groups (
       group_id integer not null auto_increment,
        name varchar(255),
        primary key (group_id)
    ) engine=InnoDB;

    create table students (
       student_id integer not null auto_increment,
        birth_year integer,
        group_id integer,
        name varchar(255),
        primary key (student_id)
    ) engine=InnoDB;

    alter table students 
       add constraint FKk44ecnoi1xpn5d3ofspe7ciss 
       foreign key (group_id) 
       references student_groups (group_id);