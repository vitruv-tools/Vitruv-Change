package tools.vitruv.change.composite.description;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import allElementTypes.NonRoot;
import allElementTypes.Root;
import java.util.List;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.composite.ChangeDescription2ChangeTransformationTest;
import tools.vitruv.change.testutils.metamodels.AllElementTypesCreators;

/**
 * Tests for {@link TransactionalChange} implementations.
 */
public class TransactionalChangeImplTest extends ChangeDescription2ChangeTransformationTest {
  private static final VitruviusChangeFactory FACTORY = VitruviusChangeFactory.getInstance();

  @Test
  @DisplayName("recorded creation change exposes affected and referenced objects")
  public void recordedCreationChangeExposesAffectedObjects() {
    final Resource resource = this.resourceAt("test");
    final Root root = AllElementTypesCreators.aet.Root();
    final TransactionalChange<EObject> change = this.<Resource>recordComposite(resource, it -> {
      final NonRoot contained = AllElementTypesCreators.aet.NonRoot();
      contained.setId("contained");
      final NonRoot referenced = AllElementTypesCreators.aet.NonRoot();
      referenced.setId("referenced");
      root.setId("root");
      root.setSingleValuedEAttribute(42);
      root.getMultiValuedContainmentEReference().add(contained);
      root.setSingleValuedContainmentEReference(referenced);
      it.getContents().add(root);
    });

    assertTrue(change.containsConcreteChange());
    assertFalse(change.getEChanges().isEmpty());
    assertFalse(change.getAffectedEObjects().isEmpty());
    assertFalse(change.getAffectedAndReferencedEObjects().isEmpty());
    assertFalse(change.getChangedURIs().isEmpty());
    assertNotNull(change.getAffectedEObjectsMetamodelDescriptor());

    final String representation = change.toString();
    assertTrue(representation.startsWith("TransactionalChangeImpl"));
    assertFalse(representation.contains("(empty)"));
  }

  @Test
  @DisplayName("copy creates an independent change with the same number of changes")
  public void copyCreatesIndependentChange() {
    final Resource resource = this.resourceAt("test");
    final Root root = AllElementTypesCreators.aet.Root();
    final TransactionalChange<EObject> change = this.<Resource>recordComposite(resource, it -> {
      root.setId("root");
      it.getContents().add(root);
    });

    final TransactionalChange<EObject> copy = change.copy();
    assertFalse(copy.getEChanges().isEmpty());
    assertEquals(change.getEChanges().size(), copy.getEChanges().size());
  }

  @Test
  @DisplayName("multi-valued reference and attribute insertions are represented")
  public void multiValuedInsertionsAreRepresented() {
    final NonRoot nonRoot = AllElementTypesCreators.aet.NonRoot();
    final Root root = this.getUniquePersistedRoot();
    final TransactionalChange<EObject> change = this.<Root>recordComposite(root, it -> {
      it.getMultiValuedContainmentEReference().add(nonRoot);
      it.getMultiValuedEAttribute().add(42);
    });

    assertTrue(change.containsConcreteChange());
    assertFalse(change.getAffectedAndReferencedEObjects().isEmpty());
    assertTrue(change.toString().contains("+="));
  }

  @Test
  @DisplayName("removal of a contained element is represented")
  public void removalOfContainedElementIsRepresented() {
    final NonRoot nonRoot = AllElementTypesCreators.aet.NonRoot();
    final Root root = this.getUniquePersistedRoot();
    root.getMultiValuedContainmentEReference().add(nonRoot);
    final TransactionalChange<EObject> change = this.<Root>recordComposite(root,
        it -> it.getMultiValuedContainmentEReference().remove(nonRoot));

    assertFalse(change.getAffectedEObjects().isEmpty());
    assertFalse(change.getAffectedAndReferencedEObjects().isEmpty());
    assertTrue(change.toString().contains("-="));
  }

  @Test
  @DisplayName("an empty change reports no concrete changes")
  public void emptyChangeReportsNoConcreteChanges() {
    final TransactionalChange<EObject> empty = FACTORY.createTransactionalChange(List.of());

    assertFalse(empty.containsConcreteChange());
    assertTrue(empty.getEChanges().isEmpty());
    assertTrue(empty.getAffectedEObjects().isEmpty());
    assertTrue(empty.getAffectedAndReferencedEObjects().isEmpty());
    assertTrue(empty.getChangedURIs().isEmpty());
    assertTrue(empty.toString().contains("(empty)"));
    assertTrue(empty.copy().getEChanges().isEmpty());
  }

  @Test
  @DisplayName("equals and hashCode are based on the contained changes")
  public void equalsAndHashCodeConsiderContainedChanges() {
    final Resource resource = this.resourceAt("test");
    final Root root = AllElementTypesCreators.aet.Root();
    final TransactionalChange<EObject> recorded = this.<Resource>recordComposite(resource, it -> {
      root.setId("root");
      it.getContents().add(root);
    });

    final List<EChange<EObject>> changes = recorded.getEChanges();
    final TransactionalChange<EObject> first = FACTORY.createTransactionalChange(changes);
    final TransactionalChange<EObject> second = FACTORY.createTransactionalChange(changes);
    final TransactionalChange<EObject> empty = FACTORY.createTransactionalChange(List.of());

    assertEquals(first, first);
    assertEquals(first, second);
    assertEquals(first.hashCode(), second.hashCode());
    assertNotEquals(first, empty);
    assertNotEquals(first, "not a transactional change");
  }
}
