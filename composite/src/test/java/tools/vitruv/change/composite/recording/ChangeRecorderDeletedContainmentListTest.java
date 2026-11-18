package tools.vitruv.change.composite.recording;

import static org.junit.jupiter.api.Assertions.assertEquals;

import edu.kit.ipd.sdq.commons.util.org.eclipse.emf.ecore.resource.ResourceSetUtil;
import java.util.ArrayList;
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
import tools.vitruv.change.atomic.feature.reference.RemoveEReference;
import tools.vitruv.change.composite.description.TransactionalChange;

/**
 * When a recorded element is deleted, the recorder removes everything it contains from its
 * container before deleting it, last element first. Each removal has the index of the element in
 * its containment list, which is not modified while the delete changes are created.
 */
class ChangeRecorderDeletedContainmentListTest {
  private static final EcoreFactory ECORE = EcoreFactory.eINSTANCE;

  private static final URI RESOURCE_URI = URI.createURI("test://test.nodes");

  private final EClass nodeClass = ECORE.createEClass();

  private final EReference children = ECORE.createEReference();

  private final EReference single = ECORE.createEReference();

  private final ResourceSet resourceSet =
      ResourceSetUtil.withGlobalFactories(new ResourceSetImpl());

  private final ChangeRecorder changeRecorder = new ChangeRecorder(resourceSet);

  ChangeRecorderDeletedContainmentListTest() {
    children.setName("children");
    children.setEType(nodeClass);
    children.setUpperBound(EStructuralFeature.UNBOUNDED_MULTIPLICITY);
    children.setContainment(true);
    single.setName("single");
    single.setEType(nodeClass);
    single.setContainment(true);
    nodeClass.setName("Node");
    nodeClass.getEStructuralFeatures().addAll(List.of(children, single));
    final EPackage nodePackage = ECORE.createEPackage();
    nodePackage.setName("nodes");
    nodePackage.setNsURI("http://vitruv.tools/test/deletedContainmentList");
    nodePackage.setNsPrefix("nodes");
    nodePackage.getEClassifiers().add(nodeClass);
  }

  @AfterEach
  void closeRecorder() {
    changeRecorder.close();
  }

  @Test
  @DisplayName("removes the contents of a deleted element at their positions, last one first")
  void removesContentsOfDeletedElementAtTheirPositions() {
    final EObject root = EcoreUtil.create(nodeClass);
    final EObject deleted = createChild(root);
    final List<EObject> deletedChildren = new ArrayList<>();
    final List<EObject> lastGrandchildren = new ArrayList<>();
    for (int i = 0; i < 5; i++) {
      final EObject child = createChild(deleted);
      deletedChildren.add(child);
      lastGrandchildren.clear();
      for (int j = 0; j < 3; j++) {
        lastGrandchildren.add(createChild(child));
      }
      child.eSet(single, EcoreUtil.create(nodeClass));
    }

    final TransactionalChange<EObject> change = recordDeletionOf(root, deleted);

    final List<RemoveEReference<EObject>> childRemovals = removalsFrom(change, deleted);
    assertEquals(deletedChildren.reversed(),
        childRemovals.stream().map(RemoveEReference::getOldValue).toList());
    assertEquals(List.of(4, 3, 2, 1, 0),
        childRemovals.stream().map(RemoveEReference::getIndex).toList());

    final List<RemoveEReference<EObject>> grandchildRemovals =
        removalsFrom(change, deletedChildren.getLast());
    assertEquals(lastGrandchildren.reversed(),
        grandchildRemovals.stream().map(RemoveEReference::getOldValue).toList());
    assertEquals(List.of(2, 1, 0),
        grandchildRemovals.stream().map(RemoveEReference::getIndex).toList());
  }

  @Test
  @DisplayName("removes the contents of several deleted elements at their positions")
  void removesContentsOfSeveralDeletedElementsAtTheirPositions() {
    final EObject root = EcoreUtil.create(nodeClass);
    final EObject first = createChild(root);
    final EObject second = createChild(root);
    final List<EObject> firstChildren = List.of(createChild(first), createChild(first));
    final List<EObject> secondChildren =
        List.of(createChild(second), createChild(second), createChild(second));

    final Resource resource = resourceSet.createResource(RESOURCE_URI);
    resource.getContents().add(root);
    changeRecorder.addToRecording(resource);
    changeRecorder.beginRecording();
    ((List<?>) root.eGet(children)).clear();
    final TransactionalChange<EObject> change = changeRecorder.endRecording();

    assertEquals(List.of(1, 0),
        removalsFrom(change, first).stream().map(RemoveEReference::getIndex).toList());
    assertEquals(firstChildren.reversed(),
        removalsFrom(change, first).stream().map(RemoveEReference::getOldValue).toList());
    assertEquals(List.of(2, 1, 0),
        removalsFrom(change, second).stream().map(RemoveEReference::getIndex).toList());
    assertEquals(secondChildren.reversed(),
        removalsFrom(change, second).stream().map(RemoveEReference::getOldValue).toList());
  }

  private EObject createChild(final EObject parent) {
    final EObject child = EcoreUtil.create(nodeClass);
    ((List<EObject>) parent.eGet(children)).add(child);
    return child;
  }

  private TransactionalChange<EObject> recordDeletionOf(final EObject root, final EObject node) {
    final Resource resource = resourceSet.createResource(RESOURCE_URI);
    resource.getContents().add(root);
    changeRecorder.addToRecording(resource);
    changeRecorder.beginRecording();
    EcoreUtil.remove(node);
    return changeRecorder.endRecording();
  }

  private List<RemoveEReference<EObject>> removalsFrom(
      final TransactionalChange<EObject> change, final EObject container) {
    return change.getEChanges().stream()
        .filter(RemoveEReference.class::isInstance)
        .map(it -> (RemoveEReference<EObject>) it)
        .filter(it -> it.getAffectedElement() == container
            && it.getAffectedFeature() == children)
        .toList();
  }
}
