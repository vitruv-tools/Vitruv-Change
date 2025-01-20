/**
 */
package tools.vitruv.change.atomic.feature.single.impl;

import java.lang.reflect.InvocationTargetException;

import java.util.Objects;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import tools.vitruv.change.atomic.AdditiveEChange;
import tools.vitruv.change.atomic.AtomicPackage;
import tools.vitruv.change.atomic.SubtractiveEChange;

import tools.vitruv.change.atomic.feature.impl.UpdateSingleValuedFeatureEChangeImpl;

import tools.vitruv.change.atomic.feature.single.ReplaceSingleValuedFeatureEChange;
import tools.vitruv.change.atomic.feature.single.SinglePackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Replace Single Valued Feature EChange</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link tools.vitruv.change.atomic.feature.single.impl.ReplaceSingleValuedFeatureEChangeImpl#isIsUnset <em>Is Unset</em>}</li>
 * </ul>
 *
 * @generated
 */
public abstract class ReplaceSingleValuedFeatureEChangeImpl<Element extends Object, Feature extends EStructuralFeature, Value extends Object> extends UpdateSingleValuedFeatureEChangeImpl<Element, Feature> implements ReplaceSingleValuedFeatureEChange<Element, Feature, Value>
{
	/**
	 * The default value of the '{@link #isIsUnset() <em>Is Unset</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isIsUnset()
	 * @generated
	 * @ordered
	 */
	protected static final boolean IS_UNSET_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isIsUnset() <em>Is Unset</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isIsUnset()
	 * @generated
	 * @ordered
	 */
	protected boolean isUnset = IS_UNSET_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ReplaceSingleValuedFeatureEChangeImpl()
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
		return SinglePackage.Literals.REPLACE_SINGLE_VALUED_FEATURE_ECHANGE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isIsUnset()
	{
		return isUnset;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setIsUnset(boolean newIsUnset)
	{
		boolean oldIsUnset = isUnset;
		isUnset = newIsUnset;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, SinglePackage.REPLACE_SINGLE_VALUED_FEATURE_ECHANGE__IS_UNSET, oldIsUnset, isUnset));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isFromNonDefaultValue()
	{
		Value _oldValue = this.getOldValue();
		Feature _affectedFeature = this.getAffectedFeature();
		Object _defaultValue = _affectedFeature.getDefaultValue();
		boolean _equals = Objects.equals(_oldValue, _defaultValue);
		return (!_equals);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isToNonDefaultValue()
	{
		Value _newValue = this.getNewValue();
		Feature _affectedFeature = this.getAffectedFeature();
		Object _defaultValue = _affectedFeature.getDefaultValue();
		boolean _equals = Objects.equals(_newValue, _defaultValue);
		return (!_equals);
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
	public Object eGet(int featureID, boolean resolve, boolean coreType)
	{
		switch (featureID)
		{
			case SinglePackage.REPLACE_SINGLE_VALUED_FEATURE_ECHANGE__IS_UNSET:
				return isIsUnset();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eSet(int featureID, Object newValue)
	{
		switch (featureID)
		{
			case SinglePackage.REPLACE_SINGLE_VALUED_FEATURE_ECHANGE__IS_UNSET:
				setIsUnset((Boolean)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID)
	{
		switch (featureID)
		{
			case SinglePackage.REPLACE_SINGLE_VALUED_FEATURE_ECHANGE__IS_UNSET:
				setIsUnset(IS_UNSET_EDEFAULT);
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID)
	{
		switch (featureID)
		{
			case SinglePackage.REPLACE_SINGLE_VALUED_FEATURE_ECHANGE__IS_UNSET:
				return isUnset != IS_UNSET_EDEFAULT;
		}
		return super.eIsSet(featureID);
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
				case AtomicPackage.ADDITIVE_ECHANGE___GET_NEW_VALUE: return SinglePackage.REPLACE_SINGLE_VALUED_FEATURE_ECHANGE___GET_NEW_VALUE;
				default: return -1;
			}
		}
		if (baseClass == SubtractiveEChange.class)
		{
			switch (baseOperationID)
			{
				case AtomicPackage.SUBTRACTIVE_ECHANGE___GET_OLD_VALUE: return SinglePackage.REPLACE_SINGLE_VALUED_FEATURE_ECHANGE___GET_OLD_VALUE;
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
			case SinglePackage.REPLACE_SINGLE_VALUED_FEATURE_ECHANGE___IS_FROM_NON_DEFAULT_VALUE:
				return isFromNonDefaultValue();
			case SinglePackage.REPLACE_SINGLE_VALUED_FEATURE_ECHANGE___IS_TO_NON_DEFAULT_VALUE:
				return isToNonDefaultValue();
			case SinglePackage.REPLACE_SINGLE_VALUED_FEATURE_ECHANGE___GET_OLD_VALUE:
				return getOldValue();
			case SinglePackage.REPLACE_SINGLE_VALUED_FEATURE_ECHANGE___GET_NEW_VALUE:
				return getNewValue();
		}
		return super.eInvoke(operationID, arguments);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString()
	{
		if (eIsProxy()) return super.toString();

		StringBuilder result = new StringBuilder(super.toString());
		result.append(" (isUnset: ");
		result.append(isUnset);
		result.append(')');
		return result.toString();
	}

} //ReplaceSingleValuedFeatureEChangeImpl
