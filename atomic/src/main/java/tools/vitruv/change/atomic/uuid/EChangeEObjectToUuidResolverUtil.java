package tools.vitruv.change.atomic.uuid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.eobject.EObjectAddedEChange;
import tools.vitruv.change.atomic.eobject.EObjectExistenceEChange;
import tools.vitruv.change.atomic.eobject.EObjectSubtractedEChange;
import tools.vitruv.change.atomic.feature.FeatureEChange;
import tools.vitruv.change.atomic.root.InsertRootEObject;

/**
 * A utility class for resolving lists of EChange&lt;EObject&gt; to lists of EChange&lt;Uuid&gt;.
 */
public final class EChangeEObjectToUuidResolverUtil {
  private EChangeEObjectToUuidResolverUtil() {
  }

  /** Resolves a list of EChange&lt;EObject&gt; to a list of EChange&lt;Uuid&gt;. The changes are
   * not applied. If they contain proxy EObjects, which cannot be resolved, the proxy objects are
   * replaced by newly created EObjects.
   * 
   * @param changes the list of EChange&lt;EObject&gt;.
   * @param targetResource the Resource, to which the changes should be applied.
   * @return the resolved list of EChange&lt;Uuid&gt;.
   */
  public static List<EChange<Uuid>> resolveChanges(List<EChange<EObject>> changes,
      Resource targetResource) {
    AtomicEChangeUuidResolver resolverUuid = new AtomicEChangeUuidResolver(
        UuidResolver.create(targetResource.getResourceSet()));
    Map<String, EObject> proxyUriToObject = new HashMap<>();
    List<EChange<Uuid>> resultList = new ArrayList<>();

    for (EChange<EObject> change : changes) {
      if (change instanceof InsertRootEObject<EObject> insertChange) {
        insertChange.setResource(targetResource);
      }

      if (change instanceof EObjectExistenceEChange<EObject> existenceChange) {
        EObject resolvedElement = resolveOrCreateElement(existenceChange.getAffectedElement(),
            existenceChange, proxyUriToObject);
        existenceChange.setAffectedElement(resolvedElement);
      }

      if (change instanceof FeatureEChange<EObject, ?> featChange) {
        EObject resolvedElement = resolveOrGetElement(featChange.getAffectedElement(), featChange,
            proxyUriToObject);
        featChange.setAffectedElement(resolvedElement);
      }

      if (change instanceof EObjectAddedEChange<EObject> addChange) {
        EObject resolvedElement = resolveOrGetElement(addChange.getNewValue(), addChange,
            proxyUriToObject);
        addChange.setNewValue(resolvedElement);
      }

      if (change instanceof EObjectSubtractedEChange<EObject> subChange) {
        EObject resolvedElement = resolveOrGetElement(subChange.getOldValue(), subChange,
            proxyUriToObject);
        subChange.setOldValue(resolvedElement);
      }

      EChange<Uuid> uuidChange = resolverUuid.assignIds(change);
      resultList.add(uuidChange);
    }

    resolverUuid.endTransaction();
    proxyUriToObject.clear();
    return resultList;
  }

  private static EObject resolveOrCreateElement(EObject element, EObjectExistenceEChange<?> change,
      Map<String, EObject> proxyUriToObj) {
    return resolveElement(element, change, (obj) -> {
      EObject newElement = EcoreUtil.create(change.getAffectedEObjectType());
      String uri = ((InternalEObject) obj).eProxyURI().toString();
      proxyUriToObj.put(uri, newElement);
      return newElement;
    });
  }

  private static EObject resolveOrGetElement(EObject element, EChange<?> change,
      Map<String, EObject> proxyUriToObj) {
    return resolveElement(element, change, (obj) -> {
      String uri = ((InternalEObject) obj).eProxyURI().toString();
      return proxyUriToObj.get(uri);
    });
  }

  private static EObject resolveElement(EObject element, EChange<?> change,
      Function<EObject, EObject> internalProxyResolver) {
    EObject resolvedElement = element;
    if (resolvedElement.eIsProxy()) {
      resolvedElement = EcoreUtil.resolve(resolvedElement, change);
    }
    if (resolvedElement == null || resolvedElement.eIsProxy()) {
      resolvedElement = internalProxyResolver.apply(resolvedElement);
    }
    return resolvedElement;
  }
}
