package tools.vitruv.change.composite.description;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.junit.jupiter.api.Test;
import tools.vitruv.change.atomic.hid.HierarchicalId;
import tools.vitruv.change.composite.recording.ChangeRecorder;

/**
 * Records random edit scripts, assigns {@link HierarchicalId}s and replays the changes into a fresh
 * resource set, which must then equal the recorded model after every transaction. The scripts
 * create, remove, move, reorder, use single-valued containment and build detached subtrees that
 * are inserted later, i.e., they exercise both positional and cache IDs.
 */
class HierarchicalIdRoundTripTest {
  private static final URI MODEL_URI = URI.createURI("model.xmi");
  private static final EPackage PACKAGE;
  private static final EClass NODE;
  private static final EAttribute NAME;
  private static final EReference CHILDREN;
  private static final EReference SLOT;

  static {
    EcoreFactory factory = EcoreFactory.eINSTANCE;
    PACKAGE = factory.createEPackage();
    PACKAGE.setName("hid");
    PACKAGE.setNsURI("http://vitruv.tools/test/hid");
    PACKAGE.setNsPrefix("hid");
    NODE = factory.createEClass();
    NODE.setName("Node");
    PACKAGE.getEClassifiers().add(NODE);
    NAME = factory.createEAttribute();
    NAME.setName("name");
    NAME.setEType(EcorePackage.Literals.ESTRING);
    NODE.getEStructuralFeatures().add(NAME);
    CHILDREN = createContainment(factory, "children", -1);
    SLOT = createContainment(factory, "slot", 1);
  }

  private static EReference createContainment(EcoreFactory factory, String name, int upperBound) {
    EReference reference = factory.createEReference();
    reference.setName(name);
    reference.setEType(NODE);
    reference.setContainment(true);
    reference.setUpperBound(upperBound);
    NODE.getEStructuralFeatures().add(reference);
    return reference;
  }

  private static ResourceSet createResourceSet() {
    ResourceSet resourceSet = new ResourceSetImpl();
    resourceSet
        .getResourceFactoryRegistry()
        .getExtensionToFactoryMap()
        .put("*", new XMIResourceFactoryImpl());
    resourceSet.getPackageRegistry().put(PACKAGE.getNsURI(), PACKAGE);
    return resourceSet;
  }

  @Test
  void replayOfAssignedChangesReproducesRecordedModel() {
    for (long seed = 0; seed < 300; seed++) {
      EditScript script = new EditScript(seed);
      ResourceSet replaySet = createResourceSet();
      for (int transaction = 0; transaction < 25; transaction++) {
        VitruviusChange<HierarchicalId> change = script.recordTransaction();
        // fresh resolvers on both sides, since cache IDs are ordered by their string, so a
        // resolver that once used more than ten of them hands out different ones
        VitruviusChangeResolverFactory.forHierarchicalIds(replaySet).resolveAndApply(change);
        Resource replayed = replaySet.getResource(MODEL_URI, false);
        assertThat(EcoreUtil.equals(replayed.getContents(), script.resource.getContents()))
            .as("seed %d, transaction %d", seed, transaction)
            .isTrue();
      }
    }
  }

  /** A model and a seeded sequence of random edits on it, recorded per transaction. */
  private static final class EditScript {
    private final ResourceSet resourceSet = createResourceSet();
    private final Resource resource = resourceSet.createResource(MODEL_URI);
    private final List<EObject> detached = new ArrayList<>();
    private final Random random;
    private int counter;

    EditScript(long seed) {
      random = new Random(seed);
    }

    VitruviusChange<HierarchicalId> recordTransaction() {
      try (ChangeRecorder recorder = new ChangeRecorder(resourceSet)) {
        recorder.addToRecording(resourceSet);
        recorder.beginRecording();
        if (resource.getContents().isEmpty()) {
          resource.getContents().add(createNode());
          for (int i = 0; i < 20; i++) {
            insertRandomly(createNode());
          }
        }
        for (int i = 0, edits = 1 + random.nextInt(8); i < edits; i++) {
          edit();
        }
        detached.clear();
        TransactionalChange<EObject> recorded = recorder.endRecording();
        return VitruviusChangeResolverFactory.forHierarchicalIds(resourceSet).assignIds(recorded);
      }
    }

    private EObject createNode() {
      EObject node = EcoreUtil.create(NODE);
      node.eSet(NAME, "n" + counter++);
      return node;
    }

    private List<EObject> attachedElements() {
      List<EObject> elements = new ArrayList<>();
      resource.getAllContents().forEachRemaining(elements::add);
      return elements;
    }

    private static EList<EObject> childrenOf(EObject node) {
      @SuppressWarnings("unchecked")
      EList<EObject> children = (EList<EObject>) node.eGet(CHILDREN);
      return children;
    }

    /** Moves are explicit (remove, then insert) to keep each step a single containment change. */
    private void insertRandomly(EObject node) {
      List<EObject> candidates = attachedElements();
      candidates.removeIf(candidate -> EcoreUtil.isAncestor(node, candidate));
      if (node.eContainer() != null || node.eResource() != null) {
        EcoreUtil.remove(node);
      }
      if (candidates.isEmpty()) {
        resource.getContents().add(node);
      } else {
        EObject container = candidates.get(random.nextInt(candidates.size()));
        if (random.nextInt(6) == 0) {
          container.eSet(SLOT, node);
        } else {
          EList<EObject> children = childrenOf(container);
          children.add(random.nextInt(children.size() + 1), node);
        }
      }
    }

    private boolean isLastRoot(EObject node) {
      return node.eContainer() == null && resource.getContents().size() == 1;
    }

    private void edit() {
      List<EObject> elements = attachedElements();
      EObject element = elements.get(random.nextInt(elements.size()));
      switch (random.nextInt(9)) {
        case 0, 1 -> insertRandomly(createNode());
        case 2 -> {
          // remove a subtree, possibly re-inserted later in the same transaction
          if (!isLastRoot(element)) {
            EcoreUtil.remove(element);
            if (random.nextBoolean()) {
              detached.add(element);
            }
          }
        }
        case 3 -> {
          if (!isLastRoot(element)) {
            insertRandomly(element);
          }
        }
        case 4 -> element.eSet(NAME, "r" + counter++);
        case 5 -> {
          // build a detached subtree, then insert it
          EObject top = createNode();
          for (int i = 0, size = random.nextInt(4); i < size; i++) {
            EObject child = createNode();
            childrenOf(top).add(random.nextInt(childrenOf(top).size() + 1), child);
            if (random.nextInt(3) == 0) {
              child.eSet(SLOT, createNode());
            }
          }
          insertRandomly(top);
        }
        case 6 -> {
          if (!detached.isEmpty()) {
            EObject node = detached.remove(random.nextInt(detached.size()));
            if (node.eContainer() == null && node.eResource() == null) {
              insertRandomly(node);
            }
          }
        }
        case 7 -> element.eSet(SLOT, random.nextBoolean() ? null : createNode());
        default -> {
          EList<EObject> children = childrenOf(element);
          if (children.size() > 1) {
            children.move(random.nextInt(children.size()), random.nextInt(children.size()));
          }
        }
      }
    }
  }
}
