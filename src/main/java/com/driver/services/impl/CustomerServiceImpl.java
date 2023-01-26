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
        
	    int id = Integer.MAX_VALUE;
		List<Driver> drivers = driverRepository2.findAll();
		for(Driver driver: drivers){
		    if(driver.getCab().getAvailable()==true){
                id = Math.min(id,driver.getDriverId());
			}
		}
		if(id==Integer.MAX_VALUE){
			throw new Exception("No cab available!");
		}
		Driver driver = driverRepository2.findById(id).get();

		trip.setFromLocation(fromLocation);
		trip.setDistanceInKm(distanceInKm);
		trip.setToLocation(toLocation);
		trip.setBill(distanceInKm*10);
		trip.getDriver().getCab().setAvailable(false);
		trip.setDriver(driver);
		
		List<TripBooking> list2 = driver.getTripBookingList();
		list2.add(trip);

		

		Customer customer = customerRepository2.findById(customerId).get();
		List<TripBooking> list = customer.getTripBookingList();
		list.add(trip);

		trip.setCustomer(customer);

	   // tripBookingRepository2.save(trip);
	   driverRepository2.save(driver);
	   customerRepository2.save(customer);

		return trip;

		
	}

	@Override
	public void cancelTrip(Integer tripId){
		//Cancel the trip having given trip Id and update TripBooking attributes accordingly
		TripBooking trip = tripBookingRepository2.findById(tripId).get();
		trip.setStatus(TripStatus.CANCELED);
		trip.setBill(0);
		trip.getDriver().getCab().setAvailable(true);

		tripBookingRepository2.save(trip);
		

	}

	@Override
	public void completeTrip(Integer tripId){
		//Complete the trip having given trip Id and update TripBooking attributes accordingly
		TripBooking trip = tripBookingRepository2.findById(tripId).get();
		trip.setStatus(TripStatus.COMPLETED);
		trip.setBill(trip.getDistanceInKm()*trip.getDriver().getCab().getPerKmRate());
		trip.getDriver().getCab().setAvailable(true);
		tripBookingRepository2.save(trip);

	}
}
