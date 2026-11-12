package tools.vitruv.change.composite.recording;

import static org.junit.jupiter.api.Assertions.assertEquals;

import edu.kit.ipd.sdq.commons.util.org.eclipse.emf.ecore.resource.ResourceSetUtil;
import java.util.List;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tools.vitruv.change.atomic.feature.reference.InsertEReference;
import tools.vitruv.change.composite.description.TransactionalChange;

/**
 * An object can already have values in its many-valued references when it is inserted, e.g., if
 * it was built while detached. The recorder then inserts each value at its position in the list.
 */
class ChangeRecorderInsertedReferenceListTest {
  private static final EcoreFactory ECORE = EcoreFactory.eINSTANCE;

  private static final URI RESOURCE_URI = URI.createURI("test://test.nodes");

  private final EClass nodeClass = ECORE.createEClass();

  private final EReference children = ECORE.createEReference();

  private final EReference targets = ECORE.createEReference();

  private final ResourceSet resourceSet =
      ResourceSetUtil.withGlobalFactories(new ResourceSetImpl());

  private final ChangeRecorder changeRecorder = new ChangeRecorder(resourceSet);

  ChangeRecorderInsertedReferenceListTest() {
    children.setName("children");
    children.setEType(nodeClass);
    children.setUpperBound(EStructuralFeature.UNBOUNDED_MULTIPLICITY);
    children.setContainment(true);
    targets.setName("targets");
    targets.setEType(nodeClass);
    targets.setUpperBound(EStructuralFeature.UNBOUNDED_MULTIPLICITY);
    targets.setUnique(false);
    nodeClass.setName("Node");
    nodeClass.getEStructuralFeatures().addAll(List.of(children, targets));
    final EPackage nodePackage = ECORE.createEPackage();
    nodePackage.setName("nodes");
    nodePackage.setNsURI("http://vitruv.tools/test/insertedReferenceList");
    nodePackage.setNsPrefix("nodes");
    nodePackage.getEClassifiers().add(nodeClass);
  }

  @AfterEach
  void closeRecorder() {
    changeRecorder.close();
  }

  @Test
  @DisplayName("records the contents of an inserted object at their positions")
  void recordsContentsOfInsertedObjectAtTheirPositions() {
    final EObject node = EcoreUtil.create(nodeClass);
    final List<EObject> nodeChildren = (List<EObject>) node.eGet(children);
    for (int i = 0; i < 5; i++) {
      nodeChildren.add(EcoreUtil.create(nodeClass));
    }

    final List<InsertEReference<EObject>> inserts =
        recordedInserts(recordInsertionOf(node), children);

    assertEquals(nodeChildren, inserts.stream().map(InsertEReference::getNewValue).toList());
    assertEquals(List.of(0, 1, 2, 3, 4),
        inserts.stream().map(InsertEReference::getIndex).toList());
  }

  @Test
  @DisplayName("records a value occurring twice in a non-unique list of an inserted object twice")
  void recordsValueOccurringTwiceInNonUniqueListAtBothPositions() {
    final EObject node = EcoreUtil.create(nodeClass);
    final EObject first = EcoreUtil.create(nodeClass);
    final EObject second = EcoreUtil.create(nodeClass);
    ((List<EObject>) node.eGet(children)).addAll(List.of(first, second));
    ((List<EObject>) node.eGet(targets)).addAll(List.of(first, second, first));

    final List<InsertEReference<EObject>> inserts =
        recordedInserts(recordInsertionOf(node), targets);

    assertEquals(List.of(first, second, first),
        inserts.stream().map(InsertEReference::getNewValue).toList());
    assertEquals(List.of(0, 1, 2), inserts.stream().map(InsertEReference::getIndex).toList());
  }

  private TransactionalChange<EObject> recordInsertionOf(final EObject node) {
    final Resource resource = resourceSet.createResource(RESOURCE_URI);
    changeRecorder.addToRecording(resource);
    changeRecorder.beginRecording();
    resource.getContents().add(node);
    return changeRecorder.endRecording();
  }

  private static List<InsertEReference<EObject>> recordedInserts(
      final TransactionalChange<EObject> change, final EReference reference) {
    return change.getEChanges().stream()
        .filter(InsertEReference.class::isInstance)
        .map(it -> (InsertEReference<EObject>) it)
        .filter(it -> it.getAffectedFeature() == reference)
        .toList();
  }
}
