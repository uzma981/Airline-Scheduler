package solution;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import baseclasses.Aircraft;
import baseclasses.CabinCrew;
import baseclasses.Crew;
import baseclasses.DoubleBookedException;
import baseclasses.FlightInfo;
import baseclasses.IAircraftDAO;
import baseclasses.ICrewDAO;
import baseclasses.IPassengerNumbersDAO;
import baseclasses.IRouteDAO;
import baseclasses.IScheduler;
import baseclasses.InvalidAllocationException;
import baseclasses.Pilot;
import baseclasses.Schedule;

/**
 * The Scheduler class is responsible for deciding which aircraft and crew will be
 * used for each of an airline's flights in a specified period of time, referred to
 * as a "scheduling horizon". A schedule must have an aircraft, two pilots, and 
 * sufficient cabin crew for the aircraft allocated to every flight in the horizon 
 * to be valid.
 */
public class Scheduler implements IScheduler {


	/**
	 * Generates a schedule, providing you with ready-loaded DAO objects to get your data from
	 * @param aircraftDAO the DAO for the aircraft to be used when scheduling
	 * @param crewDAO the DAO for the crew to be used when scheduling
	 * @param routeDAO the DAO to use for routes when scheduling
	 * @param passengerNumbersDAO the DAO to use for passenger numbers when scheduling
	 * @param startDate the start of the scheduling horizon
	 * @param endDate the end of the scheduling horizon
	 * @return The generated schedule - which must happen inside 2 minutes
	 */

	@Override
	public Schedule generateSchedule(IAircraftDAO aircraftDAO, ICrewDAO crewDAO, IRouteDAO routeDAO, 
			IPassengerNumbersDAO passengerNumbersDAO, LocalDate startDate, LocalDate endDate) {

		System.currentTimeMillis();

		Schedule schedular = new Schedule(routeDAO, startDate, endDate);//Schedule object for the specified routes and date range
		List<Aircraft> plane = aircraftDAO.getAllAircraft();

		List<CabinCrew> cabincrew= crewDAO.getAllCabinCrew();

		List<Pilot> pilotList= crewDAO.getAllPilots();

		List<FlightInfo> info=schedular.getRemainingAllocations();
		//flight info objects are added to a list of flights


		System.out.println(info.size()+" want ");

		for(FlightInfo flights: info) {//for every flight
			Collections.shuffle(pilotList);

			Collections.shuffle(cabincrew);

			try{

				for(int i =0;i<plane.size();i++) {//for loop through the aircrafts

					if(!(schedular.hasConflict(plane.get(i), flights))) {//if it doesnt have conflict, allocate the  1 aircraft to the flight
						schedular.allocateAircraftTo(plane.get(i), flights);
						break;
					}
				}
				try {
					boolean captainFound=false;// a boolean to check if the captain is found

					for(int z=0;z<pilotList.size();z++) {//for loop through the full pilots list

						// if any pilot free and doesnt have conflict
						if(!(schedular.hasConflict(pilotList.get(z), flights))){
							if(captainFound==false) {//if the captain isnt found yet, allocate a captain then change captainFound as to be true.
								schedular.allocateCaptainTo(pilotList.get(z), flights);
								captainFound=true;
							}else {// allocate the first officer.
								schedular.allocateFirstOfficerTo(pilotList.get(z), flights);
								break;// only do 1 
							}

						}

					}

				}
				catch(DoubleBookedException e) {
					System.out.println("pilot is double booked");

				}

				int add = schedular.getAircraftFor(flights).getCabinCrewRequired();// gets the required cabin crew for the aircraft that is allocated into the flight

				try {

					for(int j=0;j<cabincrew.size();j++) {// for loops through the cabin crew list

						if(!(schedular.hasConflict(cabincrew.get(j), flights) )  ){// if it doesnt have conflict

							//&& cabincrew.get(j).isQualifiedFor(schedular.getAircraftFor(flights)) 
							schedular.allocateCabinCrewTo(cabincrew.get(j), flights);//allocate the cabin crew
							add--;//keep doing it until the full required cabin crew for the flight are allocated
							if(add<0) {
								break;
							}
						}
					}

				}
				catch(DoubleBookedException e) {
					System.out.println("cabin crew is double booked");

				}
				System.currentTimeMillis();



			}
			catch(DoubleBookedException e) {
				System.out.println("aircraft was double booked");
			}

			try {

				schedular.completeAllocationFor(flights);


			}
			catch(InvalidAllocationException e) {
				e.printStackTrace();
				System.out.println("error");
			}


		}
		System.currentTimeMillis();
		System.out.println("number of completed "+ schedular.getCompletedAllocations().size());
		return schedular;


	}
}

