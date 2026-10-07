package tools.vitruv.change.composite.recording;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;

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
import tools.vitruv.change.atomic.feature.FeatureEChange;
import tools.vitruv.change.atomic.hid.HierarchicalId;
import tools.vitruv.change.composite.description.TransactionalChange;
import tools.vitruv.change.composite.description.VitruviusChange;
import tools.vitruv.change.composite.description.VitruviusChangeResolverFactory;

/**
 * A created object's ordinary reference may point to the object's container, e.g., a SysML v2
 * {@code FeatureTyping.typedFeature} refers to its owner. Only the container reference itself
 * (the opposite of the containment) is implied by the containment and must not be recorded.
 */
class ChangeRecorderContainerValuedReferenceTest {
  private static final EcoreFactory ECORE = EcoreFactory.eINSTANCE;

  private static final URI RESOURCE_URI = URI.createURI("test://test.nodes");

  private final EClass nodeClass = ECORE.createEClass();

  private final EReference children = ECORE.createEReference();

  private final EReference parent = ECORE.createEReference();

  private final EReference subject = ECORE.createEReference();

  private final ResourceSet resourceSet =
      ResourceSetUtil.withGlobalFactories(new ResourceSetImpl());

  private final ChangeRecorder changeRecorder = new ChangeRecorder(resourceSet);

  ChangeRecorderContainerValuedReferenceTest() {
    children.setName("children");
    children.setEType(nodeClass);
    children.setUpperBound(EStructuralFeature.UNBOUNDED_MULTIPLICITY);
    children.setContainment(true);
    parent.setName("parent");
    parent.setEType(nodeClass);
    children.setEOpposite(parent);
    parent.setEOpposite(children);
    subject.setName("subject");
    subject.setEType(nodeClass);
    nodeClass.setName("Node");
    nodeClass.getEStructuralFeatures().addAll(List.of(children, parent, subject));
    final EPackage nodePackage = ECORE.createEPackage();
    nodePackage.setName("nodes");
    nodePackage.setNsURI("http://vitruv.tools/test/containerValuedReference");
    nodePackage.setNsPrefix("nodes");
    nodePackage.getEClassifiers().add(nodeClass);
  }

  @AfterEach
  void closeRecorder() {
    changeRecorder.close();
  }

  @Test
  @DisplayName("records a reference of a created object whose value is the object's container")
  void recordsReferenceToContainerOfCreatedObject() {
    final List<EStructuralFeature> recordedFeatures = recordedFeatures(recordInsertedNode());

    assertEquals(1, recordedFeatures.stream().filter(subject::equals).count());
  }

  @Test
  @DisplayName("does not record the container reference of a created object")
  void doesNotRecordContainerReferenceOfCreatedObject() {
    final List<EStructuralFeature> recordedFeatures = recordedFeatures(recordInsertedNode());

    assertFalse(recordedFeatures.contains(parent), "container reference was recorded");
  }

  @Test
  @DisplayName("records the children of an object contained in a reference it declares itself")
  void recordsChildrenOfObjectContainedInReferenceItDeclares() {
    final List<EStructuralFeature> recordedFeatures = recordedFeatures(recordInsertedNode());

    // the child is contained in children and has children itself
    assertEquals(2, recordedFeatures.stream().filter(children::equals).count());
  }

  @Test
  @DisplayName("replays a reference of a created object whose value is the object's container")
  void replaysReferenceToContainerOfCreatedObject() {
    final VitruviusChange<HierarchicalId> change = VitruviusChangeResolverFactory
        .forHierarchicalIds(resourceSet).assignIds(recordInsertedNode());
    final ResourceSet replayResourceSet =
        ResourceSetUtil.withGlobalFactories(new ResourceSetImpl());
    replayResourceSet.createResource(RESOURCE_URI);

    VitruviusChangeResolverFactory.forHierarchicalIds(replayResourceSet).resolveAndApply(change);

    final EObject replayedNode = replayResourceSet.getResource(RESOURCE_URI, false)
        .getContents().get(0);
    final EObject replayedChild = ((List<EObject>) replayedNode.eGet(children)).get(0);
    assertSame(replayedNode, replayedChild.eGet(subject));
  }

  private TransactionalChange<EObject> recordInsertedNode() {
    final Resource resource = resourceSet.createResource(RESOURCE_URI);
    final EObject node = EcoreUtil.create(nodeClass);
    final EObject child = EcoreUtil.create(nodeClass);
    final EObject grandchild = EcoreUtil.create(nodeClass);
    ((List<EObject>) node.eGet(children)).add(child);
    ((List<EObject>) child.eGet(children)).add(grandchild);
    child.eSet(subject, node);

    changeRecorder.addToRecording(resource);
    changeRecorder.beginRecording();
    resource.getContents().add(node);
    return changeRecorder.endRecording();
  }

  private static List<EStructuralFeature> recordedFeatures(
      final TransactionalChange<EObject> change) {
    return change.getEChanges().stream()
        .filter(FeatureEChange.class::isInstance)
        .map(it -> ((FeatureEChange<?, ?>) it).getAffectedFeature())
        .map(EStructuralFeature.class::cast)
        .toList();
  }
}
