package tools.vitruv.testutils;

import java.io.IOException;
import java.nio.file.Files;

import edu.kit.ipd.sdq.activextendannotations.Utility;
import tools.vitruv.change.composite.propagation.ChangeableModelRepository;
import tools.vitruv.change.interaction.InternalUserInteractor;
import tools.vitruv.change.interaction.UserInteractionFactory;
import tools.vitruv.change.propagation.ChangePropagationSpecificationProvider;
import tools.vitruv.change.propagation.PersistableChangeRecordingModelRepository;
import tools.vitruv.change.propagation.impl.DefaultChangeRecordingModelRepository;
import tools.vitruv.change.propagation.impl.DefaultChangeableModelRepository;

@Utility
public class TestModelRepositoryFactory {
	/**
	 * Creates a {@link ChangeableModelRepository} for the given change propagation specification provider and user
	 * interaction, which stores metadata in a temporary folder.
	 * @param changePropagationSpecificationProvider provides the {@link ChangePropagationSpecifications} to use in the
	 * repository
	 * @param userInteraction the {@link TestUserInteraction} to use for interactions during change propagation
	 * @return the test model repository
	 * @throws IOException is thrown if no temporary directory can be created
	 */
	public static ChangeableModelRepository createTestChangeableModelRepository(
			ChangePropagationSpecificationProvider changePropagationSpecificationProvider, TestUserInteraction userInteraction) throws IOException {
		InternalUserInteractor userInteractor = UserInteractionFactory.instance
				.createUserInteractor(new TestUserInteraction.ResultProvider(userInteraction));
		PersistableChangeRecordingModelRepository recordingModelRepository = new DefaultChangeRecordingModelRepository(null,
				Files.createTempDirectory(null));
		ChangeableModelRepository changeableModelRepository = new DefaultChangeableModelRepository(recordingModelRepository,
				changePropagationSpecificationProvider, userInteractor);
		return changeableModelRepository;
	}
}
