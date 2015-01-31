/*Query 1*/
select count(*) from User;

/*Query 2*/
select count(*) 
from(
	select count(*)
	from ItemLocation il, Location l 
	where il.LocId = l.LocId and locText collate latin1_general_cs = 'New York'
	group by il.ItemId
) foo;

/*Query 3*/
select count(*)
from (
	select count(*)
	from ItemCategory ic, Item i
	where i.ItemId = ic.ItemId
	group by ic.ItemId
	having count(ic.ItemId) = 4
) foo;

/*Query 4*/
select ItemId
from Item i
where i.numBids > 0 and 'Dec-20-01 00:00:01' BETWEEN i.endTime and i.startTime and i.currentBid >= ALL(
	select currentBid 
	from Item
	where numBids > 0 and 'Dec-20-01 00:00:01' BETWEEN i.endTime and i.startTime
);


/*Query 5*/
/*select count(*)
from(
	select count(*)
	from Item i, User u
	where i.sellId = u.UserId and u.sellRating > 1000
	group by u.UserId
) foo;*/
select count(*)
from User
where SellRating > 1000;

/*Query 6*/
select count(*)
from (
	select count(*)
	from Bid b, Item i
	where b.UserId = i.sellId
	group by UserId
) foo;

/*Query 7*/

/*select count(*)*/
select count(*)
from (
	select count(*)
	from ItemCategory ic, Bid b
	where ic.ItemId = b.ItemId
	and b.amount>100
	group by ic.category
) foo;