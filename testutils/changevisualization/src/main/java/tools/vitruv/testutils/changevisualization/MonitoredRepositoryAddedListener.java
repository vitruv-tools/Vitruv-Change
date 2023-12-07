package tools.vitruv.testutils.changevisualization;

import tools.vitruv.testutils.changevisualization.common.ModelRepositoryChanges;

public interface MonitoredRepositoryAddedListener {
	public void addedMonitoredRepository(ModelRepositoryChanges newModelRepositoryChanges);
	public void addedMonitoredRepositoryFromFile(ModelRepositoryChanges newModelRepositoryChanges);
}
