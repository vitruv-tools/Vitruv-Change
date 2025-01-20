/**
 */
package tools.vitruv.change.atomic.impl;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;

import tools.vitruv.change.atomic.AdditiveEChange;
import tools.vitruv.change.atomic.AtomicPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Additive EChange</b></em>'.
 * <!-- end-user-doc -->
 *
 * @generated
 */
public abstract class AdditiveEChangeImpl<Element extends Object, Value extends Object> extends EChangeImpl<Element> implements AdditiveEChange<Element, Value>
{
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected AdditiveEChangeImpl()
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
		return AtomicPackage.Literals.ADDITIVE_ECHANGE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Value getNewValue()
	{
		// TODO: implement this method
		// Ensure that you remove @generated or mark it @generated NOT
		throw new UnsupportedOperationException();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eInvoke(int operationID, EList<?> arguments) throws InvocationTargetException
	{
		switch (operationID)
		{
			case AtomicPackage.ADDITIVE_ECHANGE___GET_NEW_VALUE:
				return getNewValue();
		}
		return super.eInvoke(operationID, arguments);
	}

} //AdditiveEChangeImpl
