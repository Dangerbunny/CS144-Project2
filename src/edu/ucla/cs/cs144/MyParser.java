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
import java.util.ArrayList;
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
class MyParser {
    
    static final String columnSeparator = "|*|";
    static DocumentBuilder builder;
    
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
        //constructBidTable(root);
        /* Fill in code here (you will probably need to write auxiliary
            methods). */
        
        
        
        /**************************************************************/
        
    }
    
    public static void flushMapToDataFile(String fileName, HashMap<Object, Object> map){
    	try {
    	    FileWriter fos = new FileWriter(fileName);
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
    	    FileWriter fos = new FileWriter(fileName);
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
    	HashMap<String, User> userMap = new HashMap<String, User>();
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
        		String dateString = getElementText(getElementByTagNameNR(bid, "Time"));
        		
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
    	
    	HashMap<Object, Object> map = new HashMap<Object, Object>(userMap);
    	ArrayList<Object> bList = new ArrayList<Object>(bidList);
    	ArrayList<Object> icList = new ArrayList<Object>(catList);
    	flushMapToDataFile("user.csv", map);
    	flushListToDataFile("bid.csv", bList);
    	flushListToDataFile("itemcategory.csv", icList);
    }
    
//    public static void constructBidTable(Element root){
//    	ArrayList<Bid> bidList = new ArrayList<Bid>();
//    	Element[] items = getElementsByTagNameNR(root, "Item");
//    	for(Element item : items){
//    		long itemId = Long.parseLong(item.getAttribute("ItemID"));
//    		Element allBids = getElementByTagNameNR(item, "Bids");
//    		Element[] bids = getElementsByTagNameNR(allBids, "Bid");
//    		for(Element bid : bids){
//    			String uid = getElementByTagNameNR(bid, "Bidder").getAttribute("UserID");;
//    			double amount = Double.parseDouble(getElementText(getElementByTagNameNR(bid, "Amount")).substring(1));
//        		String dateString = getElementText(getElementByTagNameNR(bid, "Time"));
//        		bidList.add(new Bid(itemId, uid, dateString, amount));
//    		}
//    	}
//    	
//    	ArrayList<Object> list = new ArrayList<Object>(bidList);
//    	flushListToDataFile("ebay-data/bid.csv", list);
//    	
//    }
    
    public static void main (String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java MyParser [file] [file] ...");
            System.exit(1);
        }
    	
    	//String testFile = "ebay-data/items-0.xml";
        
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
        
        /* Process all files listed on command line. */
        for (int i = 0; i < args.length; i++) {
            File currentFile = new File(args[i]);
            processFile(currentFile);
        }
    }
}
