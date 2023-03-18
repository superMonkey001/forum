create table user_link
(
    `linkId` int primary key auto_increment,
    `fromId`   bigint not null,
    `toId`     bigint not null
);
