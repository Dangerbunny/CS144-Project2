TEAM_NAME: RC_Wonders

Part B Answers

1.  User(UserId, BidRating, SellRating) key = UserId
    Bid(ItemId, UserId, time, amount)
    ItemCategory(ItemId, category)
    Item(ItemId, name, currentBid, buyout, minBid, numBids, startTime, endTime, sellId, description) key = ItemId
    BidLocation(UserId, LocId) key = UserId
    ItemLocation(ItemId, LocId) key = ItemId
    Location(LocId, lat, long, locText, country) key = LocId

2.  Relation - Location, FD: (lat,long) -> country

3.  All are in BCNF except the Location relation. This we decided to leave non normalized for the following reason:
    The gain in space complexity from splitting the table so that country data is not repeated in Location is offset
    by the time complexity cost of performing the join that would be necessary to link together Location and country.

4.  Same as 3. Although ItemCategory contains the MVD itemID ->> category, its is also a trivial MVD and thus does not
    violate 4NF