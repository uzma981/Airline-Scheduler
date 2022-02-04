package solution;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

import baseclasses.DataLoadingException;
import baseclasses.IPassengerNumbersDAO;

/**
 * The PassengerNumbersDAO is responsible for loading an SQLite database
 * containing forecasts of passenger numbers for flights on dates
 */
public class PassengerNumbersDAO implements IPassengerNumbersDAO {
	HashMap<String,Integer> Hashmap=new HashMap<String,Integer>();
	//create a key out of date and flight number
	//use get to find the contains. 

	/**
	 * Returns the number of passenger number entries in the cache
	 * @return the number of passenger number entries in the cache
	 */
	@Override
	public int getNumberOfEntries() {
		return Hashmap.size();
	}

	/**
	 * Returns the predicted number of passengers for a given flight on a given date, or -1 if no data available
	 * @param flightNumber The flight number of the flight to check for
	 * @param date the date of the flight to check for
	 * @return the predicted number of passengers, or -1 if no data available
	 */
	@Override
	public int getPassengerNumbersFor(int flightNumber, LocalDate date) {

		String dateinput= date.toString();

		if(Hashmap.containsKey(dateinput+flightNumber)){//find out if there is a record in the hashmap for key
			return Hashmap.get(dateinput+flightNumber);
		}

		return -1;

	}

	/**
	 * Loads the passenger numbers data from the specified SQLite database into a cache for future calls to getPassengerNumbersFor()
	 * Multiple calls to this method are additive, but flight numbers/dates previously cached will be overwritten
	 * The cache can be reset by calling reset() 
	 * @param p The path of the SQLite database to load data from
	 * @throws DataLoadingException If there is a problem loading from the database
	 */
	@Override
	public void loadPassengerNumbersData(Path p) throws DataLoadingException {

		Connection conn=null;

		try {

			conn=DriverManager.getConnection("jdbc:sqlite:"+p.toString());

			//run query

			Statement statement=conn.createStatement();


			ResultSet x =statement.executeQuery("SELECT * FROM PassengerNumbers");
			while(x.next()) {

				int flightNo=x.getInt("FlightNumber");
				String date= x.getString("Date");
				int loadEstimate=x.getInt("LoadEstimate");
				//load estimate- this what goes in 
				Hashmap.put(date+flightNo,loadEstimate);
			}

			//hashmap stores key and values.
			//making the key out of both date and flightNo.- key.


		}
		catch (SQLException | NullPointerException | IllegalArgumentException e) {
			e.printStackTrace();
			throw new DataLoadingException(e);
		}


	}

	/**
	 * Removes all data from the DAO, ready to start again if needed
	 */
	@Override
	public void reset() {
		Hashmap.clear();

	}

}
