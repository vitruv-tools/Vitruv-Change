/**
 */
package tools.vitruv.change.atomic.feature;

import org.eclipse.emf.ecore.EStructuralFeature;

import tools.vitruv.change.atomic.EChange;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>EChange</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * *
 * Abstract EChange which changes an attribute or reference of an EObject.
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link tools.vitruv.change.atomic.feature.FeatureEChange#getAffectedFeature <em>Affected Feature</em>}</li>
 *   <li>{@link tools.vitruv.change.atomic.feature.FeatureEChange#getAffectedElement <em>Affected Element</em>}</li>
 * </ul>
 *
 * @see tools.vitruv.change.atomic.feature.FeaturePackage#getFeatureEChange()
 * @model abstract="true" ElementBounds="org.eclipse.emf.ecore.EJavaObject"
 * @generated
 */
public interface FeatureEChange<Element extends Object, Feature extends EStructuralFeature> extends EChange<Element>
{
	/**
	 * Returns the value of the '<em><b>Affected Feature</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * *
	 * The affected attribute or reference.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Affected Feature</em>' reference.
	 * @see #setAffectedFeature(EStructuralFeature)
	 * @see tools.vitruv.change.atomic.feature.FeaturePackage#getFeatureEChange_AffectedFeature()
	 * @model required="true"
	 * @generated
	 */
	Feature getAffectedFeature();

	/**
	 * Sets the value of the '{@link tools.vitruv.change.atomic.feature.FeatureEChange#getAffectedFeature <em>Affected Feature</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Affected Feature</em>' reference.
	 * @see #getAffectedFeature()
	 * @generated
	 */
	void setAffectedFeature(Feature value);

	/**
	 * Returns the value of the '<em><b>Affected Element</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * *
	 * The affected EObject of the change.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Affected Element</em>' reference.
	 * @see #setAffectedElement(Object)
	 * @see tools.vitruv.change.atomic.feature.FeaturePackage#getFeatureEChange_AffectedElement()
	 * @model kind="reference" resolveProxies="false" required="true"
	 * @generated
	 */
	Element getAffectedElement();

	/**
	 * Sets the value of the '{@link tools.vitruv.change.atomic.feature.FeatureEChange#getAffectedElement <em>Affected Element</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Affected Element</em>' reference.
	 * @see #getAffectedElement()
	 * @generated
	 */
	void setAffectedElement(Element value);

} // FeatureEChange
