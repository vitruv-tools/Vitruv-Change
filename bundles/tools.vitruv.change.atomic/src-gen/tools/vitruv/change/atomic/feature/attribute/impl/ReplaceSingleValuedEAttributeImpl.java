/**
 */
package tools.vitruv.change.atomic.feature.attribute.impl;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import tools.vitruv.change.atomic.feature.attribute.AdditiveAttributeEChange;
import tools.vitruv.change.atomic.feature.attribute.AttributePackage;
import tools.vitruv.change.atomic.feature.attribute.ReplaceSingleValuedEAttribute;
import tools.vitruv.change.atomic.feature.attribute.SubtractiveAttributeEChange;
import tools.vitruv.change.atomic.feature.attribute.UpdateAttributeEChange;

import tools.vitruv.change.atomic.feature.single.impl.ReplaceSingleValuedFeatureEChangeImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Replace Single Valued EAttribute</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link tools.vitruv.change.atomic.feature.attribute.impl.ReplaceSingleValuedEAttributeImpl#getNewValue <em>New Value</em>}</li>
 *   <li>{@link tools.vitruv.change.atomic.feature.attribute.impl.ReplaceSingleValuedEAttributeImpl#isWasUnset <em>Was Unset</em>}</li>
 *   <li>{@link tools.vitruv.change.atomic.feature.attribute.impl.ReplaceSingleValuedEAttributeImpl#getOldValue <em>Old Value</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ReplaceSingleValuedEAttributeImpl<Element extends Object, Value extends Object> extends ReplaceSingleValuedFeatureEChangeImpl<Element, EAttribute, Value> implements ReplaceSingleValuedEAttribute<Element, Value>
{
	/**
	 * The cached value of the '{@link #getNewValue() <em>New Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getNewValue()
	 * @generated
	 * @ordered
	 */
	protected Value newValue;

	/**
	 * The default value of the '{@link #isWasUnset() <em>Was Unset</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isWasUnset()
	 * @generated
	 * @ordered
	 */
	protected static final boolean WAS_UNSET_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isWasUnset() <em>Was Unset</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isWasUnset()
	 * @generated
	 * @ordered
	 */
	protected boolean wasUnset = WAS_UNSET_EDEFAULT;

	/**
	 * The cached value of the '{@link #getOldValue() <em>Old Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getOldValue()
	 * @generated
	 * @ordered
	 */
	protected Value oldValue;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ReplaceSingleValuedEAttributeImpl()
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
		return AttributePackage.Literals.REPLACE_SINGLE_VALUED_EATTRIBUTE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Value getNewValue()
	{
		return newValue;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setNewValue(Value newNewValue)
	{
		Value oldNewValue = newValue;
		newValue = newNewValue;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, AttributePackage.REPLACE_SINGLE_VALUED_EATTRIBUTE__NEW_VALUE, oldNewValue, newValue));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isWasUnset()
	{
		return wasUnset;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setWasUnset(boolean newWasUnset)
	{
		boolean oldWasUnset = wasUnset;
		wasUnset = newWasUnset;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, AttributePackage.REPLACE_SINGLE_VALUED_EATTRIBUTE__WAS_UNSET, oldWasUnset, wasUnset));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Value getOldValue()
	{
		return oldValue;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setOldValue(Value newOldValue)
	{
		Value oldOldValue = oldValue;
		oldValue = newOldValue;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, AttributePackage.REPLACE_SINGLE_VALUED_EATTRIBUTE__OLD_VALUE, oldOldValue, oldValue));
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
			case AttributePackage.REPLACE_SINGLE_VALUED_EATTRIBUTE__NEW_VALUE:
				return getNewValue();
			case AttributePackage.REPLACE_SINGLE_VALUED_EATTRIBUTE__WAS_UNSET:
				return isWasUnset();
			case AttributePackage.REPLACE_SINGLE_VALUED_EATTRIBUTE__OLD_VALUE:
				return getOldValue();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue)
	{
		switch (featureID)
		{
			case AttributePackage.REPLACE_SINGLE_VALUED_EATTRIBUTE__NEW_VALUE:
				setNewValue((Value)newValue);
				return;
			case AttributePackage.REPLACE_SINGLE_VALUED_EATTRIBUTE__WAS_UNSET:
				setWasUnset((Boolean)newValue);
				return;
			case AttributePackage.REPLACE_SINGLE_VALUED_EATTRIBUTE__OLD_VALUE:
				setOldValue((Value)newValue);
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
			case AttributePackage.REPLACE_SINGLE_VALUED_EATTRIBUTE__NEW_VALUE:
				setNewValue((Value)null);
				return;
			case AttributePackage.REPLACE_SINGLE_VALUED_EATTRIBUTE__WAS_UNSET:
				setWasUnset(WAS_UNSET_EDEFAULT);
				return;
			case AttributePackage.REPLACE_SINGLE_VALUED_EATTRIBUTE__OLD_VALUE:
				setOldValue((Value)null);
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
			case AttributePackage.REPLACE_SINGLE_VALUED_EATTRIBUTE__NEW_VALUE:
				return newValue != null;
			case AttributePackage.REPLACE_SINGLE_VALUED_EATTRIBUTE__WAS_UNSET:
				return wasUnset != WAS_UNSET_EDEFAULT;
			case AttributePackage.REPLACE_SINGLE_VALUED_EATTRIBUTE__OLD_VALUE:
				return oldValue != null;
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public int eBaseStructuralFeatureID(int derivedFeatureID, Class<?> baseClass)
	{
		if (baseClass == UpdateAttributeEChange.class)
		{
			switch (derivedFeatureID)
			{
				default: return -1;
			}
		}
		if (baseClass == AdditiveAttributeEChange.class)
		{
			switch (derivedFeatureID)
			{
				case AttributePackage.REPLACE_SINGLE_VALUED_EATTRIBUTE__NEW_VALUE: return AttributePackage.ADDITIVE_ATTRIBUTE_ECHANGE__NEW_VALUE;
				case AttributePackage.REPLACE_SINGLE_VALUED_EATTRIBUTE__WAS_UNSET: return AttributePackage.ADDITIVE_ATTRIBUTE_ECHANGE__WAS_UNSET;
				default: return -1;
			}
		}
		if (baseClass == SubtractiveAttributeEChange.class)
		{
			switch (derivedFeatureID)
			{
				case AttributePackage.REPLACE_SINGLE_VALUED_EATTRIBUTE__OLD_VALUE: return AttributePackage.SUBTRACTIVE_ATTRIBUTE_ECHANGE__OLD_VALUE;
				default: return -1;
			}
		}
		return super.eBaseStructuralFeatureID(derivedFeatureID, baseClass);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public int eDerivedStructuralFeatureID(int baseFeatureID, Class<?> baseClass)
	{
		if (baseClass == UpdateAttributeEChange.class)
		{
			switch (baseFeatureID)
			{
				default: return -1;
			}
		}
		if (baseClass == AdditiveAttributeEChange.class)
		{
			switch (baseFeatureID)
			{
				case AttributePackage.ADDITIVE_ATTRIBUTE_ECHANGE__NEW_VALUE: return AttributePackage.REPLACE_SINGLE_VALUED_EATTRIBUTE__NEW_VALUE;
				case AttributePackage.ADDITIVE_ATTRIBUTE_ECHANGE__WAS_UNSET: return AttributePackage.REPLACE_SINGLE_VALUED_EATTRIBUTE__WAS_UNSET;
				default: return -1;
			}
		}
		if (baseClass == SubtractiveAttributeEChange.class)
		{
			switch (baseFeatureID)
			{
				case AttributePackage.SUBTRACTIVE_ATTRIBUTE_ECHANGE__OLD_VALUE: return AttributePackage.REPLACE_SINGLE_VALUED_EATTRIBUTE__OLD_VALUE;
				default: return -1;
			}
		}
		return super.eDerivedStructuralFeatureID(baseFeatureID, baseClass);
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
		result.append(" (newValue: ");
		result.append(newValue);
		result.append(", wasUnset: ");
		result.append(wasUnset);
		result.append(", oldValue: ");
		result.append(oldValue);
		result.append(')');
		return result.toString();
	}

} //ReplaceSingleValuedEAttributeImpl
