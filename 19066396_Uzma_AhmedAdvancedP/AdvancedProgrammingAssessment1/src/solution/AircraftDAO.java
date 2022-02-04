package solution;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import baseclasses.Aircraft;
import baseclasses.Aircraft.Manufacturer;
import baseclasses.DataLoadingException;


import baseclasses.IAircraftDAO;


/**
 * The AircraftDAO class is responsible for loading aircraft data from CSV files
 * and contains methods to help the system find aircraft when scheduling
 */
public class AircraftDAO implements IAircraftDAO {

	//The data structure we'll use to store the aircraft we've loaded
	List<Aircraft> aircraftList = new ArrayList<>();

	/**
	 * Loads the aircraft data from the specified file, adding them to the currently loaded aircraft
	 * Multiple calls to this function, perhaps on different files, would thus be cumulative
	 * @param p A Path pointing to the file from which data could be loaded
	 * @throws DataLoadingException if anything goes wrong. The exception's "cause" indicates the underlying exception
	 *
	 * Initially, this contains some starter code to help you get started in reading the CSV file...
	 */
	@Override
	public void loadAircraftData(Path p) throws DataLoadingException  {	
		try {
			//open the file
			BufferedReader reader = Files.newBufferedReader(p);

			//read the file line by line
			String line = "";

			//skip the first line of the file - headers
			reader.readLine();

			while( (line = reader.readLine()) != null) {
				//each line has fields separated by commas, split into an array of fields
				String[] fields = line.split(",");

				//put some of the fields into variables: check which fields are where atop the CSV file itself
				String tailcode = fields[0];
				String model=fields[1];
				String type = fields[2];
			
				Manufacturer manufacturer=Manufacturer.valueOf(fields[3].toUpperCase());
				String startingPosition=fields[4];
				int seats = Integer.parseInt(fields[5]);
				int cabinCrewRequired=Integer.parseInt(fields[6]);

				//create an Aircraft object, and set  its properties
				Aircraft a = new Aircraft();
				a.setTailCode(tailcode);
				a.setTypeCode(type);
				a.setSeats(seats);
				a.setCabinCrewRequired(cabinCrewRequired);
				a.setModel(model);
				a.setManufacturer(manufacturer);
				a.setStartingPosition(startingPosition);

				//add the aircraft to our list
				aircraftList.add(a);


				/*String str= "Aircraft: " + tailcode + " is a " + type + " with " + seats + " seats."+" With a model ="+model+" and manufacturer: "+manufacturer+"starting position " +startingPosition;

				String aircraft[]=str.split("+");

				for(int i=0;i<aircraft.length;i++) {
					System.out.println(aircraft[i]);
				}*/

				System.out.println("Aircraft: " + tailcode + " is a " + type + " with " + seats + " seats."+" With a model ="+model+" and manufacturer: "+manufacturer+"starting position " +startingPosition);
			}
		}

		catch (IOException | IllegalArgumentException | NullPointerException e) {//exception e
			e.printStackTrace();
			throw new DataLoadingException(e);
		}

	}




	/**
	 * Returns a list of all the loaded Aircraft with at least the specified number of seats
	 * @param seats the number of seats required
	 * @return a List of all the loaded aircraft with at least this many seats
	 */
	@Override
	public List<Aircraft> findAircraftBySeats(int seats) {
		List<Aircraft> loadedAircraft = new ArrayList<>();

		
		for(int i =0;i<aircraftList.size();i++) {
			if(aircraftList.get(i).getSeats()>=seats) {//if the aircarft seats is greater than the specificed seats - closest possible number
				loadedAircraft.add(aircraftList.get(i));// add the specificed aircarft to the array list
			}
		}

		return loadedAircraft;//returns the List of all loaded aircraft
	}

	/**
	 * Returns a list of all the loaded Aircraft that start at the specified airport code
	 * @param startingPosition the three letter airport code of the airport at which the desired aircraft start
	 * @return a List of all the loaded aircraft that start at the specified airport
	 */
	@Override
	public List<Aircraft> findAircraftByStartingPosition(String startingPosition) {
		List<Aircraft> loadedAircrafts = new ArrayList<Aircraft>();
		for(Aircraft aircrafts: aircraftList) {
			if(aircrafts.getStartingPosition().equals(startingPosition))
				loadedAircrafts.add(aircrafts);
		}

		return loadedAircrafts;

	}

	/**
	 * Returns the individual Aircraft with the specified tail code.
	 * @param tailCode the tail code for which to search
	 * @return the aircraft with that tail code, or null if not found
	 */
	@Override
	public Aircraft findAircraftByTailCode(String tailCode) {

		for(int i=0;i<aircraftList.size();i++) {
			if(aircraftList.get(i).getTailCode().equals(tailCode)) {
				return aircraftList.get(i);
			}
		}
		
		return null;
	}

	/**
	 * Returns a List of all the loaded Aircraft with the specified type code
	 * @param typeCode the type code of the aircraft you wish to find
	 * @return a List of all the loaded Aircraft with the specified type code
	 */
	@Override
	public List<Aircraft> findAircraftByType(String typeCode) {
		List<Aircraft> loadedAircraft = new ArrayList<>();

		for(Aircraft aircrafts: aircraftList) {
			if(aircrafts.getTypeCode().equals(typeCode)) {
				loadedAircraft.add(aircrafts);
			}
		}

		return loadedAircraft;
	}

	/**
	 * Returns a List of all the currently loaded aircraft
	 * @return a List of all the currently loaded aircraft
	 */
	@Override
	public List<Aircraft> getAllAircraft() {
		List<Aircraft> loadedAircraft = new ArrayList<Aircraft>();
		for (Aircraft aircrafts : aircraftList) {
			loadedAircraft.add(aircrafts);
		}
		return loadedAircraft;
	}

	/**
	 * Returns the number of aircraft currently loaded 
	 * @return the number of aircraft currently loaded
	 */
	@Override
	public int getNumberOfAircraft()  {

		return aircraftList.size();
	}

	/**
	 * Unloads all of the aircraft currently loaded, ready to start again if needed
	 */
	@Override
	public void reset() {
		aircraftList.clear();

	}

}
