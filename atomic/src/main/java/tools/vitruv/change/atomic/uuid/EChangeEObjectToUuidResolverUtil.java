package tools.vitruv.change.atomic.uuid;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.eobject.EObjectAddedEChange;
import tools.vitruv.change.atomic.eobject.EObjectExistenceEChange;
import tools.vitruv.change.atomic.eobject.EObjectSubtractedEChange;
import tools.vitruv.change.atomic.feature.FeatureEChange;
import tools.vitruv.change.atomic.root.InsertRootEObject;
import tools.vitruv.change.atomic.feature.attribute.ReplaceSingleValuedEAttribute;
import tools.vitruv.change.atomic.feature.reference.InsertEReference;

/**
 * A utility class for resolving lists of EChange&lt;EObject&gt; to lists of EChange&lt;Uuid&gt;.
 */
public final class EChangeEObjectToUuidResolverUtil {
  /**
   * Result of resolving a list of EChange&lt;EObject&gt; to a list of EChange&lt;Uuid&gt;.
   * Besides containing the list of resolved changes, it also includes a mapping of
   * EObjects from the involved model and changes to Uuids.
   */
  public static record SingleResolutionResult(List<EChange<Uuid>> resolvedChanges, Map<EObject, Uuid> uuidMapping) {}

  /**
   * Result of resolving a list of lists of EChange&lt;EObject&gt; to a list of lists of EChange&lt;Uuid&gt;.
   * Besides containing the list of lists of resolved changes, it also includes a mapping of
   * EObjects from the involved model and changes to Uuids.
   */
  public static record MultiResolutionResult(List<List<EChange<Uuid>>> resolvedChanges, Map<EObject, Uuid> uuidMapping) {}
  

  private EChangeEObjectToUuidResolverUtil() {
  }
  
  private Resource targetResource;
  private UuidResolver uuidResolver;
  private AtomicEChangeUuidResolver echangeUuidResolver;
  private Map<String, EObject> proxyUriToObject;

  /** Resolves a list of EChange&lt;EObject&gt; to a list of EChange&lt;Uuid&gt;. The changes are
   * not applied. If they contain proxy EObjects, which cannot be resolved, the proxy objects are
   * replaced by newly created EObjects. If the targetResource contains model elements, this method
   * will assign new UUIDs to each existing element.
   * 
   * @param changes the list of EChange&lt;EObject&gt;.
   * @param targetResource the Resource, to which the changes should be applied.
   * @return the resolution result.
   */
  public static SingleResolutionResult resolveSingleChanges(List<EChange<EObject>> changes,
      Resource targetResource) {
    UuidResolver uuidResolver = UuidResolver.create(targetResource.getResourceSet());
    targetResource.getAllContents().forEachRemaining(uuidResolver::registerEObject);
    return resolveSingleChanges(changes, targetResource, uuidResolver);
  }
  
  
  /** Resolves a list of lists of EChange&lt;EObject&gt; to a list of lists of EChange&lt;Uuid&gt;. The changes are
   * not applied. If they contain proxy EObjects, which cannot be resolved, the proxy objects are
   * replaced by newly created EObjects. If the targetResource contains model elements, this method
   * will assign new UUIDs to each existing element.
   * 
   * @param nestedChanges the list of lists of EChange&lt;EObject&gt;.
   * @param targetResource the Resource, to which the changes should be applied.
   * @return the resolution result.
   */
  public static MultiResolutionResult resolveMultipleChanges(List<List<EChange<EObject>>> nestedChanges,
	      Resource targetResource) {
	    UuidResolver uuidResolver = UuidResolver.create(targetResource.getResourceSet());
	    targetResource.getAllContents().forEachRemaining(uuidResolver::registerEObject);
	    return resolveMultipleChanges(nestedChanges, targetResource, uuidResolver);
	  }
  
  

  /**
   * Resolves a list of EChange&lt;EObject&gt; to a list of EChange&lt;Uuid&gt;. The changes are
   * not applied. If they contain proxy EObjects, which cannot be resolved, the proxy objects are
   * replaced by newly created EObjects. The method loads Uuids for existing elements from the
   * given file path.
   * 
   * @param changes the list of EChange&lt;EObject&gt;.
   * @param targetResource the Resource, to which the changes should be applied.
   * @param uuidFilePath path to a file, which contains a mapping of elements to Uuids.
   * @return the resolution result.
   * @throws IOException if the file cannot be loaded.
   */
  public static SingleResolutionResult resolveSingleChanges(List<EChange<EObject>> changes,
      Resource targetResource, Path uuidFilePath) throws IOException {
    UuidResolver uuidResolver = UuidResolver.create(targetResource.getResourceSet());
    uuidResolver.loadFromUri(URI.createFileURI(uuidFilePath.toString()));
    return resolveSingleChanges(changes, targetResource, uuidResolver);
  }
  
  /**
   * Resolves a list of lists of EChange&lt;EObject&gt; to a list of lists of EChange&lt;Uuid&gt;. The changes are
   * not applied. If they contain proxy EObjects, which cannot be resolved, the proxy objects are
   * replaced by newly created EObjects. The method loads Uuids for existing elements from the
   * given file path.
   * 
   * @param changes the list of lists of EChange&lt;EObject&gt;.
   * @param targetResource the Resource, to which the changes should be applied.
   * @param uuidFilePath path to a file, which contains a mapping of elements to Uuids.
   * @return the resolution result.
   * @throws IOException if the file cannot be loaded.
   */
  public static MultiResolutionResult resolveMultipleChanges(List<List<EChange<EObject>>> nestedChanges,
      Resource targetResource, Path uuidFilePath) throws IOException {
    UuidResolver uuidResolver = UuidResolver.create(targetResource.getResourceSet());
    uuidResolver.loadFromUri(URI.createFileURI(uuidFilePath.toString()));
    return resolveMultipleChanges(nestedChanges, targetResource, uuidResolver);
  }
  
  private static SingleResolutionResult resolveSingleChanges(List<EChange<EObject>> changes,
	      Resource targetResource, UuidResolver uuidResolver) {
	  EChangeEObjectToUuidResolverUtil util = new EChangeEObjectToUuidResolverUtil();
	  util.targetResource = targetResource;
	  util.uuidResolver = uuidResolver;
	  util.echangeUuidResolver = new AtomicEChangeUuidResolver(uuidResolver); 
	  util.proxyUriToObject = new HashMap<>();
	  return util.resolveSingleChanges(changes);	  	  
  }
  private static MultiResolutionResult resolveMultipleChanges(List<List<EChange<EObject>>> nestedChanges,
	      Resource targetResource, UuidResolver uuidResolver) {	 
	  EChangeEObjectToUuidResolverUtil util = new EChangeEObjectToUuidResolverUtil();
	  util.targetResource = targetResource;
	  util.uuidResolver = uuidResolver;
	  util.echangeUuidResolver = new AtomicEChangeUuidResolver(uuidResolver); 
	  util.proxyUriToObject = new HashMap<>();
	  
	  List<List<EChange<Uuid>>> resolvedNestedChanges = new ArrayList<>();
	  for (List<EChange<EObject>> changes : nestedChanges) {
		  SingleResolutionResult singleResult = util.resolveSingleChanges(changes);
		  resolvedNestedChanges.add(singleResult.resolvedChanges);
	  }
	  return new MultiResolutionResult(resolvedNestedChanges, util.uuidResolver.getRegisteredUuids());
  }

  private SingleResolutionResult resolveSingleChanges(List<EChange<EObject>> changes) {
    List<EChange<Uuid>> resultList = new ArrayList<>();

    for (int i = 0; i < changes.size(); i++) {
    	EChange<EObject> change = changes.get(i);
//    	System.out.println("Resolving change " + (i + 1) + "/" + changes.size() + ": " + change);
//    	System.out.println("Change type: " + change.getClass().getSimpleName());
//    	if(i == 700) {
//    		System.out.println("Debug breakpoint reached at change index 700");
//		}

      if (change instanceof InsertRootEObject<EObject> insertChange) {
        insertChange.setResource(targetResource);
      }

      if (change instanceof EObjectExistenceEChange<EObject> existenceChange) {
        EObject resolvedElement = resolveOrCreateElement(existenceChange.getAffectedElement(),
            existenceChange, proxyUriToObject);
        existenceChange.setAffectedElement(resolvedElement);
      }

      if (change instanceof FeatureEChange<EObject, ?> featChange) {
//    	System.out.println("Resolving FeatureEChange for element: " + featChange.getAffectedElement());
//    	System.out.println("Affected element class: " + featChange.getAffectedElement().getClass().getName());
//    	System.out.println("Affected feature: " + featChange.getAffectedFeature().getName());
    	EObject resolvedElement = resolveOrGetElement(featChange.getAffectedElement(), featChange,
            proxyUriToObject);
        //existierende Elemente aus dem Model werden hier nicht in "proxyUriToObject" abgelegt
        featChange.setAffectedElement(resolvedElement);
      }
      
      if (change instanceof InsertEReference<EObject> insertRefChange) {
    	  //get the actual reference-feature-list of the affectedElement
    	  EStructuralFeature referenceFeature = insertRefChange.getAffectedFeature();
    	  if (!referenceFeature.isMany()) {
	    	  EObject affectedElement = insertRefChange.getAffectedElement();
	    	  Object x = affectedElement.eGet(referenceFeature);
//	    	  System.out.println("Getting the Reference in which you are supposed to add: " + x);
    	  }    			  
      }

      if (change instanceof EObjectAddedEChange<EObject> addChange) {
//    	System.out.println("New Value: " + addChange.getNewValue().getClass().getName());    	  
    	EObject resolvedElement = resolveOrGetElement(addChange.getNewValue(), addChange,
            proxyUriToObject);
        
        addChange.setNewValue(resolvedElement);
      }

      if (change instanceof EObjectSubtractedEChange<EObject> subChange) {
    	EObject oldValueToBeResolved = subChange.getOldValue();
    	if (oldValueToBeResolved != null) {
    	  EObject resolvedElement = resolveOrGetElement(subChange.getOldValue(), subChange, proxyUriToObject);
    	  subChange.setOldValue(resolvedElement);    		
    	} else {
		  if (change instanceof EObjectAddedEChange<EObject> addChange) {
			/* This could be a ReplaceSingleValuedEReference change, where the old value is null,
			 * but the new value is not. In this case, we can skip resolving the old value, 
			 * because it is not relevant for the change. We can only resolve the new value,
			 * which is done in the previous if block. 
			 */
//			 System.out.println("Old value is null for change: " + change + ", but it is fine.");
		  }
    	}    	  
      }
      
      if (change instanceof ReplaceSingleValuedEAttribute<EObject, ?> replaceAttrChange) {
    	  
    	  ReplaceSingleValuedEAttribute<EObject, Object> typedChange =
    			    (ReplaceSingleValuedEAttribute<EObject, Object>) replaceAttrChange;
    	  
//    	  System.out.println("Resolving ReplaceSingleValuedEAttribute:");
//    	  System.out.print("\tAffected feature is " + replaceAttrChange.getAffectedFeature().getName());
//    	  System.out.println(":" + replaceAttrChange.getAffectedFeature().getEType().getName());
    	  
    	  EStructuralFeature eattribute = replaceAttrChange.getAffectedFeature();
    	  EStructuralFeature newValueFeature = replaceAttrChange.getAffectedFeature();
    	  EClassifier eattributeType = eattribute.getEType();
    	  EDataType eattributeDataType = (EDataType) eattributeType;
    	  
    	  //if it is not a string, parse string in datatype value    	  
    	  
    	  Object stringNewValue = replaceAttrChange.getNewValue();
//    	  System.out.print("\tNew Value is " + replaceAttrChange.getNewValue());
//    	  System.out.println(":" + stringNewValue.getClass().getName());
    	  
//    	  System.out.println("\tExpected Type is " + eattributeDataType.getName());
    	  
    	  Object newValue = null;
    	  if (!eattributeDataType.isInstance(stringNewValue)) {
    	  //if (stringNewValue instanceof String && !eattributeDataType.getName().equals("EString")) {
    		  newValue = eattributeType.getEPackage().getEFactoryInstance().createFromString(eattributeDataType, stringNewValue.toString());
    		  if (stringNewValue.equals("true")) {
    			  //newValue soll neuer EBoolean werden
    			  newValue = Boolean.TRUE;
    		  } else if (stringNewValue.equals("false")) {
				  //newValue soll neuer EBoolean werden
				  newValue = Boolean.FALSE;
			  }    		  
    		  typedChange.setNewValue(newValue);
    	  }
      }

      EChange<Uuid> uuidChange = echangeUuidResolver.assignIds(change);
      resultList.add(uuidChange);
    }

    //proxyUriToObject.clear();
    return new SingleResolutionResult(resultList, uuidResolver.getRegisteredUuids());
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
