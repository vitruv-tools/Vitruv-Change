package tools.vitruv.change.atomic.feature;

import allElementTypes.AllElementTypesPackage;
import allElementTypes.Root;
import edu.kit.ipd.sdq.commons.util.org.eclipse.emf.ecore.resource.ResourceSetUtil;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import java.util.function.Consumer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.EChangeTest;
import tools.vitruv.change.atomic.uuid.Uuid;
import tools.vitruv.change.atomic.uuid.UuidResolver;
import tools.vitruv.change.testutils.metamodels.AllElementTypesCreators;

/**
 * Test class for {@link FeatureEChange} which is used by every {@link EChange} which modifies {@link EStructuralFeature}s
 * (single- or multi-valued attributes or references) of a {@link EObject}.
 */
public class FeatureEChangeTest extends EChangeTest {
  private Root affectedEObject;

  private EAttribute affectedFeature;

  private UuidResolver uuidResolver2;

  private Resource resource2;

  @BeforeEach
  public final void beforeTest() {
    this.affectedEObject = this.getRootObject();
    this.affectedFeature = AllElementTypesPackage.Literals.IDENTIFIED__ID;
    final ResourceSet resourceSet2 = ResourceSetUtil.withGlobalFactories(new ResourceSetImpl());
    this.resource2 = resourceSet2.getResource(this.getResource().getURI(), true);
    this.uuidResolver2 = UuidResolver.create(resourceSet2);
    final Consumer<EObject> _function = (EObject it) -> {
      this.uuidResolver2.registerEObject(it);
    };
    this.resource2.getAllContents().forEachRemaining(_function);
  }

  /**
   * Tests a failed resolve.
   */
  @Test
  public void resolveEFeatureChangeFails() {
    this.affectedEObject = this.prepareSecondRoot();
    Assertions.assertNull(this.resource2.getEObject(EcoreUtil.getURI(this.affectedEObject).fragment()));
    final EChange<Uuid> unresolvedChange = this.createUnresolvedChange();
    final Executable _function = () -> {
      this.applyForwardAndResolve(unresolvedChange);
    };
    Assertions.<IllegalStateException>assertThrows(IllegalStateException.class, _function);
  }

  /**
   * Test whether resolving the EFeatureChange fails by giving a null EObject
   */
  @Test
  public void resolveEFeatureAffectedObjectNull() {
    this.affectedEObject = null;
    final Executable _function = () -> {
      this.createUnresolvedChange();
    };
    Assertions.<IllegalArgumentException>assertThrows(IllegalArgumentException.class, _function);
  }

  /**
   * Tests whether creating the EFeatureChange fails by giving a null EFeature
   */
  @Test
  public void createEFeatureAffectedFeatureNull() {
    this.affectedFeature = null;
    final Executable _function = () -> {
      this.createUnresolvedChange();
    };
    Assertions.<IllegalArgumentException>assertThrows(IllegalArgumentException.class, _function);
  }

  /**
   * Creates and inserts a new root element in the resource 1.
   */
  private Root prepareSecondRoot() {
    final Root root = AllElementTypesCreators.aet.Root();
    this.getResource().getContents().add(root);
    return root;
  }

  /**
   * Creates new unresolved change.
   */
  private EChange<Uuid> createUnresolvedChange() {
    return this.unresolve(this.getAtomicFactory().<Root, Object>createReplaceSingleAttributeChange(this.affectedEObject, this.affectedFeature, null, null));
  }
}
