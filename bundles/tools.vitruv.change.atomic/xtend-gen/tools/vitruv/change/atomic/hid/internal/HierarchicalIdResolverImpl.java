package tools.vitruv.change.atomic.hid.internal;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import edu.kit.ipd.sdq.commons.util.org.eclipse.emf.common.util.URIUtil;
import edu.kit.ipd.sdq.commons.util.org.eclipse.emf.ecore.resource.ResourceSetUtil;
import java.util.Iterator;
import java.util.PriorityQueue;
import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.Conversions;
import tools.vitruv.change.atomic.hid.HierarchicalId;
import tools.vitruv.change.atomic.hid.ObjectResolutionUtil;

/**
 * {@link HierarchicalIdResolver}
 */
@SuppressWarnings("all")
public class HierarchicalIdResolverImpl implements HierarchicalIdResolver {
  /**
   * The cache IDs repository provides cache IDs with values starting from 0, always providing the
   * one with the lowest index first. It ensures that the same sequence of taking and pushing entries
   * always gives the same values.
   */
  public static class CacheIdsRepository {
    private final PriorityQueue<HierarchicalId> entries = new PriorityQueue<HierarchicalId>();

    private int maxValue;

    public HierarchicalId pop() {
      HierarchicalId _xblockexpression = null;
      {
        boolean _isEmpty = this.entries.isEmpty();
        if (_isEmpty) {
          int _plusPlus = this.maxValue++;
          String _plus = (HierarchicalIdResolverImpl.CACHE_PREFIX + Integer.valueOf(_plusPlus));
          HierarchicalId _hierarchicalId = new HierarchicalId(_plus);
          this.push(_hierarchicalId);
        }
        _xblockexpression = this.entries.poll();
      }
      return _xblockexpression;
    }

    public HierarchicalId peek() {
      HierarchicalId _xifexpression = null;
      boolean _isEmpty = this.entries.isEmpty();
      if (_isEmpty) {
        return new HierarchicalId((HierarchicalIdResolverImpl.CACHE_PREFIX + Integer.valueOf(this.maxValue)));
      } else {
        _xifexpression = this.entries.peek();
      }
      return _xifexpression;
    }

    public boolean push(final HierarchicalId value) {
      boolean _xblockexpression = false;
      {
        Preconditions.checkState(HierarchicalIdResolverImpl.isCache(value), "%s is a not a cache ID", value);
        _xblockexpression = this.entries.add(value);
      }
      return _xblockexpression;
    }

    public boolean isNoneMissing() {
      int _length = ((Object[])Conversions.unwrapArray(this.entries, Object.class)).length;
      return (_length == this.maxValue);
    }
  }

  private static final Logger logger = Logger.getLogger(HierarchicalIdResolverImpl.class);

  private static final String CACHE_PREFIX = "cache:/";

  private final ResourceSet resourceSet;

  private final BiMap<EObject, HierarchicalId> eObjectToId = HashBiMap.<EObject, HierarchicalId>create();

  /**
   * Instantiates an ID resolver with the given {@link ResourceSet} for resolving objects.
   * 
   * @param resourceSet -
   * 		the {@link ResourceSet} to load model elements from, may not be {@code null}
   * @throws IllegalArgumentExceptionif given {@link ResourceSet} is {@code null}
   */
  public HierarchicalIdResolverImpl(final ResourceSet resourceSet) {
    Preconditions.checkArgument((resourceSet != null), "Resource set may not be null");
    this.resourceSet = resourceSet;
  }

  @Override
  public void endTransaction() {
    this.cleanupRemovedElements();
    Preconditions.checkState(this.cacheIds.isNoneMissing(), "there are still elements in cache although a transaction has been closed");
  }

  private void cleanupRemovedElements() {
    for (final Iterator<EObject> iterator = this.eObjectToId.keySet().iterator(); iterator.hasNext();) {
      {
        final EObject object = iterator.next();
        if (((object.eResource() == null) || (object.eResource().getResourceSet() == null))) {
          final HierarchicalId id = this.eObjectToId.get(object);
          boolean _isCache = HierarchicalIdResolverImpl.isCache(id);
          if (_isCache) {
            this.cacheIds.push(id);
          }
          iterator.remove();
        }
      }
    }
  }

  @Override
  public Resource getResource(final URI uri) {
    return ResourceSetUtil.getOrCreateResource(this.resourceSet, uri);
  }

  @Override
  public HierarchicalId getAndUpdateId(final EObject eObject) {
    HierarchicalId _xifexpression = null;
    Resource _eResource = eObject.eResource();
    boolean _tripleNotEquals = (_eResource != null);
    if (_tripleNotEquals) {
      _xifexpression = this.registerObjectInResource(eObject);
    } else {
      _xifexpression = this.getOrRegisterCachedObject(eObject);
    }
    return _xifexpression;
  }

  private HierarchicalId registerObjectInResource(final EObject eObject) {
    String _string = eObject.eResource().getURI().appendFragment(ObjectResolutionUtil.getHierarchicUriFragment(eObject)).toString();
    final HierarchicalId id = new HierarchicalId(_string);
    this.register(id, eObject);
    return id;
  }

  private HierarchicalId getOrRegisterCachedObject(final EObject eObject) {
    final HierarchicalId storedId = this.eObjectToId.get(eObject);
    boolean _isCache = HierarchicalIdResolverImpl.isCache(storedId);
    if (_isCache) {
      return storedId;
    } else {
      final HierarchicalId id = this.cacheIds.peek();
      this.register(id, eObject);
      return id;
    }
  }

  @Override
  public EObject getEObject(final HierarchicalId id) {
    final EObject eObject = this.getEObjectOrNull(id);
    Preconditions.checkState((eObject != null), "no EObject could be found for ID: %s", id);
    return eObject;
  }

  private EObject getEObjectOrNull(final HierarchicalId id) {
    final URI uri = URI.createURI(id.getId());
    EObject _elvis = null;
    EObject _elvis_1 = null;
    EObject _elvis_2 = null;
    EObject _eObjectIfReadonlyUri = this.getEObjectIfReadonlyUri(uri);
    if (_eObjectIfReadonlyUri != null) {
      _elvis_2 = _eObjectIfReadonlyUri;
    } else {
      EObject _storedEObject = this.getStoredEObject(uri);
      _elvis_2 = _storedEObject;
    }
    if (_elvis_2 != null) {
      _elvis_1 = _elvis_2;
    } else {
      EObject _andRegisterNonStoredEObject = this.getAndRegisterNonStoredEObject(uri);
      _elvis_1 = _andRegisterNonStoredEObject;
    }
    if (_elvis_1 != null) {
      _elvis = _elvis_1;
    } else {
      _elvis = null;
    }
    return _elvis;
  }

  private EObject getEObjectIfReadonlyUri(final URI uri) {
    boolean _isReadOnly = HierarchicalIdResolverImpl.isReadOnly(uri);
    if (_isReadOnly) {
      boolean _hasFragment = uri.hasFragment();
      if (_hasFragment) {
        return this.resourceSet.getEObject(uri, true);
      }
    }
    return null;
  }

  private EObject getStoredEObject(final URI uri) {
    BiMap<HierarchicalId, EObject> _inverse = this.eObjectToId.inverse();
    String _string = uri.toString();
    HierarchicalId _hierarchicalId = new HierarchicalId(_string);
    return _inverse.get(_hierarchicalId);
  }

  private EObject getAndRegisterNonStoredEObject(final URI uri) {
    final EObject candidate = this.resourceSet.getEObject(uri, false);
    if ((candidate != null)) {
      this.getAndUpdateId(candidate);
    }
    return candidate;
  }

  private void register(final HierarchicalId id, final EObject eObject) {
    Preconditions.checkState((eObject != null), "object must not be null");
    boolean _isTraceEnabled = HierarchicalIdResolverImpl.logger.isTraceEnabled();
    if (_isTraceEnabled) {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("Adding ID ");
      _builder.append(id);
      _builder.append(" for EObject: ");
      _builder.append(eObject);
      HierarchicalIdResolverImpl.logger.trace(_builder);
    }
    final EObject oldObject = this.eObjectToId.inverse().get(id);
    final HierarchicalId oldId = this.eObjectToId.get(eObject);
    if (((oldObject != null) && (oldObject != eObject))) {
      this.eObjectToId.remove(oldObject);
    }
    if (((oldId != null) && (oldId != id))) {
      this.eObjectToId.inverse().remove(oldId);
    }
    this.eObjectToId.put(eObject, id);
    boolean _isCache = HierarchicalIdResolverImpl.isCache(oldId);
    if (_isCache) {
      this.cacheIds.push(oldId);
    }
    boolean _isCache_1 = HierarchicalIdResolverImpl.isCache(id);
    if (_isCache_1) {
      final HierarchicalId entry = this.cacheIds.pop();
      boolean _equals = Objects.equal(id, entry);
      Preconditions.checkState(_equals, "expected cache ID was %s but actually gave %s", id, entry);
    }
  }

  @Override
  public boolean hasEObject(final HierarchicalId id) {
    EObject _eObjectOrNull = this.getEObjectOrNull(id);
    return (_eObjectOrNull != null);
  }

  private static boolean isReadOnly(final URI uri) {
    return ((uri != null) && (URIUtil.isPathmap(uri) || uri.isArchive()));
  }

  private static boolean isCache(final HierarchicalId id) {
    return ((id != null) && id.getId().startsWith(HierarchicalIdResolverImpl.CACHE_PREFIX));
  }

  private final HierarchicalIdResolverImpl.CacheIdsRepository cacheIds = new HierarchicalIdResolverImpl.CacheIdsRepository();
}
