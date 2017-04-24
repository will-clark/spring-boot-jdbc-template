package org.willclark;

import java.io.FileNotFoundException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/teams")
public class TeamController {

	@Autowired
	private TeamDAO service;
	
	@RequestMapping(method = RequestMethod.GET)
	public List<Team> list() {
		return service.findAll();
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public Team view(@PathVariable("id") long id) throws FileNotFoundException {
		Team team = service.find(id);
		if (team == null) throw new FileNotFoundException("team id = " + id + " was not found in the database");
		
		return team;
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public Team create(@RequestBody Team team) throws FileNotFoundException {		
		service.create(team);
		
		return team;
	}
	
	@RequestMapping(method = RequestMethod.PUT)
	public Team update(@PathVariable("id") long id, @RequestBody Team update) throws FileNotFoundException {
		Team team = service.find(id);
		if (team == null) throw new FileNotFoundException("team id = " + id + " was not found in the database");

		team.setName(update.getName());
		team.setCity(update.getCity());
		team.setColors(update.getColors());
		
		service.update(team);
		
		return team;
	}
	
	@RequestMapping(method = RequestMethod.DELETE)
	public void delete(@PathVariable("id") long id) throws FileNotFoundException {
		Team team = service.find(id);
		if (team == null) throw new FileNotFoundException("team id = " + id + " was not found in the database");
		
		service.delete(team);
	}
	
}
