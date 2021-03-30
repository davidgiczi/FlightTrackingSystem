package com.giczi.david.flight.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.giczi.david.flight.domain.FlightTicket;
import com.giczi.david.flight.domain.Passenger;
import com.giczi.david.flight.repository.FlightTicketRepository;

@Service
public class FlightTicketService {

	
	private FlightTicketRepository ticketRepo;
	private List<String> departurePlaces;
	private List<String> arrivalPlaces;
	private List<String> planes;
	
	@Autowired
	public void setTicketRepo(FlightTicketRepository ticketRepo) {
		this.ticketRepo = ticketRepo;
	}
	
	public void init() {
		
		departurePlaces = new ArrayList<>();
		departurePlaces.add("Budapest");
		departurePlaces.add("Debrecen");
		arrivalPlaces = new ArrayList<>();
		arrivalPlaces.add("Prague");
		arrivalPlaces.add("Berlin");
		arrivalPlaces.add("Barcelona");
		arrivalPlaces.add("London");
		arrivalPlaces.add("Paris");
		arrivalPlaces.add("Helsinki");
		arrivalPlaces.add("Moscow");
		planes = new ArrayList<>();
		planes.add("NKS-137");
		planes.add("THY-1G6");
		planes.add("N-X-211");
	}
	
		
	public List<String> getDeparturePlaces() {
		return departurePlaces;
	}


	public List<String> getArrivalPlaces() {
		return arrivalPlaces;
	}


	public List<String> getPlanes() {
		return planes;
	}


	public void saveFlightTicket(FlightTicket ticket) {
		
		if(ticket != null) {
			ticketRepo.save(ticket);
		}
		
	}

	public List<HighlightedFlightTicket> findNotDeletedTicketsByPassengerId(Passenger passenger){
		
		List<FlightTicket> tickets = ticketRepo.findNotDeletedTicketsByUserId(passenger.getId());
		
		return new Highlighter().createInputFlightTicketStore(tickets);
	}
	
	public void cancelTicket(Long id) {
		
		Optional<FlightTicket> ticketOtional = ticketRepo.findById(id);
		FlightTicket ticket = ticketOtional.get();
		ticket.setDeleted(true);
		ticketRepo.save(ticket);
		
	}
	public List<HighlightedFlightTicket> findByTextAndUserName(String text, Long id){
		
		List<FlightTicket> tickets= new ArrayList<>();
		
		if(Character.isLetter(text.charAt(0)) && Character.isUpperCase(text.charAt(0))) {
			text = text.charAt(0) + text.substring(1, text.length()).toLowerCase();
		}
		else if(Character.isLetter(text.charAt(0)) && Character.isLowerCase(text.charAt(0))) {
			text = String.valueOf(text.charAt(0)).toUpperCase() + text.substring(1, text.length()).toLowerCase();
		}
		
		tickets = ticketRepo.findByTextAndUserName(text, id);
		
		if(tickets.isEmpty()) {
			tickets = ticketRepo.findByTextAndUserName(text.toUpperCase(), id);
		}
		if(tickets.isEmpty()) {
			tickets = ticketRepo.findByTextAndUserName(text.toLowerCase(), id);
		}
		
		Highlighter highlighter = new Highlighter();
		highlighter.setSearchedExpression(text.toLowerCase());
		highlighter.createInputFlightTicketStore(tickets);
		highlighter.createHighlightedFlightTicketStore();
		
		return highlighter.getHighlightedFlightTicketStore();
	}
}
