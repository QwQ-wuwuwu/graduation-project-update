use `zgj`;
create table if not exists `user`(
    id char(19) primary key,
    number char(10) not null unique,
    password varchar(64) not null,
    role int not null,
    insert_time datetime not null default CURRENT_TIMESTAMP,
    update_time datetime not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    index(number)
    );
create table if not exists `teacher` (
    id char(19) primary key,
    number char(10) not null unique,
    name varchar(20) not null,
    total int not null,
    left_select int not null,
    group_id int not null , /*老师所在的评审小组*/
    insert_time datetime not null default CURRENT_TIMESTAMP,
    update_time datetime not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    foreign key(number) references `user`(number),
    index(number,group_id)
    );
create table if not exists `student`(
    id char(19) primary key,
    name varchar(20) not null,
    teacher_id char(19) null,
    number char(10) not null unique,
    teacher_name varchar(50) null,
    queue_number tinyint null,
    group_id int null , /*加入的评审小组*/
    project_title varchar(50) null , /*毕设题目*/
    insert_time datetime not null default CURRENT_TIMESTAMP,
    select_time datetime null,
    foreign key(teacher_id) references `teacher`(id),
    foreign key(number) references `user`(number),
    index(teacher_id,number)
    );
create table if not exists `process` ( /*开题答辩/期中检查/毕业答辩/演示四个过程分值比例*/
    id char(19) primary key ,
    process_name varchar(50) not null,
    items json null comment '[{"name","number","score","detail"}]',/*打分项目，编号，分值比例，细节描述*/
    point tinyint null,
    auth char(5) not null, /*指导老师评分还是审核老师评分*/
    student_attach json null comment '[{"number", "name", "ext", "description"}]', /*学生附件*/
    insert_time datetime not null default current_timestamp,
    update_time datetime not null default current_timestamp on update current_timestamp
    );
create table if not exists `process_score` ( /*过程得分情况*/
    id char(19) primary key,
    student_id char(19) not null,
    process_id char(19) not null ,
    teacher_id char(19) not null,
    detail json null comment '{"teacherName","score",detail:[{"number","point"}]}', /*打分老师，得分细节*/
    insert_time datetime not null default current_timestamp,
    update_time datetime not null default current_timestamp on update current_timestamp,
    index(student_id,process_id),
    unique(student_id,process_id,teacher_id)
    );
create table if not exists `file` (
    id char(19) primary key ,
    student_number char(10) null,
    detail varchar(100) null, /*开题或毕设报告*/
    process_id char(19) null ,/*表明属于哪个过程*/
    number tinyint null,
    insert_time datetime not null default current_timestamp,
    update_time datetime not null default current_timestamp on update current_timestamp,
    index(student_number),
    index(process_id)
    );