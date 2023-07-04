package tools.vitruv.change.atomic.hid;

public final class HierarchicalId implements Comparable<HierarchicalId> {
	private String id;
	
	public HierarchicalId(String id) {
		this.id = id;
	}
	
	public String getId() {
		return id;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof HierarchicalId other) {
			return other.getId().equals(id);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return id.hashCode();
	}
	
	@Override
	public int compareTo(HierarchicalId o) {
		return id.compareTo(o.getId());
	}
	
	@Override
	public String toString() {
		return "Id(" + id + ")";
	}
}
