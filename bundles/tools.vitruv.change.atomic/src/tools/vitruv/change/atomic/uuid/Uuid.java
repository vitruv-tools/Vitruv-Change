package tools.vitruv.change.atomic.uuid;

public final class Uuid {
	private String rawValue;
	
	Uuid(String rawValue) {
		this.rawValue = rawValue;
	}
	
	String getRawValue() {
		return rawValue;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Uuid other) {
			return other.getRawValue().equals(rawValue);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return rawValue.hashCode();
	}
	
	@Override
	public String toString() {
		return "Uuid(" + rawValue + ")";
	}
}
