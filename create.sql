create table User(
    UserId varchar(255) primary key, 
    BidRating int, 
    SellRating int);
create table Bid(
    ItemId bigint, 
    UserId varchar(255), 
    time timestamp, 
    amount double(16,2));
create table ItemCategory(
    ItemId bigint, 
    category varchar(255)); 
create table Item(
    ItemId bigint primary key, 
    name varchar(255), 
    currentBid double(16,2), 
    buyout double(16,2), 
    minBid double(16,2), 
    numBids int, 
    startTime timestamp, 
    endTime timestamp, 
    sellId varchar(255), 
    description varchar(4000));
create table BidLocation(
    UserId varchar(255) primary key, 
    LocId int);
create table ItemLocation(
    ItemId bigint primary key, 
    LocId int);
create table Location(
    LocId int primary key, 
    lat real, 
    lon real, 
    locText varchar(255), 
    country varchar(255));