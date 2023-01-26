package com.driver.services.impl;

import com.driver.model.TripBooking;
import com.driver.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.driver.model.Cab;
import com.driver.model.Customer;
import com.driver.model.Driver;
import com.driver.repository.CustomerRepository;
import com.driver.repository.DriverRepository;
import com.driver.repository.TripBookingRepository;
import com.driver.model.TripStatus;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

	@Autowired
	CustomerRepository customerRepository2;

	@Autowired
	DriverRepository driverRepository2;

	@Autowired
	TripBookingRepository tripBookingRepository2;

	@Override
	public void register(Customer customer) {
		//Save the customer in database
		customerRepository2.save(customer);
	}

	@Override
	public void deleteCustomer(Integer customerId) {
		// Delete customer without using deleteById function
		Customer customer = customerRepository2.findById(customerId).get();
		customerRepository2.delete(customer);


	}

	@Override
	public TripBooking bookTrip(int customerId, String fromLocation, String toLocation, int distanceInKm) throws Exception{
		//Book the driver with lowest driverId who is free (cab available variable is Boolean.TRUE). If no driver is available, throw "No cab available!" exception
		//Avoid using SQL query
        TripBooking trip = new TripBooking();
        
	    //int id = Integer.MAX_VALUE;
		Driver driver = null;
		List<Driver> drivers = driverRepository2.findAll();
		// for(Driver driver: drivers){
		//     if(driver.getCab().getAvailable()==Boolean.TRUE){
        //         id = Math.min(id,driver.getDriverId());
		// 	}
		// }
		// if(id==Integer.MAX_VALUE){
		// 	throw new Exception("No cab available!");
		// }
		// Driver driver = driverRepository2.findById(id).get();

		for(Driver driver1 : drivers){
			if(driver.getCab().getAvailable()==Boolean.TRUE){
				if(driver==null || driver.getDriverId()>driver1.getDriverId()){
					driver = driver1;
				}
			}
		}

		if(driver == null){
			throw new Exception("No cab available!");
		}
		Customer customer = customerRepository2.findById(customerId).get();
        
		trip.setDriver(driver);
		trip.setCustomer(customer);
		trip.setFromLocation(fromLocation);
		trip.setDistanceInKm(distanceInKm);
		trip.setToLocation(toLocation);
		trip.setBill(distanceInKm*driver.getCab().getPerKmRate());
		trip.setStatus(TripStatus.CONFIRMED);
	    driver.getCab().setAvailable(Boolean.FALSE);
		

		driver.getTripBookingList().add(trip);
		driverRepository2.save(driver);

		customer.getTripBookingList().add(trip);
		customerRepository2.save(customer);
		
		

		return trip;

		
	}

	@Override
	public void cancelTrip(Integer tripId){
		//Cancel the trip having given trip Id and update TripBooking attributes accordingly
		TripBooking trip = tripBookingRepository2.findById(tripId).get();
		trip.setStatus(TripStatus.CANCELED);
		trip.setBill(0);
		trip.getDriver().getCab().setAvailable(Boolean.TRUE);

		tripBookingRepository2.save(trip);
		

	}

	@Override
	public void completeTrip(Integer tripId){
		//Complete the trip having given trip Id and update TripBooking attributes accordingly
		TripBooking trip = tripBookingRepository2.findById(tripId).get();
		trip.setStatus(TripStatus.COMPLETED);
		trip.setBill(trip.getDistanceInKm()*trip.getDriver().getCab().getPerKmRate());
		trip.getDriver().getCab().setAvailable(Boolean.TRUE);
		tripBookingRepository2.save(trip);

	}
}
