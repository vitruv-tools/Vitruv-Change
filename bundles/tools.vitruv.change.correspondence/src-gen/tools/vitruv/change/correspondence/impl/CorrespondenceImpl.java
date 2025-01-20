/**
 */
package tools.vitruv.change.correspondence.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EObjectEList;

import tools.vitruv.change.correspondence.Correspondence;
import tools.vitruv.change.correspondence.CorrespondencePackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Correspondence</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link tools.vitruv.change.correspondence.impl.CorrespondenceImpl#getTag <em>Tag</em>}</li>
 *   <li>{@link tools.vitruv.change.correspondence.impl.CorrespondenceImpl#getLeftEObjects <em>Left EObjects</em>}</li>
 *   <li>{@link tools.vitruv.change.correspondence.impl.CorrespondenceImpl#getRightEObjects <em>Right EObjects</em>}</li>
 * </ul>
 *
 * @generated
 */
public abstract class CorrespondenceImpl extends MinimalEObjectImpl.Container implements Correspondence
{
	/**
	 * The default value of the '{@link #getTag() <em>Tag</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTag()
	 * @generated
	 * @ordered
	 */
	protected static final String TAG_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getTag() <em>Tag</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTag()
	 * @generated
	 * @ordered
	 */
	protected String tag = TAG_EDEFAULT;

	/**
	 * The cached value of the '{@link #getLeftEObjects() <em>Left EObjects</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getLeftEObjects()
	 * @generated
	 * @ordered
	 */
	protected EList<EObject> leftEObjects;

	/**
	 * The cached value of the '{@link #getRightEObjects() <em>Right EObjects</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRightEObjects()
	 * @generated
	 * @ordered
	 */
	protected EList<EObject> rightEObjects;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected CorrespondenceImpl()
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
		return CorrespondencePackage.Literals.CORRESPONDENCE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getTag()
	{
		return tag;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setTag(String newTag)
	{
		String oldTag = tag;
		tag = newTag;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CorrespondencePackage.CORRESPONDENCE__TAG, oldTag, tag));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<EObject> getLeftEObjects()
	{
		if (leftEObjects == null)
		{
			leftEObjects = new EObjectEList<EObject>(EObject.class, this, CorrespondencePackage.CORRESPONDENCE__LEFT_EOBJECTS);
		}
		return leftEObjects;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<EObject> getRightEObjects()
	{
		if (rightEObjects == null)
		{
			rightEObjects = new EObjectEList<EObject>(EObject.class, this, CorrespondencePackage.CORRESPONDENCE__RIGHT_EOBJECTS);
		}
		return rightEObjects;
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
			case CorrespondencePackage.CORRESPONDENCE__TAG:
				return getTag();
			case CorrespondencePackage.CORRESPONDENCE__LEFT_EOBJECTS:
				return getLeftEObjects();
			case CorrespondencePackage.CORRESPONDENCE__RIGHT_EOBJECTS:
				return getRightEObjects();
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
			case CorrespondencePackage.CORRESPONDENCE__TAG:
				setTag((String)newValue);
				return;
			case CorrespondencePackage.CORRESPONDENCE__LEFT_EOBJECTS:
				getLeftEObjects().clear();
				getLeftEObjects().addAll((Collection<? extends EObject>)newValue);
				return;
			case CorrespondencePackage.CORRESPONDENCE__RIGHT_EOBJECTS:
				getRightEObjects().clear();
				getRightEObjects().addAll((Collection<? extends EObject>)newValue);
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
			case CorrespondencePackage.CORRESPONDENCE__TAG:
				setTag(TAG_EDEFAULT);
				return;
			case CorrespondencePackage.CORRESPONDENCE__LEFT_EOBJECTS:
				getLeftEObjects().clear();
				return;
			case CorrespondencePackage.CORRESPONDENCE__RIGHT_EOBJECTS:
				getRightEObjects().clear();
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
			case CorrespondencePackage.CORRESPONDENCE__TAG:
				return TAG_EDEFAULT == null ? tag != null : !TAG_EDEFAULT.equals(tag);
			case CorrespondencePackage.CORRESPONDENCE__LEFT_EOBJECTS:
				return leftEObjects != null && !leftEObjects.isEmpty();
			case CorrespondencePackage.CORRESPONDENCE__RIGHT_EOBJECTS:
				return rightEObjects != null && !rightEObjects.isEmpty();
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
		result.append(" (tag: ");
		result.append(tag);
		result.append(')');
		return result.toString();
	}

} //CorrespondenceImpl
