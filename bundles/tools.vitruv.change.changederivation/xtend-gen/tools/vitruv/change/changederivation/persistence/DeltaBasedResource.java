package tools.vitruv.change.changederivation.persistence;

import edu.kit.ipd.sdq.commons.util.org.eclipse.emf.ecore.resource.ResourceSetUtil;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.ListExtensions;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.hid.HierarchicalId;
import tools.vitruv.change.changederivation.DefaultStateBasedChangeResolutionStrategy;
import tools.vitruv.change.composite.description.VitruviusChangeFactory;
import tools.vitruv.change.composite.description.VitruviusChangeResolver;

/**
 * This class provides functionality to save and load a resource in a model delta representation.
 * It uses the {@link DefaultStateBasedChangeResolutionStrategy} to save a state-based resource as a sequence of
 * {@link EChange}s. The saved delta sequence can be loaded again, and the resulting changes can be
 * applied to create the state-based resource again.
 */
@SuppressWarnings("all")
public class DeltaBasedResource extends ResourceImpl {
  public DeltaBasedResource(final URI uri) {
    super(uri);
  }

  private static List<EChange<HierarchicalId>> loadDeltas(final URI modelUri) {
    ResourceSetImpl _resourceSetImpl = new ResourceSetImpl();
    final ResourceSet resourceSet = ResourceSetUtil.withGlobalFactories(_resourceSetImpl);
    final Resource resource = resourceSet.getResource(modelUri, true);
    final Function1<EObject, EChange<HierarchicalId>> _function = (EObject it) -> {
      return ((EChange<HierarchicalId>) it);
    };
    return IterableExtensions.<EChange<HierarchicalId>>toList(ListExtensions.<EObject, EChange<HierarchicalId>>map(resource.getContents(), _function));
  }

  @Override
  public void doLoad(final InputStream inputStream, final Map<?, ?> options) throws IOException {
    final List<EChange<HierarchicalId>> deltas = DeltaBasedResource.loadDeltas(this.getURI());
    VitruviusChangeResolver.forHierarchicalIds(this.resourceSet).resolveAndApply(
      VitruviusChangeFactory.getInstance().<HierarchicalId>createTransactionalChange(deltas));
  }

  @Override
  public void doSave(final OutputStream outputStream, final Map<?, ?> options) throws IOException {
    final List<EChange<HierarchicalId>> deltaChanges = new DefaultStateBasedChangeResolutionStrategy().getChangeSequenceForCreated(this).getEChanges();
    ResourceSetImpl _resourceSetImpl = new ResourceSetImpl();
    final ResourceSet resourceSet = ResourceSetUtil.withGlobalFactories(_resourceSetImpl);
    final Resource resource = resourceSet.createResource(this.getURI());
    resource.getContents().addAll(deltaChanges);
    try (final OutputStream out = outputStream) {
      resource.save(out, Collections.EMPTY_MAP);
    }
  }
}
