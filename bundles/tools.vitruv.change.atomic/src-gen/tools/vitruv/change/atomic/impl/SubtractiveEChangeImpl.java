/**
 */
package tools.vitruv.change.atomic.impl;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;

import tools.vitruv.change.atomic.AtomicPackage;
import tools.vitruv.change.atomic.SubtractiveEChange;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Subtractive EChange</b></em>'.
 * <!-- end-user-doc -->
 *
 * @generated
 */
public abstract class SubtractiveEChangeImpl<Element extends Object, Value extends Object> extends EChangeImpl<Element> implements SubtractiveEChange<Element, Value>
{
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected SubtractiveEChangeImpl()
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
		return AtomicPackage.Literals.SUBTRACTIVE_ECHANGE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Value getOldValue()
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
			case AtomicPackage.SUBTRACTIVE_ECHANGE___GET_OLD_VALUE:
				return getOldValue();
		}
		return super.eInvoke(operationID, arguments);
	}

} //SubtractiveEChangeImpl
