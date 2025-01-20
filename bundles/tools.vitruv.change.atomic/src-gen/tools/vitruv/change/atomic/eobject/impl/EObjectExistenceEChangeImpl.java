/**
 */
package tools.vitruv.change.atomic.eobject.impl;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import tools.vitruv.change.atomic.eobject.EObjectExistenceEChange;
import tools.vitruv.change.atomic.eobject.EobjectPackage;

import tools.vitruv.change.atomic.impl.EChangeImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>EObject Existence EChange</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link tools.vitruv.change.atomic.eobject.impl.EObjectExistenceEChangeImpl#getAffectedElement <em>Affected Element</em>}</li>
 *   <li>{@link tools.vitruv.change.atomic.eobject.impl.EObjectExistenceEChangeImpl#getIdAttributeValue <em>Id Attribute Value</em>}</li>
 *   <li>{@link tools.vitruv.change.atomic.eobject.impl.EObjectExistenceEChangeImpl#getAffectedEObjectType <em>Affected EObject Type</em>}</li>
 * </ul>
 *
 * @generated
 */
public abstract class EObjectExistenceEChangeImpl<Element extends Object> extends EChangeImpl<Element> implements EObjectExistenceEChange<Element>
{
	/**
	 * The cached value of the '{@link #getAffectedElement() <em>Affected Element</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAffectedElement()
	 * @generated
	 * @ordered
	 */
	protected Element affectedElement;

	/**
	 * The default value of the '{@link #getIdAttributeValue() <em>Id Attribute Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getIdAttributeValue()
	 * @generated
	 * @ordered
	 */
	protected static final String ID_ATTRIBUTE_VALUE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getIdAttributeValue() <em>Id Attribute Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getIdAttributeValue()
	 * @generated
	 * @ordered
	 */
	protected String idAttributeValue = ID_ATTRIBUTE_VALUE_EDEFAULT;

	/**
	 * The cached value of the '{@link #getAffectedEObjectType() <em>Affected EObject Type</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAffectedEObjectType()
	 * @generated
	 * @ordered
	 */
	protected EClass affectedEObjectType;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected EObjectExistenceEChangeImpl()
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
		return EobjectPackage.Literals.EOBJECT_EXISTENCE_ECHANGE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Element getAffectedElement()
	{
		return affectedElement;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setAffectedElement(Element newAffectedElement)
	{
		Element oldAffectedElement = affectedElement;
		affectedElement = newAffectedElement;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EobjectPackage.EOBJECT_EXISTENCE_ECHANGE__AFFECTED_ELEMENT, oldAffectedElement, affectedElement));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getIdAttributeValue()
	{
		return idAttributeValue;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setIdAttributeValue(String newIdAttributeValue)
	{
		String oldIdAttributeValue = idAttributeValue;
		idAttributeValue = newIdAttributeValue;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EobjectPackage.EOBJECT_EXISTENCE_ECHANGE__ID_ATTRIBUTE_VALUE, oldIdAttributeValue, idAttributeValue));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getAffectedEObjectType()
	{
		if (affectedEObjectType != null && affectedEObjectType.eIsProxy())
		{
			InternalEObject oldAffectedEObjectType = (InternalEObject)affectedEObjectType;
			affectedEObjectType = (EClass)eResolveProxy(oldAffectedEObjectType);
			if (affectedEObjectType != oldAffectedEObjectType)
			{
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, EobjectPackage.EOBJECT_EXISTENCE_ECHANGE__AFFECTED_EOBJECT_TYPE, oldAffectedEObjectType, affectedEObjectType));
			}
		}
		return affectedEObjectType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass basicGetAffectedEObjectType()
	{
		return affectedEObjectType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setAffectedEObjectType(EClass newAffectedEObjectType)
	{
		EClass oldAffectedEObjectType = affectedEObjectType;
		affectedEObjectType = newAffectedEObjectType;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EobjectPackage.EOBJECT_EXISTENCE_ECHANGE__AFFECTED_EOBJECT_TYPE, oldAffectedEObjectType, affectedEObjectType));
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
			case EobjectPackage.EOBJECT_EXISTENCE_ECHANGE__AFFECTED_ELEMENT:
				return getAffectedElement();
			case EobjectPackage.EOBJECT_EXISTENCE_ECHANGE__ID_ATTRIBUTE_VALUE:
				return getIdAttributeValue();
			case EobjectPackage.EOBJECT_EXISTENCE_ECHANGE__AFFECTED_EOBJECT_TYPE:
				if (resolve) return getAffectedEObjectType();
				return basicGetAffectedEObjectType();
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
			case EobjectPackage.EOBJECT_EXISTENCE_ECHANGE__AFFECTED_ELEMENT:
				setAffectedElement((Element)newValue);
				return;
			case EobjectPackage.EOBJECT_EXISTENCE_ECHANGE__ID_ATTRIBUTE_VALUE:
				setIdAttributeValue((String)newValue);
				return;
			case EobjectPackage.EOBJECT_EXISTENCE_ECHANGE__AFFECTED_EOBJECT_TYPE:
				setAffectedEObjectType((EClass)newValue);
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
			case EobjectPackage.EOBJECT_EXISTENCE_ECHANGE__AFFECTED_ELEMENT:
				setAffectedElement((Element)null);
				return;
			case EobjectPackage.EOBJECT_EXISTENCE_ECHANGE__ID_ATTRIBUTE_VALUE:
				setIdAttributeValue(ID_ATTRIBUTE_VALUE_EDEFAULT);
				return;
			case EobjectPackage.EOBJECT_EXISTENCE_ECHANGE__AFFECTED_EOBJECT_TYPE:
				setAffectedEObjectType((EClass)null);
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
			case EobjectPackage.EOBJECT_EXISTENCE_ECHANGE__AFFECTED_ELEMENT:
				return affectedElement != null;
			case EobjectPackage.EOBJECT_EXISTENCE_ECHANGE__ID_ATTRIBUTE_VALUE:
				return ID_ATTRIBUTE_VALUE_EDEFAULT == null ? idAttributeValue != null : !ID_ATTRIBUTE_VALUE_EDEFAULT.equals(idAttributeValue);
			case EobjectPackage.EOBJECT_EXISTENCE_ECHANGE__AFFECTED_EOBJECT_TYPE:
				return affectedEObjectType != null;
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
		result.append(" (idAttributeValue: ");
		result.append(idAttributeValue);
		result.append(')');
		return result.toString();
	}

} //EObjectExistenceEChangeImpl
