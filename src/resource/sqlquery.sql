select * from cuser

create table test (content varchar(50), datetime varchar(50), mark varchar(10))

insert into test(content,datetime) values(?,?)

drop table test

select * from test order by content desc 

update test set mark='N' where content = 'AM7001WM18M050002'