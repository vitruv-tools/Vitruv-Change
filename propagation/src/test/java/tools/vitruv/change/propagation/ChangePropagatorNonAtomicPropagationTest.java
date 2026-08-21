package tools.vitruv.change.propagation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;
import org.eclipse.emf.ecore.EObject;
import org.junit.jupiter.api.Test;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.uuid.Uuid;
import tools.vitruv.change.composite.MetamodelDescriptor;
import tools.vitruv.change.composite.description.PropagatedChange;
import tools.vitruv.change.composite.description.TransactionalChange;
import tools.vitruv.change.composite.description.VitruviusChange;
import tools.vitruv.change.correspondence.Correspondence;
import tools.vitruv.change.correspondence.view.EditableCorrespondenceModelView;
import tools.vitruv.change.interaction.InternalUserInteractor;
import tools.vitruv.change.propagation.impl.AbstractChangePropagationSpecification;
import tools.vitruv.change.propagation.impl.ChangePropagator;
import tools.vitruv.change.utils.ResourceAccess;

/**
 * Tests that {@link ChangePropagator} offers a whole {@link VitruviusChange} to
 * {@link
 * ChangePropagationSpecification}s that declare
 * {@code doesHandleNonAtomicChanges()}, instead of
 * only ever propagating one {@link EChange} at a time.
 */
class ChangePropagatorNonAtomicPropagationTest {
  private final MetamodelDescriptor sourceDescriptor = MetamodelDescriptor.with("models");
  private final MetamodelDescriptor targetDescriptor = MetamodelDescriptor.with("otherModels");

  @Test
  @SuppressWarnings("unchecked")
  void nonAtomicSpecificationReceivesWholeChangeWhileOthersStillReceiveEChanges() {
    var nonAtomicSpecification = new NonAtomicChangePropagationSpecification(sourceDescriptor, targetDescriptor);
    var atomicOnlySpecification = new AtomicOnlyChangePropagationSpecification(sourceDescriptor, targetDescriptor);
    var changePropagationProvider = new ChangePropagationSpecificationRepository(
        List.of(nonAtomicSpecification, atomicOnlySpecification));

    EObject affectedObject = mock(EObject.class);
    EChange<EObject> eChange = mock(EChange.class);
    TransactionalChange<EObject> resolvedChange = mock(TransactionalChange.class);
    when(resolvedChange.getAffectedEObjects()).thenReturn(Set.of(affectedObject));
    when(resolvedChange.getAffectedEObjectsMetamodelDescriptors())
        .thenReturn(Set.of(sourceDescriptor));
    when(resolvedChange.getEChanges()).thenReturn(List.of(eChange));
    when(resolvedChange.containsConcreteChange()).thenReturn(true);
    when(resolvedChange.getUserInteractions()).thenReturn(List.of());

    EditableCorrespondenceModelView<Correspondence> correspondenceModel = mock(EditableCorrespondenceModelView.class);
    ChangeRecordingModelRepository modelRepository = mock(ChangeRecordingModelRepository.class);
    when(modelRepository.applyChange(any())).thenReturn(resolvedChange);
    when(modelRepository.getCorrespondenceModel()).thenReturn(correspondenceModel);
    when(modelRepository.recordChanges(any()))
        .thenAnswer(
            invocation -> {
              Runnable changeApplicator = invocation.getArgument(0);
              changeApplicator.run();
              return List.<TransactionalChange<EObject>>of();
            });

    InternalUserInteractor userInteractor = mock(InternalUserInteractor.class);
    ChangePropagator propagator = new ChangePropagator(
        modelRepository,
        changePropagationProvider,
        userInteractor,
        ChangePropagationMode.SINGLE_STEP);

    VitruviusChange<Uuid> inputChange = mock(VitruviusChange.class);

    // The non-atomic-enabled specification is offered the whole change exactly once
    // ...
    assertEquals(1, nonAtomicSpecification.nonAtomicInvocations);
    // ... and, like every other specification, is still called once per atomic
    // EChange, too.
    assertEquals(1, nonAtomicSpecification.atomicInvocations);

    // A specification that does not opt into non-atomic handling never receives the
    // whole change
    // ...
    assertFalse(atomicOnlySpecification.doesHandleNonAtomicChanges());
    assertEquals(0, atomicOnlySpecification.nonAtomicInvocations);
    // ... but is still called once per atomic EChange, as before.
    assertEquals(1, atomicOnlySpecification.atomicInvocations);

    // Since neither specification recorded any actual model changes, only the
    // atomic propagation
    // step (which always reports a result) produces a PropagatedChange; the
    // non-atomic step,
    // having recorded nothing, contributes none.
    List<PropagatedChange> result = propagator.propagateChange(inputChange);
    assertEquals(1, result.size());
    assertEquals(resolvedChange, result.get(0).getOriginalChange());
  }

  private static class NonAtomicChangePropagationSpecification
      extends AbstractChangePropagationSpecification {
    private int nonAtomicInvocations = 0;
    private int atomicInvocations = 0;

    NonAtomicChangePropagationSpecification(
        MetamodelDescriptor sourceDescriptor, MetamodelDescriptor targetDescriptor) {
      super(sourceDescriptor, targetDescriptor);
    }

    @Override
    public boolean doesHandleNonAtomicChanges() {
      return true;
    }

    @Override
    public void propagateNonAtomicChange(
        VitruviusChange<EObject> change,
        EditableCorrespondenceModelView<Correspondence> correspondenceModel,
        ResourceAccess resourceAccess) {
      nonAtomicInvocations++;
    }

    @Override
    public void propagateChange(
        EChange<EObject> change,
        EditableCorrespondenceModelView<Correspondence> correspondenceModel,
        ResourceAccess resourceAccess) {
      atomicInvocations++;
    }
  }

  private static class AtomicOnlyChangePropagationSpecification
      extends AbstractChangePropagationSpecification {
    private int nonAtomicInvocations = 0;
    private int atomicInvocations = 0;

    AtomicOnlyChangePropagationSpecification(
        MetamodelDescriptor sourceDescriptor, MetamodelDescriptor targetDescriptor) {
      super(sourceDescriptor, targetDescriptor);
    }

    @Override
    public void propagateNonAtomicChange(
        VitruviusChange<EObject> change,
        EditableCorrespondenceModelView<Correspondence> correspondenceModel,
        ResourceAccess resourceAccess) {
      nonAtomicInvocations++;
    }

    @Override
    public void propagateChange(
        EChange<EObject> change,
        EditableCorrespondenceModelView<Correspondence> correspondenceModel,
        ResourceAccess resourceAccess) {
      atomicInvocations++;
    }
  }
}
