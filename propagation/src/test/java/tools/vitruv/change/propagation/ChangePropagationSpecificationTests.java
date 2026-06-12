package tools.vitruv.change.propagation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.junit.jupiter.api.Test;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.eobject.CreateEObject;
import tools.vitruv.change.atomic.eobject.DeleteEObject;
import tools.vitruv.change.atomic.eobject.EobjectFactory;
import tools.vitruv.change.atomic.eobject.EobjectPackage;
import tools.vitruv.change.atomic.feature.attribute.AttributeFactory;
import tools.vitruv.change.atomic.feature.attribute.AttributePackage;
import tools.vitruv.change.atomic.feature.attribute.ReplaceSingleValuedEAttribute;
import tools.vitruv.change.composite.MetamodelDescriptor;
import tools.vitruv.change.composite.description.AnnotationSource;
import tools.vitruv.change.correspondence.Correspondence;
import tools.vitruv.change.correspondence.view.EditableCorrespondenceModelView;
import tools.vitruv.change.interaction.UserInteractionFactory;
import tools.vitruv.change.interaction.impl.PredefinedInteractionResultProviderImpl;
import tools.vitruv.change.propagation.impl.AbstractChangePropagationSpecification;
import tools.vitruv.change.propagation.impl.CompositeChangePropagationSpecification;
import tools.vitruv.change.utils.ResourceAccess;

class ChangePropagationSpecificationTests {
  private MetamodelDescriptor descriptorA = MetamodelDescriptor.with("modelsA");
  private MetamodelDescriptor descriptorB = MetamodelDescriptor.with("modelsB");
  private MetamodelDescriptor descriptorD = MetamodelDescriptor.with("modelsD");

  private MockChangePropagationSpecification specAtoBOnAttributeChange =
      new MockChangePropagationSpecification(
      AttributePackage.eINSTANCE.getReplaceSingleValuedEAttribute(), descriptorA, descriptorB);

  private MockChangePropagationSpecification specAtoBOnElementAddChange =
      new MockChangePropagationSpecification(
      EobjectPackage.eINSTANCE.getCreateEObject(), descriptorA, descriptorB);

  private MockChangePropagationSpecification specBtoAOnElementDeleteChange =
      new MockChangePropagationSpecification(
      EobjectPackage.eINSTANCE.getDeleteEObject(), descriptorB, descriptorA);
  
  @Test
  void testRegistry() {
    var registry = new ChangePropagationSpecificationRepository(
        List.of(specAtoBOnAttributeChange,
          specAtoBOnElementAddChange, 
          specBtoAOnElementDeleteChange)
    );
 
    var resultsForDescriptorA = registry.getChangePropagationSpecifications(descriptorA);
    assertTrue(resultsForDescriptorA.contains(specAtoBOnAttributeChange));
    assertTrue(resultsForDescriptorA.contains(specAtoBOnElementAddChange));
    assertEquals(2, resultsForDescriptorA.size());

    var resultsForDescriptorB = registry.getChangePropagationSpecifications(descriptorB);
    assertTrue(resultsForDescriptorB.contains(specBtoAOnElementDeleteChange));

    var resultsForDescriptorD = registry.getChangePropagationSpecifications(descriptorD);
    assertTrue(resultsForDescriptorD.isEmpty());

    var allSpecifications = new HashSet<>();
    registry.iterator().forEachRemaining(allSpecifications::add);
    assertEquals(3, allSpecifications.size());
  }

  @Test
  void testCompositeChangePropagationSpecification() {
    var mockUserInteractor = UserInteractionFactory.instance.createUserInteractor(
      new PredefinedInteractionResultProviderImpl(null)
    );
    var compositeSpecificationForDescriptorsAAndB = 
        new CompositeChangePropagationSpecification(descriptorA, descriptorB);
    compositeSpecificationForDescriptorsAAndB.setUserInteractor(mockUserInteractor);
    compositeSpecificationForDescriptorsAAndB.addChangePreprocessor(specAtoBOnElementAddChange);
    compositeSpecificationForDescriptorsAAndB.addChangeMainprocessor(specAtoBOnAttributeChange);

    CreateEObject<EObject> change1 = EobjectFactory.eINSTANCE.createCreateEObject();
    ReplaceSingleValuedEAttribute<EObject, Integer> change2 =
        AttributeFactory.eINSTANCE.createReplaceSingleValuedEAttribute();

    // Change Handling
    assertTrue(specAtoBOnAttributeChange.doesHandleChange(change2, null));
    assertFalse(specAtoBOnAttributeChange.doesHandleChange(change1, null));
    assertTrue(specAtoBOnElementAddChange.doesHandleChange(change1, null));
    assertFalse(specAtoBOnElementAddChange.doesHandleChange(change2, null));

    DeleteEObject<EObject> change3 = EobjectFactory.eINSTANCE.createDeleteEObject();
    assertTrue(compositeSpecificationForDescriptorsAAndB.doesHandleChange(change1, null));
    assertTrue(compositeSpecificationForDescriptorsAAndB.doesHandleChange(change2, null));
    assertFalse(compositeSpecificationForDescriptorsAAndB.doesHandleChange(change3, null));
  
    // Notifier tests; expect 2 events/one per CPS
    var mockObserver = new MockObserver();
    compositeSpecificationForDescriptorsAAndB.registerObserver(mockObserver);
    compositeSpecificationForDescriptorsAAndB.propagateChange(change1, null, null);
    assertEquals(2, mockObserver.cpsStarts);
    assertEquals(2, mockObserver.cpsEnds);
    assertEquals(2, mockObserver.objectsCreated);

    // Deregister; expect no changes
    compositeSpecificationForDescriptorsAAndB.deregisterObserver(mockObserver);
    compositeSpecificationForDescriptorsAAndB.propagateChange(change1, null, null);
    assertEquals(2, mockObserver.cpsEnds);
  }

  @Test
  void annotationsForwardedThroughCompositeSpec() {
    var mockUserInteractor = UserInteractionFactory.instance.createUserInteractor(
        new PredefinedInteractionResultProviderImpl(null));
    var recordingPreprocessor = new AnnotationRecordingSpec(descriptorA, descriptorB);
    var recordingMainprocessor = new AnnotationRecordingSpec(descriptorA, descriptorB);
    var composite = new CompositeChangePropagationSpecification(descriptorA, descriptorB);
    composite.setUserInteractor(mockUserInteractor);
    composite.addChangePreprocessor(recordingPreprocessor);
    composite.addChangeMainprocessor(recordingMainprocessor);

    record Tag(String value) {}
    var tag = new Tag("hello");
    // AnnotationSource.of() is needed because the generic SAM prevents a direct lambda assignment.
    AnnotationSource annotations = AnnotationSource.of(type -> type == Tag.class ? Optional.of(tag) : Optional.empty());

    CreateEObject<EObject> change = EobjectFactory.eINSTANCE.createCreateEObject();
    composite.propagateChange(change, annotations, null, null);

    assertSame(tag, recordingPreprocessor.lastAnnotations.getAnnotation(Tag.class).orElseThrow());
    assertSame(tag, recordingMainprocessor.lastAnnotations.getAnnotation(Tag.class).orElseThrow());
  }

  @Test
  void defaultPropagateOverloadUsesEmptyAnnotations() {
    var recordingSpec = new AnnotationRecordingSpec(descriptorA, descriptorB);
    CreateEObject<EObject> change = EobjectFactory.eINSTANCE.createCreateEObject();
    recordingSpec.propagateChange(change, null, null);
    assertTrue(recordingSpec.lastAnnotations.getAnnotation(Object.class).isEmpty());
  }

  @Test
  void handleIncompatibleSubprocessors() {
    var compositeSpecificationForDescriptorsBAndA =
        new CompositeChangePropagationSpecification(descriptorB, descriptorA);
    assertThrows(IllegalArgumentException.class,
        () -> compositeSpecificationForDescriptorsBAndA
        .addChangePreprocessor(specAtoBOnAttributeChange));
    assertThrows(IllegalArgumentException.class,
        () -> compositeSpecificationForDescriptorsBAndA
        .addChangeMainprocessor(specAtoBOnAttributeChange));
  }

  private static class MockObserver implements ChangePropagationObserver {
    private int cpsStarts = 0;
    private int cpsEnds = 0;
    private int objectsCreated = 0;
   
    @Override
    public void changePropagationStarted(ChangePropagationSpecification specification,
        EChange<EObject> change) {
      cpsStarts++;
    }
  
    @Override
    public void changePropagationStopped(ChangePropagationSpecification specification, 
        EChange<EObject> change) {
      cpsEnds++;
    }

    @Override
    public void objectCreated(EObject createdObject) {
      objectsCreated++;      
    }
  }

  /** Records the {@link AnnotationSource} passed to the annotated propagateChange overload. */
  private static class AnnotationRecordingSpec extends AbstractChangePropagationSpecification {
    AnnotationSource lastAnnotations;

    private AnnotationRecordingSpec(
        MetamodelDescriptor sourceDescriptor, MetamodelDescriptor targetDescriptor) {
      super(sourceDescriptor, targetDescriptor);
    }

    @Override
    public boolean doesHandleChange(EChange<EObject> change,
        EditableCorrespondenceModelView<Correspondence> correspondenceModel) {
      return true;
    }

    @Override
    public void propagateChange(EChange<EObject> change,
        EditableCorrespondenceModelView<Correspondence> correspondenceModel,
        ResourceAccess resourceAccess) {
      lastAnnotations = AnnotationSource.EMPTY;
    }

    @Override
    public void propagateChange(EChange<EObject> change,
        AnnotationSource changeAnnotations,
        EditableCorrespondenceModelView<Correspondence> correspondenceModel,
        ResourceAccess resourceAccess) {
      lastAnnotations = changeAnnotations;
    }
  }

  /**
   * A mock change propagation specification.
   * It "handles" changes for a given change type (a concrete subclass of {@link EChange})
   * by doing nothing, expect calling notifiers.
   */
  private static class MockChangePropagationSpecification extends
      AbstractChangePropagationSpecification {
    /**
     * Change type to handle.
     */
    private final EClass eChangeType;
  
    private MockChangePropagationSpecification(EClass eChangeType,
        MetamodelDescriptor sourceDescriptor, MetamodelDescriptor targetDescriptor) {
      super(sourceDescriptor, targetDescriptor);
      this.eChangeType = eChangeType;
    }

    @Override
    public boolean doesHandleChange(EChange<EObject> change,
        EditableCorrespondenceModelView<Correspondence> correspondenceModel) {
      return change.eClass().equals(eChangeType);
    }

    @Override
    public void propagateChange(EChange<EObject> change,
        EditableCorrespondenceModelView<Correspondence> correspondenceModel,
        ResourceAccess resourceAccess) {
      notifyChangePropagationStarted(this, change);
      notifyObjectCreated(change);
      notifyChangePropagationStopped(this, change);
      return;
    }
  }
}
