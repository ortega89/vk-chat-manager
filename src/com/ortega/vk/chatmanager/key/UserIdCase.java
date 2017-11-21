package com.ortega.vk.chatmanager.key;
import com.vk.api.sdk.queries.users.UsersNameCase;

public class UserIdCase {
	private int userId;
	private UsersNameCase theCase;
	
	public UserIdCase(int userId, UsersNameCase theCase) {
		this.userId = userId;
		this.theCase = theCase;
	}

	public int getUserId() {
		return userId;
	}

	public UsersNameCase getTheCase() {
		return theCase;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((theCase == null) ? 0 : theCase.hashCode());
		result = prime * result + userId;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserIdCase other = (UserIdCase) obj;
		if (theCase != other.theCase)
			return false;
		if (userId != other.userId)
			return false;
		return true;
	}
}
