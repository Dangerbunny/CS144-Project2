/* CS144
 *
 * Parser skeleton for processing item-???.xml files. Must be compiled in
 * JDK 1.5 or above.
 *
 * Instructions:
 *
 * This program processes all files passed on the command line (to parse
 * an entire diectory, type "java MyParser myFiles/*.xml" at the shell).
 *
 * At the point noted below, an individual XML file has been parsed into a
 * DOM Document node. You should fill in code to process the node. Java's
 * interface for the Document Object Model (DOM) is in package
 * org.w3c.dom. The documentation is available online at
 *
 * http://java.sun.com/j2se/1.5.0/docs/api/index.html
 *
 * A tutorial of Java's XML Parsing can be found at:
 *
 * http://java.sun.com/webservices/jaxp/
 *
 * Some auxiliary methods have been written for you. You may find them
 * useful.
 */

package edu.ucla.cs.cs144;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;



class User{
	
	String uid;
	int bRating;
	int sRating;
	public User(String uid){
		this.uid = uid;
	}

	public void setbRating(int bRating) {
		this.bRating = bRating;
	}
	public void setsRating(int sRating) {
		this.sRating = sRating;
	}
	public String toString(){
		 return uid+"\t" + bRating+"\t" + sRating;
	}
}

class Bid{
	long iid;
	String uid;
	String dateString;
	double amount;
	
	public Bid(long iid, String uid, String dateString, double amount) {
		super();
		this.iid = iid;
		this.uid = uid;
		this.dateString = dateString;
		this.amount = amount;
	}
	public String toString(){
		 return iid+"\t" + uid+"\t" + dateString+"\t" + amount;
	}
}

class ItemCategories{
	
	long iid;
	ArrayList<String> categories;
	
	public ItemCategories(long iid) {
		super();
		this.iid = iid;
		categories = new ArrayList<String>();
	}

	public void addCat(String category){
		categories.add(category);
	}
	
	public String toString(){
		String data = "";
		for(int i = 0; i < categories.size()-1; i++)
			data += iid+"\t" + categories.get(i)+"\n";
		data+=iid+"\t" + categories.get(categories.size()-1);
		return data;
	}
	
}

class Item{
	
	long itemId;
	String name;
	double currBid;
	double buyout;
	double minBid;
	int numBids;
	String startTime;
	String endTime;
	String sid;
	String desc;
	
	public Item(long itemId){
		this.itemId = itemId;
	}
	public void setname(String name) {
		this.name = name;
	}
	public void setcurrBid(double currBid) {
		this.currBid = currBid;
	}
	public void setbuyout(double d) {
		this.buyout = d;
	}
	public void setminBid(double d) {
		this.minBid = d;
	}
	public void setnumBids(int numBids) {
		this.numBids = numBids;
	}
	public void setstartTime(String startTime) {
		this.startTime = startTime;
	}
	public void setendTime(String endTime) {
		this.endTime = endTime;
	}
	public void setsid(String sid) {
		this.sid = sid;
	}
	public void setdesc(String desc) {
		this.desc = desc;
	}
	public String toString(){
		 return itemId +"\t"+ name +"\t"+ currBid +"\t" 
				 + buyout +"\t"+ minBid +"\t"+ numBids +"\t" 
				 + startTime +"\t"+ endTime +"\t"+ sid +"\t" 
				 + desc;
	}
}

class BidLocation{
	String uid;
	int locId;
	
	public BidLocation(String uid){
		this.uid = uid;
	}
	public void setlocId(int locId) {
		this.locId = locId;
	}
	public String toString(){
		 return uid +"\t"+ locId;
	}
}

class ItemLocation{
	long itemId;
	int locId;
	
	public ItemLocation(long itemId){
		this.itemId = itemId;
	}
	public void setlocId(int locId) {
		this.locId = locId;
	}
	public String toString(){
		 return itemId +"\t"+ locId;
	}
}

class Location{
	
	int locId;
	float lat;
	float lon;
	String text;
	String country;
	
	public Location(int locId){
		this.locId = locId;
	}
	public void setlat(float lat) {
		this.lat = lat;
	}
	public void setlon(float lon) {
		this.lon = lon;
	}
	public void settext(String text) {
		this.text = text;
	}
	public void setcountry(String country) {
		this.country = country;
	}
	public String toString(){
		String latStr;
		String lonStr;
		if(lat == -1000)
			latStr = "\\N";
		else
			latStr = String.valueOf(lat);
		if(lon == -1000)
			lonStr = "\\N";
		else
			lonStr = String.valueOf(lon);
		return locId +"\t"+ latStr +"\t"+ lonStr +"\t"+ text +"\t"+ country;
	}
}

class TempLocation{
	
	String text;
	int locId;
	float lat;
	float lon;
	
	public TempLocation(String text, int locId){
		this.text = text;
		this.locId = locId;
	}
	public void setlat(float lat) {
		this.lat = lat;
	}
	public float getlat(){
		return this.lat;
	}
	public void setlon(float lon) {
		this.lon = lon;
	}
	public float getlon(){
		return this.lon;
	}
	public int getlocId(){
		return this.locId;
	}
}

class MyParser {
    
    static final String columnSeparator = "|*|";
    static DocumentBuilder builder;
    
    static int locIdCounter = 0;

    static HashMap<String, User> userMap = new HashMap<String, User>();
    
    static SimpleDateFormat inFormatter =
            new SimpleDateFormat("MMM-dd-yy HH:mm:ss");
    static SimpleDateFormat outFormatter = 
    		new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    
    static final String[] typeName = {
	"none",
	"Element",
	"Attr",
	"Text",
	"CDATA",
	"EntityRef",
	"Entity",
	"ProcInstr",
	"Comment",
	"Document",
	"DocType",
	"DocFragment",
	"Notation",
    };
	private static final int maxDescLength = 4000;
    
    static class MyErrorHandler implements ErrorHandler {
        
        public void warning(SAXParseException exception)
        throws SAXException {
            fatalError(exception);
        }
        
        public void error(SAXParseException exception)
        throws SAXException {
            fatalError(exception);
        }
        
        public void fatalError(SAXParseException exception)
        throws SAXException {
            exception.printStackTrace();
            System.out.println("There should be no errors " +
                               "in the supplied XML files.");
            System.exit(3);
        }
        
    }
    
    /* Non-recursive (NR) version of Node.getElementsByTagName(...)
     */
    static Element[] getElementsByTagNameNR(Element e, String tagName) {
        Vector< Element > elements = new Vector< Element >();
        Node child = e.getFirstChild();
        while (child != null) {
            if (child instanceof Element && child.getNodeName().equals(tagName))
            {
                elements.add( (Element)child );
            }
            child = child.getNextSibling();
        }
        Element[] result = new Element[elements.size()];
        elements.copyInto(result);
        return result;
    }
    
    /* Returns the first subelement of e matching the given tagName, or
     * null if one does not exist. NR means Non-Recursive.
     */
    static Element getElementByTagNameNR(Element e, String tagName) {
        Node child = e.getFirstChild();
        while (child != null) {
            if (child instanceof Element && child.getNodeName().equals(tagName))
                return (Element) child;
            child = child.getNextSibling();
        }
        return null;
    }
    
    /* Returns the text associated with the given element (which must have
     * type #PCDATA) as child, or "" if it contains no text.
     */
    static String getElementText(Element e) {
        if (e.getChildNodes().getLength() == 1) {
            Text elementText = (Text) e.getFirstChild();
            return elementText.getNodeValue();
        }
        else
            return "";
    }
    
    /* Returns the text (#PCDATA) associated with the first subelement X
     * of e with the given tagName. If no such X exists or X contains no
     * text, "" is returned. NR means Non-Recursive.
     */
    static String getElementTextByTagNameNR(Element e, String tagName) {
        Element elem = getElementByTagNameNR(e, tagName);
        if (elem != null)
            return getElementText(elem);
        else
            return "";
    }
    
    /* Returns the amount (in XXXXX.xx format) denoted by a money-string
     * like $3,453.23. Returns the input if the input is an empty string.
     */
    static String strip(String money) {
        if (money.equals(""))
            return money;
        else {
            double am = 0.0;
            NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.US);
            try { am = nf.parse(money).doubleValue(); }
            catch (ParseException e) {
                System.out.println("This method should work for all " +
                                   "money values you find in our data.");
                System.exit(20);
            }
            nf.setGroupingUsed(false);
            return nf.format(am).substring(1);
        }
    }
    
    /* Process one items-???.xml file.
     */
    static void processFile(File xmlFile) {
        Document doc = null;
        try {
            doc = builder.parse(xmlFile);
        }
        catch (IOException e) {
            e.printStackTrace();
            System.exit(3);
        }
        catch (SAXException e) {
            System.out.println("Parsing error on file " + xmlFile);
            System.out.println("  (not supposed to happen with supplied XML files)");
            e.printStackTrace();
            System.exit(3);
        }
        
        /* At this point 'doc' contains a DOM representation of an 'Items' XML
         * file. Use doc.getDocumentElement() to get the root Element. */
        System.out.println("Successfully parsed - " + xmlFile);
        
        Element root = doc.getDocumentElement();
        constructTables(root);
        constructItemTable(root);
        constructLocationTables(root);
        //constructBidTable(root);
        /* Fill in code here (you will probably need to write auxiliary
            methods). */
        
        
        
        /**************************************************************/
        
    }
    
    public static void flushMapToDataFile(String fileName, HashMap<Object, Object> map){
    	try {
    	    FileWriter fos = new FileWriter(fileName, true);
    	    PrintWriter dos = new PrintWriter(fos);
    	    for (Object o : map.values()){
    	    	
    		    dos.print(o);
    		    dos.println();
    	    }
    	    dos.close();
    	    fos.close();
    	    } catch (IOException e) {
    	    	System.out.println("Error Printing Tab Delimited File");
    	    }
    }
    
    public static void flushListToDataFile(String fileName, ArrayList<Object> list){
    	try {
    	    FileWriter fos = new FileWriter(fileName, true);
    	    PrintWriter dos = new PrintWriter(fos);
    	    for (Object o : list){
    	    	
    		    dos.print(o);
    		    dos.println();
    	    }
    	    dos.close();
    	    fos.close();
    	    } catch (IOException e) {
    	    	System.out.println("Error Printing Tab Delimited File");
    	    }
    }
    
    /**
     * This method is messy, but it is necessary to perform all parsing at once for efficiency reasons
     * Splitting each table construction into it's own function is more readable, but would involve 
     * SIGNIFICANT extra iteration over data.
     * @param root
     */
    public static void constructTables(Element root){
    	/*
    	 * Data structures to be populated and then flushed to data files for SQL loading
    	 */
    	ArrayList<Bid> bidList = new ArrayList<Bid>();
    	ArrayList<ItemCategories> catList = new ArrayList<ItemCategories>();
    	
    	Element[] items = getElementsByTagNameNR(root, "Item");
    	for(Element item : items){
    		Element seller = getElementByTagNameNR(item, "Seller");
    		Element allBids = getElementByTagNameNR(item, "Bids");
    		Element[] bids = getElementsByTagNameNR(allBids, "Bid");
    		Element[] categories = getElementsByTagNameNR(item, "Category");
    		
    		int sRating = Integer.parseInt(seller.getAttribute("Rating"));
    		String sellId = seller.getAttribute("UserID");
    		long itemId = Long.parseLong(item.getAttribute("ItemID"));
    		
    		//Add/Modify a User object
    		if(userMap.get(sellId) != null){
    			userMap.get(sellId).setsRating(sRating);
    		} else {
    			User u = new User(sellId);
    			u.setsRating(sRating);
    			userMap.put(sellId, u);
    		}
    		
    		for(Element bid : bids){
    			Element bidder = getElementByTagNameNR(bid, "Bidder");
    			
    			int bRating = Integer.parseInt(bidder.getAttribute("Rating"));
        		String buyId = bidder.getAttribute("UserID");
        		double amount = Double.parseDouble(strip(getElementText(getElementByTagNameNR(bid, "Amount"))));
        		
        		Date bidDate = null;
        		String dateString = null;
				try {
					bidDate = inFormatter.parse(getElementText(getElementByTagNameNR(bid, "Time")));
					dateString = outFormatter.format(bidDate);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        		
        		
        		
        		//Add a new bid
        		bidList.add(new Bid(itemId, buyId, dateString, amount));
        		
        		//Add/Modify a User object
        		if(userMap.get(buyId) != null){
        			userMap.get(buyId).setbRating(bRating);
        		} else {
        			User u = new User(buyId);
        			u.setbRating(bRating);
        			userMap.put(buyId, u);
        		}
    		}
    		
    		ItemCategories iCats = new ItemCategories(itemId);
    		
    		for(Element category : categories){
    			iCats.addCat(getElementText(category));
    		}
    		
    		//Add a new ItemCategories object
    		catList.add(iCats);
    	}
    	
    	
    	ArrayList<Object> bList = new ArrayList<Object>(bidList);
    	ArrayList<Object> icList = new ArrayList<Object>(catList);
    	
    	flushListToDataFile("bid.csv", bList);
    	flushListToDataFile("itemcategory.csv", icList);
    }
    
    public static void constructItemTable(Element root){
    	HashMap<Long, Item> itemMap = new HashMap<Long, Item>();
    	Element[] items = getElementsByTagNameNR(root, "Item");
    	for( Element item : items){
    		String itemIdStr = item.getAttribute("ItemID");
    		long itemId = Integer.parseInt(itemIdStr);
    		if(itemMap.get(itemId) != null){				//to be safe...but do we need this?  I think I remember a Piazza post saying every Item listing is unique
    			itemMap.get(itemId).setname(getElementText(getElementByTagNameNR(item, "Name")));
    			itemMap.get(itemId).setcurrBid(Double.parseDouble(strip(getElementText(getElementByTagNameNR(item, "Currently")))));
    			if(getElementByTagNameNR(item, "Buy_Price") != null)
    				itemMap.get(itemId).setbuyout(Double.parseDouble(strip((getElementText(getElementByTagNameNR(item, "Buy_Price"))))));
    			itemMap.get(itemId).setminBid(Double.parseDouble(strip(getElementText(getElementByTagNameNR(item, "First_Bid")))));
    			itemMap.get(itemId).setnumBids(Integer.parseInt(getElementText(getElementByTagNameNR(item, "Number_of_Bids"))));
    			
    			Date startD = null, endD = null;
    			String sdString = null, edString = null;
				try {
					startD = inFormatter.parse(getElementText((getElementByTagNameNR(item, "Started"))));
					endD = inFormatter.parse(getElementText(getElementByTagNameNR(item, "Ends")));
					sdString = outFormatter.format(startD);
					edString = outFormatter.format(endD);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    			
    			
    			itemMap.get(itemId).setstartTime(sdString);
    			itemMap.get(itemId).setendTime(edString);
    			
    			itemMap.get(itemId).setsid(getElementByTagNameNR(item, "Seller").getAttribute("UserID"));
    			String desc = truncate(getElementText(getElementByTagNameNR(item, "Description")), maxDescLength);
    			desc = desc.replace("\"", "\\\"");
    			itemMap.get(itemId).setdesc(desc);
    		}
    		else {
    			Item i = new Item(itemId);
    			i.setname(getElementText(getElementByTagNameNR(item, "Name")));
    			i.setcurrBid(Double.parseDouble(strip(getElementText(getElementByTagNameNR(item, "Currently")))));
    			if(getElementByTagNameNR(item, "Buy_Price") != null)
    				i.setbuyout(Double.parseDouble(strip((getElementText(getElementByTagNameNR(item, "Buy_Price"))))));
    			i.setminBid(Double.parseDouble(strip(getElementText(getElementByTagNameNR(item, "First_Bid")))));
    			i.setnumBids(Integer.parseInt(getElementText(getElementByTagNameNR(item, "Number_of_Bids"))));

    			Date startD = null, endD = null;
    			String sdString = null, edString = null;
				try {
					startD = inFormatter.parse(getElementText((getElementByTagNameNR(item, "Started"))));
					endD = inFormatter.parse(getElementText(getElementByTagNameNR(item, "Ends")));
					sdString = outFormatter.format(startD);
					edString = outFormatter.format(endD);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    			
    			
    			i.setstartTime(sdString);
    			i.setendTime(edString);
    			
    			i.setsid(getElementByTagNameNR(item, "Seller").getAttribute("UserID"));
    			String desc = truncate(getElementText(getElementByTagNameNR(item, "Description")), maxDescLength);
    			desc = desc.replace("\"", "\\\"");
    			i.setdesc(desc);
    			itemMap.put(itemId, i);
    		}
    	}
    	HashMap<Object, Object> map = new HashMap<Object, Object>(itemMap);
    	flushMapToDataFile("item.csv", map);
    }
    
    public static void constructLocationTables(Element root){
    	HashMap<String, BidLocation> bidLocMap = new HashMap<String, BidLocation>();
    	HashMap<Long, ItemLocation> itemLocMap = new HashMap<Long, ItemLocation>();
    	HashMap<Integer, Location> locMap = new HashMap<Integer, Location>();
    	HashMap<String, TempLocation> tempLocMap = new HashMap<String, TempLocation>();
    	
    	int locId  = 0;
    	Element[] items = getElementsByTagNameNR(root, "Item");
    	for(Element item : items){
    		//Item location data
    		Element location = getElementByTagNameNR(item, "Location");
    		String locText = location.getTextContent();
    		//Get Latitude and Longitude
    		String latStr = location.getAttribute("Latitude");
    		float lat = -1000, lon= -1000;
    		if(latStr.length() != 0)
    			lat = Float.parseFloat(latStr);
    		String lonStr = location.getAttribute("Longitude");
    		if(lonStr.length() != 0)
    			lon = Float.parseFloat(lonStr);
    		//Get Country
    		Element country = getElementByTagNameNR(item, "Country");
    		String countryStr = country.getTextContent();
    		//Get ItemID
    		String itemIdStr = item.getAttribute("ItemID");
    		long itemId = Long.parseLong(itemIdStr);
    		
    		//Temp stuff to keep track of locIds
    		String tempLocText = locText+latStr+lonStr;
    		if(tempLocMap.get(tempLocText) == null){
    			locId = getNextLocId();
    			TempLocation tl = new TempLocation(tempLocText,locId);
    			tempLocMap.put(tempLocText, tl);
    		}else{
    			TempLocation tl2 = tempLocMap.get(tempLocText);
    			locId = tl2.getlocId();
    		}
			
			//Item Location Add
			if(itemLocMap.get(itemId) == null){
				ItemLocation il = new ItemLocation(itemId);
				il.setlocId(locId);
				itemLocMap.put(itemId,il);
			}
			//Location Add
			if(locMap.get(locId) == null){
				Location loc = new Location(locId);
				loc.setlat(lat);
				loc.setlon(lon);
				loc.settext(locText);
				loc.setcountry(countryStr);
				locMap.put(locId,loc);
			}
    		
    		//Bidder location data
    		Element bidList = getElementByTagNameNR(item, "Bids");
    		Element[] bids = getElementsByTagNameNR(bidList, "Bid");
    		for(Element bid : bids){
    			Element bidder = getElementByTagNameNR(bid, "Bidder");
    			Element bidderLocation = getElementByTagNameNR(bidder,"Location");
    			Element bidderCountry = getElementByTagNameNR(bidder,"Country");
    			if(bidderLocation != null){
	        		String uid = bidder.getAttribute("UserID");
	        		
	        		String bidLocText = bidderLocation.getTextContent();
	        		String bidCountryStr = null;
	        		if(bidderCountry != null)
	        			bidCountryStr = bidderCountry.getTextContent();
	        		String bidLatStr = bidderLocation.getAttribute("Latitude");
	        		String bidLonStr = bidderLocation.getAttribute("Longitude");
	        		
	        		float bidLon = -1000, bidLat = -1000;
	        		
	        		if(bidLatStr.length() != 0)
	        			bidLat = Float.parseFloat(latStr);
	        		if(bidLonStr.length() != 0)
	        			bidLon = Float.parseFloat(lonStr);
	        		
	        		String tempBidLocText = bidLocText+bidLatStr+bidLonStr;
	        		if(tempLocMap.get(tempBidLocText) == null){
	        			locId = getNextLocId();
	        			TempLocation btl = new TempLocation(tempBidLocText,locId);
	        			tempLocMap.put(tempBidLocText, btl);
	        		}else{
	        			TempLocation btl2 = tempLocMap.get(tempBidLocText);
	        			locId = btl2.getlocId();
	        		}
	        		//BidLocation Add
	        		if(bidLocMap.get(uid) == null){
	        			BidLocation bl = new BidLocation(uid);
	        			bl.setlocId(locId);
	        			bidLocMap.put(uid, bl);
	        		}
	        		//Location Add
	        		if(locMap.get(locId) == null){
	    				Location bidLoc = new Location(locId);
	    				bidLoc.setlat(bidLat);
	    				bidLoc.setlon(bidLon);
	    				bidLoc.settext(bidLocText);
	    				bidLoc.setcountry(bidCountryStr);
	    				locMap.put(locId,bidLoc);
	        		}
    			}
    		}
    	}
    	HashMap<Object, Object> map1 = new HashMap<Object, Object>(bidLocMap);
    	flushMapToDataFile("bidlocation.csv", map1);
    	HashMap<Object, Object> map2 = new HashMap<Object, Object>(itemLocMap);
    	flushMapToDataFile("itemlocation.csv", map2);
    	HashMap<Object, Object> map3 = new HashMap<Object, Object>(locMap);
    	flushMapToDataFile("location.csv", map3);
    	
    }
    
    public static int getNextLocId(){
    	locIdCounter = locIdCounter + 1;
    	return locIdCounter;
    }
    
    public static String truncate(String value, int length)
    {
      if (value != null && value.length() > length)
        value = value.substring(0, length);
      return value;
    }
    
    
    public static void main (String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java MyParser [file] [file] ...");
            System.exit(1);
        }
    	
//    	String testFile = "ebay-data/items-0.xml";
        
        /* Initialize parser. */
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(false);
            factory.setIgnoringElementContentWhitespace(true);      
            builder = factory.newDocumentBuilder();
            builder.setErrorHandler(new MyErrorHandler());
        }
        catch (FactoryConfigurationError e) {
            System.out.println("unable to get a document builder factory");
            System.exit(2);
        } 
        catch (ParserConfigurationException e) {
            System.out.println("parser was unable to be configured");
            System.exit(2);
        }
        
        for (int i = 0; i < args.length; i++) {
            File currentFile = new File(args[i]);
            processFile(currentFile);
        }

        HashMap<Object, Object> map = new HashMap<Object, Object>(userMap);
        flushMapToDataFile("user.csv", map);
    }
}
