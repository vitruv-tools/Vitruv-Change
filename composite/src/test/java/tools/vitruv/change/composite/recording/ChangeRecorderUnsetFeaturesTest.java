package tools.vitruv.change.composite.recording;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import edu.kit.ipd.sdq.commons.util.org.eclipse.emf.ecore.resource.ResourceSetUtil;
import java.util.List;
import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.impl.DynamicEObjectImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tools.vitruv.change.atomic.feature.FeatureEChange;
import tools.vitruv.change.composite.description.TransactionalChange;
import tools.vitruv.change.composite.description.VitruviusChangeResolverFactory;

/**
 * Some metamodels compute the value of a stored (changeable, non-derived, non-transient) feature
 * from other features and report it as not set, e.g., subsetted unions and redefinitions in the
 * SysML v2 pilot implementation. Such a feature carries no data of its own, so recording an
 * object created with values must not record it, as EMF's own copy and serialization do not.
 */
class ChangeRecorderUnsetFeaturesTest {
  private static final EcoreFactory ECORE = EcoreFactory.eINSTANCE;

  private final EClass nodeClass = ECORE.createEClass();

  private final EAttribute name = attribute("name");

  private final EAttribute computedLabel = attribute("computedLabel");

  private final EReference children = reference("children", true);

  private final EReference computedNeighbors = reference("computedNeighbors", false);

  private final ResourceSet resourceSet =
      ResourceSetUtil.withGlobalFactories(new ResourceSetImpl());

  private final ChangeRecorder changeRecorder = new ChangeRecorder(resourceSet);

  ChangeRecorderUnsetFeaturesTest() {
    nodeClass.setName("Node");
    nodeClass.getEStructuralFeatures()
        .addAll(List.of(name, computedLabel, children, computedNeighbors));
    final EPackage nodePackage = ECORE.createEPackage();
    nodePackage.setName("nodes");
    nodePackage.setNsURI("http://vitruv.tools/test/unsetFeatures");
    nodePackage.setNsPrefix("nodes");
    nodePackage.getEClassifiers().add(nodeClass);
  }

  @AfterEach
  void closeRecorder() {
    changeRecorder.close();
  }

  @Test
  @DisplayName("does not record features reported as unset on a created object")
  void doesNotRecordUnsetFeaturesOfCreatedObject() {
    final List<EStructuralFeature> recordedFeatures = recordedFeatures(recordInsertedNode());

    assertFalse(recordedFeatures.contains(computedLabel), "computed label was recorded");
    assertFalse(recordedFeatures.contains(computedNeighbors), "computed neighbors were recorded");
  }

  @Test
  @DisplayName("records the set features of a created object")
  void recordsSetFeaturesOfCreatedObject() {
    final List<EStructuralFeature> recordedFeatures = recordedFeatures(recordInsertedNode());

    assertEquals(2, recordedFeatures.stream().filter(name::equals).count(), "names");
    assertEquals(1, recordedFeatures.stream().filter(children::equals).count(), "children");
  }

  @Test
  @DisplayName("replays a created object with features reported as unset")
  void replaysCreatedObjectWithUnsetFeatures() {
    final TransactionalChange<EObject> change = recordInsertedNode();

    // assigning hierarchical IDs applies the change backward and forward again
    assertDoesNotThrow(
        () -> VitruviusChangeResolverFactory.forHierarchicalIds(resourceSet).assignIds(change));
  }

  private TransactionalChange<EObject> recordInsertedNode() {
    final Resource resource = resourceSet.createResource(URI.createURI("test://test.nodes"));
    final EObject node = new ComputingNode(nodeClass);
    node.eSet(name, "parent");
    final EObject child = new ComputingNode(nodeClass);
    child.eSet(name, "child");
    ((List<EObject>) node.eGet(children)).add(child);

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

  private static EAttribute attribute(final String featureName) {
    final EAttribute attribute = ECORE.createEAttribute();
    attribute.setName(featureName);
    attribute.setEType(EcorePackage.Literals.ESTRING);
    return attribute;
  }

  private EReference reference(final String featureName, final boolean containment) {
    final EReference reference = ECORE.createEReference();
    reference.setName(featureName);
    reference.setEType(nodeClass);
    reference.setUpperBound(EStructuralFeature.UNBOUNDED_MULTIPLICITY);
    reference.setContainment(containment);
    return reference;
  }

  /**
   * A node whose {@code computedLabel} and {@code computedNeighbors} are computed from its other
   * features, reported as not set and cannot be set, like a redefinition and a union.
   */
  private final class ComputingNode extends DynamicEObjectImpl {
    ComputingNode(final EClass eClass) {
      super(eClass);
    }

    @Override
    public Object eGet(final EStructuralFeature feature, final boolean resolve,
        final boolean coreType) {
      if (feature == computedLabel) {
        return "Node " + eGet(name);
      }
      if (feature == computedNeighbors) {
        return ECollections.unmodifiableEList((List<EObject>) eGet(children));
      }
      return super.eGet(feature, resolve, coreType);
    }

    @Override
    public boolean eIsSet(final EStructuralFeature feature) {
      return feature != computedLabel && feature != computedNeighbors && super.eIsSet(feature);
    }

    @Override
    public void eSet(final EStructuralFeature feature, final Object newValue) {
      if (feature == computedLabel || feature == computedNeighbors) {
        throw new UnsupportedOperationException(feature.getName() + " is computed");
      }
      super.eSet(feature, newValue);
    }
  }
}
