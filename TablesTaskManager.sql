create database [Network-task-manager-db];

go

use [Network-task-manager-db];

go

create table Person (
	Id_of_person uniqueidentifier primary key not null,
	Login_of_person varchar(30) unique not null,
	Password_of_person varchar(80) not null,
	Phone_number varchar(12) unique not null,
	Email varchar(200) unique not null,
	Secret_word varchar(80),
	Role_of_person varchar(5) check (Role_of_person in ('ADMIN', 'USER')) not null,
	Registered_at datetime2(0) not null
);

go

create table Task (
	Id_of_task uniqueidentifier primary key not null,
	Name_of_task varchar(30) not null,
	Description_of_task varchar(350),
	Status_of_task varchar(9) check (Status_of_task in ('NEEDSTODO', 'INPROCESS', 'DONE')) not null,
	Priority_of_task varchar(6) check (Priority_of_task in ('LOW', 'MEDIUM', 'HIGH')) not null,
	Id_of_creator uniqueidentifier not null,
	Id_of_executor uniqueidentifier,
	Created_at datetime2(0) not null,
	Updated_at datetime2(0) not null,
	foreign key (Id_of_creator) references Person (Id_of_person),
	foreign key (Id_of_executor) references Person (Id_of_person)
);

go