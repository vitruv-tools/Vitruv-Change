/**
 */
package tools.vitruv.change.atomic.feature.list.impl;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

import tools.vitruv.change.atomic.AdditiveEChange;
import tools.vitruv.change.atomic.AtomicPackage;

import tools.vitruv.change.atomic.feature.list.InsertInListEChange;
import tools.vitruv.change.atomic.feature.list.ListPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Insert In List EChange</b></em>'.
 * <!-- end-user-doc -->
 *
 * @generated
 */
public abstract class InsertInListEChangeImpl<Element extends Object, Feature extends EStructuralFeature, Value extends Object> extends UpdateSingleListEntryEChangeImpl<Element, Feature> implements InsertInListEChange<Element, Feature, Value>
{
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected InsertInListEChangeImpl()
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
		return ListPackage.Literals.INSERT_IN_LIST_ECHANGE;
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
	public int eDerivedOperationID(int baseOperationID, Class<?> baseClass)
	{
		if (baseClass == AdditiveEChange.class)
		{
			switch (baseOperationID)
			{
				case AtomicPackage.ADDITIVE_ECHANGE___GET_NEW_VALUE: return ListPackage.INSERT_IN_LIST_ECHANGE___GET_NEW_VALUE;
				default: return -1;
			}
		}
		return super.eDerivedOperationID(baseOperationID, baseClass);
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
			case ListPackage.INSERT_IN_LIST_ECHANGE___GET_NEW_VALUE:
				return getNewValue();
		}
		return super.eInvoke(operationID, arguments);
	}

} //InsertInListEChangeImpl
