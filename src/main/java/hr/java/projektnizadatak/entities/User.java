package hr.java.projektnizadatak.entities;

import hr.java.projektnizadatak.shared.exceptions.UnsupportedAlgorithmException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Objects;
import java.util.regex.Pattern;

public final class User implements Serializable, Recordable {
	private static final Logger logger = LoggerFactory.getLogger(User.class);

	private static final Pattern VALID_USERNAME_PATTERN = Pattern.compile("^[A-Za-z0-9\\-_ ]{4,32}$");

	private final String username;
	private final String passwordHash;

	private final UserRole userRole;


	public User(String username, String passwordHash, UserRole userRole) {
		this.username = username;
		this.passwordHash = passwordHash;
		this.userRole = userRole;
	}

	public static String hashPassword(String password) {
		try {
			var digest = MessageDigest.getInstance("SHA-256")
				.digest(password.getBytes());

			return new String(Base64.getEncoder()
				.encode(digest));
		} catch (NoSuchAlgorithmException e) {
			String m = "System doesn't support SHA-256";
			logger.error(m);

			throw new UnsupportedAlgorithmException(m, e);
		}
	}

	public static boolean isUsernameValid(String username) {
		return VALID_USERNAME_PATTERN.matcher(username)
			.matches();
	}

	public String username() {return username;}

	public String passwordHash() {return passwordHash;}



	@Override
	public int hashCode() {
		return Objects.hash(username, passwordHash);
	}

	@Override
	public String toString() {
		return "User{" +
			"username=" + username +
			", passwordHash=" + passwordHash +
			'}';
	}

	@Override
	public String displayShort() {
		var sb = new StringBuilder(username)
			.append(" (").append("); ");


		sb.append("; ");

		return sb.toString();
	}

	@Override
	public String displayFull() {
		return displayShort() + "; " + passwordHash;
	}

	public static class UserBuilder {
		private final String username;
		private String passwordHash;
		private UserRole userRole;

		public UserBuilder(String username) {
			this.username = username;
		}

		public UserBuilder(User original) {
			this.username = original.getUsername();
			this.passwordHash = original.getPasswordHash();
			this.userRole = original.getUserRole();
		}

		public UserBuilder withPasswordHash(String passwordHash) {
			this.passwordHash = passwordHash;
			return this;
		}

		public UserBuilder withPassword(String password) {
			this.passwordHash = hashPassword(password);
			return this;
		}

		public UserBuilder withUserRole(UserRole userRole) {
			this.userRole = userRole;
			return this;
		}

		public User build() {
			return new User(username, passwordHash, userRole);
		}
	}

	public String getUsername() {
		return username;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public UserRole getUserRole() {
		return userRole;
	}
}
