package hr.java.projektnizadatak.entities;

import java.util.List;

import hr.java.projektnizadatak.shared.exceptions.DataStoreException;

public interface UsersStore {
	void create(User user);
	
	List<User> read();

	void update(User oldUser, User newUser);
	
	void overrideAll(List<User> users) throws DataStoreException;
}
