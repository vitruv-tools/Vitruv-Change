/**
 */
package tools.vitruv.change.atomic.feature.attribute.impl;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EGenericType;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.ETypeParameter;
import org.eclipse.emf.ecore.EcorePackage;

import org.eclipse.emf.ecore.impl.EPackageImpl;

import tools.vitruv.change.atomic.AtomicPackage;

import tools.vitruv.change.atomic.eobject.EobjectPackage;

import tools.vitruv.change.atomic.eobject.impl.EobjectPackageImpl;

import tools.vitruv.change.atomic.feature.FeaturePackage;

import tools.vitruv.change.atomic.feature.attribute.AdditiveAttributeEChange;
import tools.vitruv.change.atomic.feature.attribute.AttributeFactory;
import tools.vitruv.change.atomic.feature.attribute.AttributePackage;
import tools.vitruv.change.atomic.feature.attribute.InsertEAttributeValue;
import tools.vitruv.change.atomic.feature.attribute.RemoveEAttributeValue;
import tools.vitruv.change.atomic.feature.attribute.ReplaceSingleValuedEAttribute;
import tools.vitruv.change.atomic.feature.attribute.SubtractiveAttributeEChange;
import tools.vitruv.change.atomic.feature.attribute.UpdateAttributeEChange;

import tools.vitruv.change.atomic.feature.impl.FeaturePackageImpl;

import tools.vitruv.change.atomic.feature.list.ListPackage;

import tools.vitruv.change.atomic.feature.list.impl.ListPackageImpl;

import tools.vitruv.change.atomic.feature.reference.ReferencePackage;

import tools.vitruv.change.atomic.feature.reference.impl.ReferencePackageImpl;

import tools.vitruv.change.atomic.feature.single.SinglePackage;

import tools.vitruv.change.atomic.feature.single.impl.SinglePackageImpl;

import tools.vitruv.change.atomic.impl.AtomicPackageImpl;

import tools.vitruv.change.atomic.root.RootPackage;

import tools.vitruv.change.atomic.root.impl.RootPackageImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class AttributePackageImpl extends EPackageImpl implements AttributePackage
{
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass updateAttributeEChangeEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass additiveAttributeEChangeEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass subtractiveAttributeEChangeEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass insertEAttributeValueEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass removeEAttributeValueEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass replaceSingleValuedEAttributeEClass = null;

	/**
	 * Creates an instance of the model <b>Package</b>, registered with
	 * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
	 * package URI value.
	 * <p>Note: the correct way to create the package is via the static
	 * factory method {@link #init init()}, which also performs
	 * initialization of the package, or returns the registered package,
	 * if one already exists.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.emf.ecore.EPackage.Registry
	 * @see tools.vitruv.change.atomic.feature.attribute.AttributePackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private AttributePackageImpl()
	{
		super(eNS_URI, AttributeFactory.eINSTANCE);
	}
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static boolean isInited = false;

	/**
	 * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
	 *
	 * <p>This method is used to initialize {@link AttributePackage#eINSTANCE} when that field is accessed.
	 * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static AttributePackage init()
	{
		if (isInited) return (AttributePackage)EPackage.Registry.INSTANCE.getEPackage(AttributePackage.eNS_URI);

		// Obtain or create and register package
		Object registeredAttributePackage = EPackage.Registry.INSTANCE.get(eNS_URI);
		AttributePackageImpl theAttributePackage = registeredAttributePackage instanceof AttributePackageImpl ? (AttributePackageImpl)registeredAttributePackage : new AttributePackageImpl();

		isInited = true;

		// Initialize simple dependencies
		EcorePackage.eINSTANCE.eClass();

		// Obtain or create and register interdependencies
		Object registeredPackage = EPackage.Registry.INSTANCE.getEPackage(AtomicPackage.eNS_URI);
		AtomicPackageImpl theAtomicPackage = (AtomicPackageImpl)(registeredPackage instanceof AtomicPackageImpl ? registeredPackage : AtomicPackage.eINSTANCE);
		registeredPackage = EPackage.Registry.INSTANCE.getEPackage(EobjectPackage.eNS_URI);
		EobjectPackageImpl theEobjectPackage = (EobjectPackageImpl)(registeredPackage instanceof EobjectPackageImpl ? registeredPackage : EobjectPackage.eINSTANCE);
		registeredPackage = EPackage.Registry.INSTANCE.getEPackage(RootPackage.eNS_URI);
		RootPackageImpl theRootPackage = (RootPackageImpl)(registeredPackage instanceof RootPackageImpl ? registeredPackage : RootPackage.eINSTANCE);
		registeredPackage = EPackage.Registry.INSTANCE.getEPackage(FeaturePackage.eNS_URI);
		FeaturePackageImpl theFeaturePackage = (FeaturePackageImpl)(registeredPackage instanceof FeaturePackageImpl ? registeredPackage : FeaturePackage.eINSTANCE);
		registeredPackage = EPackage.Registry.INSTANCE.getEPackage(SinglePackage.eNS_URI);
		SinglePackageImpl theSinglePackage = (SinglePackageImpl)(registeredPackage instanceof SinglePackageImpl ? registeredPackage : SinglePackage.eINSTANCE);
		registeredPackage = EPackage.Registry.INSTANCE.getEPackage(ListPackage.eNS_URI);
		ListPackageImpl theListPackage = (ListPackageImpl)(registeredPackage instanceof ListPackageImpl ? registeredPackage : ListPackage.eINSTANCE);
		registeredPackage = EPackage.Registry.INSTANCE.getEPackage(ReferencePackage.eNS_URI);
		ReferencePackageImpl theReferencePackage = (ReferencePackageImpl)(registeredPackage instanceof ReferencePackageImpl ? registeredPackage : ReferencePackage.eINSTANCE);

		// Create package meta-data objects
		theAttributePackage.createPackageContents();
		theAtomicPackage.createPackageContents();
		theEobjectPackage.createPackageContents();
		theRootPackage.createPackageContents();
		theFeaturePackage.createPackageContents();
		theSinglePackage.createPackageContents();
		theListPackage.createPackageContents();
		theReferencePackage.createPackageContents();

		// Initialize created meta-data
		theAttributePackage.initializePackageContents();
		theAtomicPackage.initializePackageContents();
		theEobjectPackage.initializePackageContents();
		theRootPackage.initializePackageContents();
		theFeaturePackage.initializePackageContents();
		theSinglePackage.initializePackageContents();
		theListPackage.initializePackageContents();
		theReferencePackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		theAttributePackage.freeze();

		// Update the registry and return the package
		EPackage.Registry.INSTANCE.put(AttributePackage.eNS_URI, theAttributePackage);
		return theAttributePackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getUpdateAttributeEChange()
	{
		return updateAttributeEChangeEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getAdditiveAttributeEChange()
	{
		return additiveAttributeEChangeEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getAdditiveAttributeEChange_NewValue()
	{
		return (EAttribute)additiveAttributeEChangeEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getAdditiveAttributeEChange_WasUnset()
	{
		return (EAttribute)additiveAttributeEChangeEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getSubtractiveAttributeEChange()
	{
		return subtractiveAttributeEChangeEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getSubtractiveAttributeEChange_OldValue()
	{
		return (EAttribute)subtractiveAttributeEChangeEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getInsertEAttributeValue()
	{
		return insertEAttributeValueEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getRemoveEAttributeValue()
	{
		return removeEAttributeValueEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getReplaceSingleValuedEAttribute()
	{
		return replaceSingleValuedEAttributeEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public AttributeFactory getAttributeFactory()
	{
		return (AttributeFactory)getEFactoryInstance();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isCreated = false;

	/**
	 * Creates the meta-model objects for the package.  This method is
	 * guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void createPackageContents()
	{
		if (isCreated) return;
		isCreated = true;

		// Create classes and their features
		updateAttributeEChangeEClass = createEClass(UPDATE_ATTRIBUTE_ECHANGE);

		additiveAttributeEChangeEClass = createEClass(ADDITIVE_ATTRIBUTE_ECHANGE);
		createEAttribute(additiveAttributeEChangeEClass, ADDITIVE_ATTRIBUTE_ECHANGE__NEW_VALUE);
		createEAttribute(additiveAttributeEChangeEClass, ADDITIVE_ATTRIBUTE_ECHANGE__WAS_UNSET);

		subtractiveAttributeEChangeEClass = createEClass(SUBTRACTIVE_ATTRIBUTE_ECHANGE);
		createEAttribute(subtractiveAttributeEChangeEClass, SUBTRACTIVE_ATTRIBUTE_ECHANGE__OLD_VALUE);

		insertEAttributeValueEClass = createEClass(INSERT_EATTRIBUTE_VALUE);

		removeEAttributeValueEClass = createEClass(REMOVE_EATTRIBUTE_VALUE);

		replaceSingleValuedEAttributeEClass = createEClass(REPLACE_SINGLE_VALUED_EATTRIBUTE);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isInitialized = false;

	/**
	 * Complete the initialization of the package and its meta-model.  This
	 * method is guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void initializePackageContents()
	{
		if (isInitialized) return;
		isInitialized = true;

		// Initialize package
		setName(eNAME);
		setNsPrefix(eNS_PREFIX);
		setNsURI(eNS_URI);

		// Obtain other dependent packages
		FeaturePackage theFeaturePackage = (FeaturePackage)EPackage.Registry.INSTANCE.getEPackage(FeaturePackage.eNS_URI);
		EcorePackage theEcorePackage = (EcorePackage)EPackage.Registry.INSTANCE.getEPackage(EcorePackage.eNS_URI);
		AtomicPackage theAtomicPackage = (AtomicPackage)EPackage.Registry.INSTANCE.getEPackage(AtomicPackage.eNS_URI);
		ListPackage theListPackage = (ListPackage)EPackage.Registry.INSTANCE.getEPackage(ListPackage.eNS_URI);
		SinglePackage theSinglePackage = (SinglePackage)EPackage.Registry.INSTANCE.getEPackage(SinglePackage.eNS_URI);

		// Create type parameters
		ETypeParameter updateAttributeEChangeEClass_Element = addETypeParameter(updateAttributeEChangeEClass, "Element");
		ETypeParameter additiveAttributeEChangeEClass_Element = addETypeParameter(additiveAttributeEChangeEClass, "Element");
		ETypeParameter additiveAttributeEChangeEClass_Value = addETypeParameter(additiveAttributeEChangeEClass, "Value");
		ETypeParameter subtractiveAttributeEChangeEClass_Element = addETypeParameter(subtractiveAttributeEChangeEClass, "Element");
		ETypeParameter subtractiveAttributeEChangeEClass_Value = addETypeParameter(subtractiveAttributeEChangeEClass, "Value");
		ETypeParameter insertEAttributeValueEClass_Element = addETypeParameter(insertEAttributeValueEClass, "Element");
		ETypeParameter insertEAttributeValueEClass_Value = addETypeParameter(insertEAttributeValueEClass, "Value");
		ETypeParameter removeEAttributeValueEClass_Element = addETypeParameter(removeEAttributeValueEClass, "Element");
		ETypeParameter removeEAttributeValueEClass_Value = addETypeParameter(removeEAttributeValueEClass, "Value");
		ETypeParameter replaceSingleValuedEAttributeEClass_Element = addETypeParameter(replaceSingleValuedEAttributeEClass, "Element");
		ETypeParameter replaceSingleValuedEAttributeEClass_Value = addETypeParameter(replaceSingleValuedEAttributeEClass, "Value");

		// Set bounds for type parameters
		EGenericType g1 = createEGenericType(ecorePackage.getEJavaObject());
		updateAttributeEChangeEClass_Element.getEBounds().add(g1);
		g1 = createEGenericType(theEcorePackage.getEJavaObject());
		additiveAttributeEChangeEClass_Element.getEBounds().add(g1);
		g1 = createEGenericType(ecorePackage.getEJavaObject());
		additiveAttributeEChangeEClass_Value.getEBounds().add(g1);
		g1 = createEGenericType(theEcorePackage.getEJavaObject());
		subtractiveAttributeEChangeEClass_Element.getEBounds().add(g1);
		g1 = createEGenericType(ecorePackage.getEJavaObject());
		subtractiveAttributeEChangeEClass_Value.getEBounds().add(g1);
		g1 = createEGenericType(theEcorePackage.getEJavaObject());
		insertEAttributeValueEClass_Element.getEBounds().add(g1);
		g1 = createEGenericType(ecorePackage.getEJavaObject());
		insertEAttributeValueEClass_Value.getEBounds().add(g1);
		g1 = createEGenericType(theEcorePackage.getEJavaObject());
		removeEAttributeValueEClass_Element.getEBounds().add(g1);
		g1 = createEGenericType(ecorePackage.getEJavaObject());
		removeEAttributeValueEClass_Value.getEBounds().add(g1);
		g1 = createEGenericType(theEcorePackage.getEJavaObject());
		replaceSingleValuedEAttributeEClass_Element.getEBounds().add(g1);
		g1 = createEGenericType(ecorePackage.getEJavaObject());
		replaceSingleValuedEAttributeEClass_Value.getEBounds().add(g1);

		// Add supertypes to classes
		g1 = createEGenericType(theFeaturePackage.getFeatureEChange());
		EGenericType g2 = createEGenericType(updateAttributeEChangeEClass_Element);
		g1.getETypeArguments().add(g2);
		g2 = createEGenericType(theEcorePackage.getEAttribute());
		g1.getETypeArguments().add(g2);
		updateAttributeEChangeEClass.getEGenericSuperTypes().add(g1);
		g1 = createEGenericType(this.getUpdateAttributeEChange());
		g2 = createEGenericType(additiveAttributeEChangeEClass_Element);
		g1.getETypeArguments().add(g2);
		additiveAttributeEChangeEClass.getEGenericSuperTypes().add(g1);
		g1 = createEGenericType(theAtomicPackage.getAdditiveEChange());
		g2 = createEGenericType(additiveAttributeEChangeEClass_Element);
		g1.getETypeArguments().add(g2);
		g2 = createEGenericType(additiveAttributeEChangeEClass_Value);
		g1.getETypeArguments().add(g2);
		additiveAttributeEChangeEClass.getEGenericSuperTypes().add(g1);
		g1 = createEGenericType(this.getUpdateAttributeEChange());
		g2 = createEGenericType(subtractiveAttributeEChangeEClass_Element);
		g1.getETypeArguments().add(g2);
		subtractiveAttributeEChangeEClass.getEGenericSuperTypes().add(g1);
		g1 = createEGenericType(theAtomicPackage.getSubtractiveEChange());
		g2 = createEGenericType(subtractiveAttributeEChangeEClass_Element);
		g1.getETypeArguments().add(g2);
		g2 = createEGenericType(subtractiveAttributeEChangeEClass_Value);
		g1.getETypeArguments().add(g2);
		subtractiveAttributeEChangeEClass.getEGenericSuperTypes().add(g1);
		g1 = createEGenericType(theListPackage.getInsertInListEChange());
		g2 = createEGenericType(insertEAttributeValueEClass_Element);
		g1.getETypeArguments().add(g2);
		g2 = createEGenericType(theEcorePackage.getEAttribute());
		g1.getETypeArguments().add(g2);
		g2 = createEGenericType(insertEAttributeValueEClass_Value);
		g1.getETypeArguments().add(g2);
		insertEAttributeValueEClass.getEGenericSuperTypes().add(g1);
		g1 = createEGenericType(this.getAdditiveAttributeEChange());
		g2 = createEGenericType(insertEAttributeValueEClass_Element);
		g1.getETypeArguments().add(g2);
		g2 = createEGenericType(insertEAttributeValueEClass_Value);
		g1.getETypeArguments().add(g2);
		insertEAttributeValueEClass.getEGenericSuperTypes().add(g1);
		g1 = createEGenericType(theListPackage.getRemoveFromListEChange());
		g2 = createEGenericType(removeEAttributeValueEClass_Element);
		g1.getETypeArguments().add(g2);
		g2 = createEGenericType(theEcorePackage.getEAttribute());
		g1.getETypeArguments().add(g2);
		g2 = createEGenericType(removeEAttributeValueEClass_Value);
		g1.getETypeArguments().add(g2);
		removeEAttributeValueEClass.getEGenericSuperTypes().add(g1);
		g1 = createEGenericType(this.getSubtractiveAttributeEChange());
		g2 = createEGenericType(removeEAttributeValueEClass_Element);
		g1.getETypeArguments().add(g2);
		g2 = createEGenericType(removeEAttributeValueEClass_Value);
		g1.getETypeArguments().add(g2);
		removeEAttributeValueEClass.getEGenericSuperTypes().add(g1);
		g1 = createEGenericType(theSinglePackage.getReplaceSingleValuedFeatureEChange());
		g2 = createEGenericType(replaceSingleValuedEAttributeEClass_Element);
		g1.getETypeArguments().add(g2);
		g2 = createEGenericType(theEcorePackage.getEAttribute());
		g1.getETypeArguments().add(g2);
		g2 = createEGenericType(replaceSingleValuedEAttributeEClass_Value);
		g1.getETypeArguments().add(g2);
		replaceSingleValuedEAttributeEClass.getEGenericSuperTypes().add(g1);
		g1 = createEGenericType(this.getAdditiveAttributeEChange());
		g2 = createEGenericType(replaceSingleValuedEAttributeEClass_Element);
		g1.getETypeArguments().add(g2);
		g2 = createEGenericType(replaceSingleValuedEAttributeEClass_Value);
		g1.getETypeArguments().add(g2);
		replaceSingleValuedEAttributeEClass.getEGenericSuperTypes().add(g1);
		g1 = createEGenericType(this.getSubtractiveAttributeEChange());
		g2 = createEGenericType(replaceSingleValuedEAttributeEClass_Element);
		g1.getETypeArguments().add(g2);
		g2 = createEGenericType(replaceSingleValuedEAttributeEClass_Value);
		g1.getETypeArguments().add(g2);
		replaceSingleValuedEAttributeEClass.getEGenericSuperTypes().add(g1);

		// Initialize classes, features, and operations; add parameters
		initEClass(updateAttributeEChangeEClass, UpdateAttributeEChange.class, "UpdateAttributeEChange", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(additiveAttributeEChangeEClass, AdditiveAttributeEChange.class, "AdditiveAttributeEChange", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		g1 = createEGenericType(additiveAttributeEChangeEClass_Value);
		initEAttribute(getAdditiveAttributeEChange_NewValue(), g1, "newValue", null, 1, 1, AdditiveAttributeEChange.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getAdditiveAttributeEChange_WasUnset(), theEcorePackage.getEBoolean(), "wasUnset", null, 0, 1, AdditiveAttributeEChange.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(subtractiveAttributeEChangeEClass, SubtractiveAttributeEChange.class, "SubtractiveAttributeEChange", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		g1 = createEGenericType(subtractiveAttributeEChangeEClass_Value);
		initEAttribute(getSubtractiveAttributeEChange_OldValue(), g1, "oldValue", null, 1, 1, SubtractiveAttributeEChange.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(insertEAttributeValueEClass, InsertEAttributeValue.class, "InsertEAttributeValue", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(removeEAttributeValueEClass, RemoveEAttributeValue.class, "RemoveEAttributeValue", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(replaceSingleValuedEAttributeEClass, ReplaceSingleValuedEAttribute.class, "ReplaceSingleValuedEAttribute", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
	}

} //AttributePackageImpl
