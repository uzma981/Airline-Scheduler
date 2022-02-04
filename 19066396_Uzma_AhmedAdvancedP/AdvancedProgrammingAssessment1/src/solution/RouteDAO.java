package solution;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import baseclasses.CabinCrew;
import baseclasses.DataLoadingException;
import baseclasses.IRouteDAO;
import baseclasses.Pilot;
import baseclasses.Route;

/**
 * The RouteDAO parses XML files of route information, each route specifying
 * where the airline flies from, to, and on which day of the week
 */
public class RouteDAO implements IRouteDAO {
	List<Route> routeList=new ArrayList<>();// created a new Array List for routes



	/**
	 * Finds all flights that depart on the specified day of the week
	 * @param dayOfWeek A three letter day of the week, e.g. "Tue"
	 * @return A list of all routes that depart on this day
	 */

	@Override
	public List<Route> findRoutesByDayOfWeek(String dayOfWeek) {
		List<Route> newRouteList=new ArrayList<>();



		for(int i=0;i<routeList.size();i++) {
			if(routeList.get(i).getDayOfWeek().equals(dayOfWeek)) {
				newRouteList.add(routeList.get(i));
			}
		}
		return newRouteList;
	}

	/**
	 * Finds all of the flights that depart from a specific airport on a specific day of the week
	 * @param airportCode the three letter code of the airport to search for, e.g. "MAN"
	 * @param dayOfWeek the three letter day of the week code to search for, e.g. "Tue"
	 * @return A list of all routes from that airport on that day
	 */
	@Override
	public List<Route> findRoutesByDepartureAirportAndDay(String airportCode, String dayOfWeek) {
		List<Route> newRouteList=new ArrayList<>();

		for(int i=0;i<routeList.size();i++) {
			if(routeList.get(i).getDayOfWeek().equals(dayOfWeek) && routeList.get(i).getDepartureAirportCode().equals(airportCode)) {
				newRouteList.add(routeList.get(i));
			}
		}
		return newRouteList;
	}

	/**
	 * Finds all of the flights that depart from a specific airport
	 * @param airportCode the three letter code of the airport to search for, e.g. "MAN"
	 * @return A list of all of the routes departing the specified airport
	 */
	@Override
	public List<Route> findRoutesDepartingAirport(String airportCode) {
		List<Route> newRouteList=new ArrayList<>();
		for(Route r:routeList) {//for every variable r in routeList array. 
			if(r.getDepartureAirportCode().equals(airportCode)){//Compare the departure airport code using equals with the input 'airport code'
				newRouteList.add(r);
			}
		}

		return newRouteList;
	}

	/**
	 * Finds all of the flights that depart on the specified date
	 * @param date the date to search for
	 * @return A list of all routes that dpeart on this date
	 */
	@Override
	public List<Route> findRoutesbyDate(LocalDate date) {
		List<Route> newRouteList= new ArrayList<>();



		for(int i=0;i<routeList.size();i++) {
			String x=date.getDayOfWeek().toString().toUpperCase();//get the day of the week from date, put that into a string convert it to lowercase

			String d=routeList.get(i).getDayOfWeek().toUpperCase();//get the day of the week from xml file convert both to lowercase


			if(x.contains(d)) {// if the input starts with the day of the week input from xml. add into the array list.
				newRouteList.add(routeList.get(i));
			}

		}
		return newRouteList;
	}

	/**
	 * 
	 * Returns The full list of all currently loaded routes
	 * @return The full list of all currently loaded routes
	 */
	@Override
	public List<Route> getAllRoutes() {
		List<Route> loadRoute = new ArrayList<>();
		for (Route c : routeList) {
			loadRoute.add(c);
		}
		return loadRoute;
	}

	/**
	 * Returns The number of routes currently loaded
	 * @return The number of routes currently loaded
	 */
	@Override
	public int getNumberOfRoutes() {
		return routeList.size();
	}

	/**
	 * Loads the route data from the specified file, adding them to the currently loaded routes
	 * Multiple calls to this function, perhaps on different files, would thus be cumulative
	 * @param p A Path pointing to the file from which data could be loaded
	 * @throws DataLoadingException if anything goes wrong. The exception's "cause" indicates the underlying exception
	 */
	@Override
	public void loadRouteData(Path p) throws DataLoadingException {


	



		try {
			InputStream routesXmlfile= Files.newInputStream(p);//this loads the XML file for routes
			//get the document builder
			DocumentBuilder docBuild = DocumentBuilderFactory.newInstance().newDocumentBuilder();

			Document doc = docBuild.parse(routesXmlfile);//build document

			Element root = doc.getDocumentElement();//extract the root element


			NodeList routes=root.getElementsByTagName("Route");

			//run through the nodeList of roots in the XML file
			for(int i=0;i<routes.getLength();i++) {
				Route newR= new Route();//create a new route class

				Node list = routes.item(i);

				if(list.getNodeType()==Node.ELEMENT_NODE) {
					Element elements=(Element)list;

					//get the node flightNo by getting the tag name flight number from the XML file. 
					Node flightNo= elements.getElementsByTagName("FlightNumber").item(0);
					Integer flightNo2= Integer.parseInt(flightNo.getTextContent());//convert into Integer format 
					newR.setFlightNumber(flightNo2);//set the flight number to the newR route class.

					//get the day of the week by tag name and get the text content within and convert it to a string. and set the day of week to the new route class
					Node dayOfWeek= elements.getElementsByTagName("DayOfWeek").item(0);
					String dayOfWeek2=dayOfWeek.getTextContent();
					newR.setDayOfWeek(dayOfWeek2);

					Node departureAirport= elements.getElementsByTagName("DepartureAirport").item(0);
					String departureAirport2=departureAirport.getTextContent();
					newR.setDepartureAirport(departureAirport2);

					Node departureTime= elements.getElementsByTagName("DepartureTime").item(0);
					LocalTime departureTime2=LocalTime.parse(departureTime.getTextContent());//convert to LocalTime format
					newR.setDepartureTime(departureTime2);


					Node depIATAcode= elements.getElementsByTagName("DepartureAirportIATACode").item(0);
					String depIATAcode2=depIATAcode.getTextContent();
					newR.setDepartureAirportCode(depIATAcode2);


					Node arrivalAirport= elements.getElementsByTagName("ArrivalAirport").item(0);
					String arrivalAirport2=arrivalAirport.getTextContent();
					newR.setArrivalAirport(arrivalAirport2);

					Node arrivalTime= elements.getElementsByTagName("ArrivalTime").item(0);
					LocalTime arrivalTime2=LocalTime.parse(arrivalTime.getTextContent());
					newR.setArrivalTime(arrivalTime2);

					Node arrIATAcode= elements.getElementsByTagName("ArrivalAirportIATACode").item(0);
					String arrIATAcode2=arrIATAcode.getTextContent();
					newR.setArrivalAirportCode(arrIATAcode2);

					Node duration= elements.getElementsByTagName("Duration").item(0);
					CharSequence duration2= duration.getTextContent();//convert to CharSequence
					Duration d=Duration.parse(duration2);//parse the Char Sequence duration2 into duration.
					newR.setDuration(d);

					routeList.add(newR);// add it into the routeList array list declared at the top.

				}

			}




		} catch (SAXException  |IllegalArgumentException | DateTimeParseException | ParserConfigurationException |  IOException  | DOMException | NullPointerException e){
			throw new DataLoadingException(e);

		}




	}

	/**
	 * Unloads all of the crew currently loaded, ready to start again if needed
	 */
	@Override
	public void reset() {
		routeList.clear();
	}


}
