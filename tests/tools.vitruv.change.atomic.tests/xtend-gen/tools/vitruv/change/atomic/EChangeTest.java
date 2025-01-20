package tools.vitruv.change.atomic;

import allElementTypes.Root;
import edu.kit.ipd.sdq.commons.util.java.lang.IterableUtil;
import edu.kit.ipd.sdq.commons.util.org.eclipse.emf.common.util.URIUtil;
import edu.kit.ipd.sdq.commons.util.org.eclipse.emf.ecore.resource.ResourceSetUtil;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.xtend.lib.annotations.AccessorType;
import org.eclipse.xtend.lib.annotations.Accessors;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.Extension;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.ListExtensions;
import org.eclipse.xtext.xbase.lib.Pure;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;
import tools.vitruv.change.atomic.command.internal.ApplyEChangeSwitch;
import tools.vitruv.change.atomic.resolve.internal.AtomicEChangeUnresolver;
import tools.vitruv.change.atomic.util.EChangeAssertHelper;
import tools.vitruv.change.atomic.uuid.AtomicEChangeUuidResolver;
import tools.vitruv.change.atomic.uuid.Uuid;
import tools.vitruv.change.atomic.uuid.UuidResolver;
import tools.vitruv.testutils.metamodels.AllElementTypesCreators;

/**
 * Default class for testing EChange changes.
 * Prepares two temporary model instances of the allelementtypes metamodel which
 * can be modified by the EChange tests. The model is stored in one temporary file.
 */
@SuppressWarnings("all")
public abstract class EChangeTest {
  private static final String METAMODEL = "allelementtypes";

  private static final String MODEL_FILE_NAME = "model";

  @Accessors(AccessorType.PROTECTED_GETTER)
  private Root rootObject;

  @Accessors(AccessorType.PROTECTED_GETTER)
  private Resource resource;

  private ResourceSet resourceSet;

  private UuidResolver uuidResolver;

  private AtomicEChangeUuidResolver atomicChangeResolver;

  private AtomicEChangeUnresolver changeUnresolver;

  @Accessors(AccessorType.PROTECTED_GETTER)
  private TypeInferringAtomicEChangeFactory atomicFactory;

  @Accessors(AccessorType.PROTECTED_GETTER)
  private TypeInferringCompoundEChangeFactory compoundFactory;

  @Extension
  protected EChangeAssertHelper helper;

  /**
   * Sets up a new model and the two model instances before every test.
   * The model is stored in a temporary file with filename {@link MODEL_FILE_NAME}
   * with extension {@link METAMODEL}. The folder is accessible by the attribute {@link testFolder}.
   * The two model instances are stored in the two {@link ResourceSet} attributes {@link resourceSet1} and
   * {@link resourceSet2}.
   */
  @BeforeEach
  public final void beforeTest(@TempDir final Path testFolder) {
    try {
      Path modelFile = testFolder.resolve(((EChangeTest.MODEL_FILE_NAME + ".") + EChangeTest.METAMODEL));
      final URI fileUri = URIUtil.createFileURI(modelFile.toFile());
      this.resourceSet = ResourceSetUtil.withGlobalFactories(new ResourceSetImpl());
      this.uuidResolver = UuidResolver.create(this.resourceSet);
      AtomicEChangeUuidResolver _atomicEChangeUuidResolver = new AtomicEChangeUuidResolver(this.uuidResolver);
      this.atomicChangeResolver = _atomicEChangeUuidResolver;
      this.resource = this.resourceSet.createResource(fileUri);
      this.rootObject = AllElementTypesCreators.aet.Root();
      this.uuidResolver.registerEObject(this.rootObject);
      EList<EObject> _contents = this.resource.getContents();
      _contents.add(this.rootObject);
      this.resource.save(null);
      AtomicEChangeUnresolver _atomicEChangeUnresolver = new AtomicEChangeUnresolver(this.uuidResolver, this.resourceSet);
      this.changeUnresolver = _atomicEChangeUnresolver;
      TypeInferringAtomicEChangeFactory _typeInferringAtomicEChangeFactory = new TypeInferringAtomicEChangeFactory();
      this.atomicFactory = _typeInferringAtomicEChangeFactory;
      TypeInferringCompoundEChangeFactory _typeInferringCompoundEChangeFactory = new TypeInferringCompoundEChangeFactory(this.atomicFactory);
      this.compoundFactory = _typeInferringCompoundEChangeFactory;
      EChangeAssertHelper _eChangeAssertHelper = new EChangeAssertHelper();
      this.helper = _eChangeAssertHelper;
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }

  protected final EList<EObject> getResourceContent() {
    return this.resource.getContents();
  }

  protected void applyBackward(final List<? extends EChange<EObject>> changes) {
    final Consumer<EChange<EObject>> _function = (EChange<EObject> it) -> {
      this.applyBackward(it);
    };
    ListExtensions.reverseView(changes).forEach(_function);
  }

  protected void applyBackward(final EChange<EObject> change) {
    ApplyEChangeSwitch.applyEChange(change, false);
  }

  protected List<EChange<EObject>> applyForwardAndResolve(final List<? extends EChange<Uuid>> changes) {
    final Function1<EChange<Uuid>, EChange<EObject>> _function = (EChange<Uuid> it) -> {
      return this.applyForwardAndResolve(it);
    };
    return IterableUtil.mapFixed(changes, _function);
  }

  protected EChange<EObject> applyForwardAndResolve(final EChange<Uuid> change) {
    return this.atomicChangeResolver.resolveAndApplyForward(change);
  }

  protected EChange<Uuid> unresolve(final EChange<? extends EObject> eChange) {
    return this.changeUnresolver.unresolve(eChange);
  }

  protected <O extends EObject> O withUuid(final O eObject) {
    this.uuidResolver.registerEObject(eObject);
    return eObject;
  }

  protected List<EChange<Uuid>> unresolve(final List<? extends EChange<? extends EObject>> eChanges) {
    return this.changeUnresolver.unresolve(eChanges);
  }

  @Pure
  protected Root getRootObject() {
    return this.rootObject;
  }

  @Pure
  protected Resource getResource() {
    return this.resource;
  }

  @Pure
  protected TypeInferringAtomicEChangeFactory getAtomicFactory() {
    return this.atomicFactory;
  }

  @Pure
  protected TypeInferringCompoundEChangeFactory getCompoundFactory() {
    return this.compoundFactory;
  }
}
