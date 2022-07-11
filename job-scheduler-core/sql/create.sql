-- for mysql
create database job_scheduler ;

use job_scheduler;

create table job
(
    id bigint not null auto_increment ,
    version bigint not null ,
    enabled smallint not null comment '0 disabled 1 enabled' ,
    name varchar(50) not null comment 'job name' ,
    next_time timestamp(3) not null default CURRENT_TIMESTAMP(3) comment 'next run time' ,
    start_time timestamp(3) null comment 'first run time' ,
    end_time timestamp(3) null comment 'final run time' ,
    state smallint not null comment '0 new 1 running 2 terminated' ,
    class_name varchar(200) not null comment 'name of job class' ,
    data blob comment 'data of job' ,
    repeatable smallint not null comment '0 not repeatable job 1 repeatable job' ,
    initial_delay int comment 'the initial delay seconds of repeatable job' ,
    delay int not null comment 'the fixed delay seconds of repeatable job' ,
    primary key (id) ,
    constraint unique_name unique(name) ,
    index idx_next_time (next_time)
)
ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ;


create table task
(
    id bigint not null auto_increment ,
    job_id bigint not null ,
    start_time timestamp(3) not null default CURRENT_TIMESTAMP(3) comment 'first run time' ,
    end_time timestamp(3) null comment 'final run time' ,
    state smallint not null comment '1 running 2 terminated' ,
    instance_id varchar(100) not null comment 'target instance of running task' ,
    next_time timestamp(3) not null default CURRENT_TIMESTAMP(3) comment 'next run time' ,
    primary key (id) ,
    constraint unique_job_id_next_time unique(job_id, next_time) ,
    index idx_job_id (job_id)
)
ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ;


create table registry
(
    id bigint not null auto_increment ,
    host varchar(100) not null comment 'client host or client ip' ,
    port int not null comment 'server port',
    ticket_time timestamp(3) not null default CURRENT_TIMESTAMP(3) comment 'client ticket time',
    enabled smallint not null comment '0 disabled 1 enabled' ,
    primary key (id) ,
    index idx_host_port (host, port)
)
ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ;

