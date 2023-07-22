package hr.java.projektnizadatak.entities;

import hr.java.projektnizadatak.shared.exceptions.InvalidUsernameException;
import hr.java.projektnizadatak.shared.exceptions.UsernameTakenException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class UserManager {
	private static final Logger logger = LoggerFactory.getLogger(UserManager.class);

	private final UsersStore usersStore;
	private String loggedInUsername = null;
	private User loggedInUserFallback = null;

	public UserManager(UsersStore usersStore) {
		this.usersStore = usersStore;
	}

	public boolean tryLoginUser(String username, String password) {
		var passwordHash = User.hashPassword(password);
		var users = usersStore.read();

		var found = users.stream()
			.filter(u -> u.username().equals(username) && u.passwordHash().equals(passwordHash))
			.findFirst();

		if (found.isPresent()) {
			var user = found.get();
			loggedInUsername = user.username();
			loggedInUserFallback = user;
		}

		return found.isPresent();
	}

	public void logout() {
		loggedInUsername = null;
	}

	public User createUser(String username, String password) throws InvalidUsernameException, UsernameTakenException {
		if (!User.isUsernameValid(username)) {
			String m = "Invalid username: " + username;
			logger.error(m);

			throw new InvalidUsernameException(m);
		}

		var users = usersStore.read();

		boolean usernameTaken = users.stream()
			.anyMatch(u -> u.username().equals(username));

		if (usernameTaken) {
			String m = "Username taken: " + username;
			logger.error(m);

			throw new UsernameTakenException(m);
		}

		var user = new User.UserBuilder(username)
			.withPassword(password).withUserRole(UserRole.USER)
			.build();

		usersStore.create(user);

		return user;
	}

	public User getLoggedInUser() {
		return usersStore.read().stream()
			.filter(u -> u.username().equals(loggedInUsername))
			.findFirst()
			.orElse(loggedInUserFallback);
	}

	public List<User> getAllUsers() {
		return usersStore.read();
	}

	public User updateUserSettings(User user) {
		var newUser = new User.UserBuilder(user)
			.build();

		usersStore.update(user, newUser);

		return newUser;
	}
}
