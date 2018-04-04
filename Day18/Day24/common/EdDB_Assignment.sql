-- Day17 Solution : Education DB

-- drop all tables
drop table if exists major_class_relationship;
drop table if exists student_class_relationship;
drop table if exists student;
drop table if exists major;
drop table if exists assignment;
drop table if exists class;
drop table if exists instructor;

-- create the tables
create table major (
	id int primary key auto_increment,
	description varchar(50) not null,
	req_sat int not null
);

create table student (
	id int primary key auto_increment,
	first_name varchar(30) not null,
	last_name varchar(30) not null,
	sat int,
	gpa decimal(4,2) not null,
	major_id int,
	foreign key (major_id) references major(id)
);

create table instructor (
	id int primary key auto_increment,
	first_name varchar(30) not null,
	last_name varchar(30) not null,
	years_experience int not null default 0,
	is_tenured tinyint not null default 0
);

create table class (
	id int primary key auto_increment,
	subject varchar(30) not null,
	section int not null,
	instructor_id int,
	foreign key (instructor_id) references instructor(id)
);

create table assignment (
	id int primary key auto_increment,
	description varchar(30) not null,
	class_id int not null,
	foreign key (class_id) references class(id)
);

create table major_class_relationship (
	id int primary key auto_increment,
	major_id int not null,
	class_id int not null,
	foreign key (major_id) references major(id),
	foreign key (class_id) references class(id)
);

create table student_class_relationship (
	id int primary key auto_increment,
	student_id int not null,
	class_id int not null,
	foreign key (student_id) references student(id),
	foreign key (class_id) references class(id)
);

-- MAJOR: Sample data
INSERT major (id, description, req_sat) VALUES (1,'General Business',800);
INSERT major (id, description, req_sat) VALUES (2,'Accounting', 1000);
INSERT major (id, description, req_sat) VALUES (3,'Finance', 1100);
INSERT major (id, description, req_sat) VALUES (4,'Math', 1300);
INSERT major (id, description, req_sat) VALUES (5,'Engineering', 1350);
INSERT major (id, description, req_sat) VALUES (6,'Education', 900);
INSERT major (id, description, req_sat) VALUES (7,'General Studies', 500);

-- STUDENT: Sample data
INSERT student VALUES(100,'Eric','Ephram',1200,3.0,1);
INSERT student VALUES(110,'Greg','Gould',1100,2.5,null);
INSERT student VALUES(120,'Adam','Ant',1300,3.2,null);
INSERT student VALUES(130,'Howard','Hess',1600,3.7,null);
INSERT student VALUES(140,'Charles','Caldwell',900,2.1,null);
INSERT student VALUES(150,'James','Joyce',1100,2.5,null);
INSERT student VALUES(160,'Doug','Dumas',1350,3.1,null);
INSERT student VALUES(170,'Kevin','Kraft',1000,2.7,null);
INSERT student VALUES(180,'Frank','Fountain',1000,2.5,null);
INSERT student VALUES(190,'Brian','Biggs',950,2.3,null);

-- INSTRUCTOR: Sample data
INSERT instructor VALUES (10,'Joe','Downey',10,1);
INSERT instructor VALUES (20,'Jane','Jones',6,1);
INSERT instructor VALUES (30,'Bill','Tensi',3,0);
INSERT instructor VALUES (40,'Sherry','Nagy',14,1);
INSERT instructor VALUES (50,'Frank','Schell',23,1);
INSERT instructor VALUES (60,'Michelle','Bellman',35,1);
INSERT instructor VALUES (70,'George','Hunt',2,0);
INSERT instructor VALUES (80,'Amy','Brock',7,0);
INSERT instructor VALUES (90,'Larry','Seger',11,1);
INSERT instructor VALUES (100,'Kathy','Miller',21,1);

-- CLASS: Sample data
-- English
INSERT class (id,subject,section,instructor_id) VALUES(10101,'English',101,10);
INSERT class (id,subject,section,instructor_id) VALUES(10102,'English',102,NULL);
INSERT class (id,subject,section,instructor_id) VALUES(10103,'English',103,NULL);
INSERT class (id,subject,section,instructor_id) VALUES(10201,'English',201,10);
INSERT class (id,subject,section,instructor_id) VALUES(10202,'English',202,NULL);
INSERT class (id,subject,section,instructor_id) VALUES(10203,'English',203,NULL);
INSERT class (id,subject,section,instructor_id) VALUES(10301,'English',301,10);
INSERT class (id,subject,section,instructor_id) VALUES(10302,'English',302,NULL);
INSERT class (id,subject,section,instructor_id) VALUES(10303,'English',303,NULL);
-- Math
INSERT class (id,subject,section,instructor_id) VALUES(20201,'Math',201,50);
INSERT class (id,subject,section,instructor_id) VALUES(20202,'Math',202,NULL);
INSERT class (id,subject,section,instructor_id) VALUES(20203,'Math',203,NULL);
INSERT class (id,subject,section,instructor_id) VALUES(20204,'Math',204,NULL);
INSERT class (id,subject,section,instructor_id) VALUES(20401,'Math',401,50);
INSERT class (id,subject,section,instructor_id) VALUES(20402,'Math',402,NULL);
INSERT class (id,subject,section,instructor_id) VALUES(20403,'Math',403,NULL);
INSERT class (id,subject,section,instructor_id) VALUES(20404,'Math',404,NULL);
-- History
INSERT class (id,subject,section,instructor_id) VALUES(30101,'History',101,80);
INSERT class (id,subject,section,instructor_id) VALUES(30202,'History',201,80);
INSERT class (id,subject,section,instructor_id) VALUES(30303,'History',301,80);
-- Computer Science
INSERT class (id,subject,section,instructor_id) VALUES(40311,'Computer Science',311,40);
INSERT class (id,subject,section,instructor_id) VALUES(40312,'Computer Science',312,40);
INSERT class (id,subject,section,instructor_id) VALUES(40313,'Computer Science',313,40);
INSERT class (id,subject,section,instructor_id) VALUES(40441,'Computer Science',441,40);
INSERT class (id,subject,section,instructor_id) VALUES(40442,'Computer Science',442,40);
INSERT class (id,subject,section,instructor_id) VALUES(40443,'Computer Science',443,40);
-- Psychology
INSERT class (id,subject,section,instructor_id) VALUES(50101,'Psychology',101,20);
INSERT class (id,subject,section,instructor_id) VALUES(50102,'Psychology',102,20);
INSERT class (id,subject,section,instructor_id) VALUES(50231,'Psychology',231,20);
INSERT class (id,subject,section,instructor_id) VALUES(50232,'Psychology',232,20);
-- Education
INSERT class (id,subject,section,instructor_id) VALUES(60221,'Education',221,60);
INSERT class (id,subject,section,instructor_id) VALUES(60222,'Education',222,60);
INSERT class (id,subject,section,instructor_id) VALUES(60223,'Education',223,60);
INSERT class (id,subject,section,instructor_id) VALUES(60351,'Education',351,70);
INSERT class (id,subject,section,instructor_id) VALUES(60352,'Education',352,70);
INSERT class (id,subject,section,instructor_id) VALUES(60353,'Education',353,70);

-- Classes needed for major: General Business
INSERT major_class_relationship (major_id, class_id) VALUES(1,10101); -- Gen bus | Eng 101
INSERT major_class_relationship (major_id, class_id) VALUES(1,10102); -- Gen bus | Eng 102
INSERT major_class_relationship (major_id, class_id) VALUES(1,10103); -- Gen bus | Eng 103
INSERT major_class_relationship (major_id, class_id) VALUES(1,20201); -- Gen bus | Mat 201
INSERT major_class_relationship (major_id, class_id) VALUES(1,20202); -- Gen bus | Mat 202
INSERT major_class_relationship (major_id, class_id) VALUES(1,20203); -- Gen bus | Mat 203
INSERT major_class_relationship (major_id, class_id) VALUES(1,30101); -- Gen bus | His 101

-- Classes needed for major: Accounting
INSERT major_class_relationship (major_id, class_id) VALUES(2,10101); -- Acct | Eng 101
INSERT major_class_relationship (major_id, class_id) VALUES(2,10102); -- Acct | Eng 101
INSERT major_class_relationship (major_id, class_id) VALUES(2,10103); -- Acct | Eng 101
INSERT major_class_relationship (major_id, class_id) VALUES(2,20201); -- Acct | Mat 201
INSERT major_class_relationship (major_id, class_id) VALUES(2,20202); -- Acct | Mat 202
INSERT major_class_relationship (major_id, class_id) VALUES(2,20203); -- Acct | Mat 203
INSERT major_class_relationship (major_id, class_id) VALUES(2,30101); -- Acct | His 101

-- Classes needed for major: Finance
INSERT major_class_relationship (major_id, class_id) VALUES(3,10101); -- Fin | Eng 101
INSERT major_class_relationship (major_id, class_id) VALUES(3,10102); -- Fin | Eng 101
INSERT major_class_relationship (major_id, class_id) VALUES(3,10103); -- Fin | Eng 101
INSERT major_class_relationship (major_id, class_id) VALUES(3,20201); -- Fin | Mat 201
INSERT major_class_relationship (major_id, class_id) VALUES(3,20202); -- Fin | Mat 202
INSERT major_class_relationship (major_id, class_id) VALUES(3,20203); -- Fin | Mat 203
INSERT major_class_relationship (major_id, class_id) VALUES(3,30101); -- Fin | His 101

-- Classes needed for major: Finance
INSERT major_class_relationship (major_id, class_id) VALUES(4,10101); -- Math | Eng 101
INSERT major_class_relationship (major_id, class_id) VALUES(4,10102); -- Math | Eng 101
INSERT major_class_relationship (major_id, class_id) VALUES(4,10103); -- Math | Eng 101
INSERT major_class_relationship (major_id, class_id) VALUES(4,20201); -- Math | Mat 201
INSERT major_class_relationship (major_id, class_id) VALUES(4,20202); -- Math | Mat 202
INSERT major_class_relationship (major_id, class_id) VALUES(4,20203); -- Math | Mat 203
INSERT major_class_relationship (major_id, class_id) VALUES(4,20204); -- Math | Mat 204
INSERT major_class_relationship (major_id, class_id) VALUES(4,20401); -- Math | Mat 201
INSERT major_class_relationship (major_id, class_id) VALUES(4,20402); -- Math | Mat 202
INSERT major_class_relationship (major_id, class_id) VALUES(4,20403); -- Math | Mat 203
INSERT major_class_relationship (major_id, class_id) VALUES(4,20404); -- Math | Mat 204
INSERT major_class_relationship (major_id, class_id) VALUES(4,30101); -- Math | His 101

INSERT major_class_relationship (major_id, class_id) VALUES(5,10101);
INSERT major_class_relationship (major_id, class_id) VALUES(5,10102);
INSERT major_class_relationship (major_id, class_id) VALUES(5,20201);
INSERT major_class_relationship (major_id, class_id) VALUES(5,20202);
INSERT major_class_relationship (major_id, class_id) VALUES(5,20203);
INSERT major_class_relationship (major_id, class_id) VALUES(5,20204);
INSERT major_class_relationship (major_id, class_id) VALUES(5,40311);
INSERT major_class_relationship (major_id, class_id) VALUES(5,40312);

INSERT major_class_relationship (major_id, class_id) VALUES(6,10101);
INSERT major_class_relationship (major_id, class_id) VALUES(6,10102);
INSERT major_class_relationship (major_id, class_id) VALUES(6,20201);
INSERT major_class_relationship (major_id, class_id) VALUES(6,20202);
INSERT major_class_relationship (major_id, class_id) VALUES(6,20203);
INSERT major_class_relationship (major_id, class_id) VALUES(6,60221);
INSERT major_class_relationship (major_id, class_id) VALUES(6,60222);
INSERT major_class_relationship (major_id, class_id) VALUES(6,60223);

INSERT major_class_relationship (major_id, class_id) VALUES(7,10101);
INSERT major_class_relationship (major_id, class_id) VALUES(7,20201);
INSERT major_class_relationship (major_id, class_id) VALUES(7,30101);
INSERT major_class_relationship (major_id, class_id) VALUES(7,40311);
INSERT major_class_relationship (major_id, class_id) VALUES(7,50101);
INSERT major_class_relationship (major_id, class_id) VALUES(7,60221);

-- Classes taken by a student


