package tools.vitruv.change.correspondence.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.vitruv.change.correspondence.Correspondence;
import tools.vitruv.change.correspondence.CorrespondenceFactory;
import tools.vitruv.change.correspondence.Correspondences;

/** Tests for {@link CorrespondenceIndex}. */
class CorrespondenceIndexTest {
  private final EObject a = EcoreFactory.eINSTANCE.createEObject();
  private final EObject b = EcoreFactory.eINSTANCE.createEObject();
  private final EObject c = EcoreFactory.eINSTANCE.createEObject();

  private Correspondences correspondences;
  private CorrespondenceIndex index;

  @BeforeEach
  void createIndex() {
    correspondences = CorrespondenceFactory.eINSTANCE.createCorrespondences();
    index = new CorrespondenceIndex(correspondences);
  }

  private static Correspondence correspondence(List<EObject> left, List<EObject> right) {
    Correspondence correspondence = CorrespondenceFactory.eINSTANCE.createManualCorrespondence();
    correspondence.getLeftEObjects().addAll(left);
    correspondence.getRightEObjects().addAll(right);
    return correspondence;
  }

  @Test
  void findsCorrespondencesByEitherSide_onlyForExactlyTheseObjectsInThisOrder() {
    Correspondence ab = correspondence(List.of(a), List.of(b));
    Correspondence acToB = correspondence(List.of(a, c), List.of(b));
    correspondences.getCorrespondences().addAll(List.of(ab, acToB));

    assertEquals(Set.of(ab), index.getCorrespondences(List.of(a)));
    assertEquals(Set.of(ab, acToB), index.getCorrespondences(List.of(b)));
    assertEquals(Set.of(acToB), index.getCorrespondences(List.of(a, c)));
    assertTrue(index.getCorrespondences(List.of(c, a)).isEmpty());
    assertTrue(index.getCorrespondences(List.of(c)).isEmpty());
  }

  @Test
  void indexesCorrespondencesThatAreAlreadyInTheContainer() {
    Correspondences filled = CorrespondenceFactory.eINSTANCE.createCorrespondences();
    Correspondence ab = correspondence(List.of(a), List.of(b));
    filled.getCorrespondences().add(ab);

    CorrespondenceIndex indexOfFilled = new CorrespondenceIndex(filled);

    assertEquals(Set.of(ab), indexOfFilled.getCorrespondences(List.of(a)));
    assertEquals(Set.of(ab), indexOfFilled.getCorrespondences(List.of(b)));
  }

  @Test
  void followsCorrespondencesThatAreRemoved() {
    Correspondence ab = correspondence(List.of(a), List.of(b));
    Correspondence ac = correspondence(List.of(a), List.of(c));
    Correspondence bc = correspondence(List.of(b), List.of(c));
    correspondences.getCorrespondences().addAll(List.of(ab, ac, bc));

    EcoreUtil.remove(ab);
    assertEquals(Set.of(ac), index.getCorrespondences(List.of(a)));
    assertEquals(Set.of(bc), index.getCorrespondences(List.of(b)));

    correspondences.getCorrespondences().clear();
    assertTrue(index.getCorrespondences(List.of(a)).isEmpty());
    assertTrue(index.getCorrespondences(List.of(c)).isEmpty());
  }

  @Test
  void followsCorrespondencesThatAreMovedToAnotherContainer() {
    Correspondence ab = correspondence(List.of(a), List.of(b));
    correspondences.getCorrespondences().add(ab);

    CorrespondenceFactory.eINSTANCE.createCorrespondences().getCorrespondences().add(ab);

    assertTrue(index.getCorrespondences(List.of(a)).isEmpty());
  }

  @Test
  void followsChangesToTheSidesOfACorrespondence() {
    Correspondence ab = correspondence(List.of(a), List.of(b));
    correspondences.getCorrespondences().add(ab);

    ab.getRightEObjects().set(0, c);
    assertTrue(index.getCorrespondences(List.of(b)).isEmpty());
    assertEquals(Set.of(ab), index.getCorrespondences(List.of(c)));

    ab.getLeftEObjects().add(b);
    assertTrue(index.getCorrespondences(List.of(a)).isEmpty());
    assertEquals(Set.of(ab), index.getCorrespondences(List.of(a, b)));
  }

  @Test
  void keepsACorrespondenceWithTheSameObjectsOnBothSides() {
    Correspondence aa = correspondence(List.of(a), List.of(a));
    correspondences.getCorrespondences().add(aa);
    assertEquals(Set.of(aa), index.getCorrespondences(List.of(a)));

    correspondences.getCorrespondences().remove(aa);
    assertTrue(index.getCorrespondences(List.of(a)).isEmpty());
  }

  @Test
  void keepsCorrespondencesThatAreMovedWithinTheContainer() {
    Correspondence ab = correspondence(List.of(a), List.of(b));
    Correspondence ac = correspondence(List.of(a), List.of(c));
    correspondences.getCorrespondences().addAll(List.of(ab, ac));

    correspondences.getCorrespondences().move(0, 1);

    assertEquals(Set.of(ab, ac), index.getCorrespondences(List.of(a)));
  }
}
