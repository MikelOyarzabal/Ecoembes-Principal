package entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;



public class User {

	private String nickname;
	private String password;
	private String email;
	

	// Constructor without parameters
	public User() { }
	
	// Constructor with parameters
	public User(String nickname, String email, String password) {
		this.nickname = nickname;		
		this.email = email;
		this.password = password;
	}
	
	// Check if a password is correct
	public boolean checkPassword(String password) {
        return this.password.equals(password);
	}

	//  Getters and setters
	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}



	// hashCode and equals
	@Override
	public int hashCode() {
		return Objects.hash(email, nickname);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		return Objects.equals(email, other.email) && 
			   Objects.equals(nickname, other.nickname);
	}
}