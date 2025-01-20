package tools.vitruv.change.atomic.command.internal;

import com.google.common.base.Preconditions;
import edu.kit.ipd.sdq.activextendannotations.Utility;
import java.util.Collections;
import java.util.List;
import org.eclipse.emf.common.command.BasicCommandStack;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;

@Utility
@SuppressWarnings("all")
final class ChangeCommandUtil {
  /**
   * Returns the editing domain of the an object. If the object has no
   * editing domain, it returns a new {@code AdapterFactoryEditingDomain}.
   * @param object the {@link EObject}
   * @return the editing domain of the object
   */
  public static EditingDomain getEditingDomain(final EObject object) {
    final EditingDomain editingDomain = AdapterFactoryEditingDomain.getEditingDomainFor(object);
    EditingDomain _elvis = null;
    if (editingDomain != null) {
      _elvis = editingDomain;
    } else {
      ComposedAdapterFactory _composedAdapterFactory = new ComposedAdapterFactory();
      BasicCommandStack _basicCommandStack = new BasicCommandStack();
      AdapterFactoryEditingDomain _adapterFactoryEditingDomain = new AdapterFactoryEditingDomain(_composedAdapterFactory, _basicCommandStack);
      _elvis = _adapterFactoryEditingDomain;
    }
    return _elvis;
  }

  /**
   * Returns whether the affected object already contains the given value in the given reference.
   * 
   * @param affectedEObject the {@link EObject} whose feature to check
   * @param reference the {@link EReference} of the <code>affectedEObject</code> to check
   * @param value the {@link EObject} to check whether it is already contained in the <code>affectedEObject</code>'s reference
   * @return whether the <code>affectedEObject</code> contains the given value in the given reference.
   */
  public static boolean alreadyContainsObject(final EObject affectedEObject, final EReference reference, final EObject value) {
    Preconditions.checkState(affectedEObject.eClass().getEAllReferences().contains(reference), 
      "Given object %s does not contain reference %s", affectedEObject, reference);
    List<EObject> _xifexpression = null;
    boolean _isMany = reference.isMany();
    if (_isMany) {
      Object _eGet = affectedEObject.eGet(reference);
      _xifexpression = ((List<EObject>) _eGet);
    } else {
      Object _eGet_1 = affectedEObject.eGet(reference);
      _xifexpression = Collections.<EObject>unmodifiableList(CollectionLiterals.<EObject>newArrayList(((EObject) _eGet_1)));
    }
    final List<EObject> featureContents = _xifexpression;
    boolean _contains = featureContents.contains(value);
    if (_contains) {
      return true;
    }
    return false;
  }

  private ChangeCommandUtil() {
    
  }
}
