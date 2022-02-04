package solution;

import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

import baseclasses.DataLoadingException;

import baseclasses.IAircraftDAO;
import baseclasses.ICrewDAO;
import baseclasses.IPassengerNumbersDAO;
import baseclasses.IRouteDAO;
import baseclasses.Route;
import baseclasses.Schedule;





/**
 * This class allows you to run the code in your classes yourself, for testing and development
 */
public class Main {

	public static void main(String[] args) throws IllegalArgumentException {	
		IAircraftDAO aircraft = new AircraftDAO();
		IRouteDAO r=new RouteDAO();
		ICrewDAO crewMembers = new CrewDAO();
		IPassengerNumbersDAO pM= new PassengerNumbersDAO();
		
		Scheduler schedular=new Scheduler();
		Schedule schedule=new Schedule(r,LocalDate.of(2021,1,19),LocalDate.of(2021, 2, 20));
				
		try {
			
			
			aircraft.loadAircraftData(Paths.get("./data/schedule_aircraft.csv"));
			crewMembers.loadCrewData(Paths.get("./data/schedule_crew.json"));
			pM.loadPassengerNumbersData(Paths.get("./data/schedule_passengers.db"));
			
	
			r.loadRouteData(Paths.get("./data/schedule_routes.xml"));
			
		
			
		
			//schedular.generateSchedule(aircraft, crewMembers, r, pM, LocalDate.now(), LocalDate.of(2021, 2, 21));
			System.out.println(schedular.generateSchedule(aircraft, crewMembers, r, pM, LocalDate.of(2021,2,10), LocalDate.of(2021, 2, 19)));
			
		}
		catch (DataLoadingException e) {
			System.err.println("Error loading aircraft data");
			e.printStackTrace();
		}
	}

}
