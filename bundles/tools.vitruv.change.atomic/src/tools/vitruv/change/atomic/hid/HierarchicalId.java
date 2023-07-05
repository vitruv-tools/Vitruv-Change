package tools.vitruv.change.atomic.hid;

/**
 * A hierarchical id is a volatile identifier which identifies an element based
 * on its hierarchical location in a resource. A hierarchical id is not bound to
 * a resource set, i.e. two elements at the same location in identical resources
 * of different resource sets will have the same hierarchical id.
 */
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
