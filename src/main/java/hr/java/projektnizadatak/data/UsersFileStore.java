package hr.java.projektnizadatak.data;

import hr.java.projektnizadatak.entities.UserRole;
import hr.java.projektnizadatak.entities.UsersStore;
import hr.java.projektnizadatak.entities.User;
import hr.java.projektnizadatak.shared.exceptions.DataStoreException;
import hr.java.projektnizadatak.shared.exceptions.ReadOrWriteErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UsersFileStore implements UsersStore {
	private static final Logger logger = LoggerFactory.getLogger(UsersFileStore.class);
	private static final Path USERS_FILE_PATH = Path.of("data/users.txt");

	public UsersFileStore() {
		FileUtil.ensureFileExists(USERS_FILE_PATH);
	}

	@Override
	public void create(User user) {
		String serializedUser = formatUser(user);

		try {
			Files.writeString(USERS_FILE_PATH, serializedUser, StandardOpenOption.APPEND);
		} catch (IOException e) {
			String m = "Error while writing file: " + USERS_FILE_PATH;
			logger.error(m);

			throw new ReadOrWriteErrorException(m, e);
		}
	}

	@Override
	public List<User> read() {
		try {
			return Files.readAllLines(USERS_FILE_PATH)
				.stream()
				.map(this::parseUser)
				.collect(Collectors.toCollection(ArrayList::new));
		} catch (IOException e) {
			String m = "Error while reading file: " + USERS_FILE_PATH;
			logger.error(m);

			throw new ReadOrWriteErrorException(m, e);
		}
	}

	@Override
	public void update(User oldUser, User newUser) {
		var users = read();

		storeAll(users.stream()
			.map(u -> u.equals(oldUser) ? newUser : u)
			.toList()
		);
	}

	@Override
	public void overrideAll(List<User> users) throws DataStoreException {

	}

	private User parseUser(String line) {
		var s = line.split(":", -1);
		var ub = new User.UserBuilder(s[0])
				.withPasswordHash(s[1])
				.withUserRole(UserRole.valueOf(s[2]));

		return ub.build();
	}


	private void storeAll(List<User> users) {
		try {
			var serialized = users.stream()
					.map(this::formatUser)
					.collect(Collectors.joining());

			Files.write(USERS_FILE_PATH, serialized.getBytes(), StandardOpenOption.APPEND);
		} catch (IOException e) {
			String m = "Error while writing file: " + USERS_FILE_PATH;
			logger.error(m);

			throw new ReadOrWriteErrorException(m, e);
		}
	}


	private String formatUser(User user) {
		StringBuilder sb = new StringBuilder()
				.append(user.getUsername())
				.append(':')
				.append(user.getPasswordHash())
				.append(':')
				.append(user.getUserRole().getName())
				.append('\n');

		return sb.toString();
	}

}
