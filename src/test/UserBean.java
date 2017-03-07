package test;

public class UserBean {

	private String name, surname;
	private String date;
	private int id;

	public UserBean() {
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String mStrDate) {
		this.date = mStrDate;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
