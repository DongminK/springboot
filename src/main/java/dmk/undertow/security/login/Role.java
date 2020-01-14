package dmk.undertow.security.login;

public enum Role {
	ADMIN("ROLE_ADMIN"), MEMBER("ROLE_MEMBER");

	private String value;

	Role(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

}
