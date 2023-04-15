package tools.vitruv.change.atomic.uuid;

public final class Uuid {
	private String uuid;
	
	Uuid(String uuid) {
		this.uuid = uuid;
	}
	
	public String getUuid() {
		return uuid;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Uuid other) {
			return other.getUuid().equals(uuid);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return uuid.hashCode();
	}
	
	@Override
	public String toString() {
		return "Uuid(" + uuid + ")";
	}
}
