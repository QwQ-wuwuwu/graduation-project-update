explain (
            select count(u.role) from user u where u.role = 2
        );
use `zgj`;
select * from user u where u.number='admin' and u.role=2;
delete from process p order by p.update_time desc limit 1;
explain (
select * from teacher t where t.left_select > 0
);
explain (
            select distinct count(t.group_id) from teacher t
        );
explain (
    select count(s.id) from student s
        );
select * from file f where f.student_number=2021213176 and f.process_id=1197424698199199744;
select * from student s where s.teacher_id is null;
select group_id from teacher
