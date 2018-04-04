-- Script to set up the education database

-- Drop the tables if they exist
drop table if exists major_class_relationship;
drop table if exists student_class_relationship;
drop table if exists assignment;
drop table if exists student;
drop table if exists class;
drop table if exists instructor;
drop table if exists major;
drop table if exists grade;

-- Drop the procedures if they exist
drop procedure if exists GetStudentMajor;
drop procedure if exists ClassesRemaining;
drop procedure if exists InsertStudent;
drop procedure if exists InsertInstructor;

-- Create the tables
create table student (
	id int primary key,
	first_name varchar(30) not null,
	last_name varchar(30) not null,
	gpa decimal(5, 1) not null,
	sat int, 
	major_id int	
);

create table major (
	id int primary key auto_increment, 
	name varchar(30) not null, 
	min_sat int not null
);

create table instructor (
	id int primary key auto_increment, 
	first_name varchar(30) not null, 
	last_name varchar(30) not null, 
	yrs_exp int not null, 
	tenured tinyint default 0, 
	major_id int
);

create table class (
	id int primary key auto_increment,
	prefix varchar(30) not null,  
	number int not null,
	pre_req int,  
	instructor_id int
);

create table grade (
	id int primary key, 
	description varchar(30)
);

create table assignment (
	id int primary key auto_increment, 
	student_id int not null,
	assignment_nbr int not null,
	class_id int not null,
	grade_id int default 0, 
	index student_id_idx (student_id)
);

create table major_class_relationship (
	id int primary key auto_increment, 
	major_id int not null, 
	class_id int not null
);

create table student_class_relationship (
	id int primary key auto_increment,
	student_id int not null, 
	class_id int not null
);

-- Create procedures
delimiter //

create procedure GetStudentMajor(in studentId int, out majorId int)
begin
  select major_id into majorId from student where id = studentId;
end //

create procedure ClassesRemaining(in studentId int)
begin
  declare majorId int;
  call GetStudentMajor(studentId, majorId);

  select id as CRN, concat(prefix, ' ', number) as Class
  from class
  where id in (select mcr.class_id 
               from major_class_relationship mcr
               where mcr.major_id = majorId and mcr.class_id not in
	        (select scr.class_id
	         from student_class_relationship scr
	         where scr.student_id = studentId))
  order by id;
end //

-- Checks whether SAT score is valid and whether the student can choose his/her preferred major based on SAT score
create procedure InsertStudent(in id_in int, in first_name_in varchar(30), in last_name_in varchar(30), in gpa_in decimal(5, 1), in sat_in int, in major_in int)
begin
  declare speciality condition for sqlstate '45000';
  declare minSATScore int;

  select min_sat into minSATScore from major where id = major_in;

  if sat_in not between 400 and 1600 then
    signal sqlstate '45000' set message_text = 'SAT score must be between 400 and 1600!';
  elseif sat_in < minSATScore then
    signal sqlstate '45000' set message_text = 'SAT score is not high enough to choose this major!';
  end if;

  insert into student (id, first_name, last_name, gpa, sat, major_id) values (id_in, first_name_in, last_name_in, gpa_in, sat_in, major_in);
end //

-- Checks whether years of experience is greater than 0
create procedure InsertInstructor(in first_name_in varchar(30), in last_name_in varchar(30), in yrs_exp_in int, in tenured_in tinyint, in major_id_in int) 
begin
  declare speciality condition for sqlstate '45000';

  if yrs_exp_in <= 0 then
    signal sqlstate '45000' set message_text = 'Years of experience must be more than 0!';
  end if;

  insert into instructor (first_name, last_name, yrs_exp, tenured, major_id) values (first_name_in, last_name_in, yrs_exp_in, tenured_in, major_id_in);
end //

delimiter ;

-- Foreign key constraints

alter table instructor add constraint fk_instructor_major foreign key (major_id) references major (id);
alter table class add constraint fk_class_instructor foreign key (instructor_id) references instructor (id);
alter table assignment add constraint fk_assignment_student foreign key (student_id) references student(id);
alter table assignment add constraint fk_assignment_class foreign key (class_id) references class(id);
alter table assignment add constraint fk_assignment_grade foreign key (grade_id) references grade (id);
alter table student add constraint fk_student_major foreign key (major_id) references major (id);
alter table major_class_relationship add constraint fk_mcr_major foreign key (major_id) references major (id);
alter table major_class_relationship add constraint fk_mcr_class foreign key (class_id) references class (id);
alter table student_class_relationship add constraint fk_scr_student foreign key (student_id) references student (id);
alter table student_class_relationship add constraint fk_scr_class foreign key (class_id) references class (id);

-- Adding pre_req as a foreign key for the Advanced challenge
alter table class add constraint fk_class_prereq foreign key (pre_req) references class (id);

-- Insertions into Grade
insert into grade (id, description) values (0, 'Not Graded');
insert into grade (id, description) values (1, 'Incomplete');
insert into grade (id, description) values (2, 'Complete and Unsatisfactory');
insert into grade (id, description) values (3, 'Complete and Satisfactory');
insert into grade (id, description) values (4, 'Exceeds Expectations');

-- Insertions into Major
insert into major (name, min_sat) values ('General Business', 800);
insert into major (name, min_sat) values ('Accounting', 1000);
insert into major (name, min_sat) values ('Finance', 1100);
insert into major (name, min_sat) values ('Math', 1300);
insert into major (name, min_sat) values ('Engineering', 1350);
insert into major (name, min_sat) values ('Education', 900);
insert into major (name, min_sat) values ('General Studies', 500);

-- Insertions into Instructor
call InsertInstructor('Jar Jar', 'Binks', 3, 0, 7);
call InsertInstructor('George', 'Feeney', 20, 1, 6);
call InsertInstructor('Edna', 'Kraboppel', 15, 0, 4);
call InsertInstructor('Stephen', 'Hawking', 18, 1, 4);
call InsertInstructor('Temperance', 'Brennen', 2, 0, 4);
call InsertInstructor('Luke', 'Skywalker', 2, 0, 1);
call InsertInstructor('Norville', 'Rogers', 7, 0, 7);
call InsertInstructor('Ren', 'Hoek', 16, 1, 2);
call InsertInstructor('Mortimer', 'Blotchkins', 5, 0, 3);
call InsertInstructor('Linus', 'van Pelt', 30, 1, 5);
call InsertInstructor('Lucy', 'van Pelt', 25, 1, 6);
call InsertInstructor('Oroku', 'Saki', 16, 1, 5);
call InsertInstructor('Baxter', 'Stockman', 11, 0, 2);
call InsertInstructor('Jack', 'Sprat', 1, 0, 3);

-- These insertions will fail because years of experience is invalid
call InsertInstructor('Bowser', 'Koopa', 0, 0, 4);
call InsertInstructor('Steve', 'Austin', -1, 0, 3);

-- Insertions into Student
call InsertStudent(100, 'Eric','Ephram', 3.3, 1100, 3);
call InsertStudent(110, 'Greg','Gould', 3.6, 980, 6);
call InsertStudent(120, 'Adam','Ant', 2.8, 1200, 6);
call InsertStudent(130, 'Howard','Hess', 3.0, 1350, 5);
call InsertStudent(140, 'Charles','Caldwell', 4.0, 1600, 4);
call InsertStudent(150, 'James','Joyce', 2.3, 1050, 2);
call InsertStudent(160, 'Doug','Dumas', 2.8, 1125, 2);
call InsertStudent(170, 'Kevin','Kraft', 3.0, 1375, 4);
call InsertStudent(180, 'Frank','Fountain', 3.2, 600, 7);
call InsertStudent(190, 'Brian','Biggs', 3.9, 1500, 4);

-- These inserts will fail because of failure to satisfy the preconditions
call InsertStudent(200, 'Fred', 'Rogers', 4.0, 399, 1);
call InsertStudent(205, 'John', 'Doe', 3.1, 1601, 2);
call InsertStudent(210, 'Scooby', 'Doo', 4.0, 800, 4);

-- Insertions into class
insert into class (prefix, number, instructor_id) values ('English', 101, 1);
insert into class (prefix, number, instructor_id, pre_req) values ('English', 102, 2, 1);
insert into class (prefix, number, instructor_id, pre_req) values ('English', 103, 7, 2);
insert into class (prefix, number, instructor_id, pre_req) values ('English', 201, 11, 3);
insert into class (prefix, number, instructor_id, pre_req) values ('English', 202, 7, 4);
insert into class (prefix, number, instructor_id, pre_req) values ('English', 203, 11, 5);
insert into class (prefix, number, instructor_id, pre_req) values ('English', 301, 1, 6);
insert into class (prefix, number, instructor_id, pre_req) values ('English', 302, 2, 7);
insert into class (prefix, number, instructor_id, pre_req) values ('English', 303, 11, 8);
insert into class (prefix, number, instructor_id) values ('Math', 201, 4);
insert into class (prefix, number, instructor_id, pre_req) values ('Math', 202, 3, 10);
insert into class (prefix, number, instructor_id, pre_req) values ('Math', 203, 3, 11);
insert into class (prefix, number, instructor_id, pre_req) values ('Math', 204, 5, 12);
insert into class (prefix, number, instructor_id, pre_req) values ('Math', 301, 3, 13);
insert into class (prefix, number, instructor_id, pre_req) values ('Math', 302, 4, 14);
insert into class (prefix, number, instructor_id, pre_req) values ('Math', 303, 4, 15);
insert into class (prefix, number, instructor_id, pre_req) values ('Math', 304, 5, 16);
insert into class (prefix, number, instructor_id) values ('History', 101, 9);
insert into class (prefix, number, instructor_id, pre_req) values ('History', 201, 6, 18);
insert into class (prefix, number, instructor_id, pre_req) values ('History', 301, 13, 19);
insert into class (prefix, number, instructor_id) values ('Computer Science', 311, 10);
insert into class (prefix, number, instructor_id, pre_req) values ('Computer Science', 312, 9, 21);
insert into class (prefix, number, instructor_id, pre_req) values ('Computer Science', 313, 12, 22);
insert into class (prefix, number, instructor_id, pre_req) values ('Computer Science', 441, 10, 23);
insert into class (prefix, number, instructor_id, pre_req) values ('Computer Science', 442, 12, 24);
insert into class (prefix, number, instructor_id, pre_req) values ('Computer Science', 443, 14, 25);
insert into class (prefix, number, instructor_id) values ('Psychology', 101, 6);
insert into class (prefix, number, instructor_id, pre_req) values ('Psychology', 102, 9, 27);
insert into class (prefix, number, instructor_id, pre_req) values ('Psychology', 231, 14, 28);
insert into class (prefix, number, instructor_id, pre_req) values ('Psychology', 232, 1, 29);
insert into class (prefix, number, instructor_id) values ('Education', 221, 2);
insert into class (prefix, number, instructor_id, pre_req) values ('Education', 222, 2, 31);
insert into class (prefix, number, instructor_id, pre_req) values ('Education', 223, 11, 32);
insert into class (prefix, number, instructor_id, pre_req) values ('Education', 351, 11, 33);
insert into class (prefix, number, instructor_id, pre_req) values ('Education', 352, 2, 34);
insert into class (prefix, number, instructor_id, pre_req) values ('Education', 353, 11, 35);

-- Insertions into assignment
insert into assignment (student_id, assignment_nbr, class_id, grade_id) values (100, 1, 3, 4);
insert into assignment (student_id, assignment_nbr, class_id, grade_id) values (100, 2, 7, 2);
insert into assignment (student_id, assignment_nbr, class_id, grade_id) values (100, 2, 11, 4);
insert into assignment (student_id, assignment_nbr, class_id, grade_id) values (100, 3, 1, 3);
insert into assignment (student_id, assignment_nbr, class_id, grade_id) values (110, 1, 31, 1);
insert into assignment (student_id, assignment_nbr, class_id, grade_id) values (110, 1, 24, 3);
insert into assignment (student_id, assignment_nbr, class_id, grade_id) values (110, 2, 19, 2);
insert into assignment (student_id, assignment_nbr, class_id, grade_id) values (110, 2, 15, 3);
insert into assignment (student_id, assignment_nbr, class_id, grade_id) values (120, 4, 1, 0);
insert into assignment (student_id, assignment_nbr, class_id, grade_id) values (120, 1, 1, 4);
insert into assignment (student_id, assignment_nbr, class_id, grade_id) values (120, 1, 35, 3);
insert into assignment (student_id, assignment_nbr, class_id, grade_id) values (130, 1, 12, 3);
insert into assignment (student_id, assignment_nbr, class_id, grade_id) values (140, 1, 22, 4);
insert into assignment (student_id, assignment_nbr, class_id, grade_id) values (150, 1, 19, 2);
insert into assignment (student_id, assignment_nbr, class_id, grade_id) values (160, 1, 12, 1);
insert into assignment (student_id, assignment_nbr, class_id, grade_id) values (170, 1, 30, 0);
insert into assignment (student_id, assignment_nbr, class_id, grade_id) values (180, 1, 3, 4);

-- Insertions into Major_Class_Relationship
insert into major_class_relationship (major_id, class_id) values (4, 10);
insert into major_class_relationship (major_id, class_id) values (4, 11);
insert into major_class_relationship (major_id, class_id) values (4, 12);
insert into major_class_relationship (major_id, class_id) values (4, 13);
insert into major_class_relationship (major_id, class_id) values (4, 14);
insert into major_class_relationship (major_id, class_id) values (4, 15);
insert into major_class_relationship (major_id, class_id) values (4, 16);
insert into major_class_relationship (major_id, class_id) values (4, 17);
insert into major_class_relationship (major_id, class_id) values (6, 31);
insert into major_class_relationship (major_id, class_id) values (6, 32);
insert into major_class_relationship (major_id, class_id) values (6, 33);
insert into major_class_relationship (major_id, class_id) values (6, 34);
insert into major_class_relationship (major_id, class_id) values (6, 35);
insert into major_class_relationship (major_id, class_id) values (6, 36);
insert into major_class_relationship (major_id, class_id) values (7, 1);
insert into major_class_relationship (major_id, class_id) values (7, 10);
insert into major_class_relationship (major_id, class_id) values (7, 18);
insert into major_class_relationship (major_id, class_id) values (7, 21);
insert into major_class_relationship (major_id, class_id) values (7, 27);
insert into major_class_relationship (major_id, class_id) values (7, 31);
insert into major_class_relationship (major_id, class_id) values (1, 1);
insert into major_class_relationship (major_id, class_id) values (2, 1);
insert into major_class_relationship (major_id, class_id) values (3, 1);
insert into major_class_relationship (major_id, class_id) values (4, 1);
insert into major_class_relationship (major_id, class_id) values (5, 1);
insert into major_class_relationship (major_id, class_id) values (6, 1);
insert into major_class_relationship (major_id, class_id) values (2, 10);
insert into major_class_relationship (major_id, class_id) values (2, 11);
insert into major_class_relationship (major_id, class_id) values (2, 12);
insert into major_class_relationship (major_id, class_id) values (2, 13);
insert into major_class_relationship (major_id, class_id) values (2, 31);
insert into major_class_relationship (major_id, class_id) values (1, 10);
insert into major_class_relationship (major_id, class_id) values (1, 11);
insert into major_class_relationship (major_id, class_id) values (1, 2);
insert into major_class_relationship (major_id, class_id) values (1, 3);
insert into major_class_relationship (major_id, class_id) values (4, 21);
insert into major_class_relationship (major_id, class_id) values (4, 22);
insert into major_class_relationship (major_id, class_id) values (4, 23);
insert into major_class_relationship (major_id, class_id) values (3, 11);
insert into major_class_relationship (major_id, class_id) values (3, 2);
insert into major_class_relationship (major_id, class_id) values (3, 18);
insert into major_class_relationship (major_id, class_id) values (3, 27);
insert into major_class_relationship (major_id, class_id) values (5, 11);
insert into major_class_relationship (major_id, class_id) values (5, 12);
insert into major_class_relationship (major_id, class_id) values (5, 13);
insert into major_class_relationship (major_id, class_id) values (5, 14);
insert into major_class_relationship (major_id, class_id) values (5, 27);
insert into major_class_relationship (major_id, class_id) values (5, 28);

-- Insertions into Student_Class_Relationship
insert into student_class_relationship (student_id, class_id) values (100, 1);
insert into student_class_relationship (student_id, class_id) values (100, 10);
insert into student_class_relationship (student_id, class_id) values (100, 18);
insert into student_class_relationship (student_id, class_id) values (100, 21);
insert into student_class_relationship (student_id, class_id) values (100, 27);
insert into student_class_relationship (student_id, class_id) values (100, 31);
insert into student_class_relationship (student_id, class_id) values (100, 2);
insert into student_class_relationship (student_id, class_id) values (100, 11);
insert into student_class_relationship (student_id, class_id) values (100, 28);
insert into student_class_relationship (student_id, class_id) values (110, 1);
insert into student_class_relationship (student_id, class_id) values (110, 2);
insert into student_class_relationship (student_id, class_id) values (110, 3);
insert into student_class_relationship (student_id, class_id) values (110, 4);
insert into student_class_relationship (student_id, class_id) values (110, 5);
insert into student_class_relationship (student_id, class_id) values (110, 6);
insert into student_class_relationship (student_id, class_id) values (110, 7);
insert into student_class_relationship (student_id, class_id) values (110, 8);
insert into student_class_relationship (student_id, class_id) values (110, 9);
insert into student_class_relationship (student_id, class_id) values (110, 10);
insert into student_class_relationship (student_id, class_id) values (120, 31);
insert into student_class_relationship (student_id, class_id) values (120, 32);
insert into student_class_relationship (student_id, class_id) values (120, 33);
insert into student_class_relationship (student_id, class_id) values (120, 34);
insert into student_class_relationship (student_id, class_id) values (120, 35);
insert into student_class_relationship (student_id, class_id) values (120, 36);
insert into student_class_relationship (student_id, class_id) values (120, 18);
insert into student_class_relationship (student_id, class_id) values (120, 27);
insert into student_class_relationship (student_id, class_id) values (130, 31);
insert into student_class_relationship (student_id, class_id) values (130, 10);
insert into student_class_relationship (student_id, class_id) values (130, 11);
insert into student_class_relationship (student_id, class_id) values (130, 12);
insert into student_class_relationship (student_id, class_id) values (130, 13);
insert into student_class_relationship (student_id, class_id) values (130, 14);
insert into student_class_relationship (student_id, class_id) values (130, 15);
insert into student_class_relationship (student_id, class_id) values (130, 16);
insert into student_class_relationship (student_id, class_id) values (130, 17);
insert into student_class_relationship (student_id, class_id) values (130, 18);
insert into student_class_relationship (student_id, class_id) values (140, 10);
insert into student_class_relationship (student_id, class_id) values (140, 11);
insert into student_class_relationship (student_id, class_id) values (140, 12);
insert into student_class_relationship (student_id, class_id) values (140, 13);
insert into student_class_relationship (student_id, class_id) values (140, 14);
insert into student_class_relationship (student_id, class_id) values (140, 15);
insert into student_class_relationship (student_id, class_id) values (140, 16);
insert into student_class_relationship (student_id, class_id) values (140, 17);
insert into student_class_relationship (student_id, class_id) values (140, 21);
insert into student_class_relationship (student_id, class_id) values (140, 22);
insert into student_class_relationship (student_id, class_id) values (140, 23);
insert into student_class_relationship (student_id, class_id) values (140, 24);
insert into student_class_relationship (student_id, class_id) values (140, 25);
insert into student_class_relationship (student_id, class_id) values (140, 26);
insert into student_class_relationship (student_id, class_id) values (140, 1);
insert into student_class_relationship (student_id, class_id) values (150, 1);
insert into student_class_relationship (student_id, class_id) values (160, 27);
insert into student_class_relationship (student_id, class_id) values (160, 28);
insert into student_class_relationship (student_id, class_id) values (160, 29);
insert into student_class_relationship (student_id, class_id) values (160, 1);
insert into student_class_relationship (student_id, class_id) values (160, 2);
insert into student_class_relationship (student_id, class_id) values (160, 10);
insert into student_class_relationship (student_id, class_id) values (160, 11);
insert into student_class_relationship (student_id, class_id) values (170, 10);
insert into student_class_relationship (student_id, class_id) values (170, 11);
insert into student_class_relationship (student_id, class_id) values (170, 27);
insert into student_class_relationship (student_id, class_id) values (170, 31);
insert into student_class_relationship (student_id, class_id) values (180, 1);
insert into student_class_relationship (student_id, class_id) values (180, 2);
insert into student_class_relationship (student_id, class_id) values (180, 10);
insert into student_class_relationship (student_id, class_id) values (180, 11);
insert into student_class_relationship (student_id, class_id) values (180, 18);
insert into student_class_relationship (student_id, class_id) values (180, 19);
insert into student_class_relationship (student_id, class_id) values (180, 21);
insert into student_class_relationship (student_id, class_id) values (180, 22);
insert into student_class_relationship (student_id, class_id) values (180, 27);
insert into student_class_relationship (student_id, class_id) values (180, 28);
insert into student_class_relationship (student_id, class_id) values (180, 31);
insert into student_class_relationship (student_id, class_id) values (180, 32);