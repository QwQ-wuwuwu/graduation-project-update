explain (
            select count(u.role) from user u where u.role = 2
        );
use `zgj`;
select * from user u where u.number='admin' and u.role=2;
update user set description = number;
delete from process p order by p.update_time desc limit 1