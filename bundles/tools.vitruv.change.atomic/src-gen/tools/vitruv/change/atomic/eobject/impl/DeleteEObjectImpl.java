/**
 */
package tools.vitruv.change.atomic.eobject.impl;

import org.eclipse.emf.ecore.EClass;

import tools.vitruv.change.atomic.eobject.DeleteEObject;
import tools.vitruv.change.atomic.eobject.EobjectPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Delete EObject</b></em>'.
 * <!-- end-user-doc -->
 *
 * @generated
 */
public class DeleteEObjectImpl<Element extends Object> extends EObjectExistenceEChangeImpl<Element> implements DeleteEObject<Element>
{
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected DeleteEObjectImpl()
	{
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass()
	{
		return EobjectPackage.Literals.DELETE_EOBJECT;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * This is specialized for the more specific type known in this context.
	 * @generated
	 */
	@Override
	public void setAffectedElement(Element newAffectedElement)
	{
		super.setAffectedElement(newAffectedElement);
	}

} //DeleteEObjectImpl
