/*Query 1*/
select count(*) from User;

/*Query 2*/
select count(*) from ItemLocation il, Location l 
where il.LocId = l.LocId and locText = 'New York';

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

/*Query 5*/
select count(*)
from Item i, User u
where i.sellId = u.UserId and u.sellRating > 1000;
/*Query 6*/