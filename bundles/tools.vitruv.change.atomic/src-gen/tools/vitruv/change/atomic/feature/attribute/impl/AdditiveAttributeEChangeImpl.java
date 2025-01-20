/**
 */
package tools.vitruv.change.atomic.feature.attribute.impl;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import tools.vitruv.change.atomic.feature.attribute.AdditiveAttributeEChange;
import tools.vitruv.change.atomic.feature.attribute.AttributePackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Additive Attribute EChange</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link tools.vitruv.change.atomic.feature.attribute.impl.AdditiveAttributeEChangeImpl#getNewValue <em>New Value</em>}</li>
 *   <li>{@link tools.vitruv.change.atomic.feature.attribute.impl.AdditiveAttributeEChangeImpl#isWasUnset <em>Was Unset</em>}</li>
 * </ul>
 *
 * @generated
 */
public abstract class AdditiveAttributeEChangeImpl<Element extends Object, Value extends Object> extends UpdateAttributeEChangeImpl<Element> implements AdditiveAttributeEChange<Element, Value>
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
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected AdditiveAttributeEChangeImpl()
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
		return AttributePackage.Literals.ADDITIVE_ATTRIBUTE_ECHANGE;
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
			eNotify(new ENotificationImpl(this, Notification.SET, AttributePackage.ADDITIVE_ATTRIBUTE_ECHANGE__NEW_VALUE, oldNewValue, newValue));
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
			eNotify(new ENotificationImpl(this, Notification.SET, AttributePackage.ADDITIVE_ATTRIBUTE_ECHANGE__WAS_UNSET, oldWasUnset, wasUnset));
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
			case AttributePackage.ADDITIVE_ATTRIBUTE_ECHANGE__NEW_VALUE:
				return getNewValue();
			case AttributePackage.ADDITIVE_ATTRIBUTE_ECHANGE__WAS_UNSET:
				return isWasUnset();
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
			case AttributePackage.ADDITIVE_ATTRIBUTE_ECHANGE__NEW_VALUE:
				setNewValue((Value)newValue);
				return;
			case AttributePackage.ADDITIVE_ATTRIBUTE_ECHANGE__WAS_UNSET:
				setWasUnset((Boolean)newValue);
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
			case AttributePackage.ADDITIVE_ATTRIBUTE_ECHANGE__NEW_VALUE:
				setNewValue((Value)null);
				return;
			case AttributePackage.ADDITIVE_ATTRIBUTE_ECHANGE__WAS_UNSET:
				setWasUnset(WAS_UNSET_EDEFAULT);
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
			case AttributePackage.ADDITIVE_ATTRIBUTE_ECHANGE__NEW_VALUE:
				return newValue != null;
			case AttributePackage.ADDITIVE_ATTRIBUTE_ECHANGE__WAS_UNSET:
				return wasUnset != WAS_UNSET_EDEFAULT;
		}
		return super.eIsSet(featureID);
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
		result.append(')');
		return result.toString();
	}

} //AdditiveAttributeEChangeImpl
