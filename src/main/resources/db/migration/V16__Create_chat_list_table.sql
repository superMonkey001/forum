create table chat_list
(
    listId     int primary key auto_increment,
    linkId     int,
    userId     bigint,
    content    varchar(128) not null,
    createtime datetime
);
