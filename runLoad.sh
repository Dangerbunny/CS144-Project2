#!/bin/bash

# Run the drop.sql batch file to drop existing tables
# Inside the drop.sql, you sould check whether the table exists. Drop them ONLY if they exists.
mysql CS144 < drop.sql

# Run the create.sql batch file to create the database and tables
mysql CS144 < create.sql

# Compile and run the parser to generate the appropriate load files
ant
ant run-all
# ...

# If the Java code does not handle duplicate removal, do this now
# echo "Checking for duplicates, user..."
# uniq -d user.csv
# echo "Checking for duplicates, bid..."
# uniq -d bid.csv
# echo "Checking for duplicates, itemcategory..."
# uniq -d itemcategory.csv
# echo "Checking for duplicates, item..."
# uniq -d item.csv
# echo "Checking for duplicates, bidlocation..."
# uniq -d bidlocation.csv
# echo "Checking for duplicates, itemlocation..."
# uniq -d itemlocation.csv
# echo "Checking for duplicates, location..."
# uniq -d location.csv
echo "Eliminating duplicates, just to be safe..."
# sort user.csv | uniq > tempUser.csv
# sort bid.csv | uniq > tempBid.csv
sort itemcategory.csv | uniq > tempICat.csv
# sort item.csv | uniq > tempItem.csv
# sort bidlocation.csv | uniq > tempBidLoc.csv
# sort itemlocation.csv | uniq > tempILoc.csv
# sort location.csv | uniq > tempLoc.csv

# cat tempUser.csv > user.csv
# cat tempBid.csv > bid.csv
cat tempICat.csv > itemcategory.csv
# cat tempItem.csv > item.csv
# cat tempBidLoc.csv > bidlocation.csv
# cat tempILoc.csv > itemlocation.csv
# cat tempLoc.csv > location.csv

# echo "Checking for duplicates, user..."
# uniq -d user.csv
# echo "Checking for duplicates, bid..."
# uniq -d bid.csv
# echo "Checking for duplicates, itemcategory..."
# uniq -d itemcategory.csv
# echo "Checking for duplicates, item..."
# uniq -d item.csv
# echo "Checking for duplicates, bidlocation..."
# uniq -d bidlocation.csv
# echo "Checking for duplicates, itemlocation..."
# uniq -d itemlocation.csv
# echo "Checking for duplicates, location..."
# uniq -d location.csv

# Run the load.sql batch file to load the data
mysql CS144 < load.sql

# Remove all temporary files
rm *.csv