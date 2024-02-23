package tools.vitruv.change.testutils.changevisualization;

import tools.vitruv.change.testutils.changevisualization.common.ModelRepositoryChanges;

public interface MonitoredRepositoryAddedListener {
	public void addedMonitoredRepository(ModelRepositoryChanges newModelRepositoryChanges);
	public void addedMonitoredRepositoryFromFile(ModelRepositoryChanges newModelRepositoryChanges);
}
