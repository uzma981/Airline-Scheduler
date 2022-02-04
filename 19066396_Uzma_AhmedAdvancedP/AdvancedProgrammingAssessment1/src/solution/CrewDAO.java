package solution;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import baseclasses.Aircraft;
import baseclasses.CabinCrew;
import baseclasses.Crew;
import baseclasses.DataLoadingException;
import baseclasses.ICrewDAO;
import baseclasses.Pilot;


/**
 * The CrewDAO is responsible for loading data from JSON-based crew files 
 * It contains various methods to help the scheduler find the right pilots and cabin crew
 */
public class CrewDAO implements ICrewDAO {
	List<CabinCrew> crew = new ArrayList<>();//created individual array lists for pilot and cabin crew
	List<Pilot> pilots=new ArrayList<>();
	/**
	 * Loads the crew data from the specified file, adding them to the currently loaded crew
	 * Multiple calls to this function, perhaps on different files, would thus be cumulative
	 * @param p A Path pointing to the file from which data could be loaded
	 * @throws DataLoadingException if anything goes wrong. The exception's "cause" indicates the underlying exception
	 */
	@Override
	public void loadCrewData(Path p) throws DataLoadingException {
		try {
			BufferedReader reader= Files.newBufferedReader(p);
			String json =""; 
			String line="";

			while((line=reader.readLine()) !=null){ 
				json =json + line;
			}

			JSONObject fullTeam= new JSONObject(json);// full team consist of both pilot and cabin crew


			JSONArray ccList= fullTeam.getJSONArray("cabincrew");// get the JSON array of cabin crew
			JSONArray pilotList = fullTeam.getJSONArray("pilots");//get the pilots


			//for loop through the pilotList
			for(int i=0; i<pilotList.length();i++) {

				Pilot pi= new Pilot();//created a New object

				JSONObject pilotM =pilotList.getJSONObject(i);//got the JSON object from the Pilot List array

				//set the variable
				pi.setForename(pilotM.getString( "forename"));
				pi.setHomeBase(pilotM.getString("home_airport"));
				pi.setSurname(pilotM.getString("surname"));

				//get the typeRatings array and called it typeR
				JSONArray typeR = pilotM.getJSONArray("type_ratings");
				// rank of Pilot stored in a string- pilot only has rank, cabin crew doesnt.
				String r = pilotM.getString("rank");

				Pilot.Rank ranks = Pilot.Rank.valueOf(r);// get the value of the Pilot rank
				pi.setRank(ranks);

				for(int j=0;j<typeR.length();j++) {// for loop through all the type rating array

					pi.setQualifiedFor(typeR.getString(j));

				}

				pilots.add(pi);// add to the original array list.
			}

			for(int i=0; i<ccList.length();i++) {

				CabinCrew C = new CabinCrew();// CabinCrew object
				JSONObject crewM =ccList.getJSONObject(i);//get the JSON object from the cabin crew Json array 

				//set the variables from the JSON file
				JSONArray trackR = crewM.getJSONArray("type_ratings");
				C.setForename(crewM.getString( "forename"));
				C.setHomeBase(crewM.getString("home_airport"));
				C.setSurname(crewM.getString("surname"));



				for(int j=0;j<trackR.length();j++) {//for loop through the Json Array of type Ratings in the cabin crew list
					C.setQualifiedFor(trackR.getString(j));

				}

				crew.add(C);
			}



		}catch(IOException | JSONException | NullPointerException | IllegalArgumentException e) {


			throw new DataLoadingException(e);
		}

	}
	/**
	 * Returns a list of all the cabin crew based at the airport with the specified airport code
	 * @param airportCode the three-letter airport code of the airport to check for
	 * @return a list of all the cabin crew based at the airport with the specified airport code
	 */
	@Override
	public List<CabinCrew> findCabinCrewByHomeBase(String airportCode) {
		//create a new array list
		List<CabinCrew> CCAirportList= new ArrayList<>();
		//for each of the elements in the cabin crew array list orgininally declared at the top
		for(CabinCrew crewMember: crew) {
			if(crewMember.getHomeBase().equals(airportCode)) {//check if the specified airport code equals to the home base of the cabin crew memeber
				CCAirportList.add(crewMember);// add it
			}
		}

		return CCAirportList;


	}

	/**
	 * Returns a list of all the cabin crew based at a specific airport AND qualified to fly a specific aircraft type
	 * @param typeCode the type of plane to find cabin crew for
	 * @param airportCode the three-letter airport code of the airport to check for
	 * @return a list of all the cabin crew based at a specific airport AND qualified to fly a specific aircraft type
	 */
	@Override
	public List<CabinCrew> findCabinCrewByHomeBaseAndTypeRating(String typeCode, String airportCode) {
		List<CabinCrew> CCAirportList= new ArrayList<>();


		for(CabinCrew crewMember: crew) {
			if( crewMember.getTypeRatings().contains(typeCode)&& crewMember.getHomeBase().equals(airportCode)) 

				CCAirportList.add(crewMember);
		}
		return CCAirportList;
	}

	/**
	 * Returns a list of all the cabin crew currently loaded who are qualified to fly the specified type of plane
	 * @param typeCode the type of plane to find cabin crew for
	 * @return a list of all the cabin crew currently loaded who are qualified to fly the specified type of plane
	 */
	@Override
	public List<CabinCrew> findCabinCrewByTypeRating(String typeCode) {
		List<CabinCrew> CCAirportList= new ArrayList<>();

		for(CabinCrew crewMember: crew) {
			if(crewMember.getTypeRatings().contains(typeCode)) {
				CCAirportList.add(crewMember);
			}
		}
		return CCAirportList;
	}

	/**
	 * Returns a list of all the pilots based at the airport with the specified airport code
	 * @param airportCode the three-letter airport code of the airport to check for
	 * @return a list of all the pilots based at the airport with the specified airport code
	 */
	@Override
	public List<Pilot> findPilotsByHomeBase(String airportCode) {
		List<Pilot> pilotAirportList= new ArrayList<>();


		for(int i=0;i<pilots.size();i++) {
			if(pilots.get(i).getHomeBase().equals(airportCode)) //List of all the loaded aircraft with at least this many seats

				pilotAirportList.add(pilots.get(i));
		}
		return pilotAirportList;
	}

	/**
	 * Returns a list of all the pilots based at a specific airport AND qualified to fly a specific aircraft type
	 * @param typeCode the type of plane to find pilots for
	 * @param airportCode the three-letter airport code of the airport to check for
	 * @return a list of all the pilots based at a specific airport AND qualified to fly a specific aircraft type
	 */
	@Override
	public List<Pilot> findPilotsByHomeBaseAndTypeRating(String typeCode, String airportCode) {
		List<Pilot> pilotAirportList= new ArrayList<>();


		for(Pilot p: pilots) {
			if( p.getTypeRatings().contains(typeCode) && p.getHomeBase().equals(airportCode)) // 2 conditions to be met 

				pilotAirportList.add(p);
		}
		return pilotAirportList;
	}

	/**
	 * Returns a list of all the pilots currently loaded who are qualified to fly the specified type of plane
	 * @param typeCode the type of plane to find pilots for
	 * @return a list of all the pilots currently loaded who are qualified to fly the specified type of plane
	 */
	@Override
	public List<Pilot> findPilotsByTypeRating(String typeCode) {
		List<Pilot> pilotAirportList= new ArrayList<>();

		for(int i=0;i<pilots.size();i++) {
			if(pilots.get(i).getTypeRatings().contains(typeCode)) {// get the pilots type rating and check against the type code inputted
				pilotAirportList.add(pilots.get(i));
			}
		}
		return pilotAirportList;

	}

	/**
	 * Returns a list of all the cabin crew currently loaded
	 * @return a list of all the cabin crew currently loaded
	 */
	@Override
	public List<CabinCrew> getAllCabinCrew() {

		List<CabinCrew> loadCabin = new ArrayList<>();
		for (CabinCrew c : crew) {
			loadCabin.add(c);
		}
		return loadCabin;
	}

	/**
	 * Returns a list of all the crew, regardless of type
	 * @return a list of all the crew, regardless of type
	 */
	@Override
	public List<Crew> getAllCrew() {
		List<Crew> crewList= new ArrayList<Crew>();

		for(int i=0;i<pilots.size();i++) {// for loop through the pilots Array. add to the crewList. 
			crewList.add(pilots.get(i));
		}

		for(int k=0;k<crew.size();k++) {//for loop through the Crew size array seperately and add to the combined crewList array.
			crewList.add(crew.get(k));

		}
		// add both pilot and cabin crew to the new array list and return it
		return crewList;
	}

	/**
	 * Returns a list of all the pilots currently loaded
	 * @return a list of all the pilots currently loaded
	 */
	@Override
	public List<Pilot> getAllPilots() {
		List<Pilot> loadedPilots = new ArrayList<>();
		for (Pilot p : pilots) {
			loadedPilots.add(p);
		}
		return loadedPilots;

	}

	@Override
	public int getNumberOfCabinCrew() {
		return crew.size();
	}

	/**
	 * Returns the number of pilots currently loaded
	 * @return the number of pilots currently loaded
	 */
	@Override
	public int getNumberOfPilots() {
		return pilots.size();
	}

	/**
	 * Unloads all of the crew currently loaded, ready to start again if needed
	 */
	@Override
	public void reset() {
		pilots.clear();//clear both of the pilot array list and the cabin crew
		crew.clear();

	}

}
