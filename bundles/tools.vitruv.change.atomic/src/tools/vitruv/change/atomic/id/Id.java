package tools.vitruv.change.atomic.id;

public final class Id {
	private String id;
	
	Id(String id) {
		this.id = id;
	}
	
	public String getId() {
		return id;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Id other) {
			return other.getId().equals(id);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return id.hashCode();
	}
	
	@Override
	public String toString() {
		return "Id(" + id + ")";
	}
}
