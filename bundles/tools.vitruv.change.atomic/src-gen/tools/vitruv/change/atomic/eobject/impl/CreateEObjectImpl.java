/**
 */
package tools.vitruv.change.atomic.eobject.impl;

import org.eclipse.emf.ecore.EClass;

import tools.vitruv.change.atomic.eobject.CreateEObject;
import tools.vitruv.change.atomic.eobject.EobjectPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Create EObject</b></em>'.
 * <!-- end-user-doc -->
 *
 * @generated
 */
public class CreateEObjectImpl<Element extends Object> extends EObjectExistenceEChangeImpl<Element> implements CreateEObject<Element>
{
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected CreateEObjectImpl()
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
		return EobjectPackage.Literals.CREATE_EOBJECT;
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

} //CreateEObjectImpl
