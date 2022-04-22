CREATE TABLE USER(
    "ID" int auto_increment primary key ,
    "ACCOUNT_ID" VARCHAR(100),
    "NAME" VARCHAR(32),
    "TOKEN" CHAR(36),
    "GMT_CREATE" BIGINT,
    "GMT_MODIFIED" BIGINT
)
