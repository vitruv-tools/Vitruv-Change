package tools.vitruv.change.atomic.resolve;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Tests for {@link ElementRemovalTracker}. */
class ElementRemovalTrackerTest {
  private ResourceSet resourceSet;
  private Resource resource;
  private EPackage root;
  private EClass child;
  private EClass otherChild;
  private ElementRemovalTracker tracker;

  @BeforeEach
  void setup() {
    resourceSet = new ResourceSetImpl();
    resource = new ResourceImpl(URI.createURI("test:/model.ecore"));
    resourceSet.getResources().add(resource);
    root = EcoreFactory.eINSTANCE.createEPackage();
    child = EcoreFactory.eINSTANCE.createEClass();
    otherChild = EcoreFactory.eINSTANCE.createEClass();
    root.getEClassifiers().add(child);
    root.getEClassifiers().add(otherChild);
    resource.getContents().add(root);
    tracker = new ElementRemovalTracker(resourceSet);
    tracker.endTransaction();
  }

  @Test
  void returnsTouchedElements_butNoUntouchedOnesThatAreStillInTheResourceSet() {
    tracker.touch(child);

    assertEquals(Set.of(child), tracker.getElementsThatMayHaveLeft());
  }

  @Test
  void returnsTheContentsOfATouchedElementThatLeftTheResourceSet() {
    resource.getContents().remove(root);
    tracker.touch(root);

    assertEquals(Set.of(root, child, otherChild), tracker.getElementsThatMayHaveLeft());
  }

  @Test
  void returnsRootsRemovedFromTheirResource_andTheirContents_withoutBeingTouched() {
    resource.getContents().clear();

    assertEquals(Set.of(root, child, otherChild), tracker.getElementsThatMayHaveLeft());
  }

  @Test
  void returnsTheRootsOfAResourceRemovedFromTheResourceSet() {
    resourceSet.getResources().remove(resource);

    assertEquals(Set.of(root, child, otherChild), tracker.getElementsThatMayHaveLeft());
  }

  @Test
  void returnsARootThatMovedToAnotherResource_withoutItsContents() {
    Resource otherResource = new ResourceImpl(URI.createURI("test:/other.ecore"));
    resourceSet.getResources().add(otherResource);
    otherResource.getContents().add(root);

    assertEquals(Set.of(root), tracker.getElementsThatMayHaveLeft());
  }

  @Test
  void endingATransaction_forgetsTouchedElements_andRemembersTheCurrentRoots() {
    tracker.touch(child);
    tracker.endTransaction();
    assertTrue(tracker.getElementsThatMayHaveLeft().isEmpty());

    EPackage newRoot = EcoreFactory.eINSTANCE.createEPackage();
    resource.getContents().add(newRoot);
    tracker.endTransaction();
    resource.getContents().remove(newRoot);

    assertEquals(Set.of(newRoot), tracker.getElementsThatMayHaveLeft());
  }
}
