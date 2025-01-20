/**
 */
package tools.vitruv.change.atomic.eobject.impl;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EGenericType;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.ETypeParameter;
import org.eclipse.emf.ecore.EcorePackage;

import org.eclipse.emf.ecore.impl.EPackageImpl;

import tools.vitruv.change.atomic.AtomicPackage;

import tools.vitruv.change.atomic.eobject.CreateEObject;
import tools.vitruv.change.atomic.eobject.DeleteEObject;
import tools.vitruv.change.atomic.eobject.EObjectAddedEChange;
import tools.vitruv.change.atomic.eobject.EObjectExistenceEChange;
import tools.vitruv.change.atomic.eobject.EObjectSubtractedEChange;
import tools.vitruv.change.atomic.eobject.EobjectFactory;
import tools.vitruv.change.atomic.eobject.EobjectPackage;

import tools.vitruv.change.atomic.feature.FeaturePackage;

import tools.vitruv.change.atomic.feature.attribute.AttributePackage;

import tools.vitruv.change.atomic.feature.attribute.impl.AttributePackageImpl;

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
public class EobjectPackageImpl extends EPackageImpl implements EobjectPackage
{
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass eObjectAddedEChangeEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass eObjectSubtractedEChangeEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass eObjectExistenceEChangeEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass createEObjectEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass deleteEObjectEClass = null;

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
	 * @see tools.vitruv.change.atomic.eobject.EobjectPackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private EobjectPackageImpl()
	{
		super(eNS_URI, EobjectFactory.eINSTANCE);
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
	 * <p>This method is used to initialize {@link EobjectPackage#eINSTANCE} when that field is accessed.
	 * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static EobjectPackage init()
	{
		if (isInited) return (EobjectPackage)EPackage.Registry.INSTANCE.getEPackage(EobjectPackage.eNS_URI);

		// Obtain or create and register package
		Object registeredEobjectPackage = EPackage.Registry.INSTANCE.get(eNS_URI);
		EobjectPackageImpl theEobjectPackage = registeredEobjectPackage instanceof EobjectPackageImpl ? (EobjectPackageImpl)registeredEobjectPackage : new EobjectPackageImpl();

		isInited = true;

		// Initialize simple dependencies
		EcorePackage.eINSTANCE.eClass();

		// Obtain or create and register interdependencies
		Object registeredPackage = EPackage.Registry.INSTANCE.getEPackage(AtomicPackage.eNS_URI);
		AtomicPackageImpl theAtomicPackage = (AtomicPackageImpl)(registeredPackage instanceof AtomicPackageImpl ? registeredPackage : AtomicPackage.eINSTANCE);
		registeredPackage = EPackage.Registry.INSTANCE.getEPackage(RootPackage.eNS_URI);
		RootPackageImpl theRootPackage = (RootPackageImpl)(registeredPackage instanceof RootPackageImpl ? registeredPackage : RootPackage.eINSTANCE);
		registeredPackage = EPackage.Registry.INSTANCE.getEPackage(FeaturePackage.eNS_URI);
		FeaturePackageImpl theFeaturePackage = (FeaturePackageImpl)(registeredPackage instanceof FeaturePackageImpl ? registeredPackage : FeaturePackage.eINSTANCE);
		registeredPackage = EPackage.Registry.INSTANCE.getEPackage(SinglePackage.eNS_URI);
		SinglePackageImpl theSinglePackage = (SinglePackageImpl)(registeredPackage instanceof SinglePackageImpl ? registeredPackage : SinglePackage.eINSTANCE);
		registeredPackage = EPackage.Registry.INSTANCE.getEPackage(ListPackage.eNS_URI);
		ListPackageImpl theListPackage = (ListPackageImpl)(registeredPackage instanceof ListPackageImpl ? registeredPackage : ListPackage.eINSTANCE);
		registeredPackage = EPackage.Registry.INSTANCE.getEPackage(AttributePackage.eNS_URI);
		AttributePackageImpl theAttributePackage = (AttributePackageImpl)(registeredPackage instanceof AttributePackageImpl ? registeredPackage : AttributePackage.eINSTANCE);
		registeredPackage = EPackage.Registry.INSTANCE.getEPackage(ReferencePackage.eNS_URI);
		ReferencePackageImpl theReferencePackage = (ReferencePackageImpl)(registeredPackage instanceof ReferencePackageImpl ? registeredPackage : ReferencePackage.eINSTANCE);

		// Create package meta-data objects
		theEobjectPackage.createPackageContents();
		theAtomicPackage.createPackageContents();
		theRootPackage.createPackageContents();
		theFeaturePackage.createPackageContents();
		theSinglePackage.createPackageContents();
		theListPackage.createPackageContents();
		theAttributePackage.createPackageContents();
		theReferencePackage.createPackageContents();

		// Initialize created meta-data
		theEobjectPackage.initializePackageContents();
		theAtomicPackage.initializePackageContents();
		theRootPackage.initializePackageContents();
		theFeaturePackage.initializePackageContents();
		theSinglePackage.initializePackageContents();
		theListPackage.initializePackageContents();
		theAttributePackage.initializePackageContents();
		theReferencePackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		theEobjectPackage.freeze();

		// Update the registry and return the package
		EPackage.Registry.INSTANCE.put(EobjectPackage.eNS_URI, theEobjectPackage);
		return theEobjectPackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getEObjectAddedEChange()
	{
		return eObjectAddedEChangeEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getEObjectAddedEChange_NewValue()
	{
		return (EReference)eObjectAddedEChangeEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getEObjectSubtractedEChange()
	{
		return eObjectSubtractedEChangeEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getEObjectSubtractedEChange_OldValue()
	{
		return (EReference)eObjectSubtractedEChangeEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getEObjectExistenceEChange()
	{
		return eObjectExistenceEChangeEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getEObjectExistenceEChange_AffectedElement()
	{
		return (EReference)eObjectExistenceEChangeEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getEObjectExistenceEChange_IdAttributeValue()
	{
		return (EAttribute)eObjectExistenceEChangeEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getEObjectExistenceEChange_AffectedEObjectType()
	{
		return (EReference)eObjectExistenceEChangeEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getCreateEObject()
	{
		return createEObjectEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getDeleteEObject()
	{
		return deleteEObjectEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EobjectFactory getEobjectFactory()
	{
		return (EobjectFactory)getEFactoryInstance();
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
		eObjectAddedEChangeEClass = createEClass(EOBJECT_ADDED_ECHANGE);
		createEReference(eObjectAddedEChangeEClass, EOBJECT_ADDED_ECHANGE__NEW_VALUE);

		eObjectSubtractedEChangeEClass = createEClass(EOBJECT_SUBTRACTED_ECHANGE);
		createEReference(eObjectSubtractedEChangeEClass, EOBJECT_SUBTRACTED_ECHANGE__OLD_VALUE);

		eObjectExistenceEChangeEClass = createEClass(EOBJECT_EXISTENCE_ECHANGE);
		createEReference(eObjectExistenceEChangeEClass, EOBJECT_EXISTENCE_ECHANGE__AFFECTED_ELEMENT);
		createEAttribute(eObjectExistenceEChangeEClass, EOBJECT_EXISTENCE_ECHANGE__ID_ATTRIBUTE_VALUE);
		createEReference(eObjectExistenceEChangeEClass, EOBJECT_EXISTENCE_ECHANGE__AFFECTED_EOBJECT_TYPE);

		createEObjectEClass = createEClass(CREATE_EOBJECT);

		deleteEObjectEClass = createEClass(DELETE_EOBJECT);
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
		AtomicPackage theAtomicPackage = (AtomicPackage)EPackage.Registry.INSTANCE.getEPackage(AtomicPackage.eNS_URI);
		EcorePackage theEcorePackage = (EcorePackage)EPackage.Registry.INSTANCE.getEPackage(EcorePackage.eNS_URI);

		// Create type parameters
		ETypeParameter eObjectAddedEChangeEClass_Element = addETypeParameter(eObjectAddedEChangeEClass, "Element");
		ETypeParameter eObjectSubtractedEChangeEClass_Element = addETypeParameter(eObjectSubtractedEChangeEClass, "Element");
		ETypeParameter eObjectExistenceEChangeEClass_Element = addETypeParameter(eObjectExistenceEChangeEClass, "Element");
		ETypeParameter createEObjectEClass_Element = addETypeParameter(createEObjectEClass, "Element");
		ETypeParameter deleteEObjectEClass_Element = addETypeParameter(deleteEObjectEClass, "Element");

		// Set bounds for type parameters
		EGenericType g1 = createEGenericType(ecorePackage.getEJavaObject());
		eObjectAddedEChangeEClass_Element.getEBounds().add(g1);
		g1 = createEGenericType(ecorePackage.getEJavaObject());
		eObjectSubtractedEChangeEClass_Element.getEBounds().add(g1);
		g1 = createEGenericType(ecorePackage.getEJavaObject());
		eObjectExistenceEChangeEClass_Element.getEBounds().add(g1);
		g1 = createEGenericType(ecorePackage.getEJavaObject());
		createEObjectEClass_Element.getEBounds().add(g1);
		g1 = createEGenericType(ecorePackage.getEJavaObject());
		deleteEObjectEClass_Element.getEBounds().add(g1);

		// Add supertypes to classes
		g1 = createEGenericType(theAtomicPackage.getAdditiveEChange());
		EGenericType g2 = createEGenericType(eObjectAddedEChangeEClass_Element);
		g1.getETypeArguments().add(g2);
		g2 = createEGenericType(eObjectAddedEChangeEClass_Element);
		g1.getETypeArguments().add(g2);
		eObjectAddedEChangeEClass.getEGenericSuperTypes().add(g1);
		g1 = createEGenericType(theAtomicPackage.getSubtractiveEChange());
		g2 = createEGenericType(eObjectSubtractedEChangeEClass_Element);
		g1.getETypeArguments().add(g2);
		g2 = createEGenericType(eObjectSubtractedEChangeEClass_Element);
		g1.getETypeArguments().add(g2);
		eObjectSubtractedEChangeEClass.getEGenericSuperTypes().add(g1);
		g1 = createEGenericType(theAtomicPackage.getEChange());
		g2 = createEGenericType(eObjectExistenceEChangeEClass_Element);
		g1.getETypeArguments().add(g2);
		eObjectExistenceEChangeEClass.getEGenericSuperTypes().add(g1);
		g1 = createEGenericType(this.getEObjectExistenceEChange());
		g2 = createEGenericType(createEObjectEClass_Element);
		g1.getETypeArguments().add(g2);
		createEObjectEClass.getEGenericSuperTypes().add(g1);
		g1 = createEGenericType(this.getEObjectExistenceEChange());
		g2 = createEGenericType(deleteEObjectEClass_Element);
		g1.getETypeArguments().add(g2);
		deleteEObjectEClass.getEGenericSuperTypes().add(g1);

		// Initialize classes, features, and operations; add parameters
		initEClass(eObjectAddedEChangeEClass, EObjectAddedEChange.class, "EObjectAddedEChange", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		g1 = createEGenericType(eObjectAddedEChangeEClass_Element);
		initEReference(getEObjectAddedEChange_NewValue(), g1, null, "newValue", null, 1, 1, EObjectAddedEChange.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(eObjectSubtractedEChangeEClass, EObjectSubtractedEChange.class, "EObjectSubtractedEChange", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		g1 = createEGenericType(eObjectSubtractedEChangeEClass_Element);
		initEReference(getEObjectSubtractedEChange_OldValue(), g1, null, "oldValue", null, 1, 1, EObjectSubtractedEChange.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(eObjectExistenceEChangeEClass, EObjectExistenceEChange.class, "EObjectExistenceEChange", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		g1 = createEGenericType(eObjectExistenceEChangeEClass_Element);
		initEReference(getEObjectExistenceEChange_AffectedElement(), g1, null, "affectedElement", null, 1, 1, EObjectExistenceEChange.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getEObjectExistenceEChange_IdAttributeValue(), theEcorePackage.getEString(), "idAttributeValue", null, 0, 1, EObjectExistenceEChange.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getEObjectExistenceEChange_AffectedEObjectType(), theEcorePackage.getEClass(), null, "affectedEObjectType", null, 0, 1, EObjectExistenceEChange.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(createEObjectEClass, CreateEObject.class, "CreateEObject", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(deleteEObjectEClass, DeleteEObject.class, "DeleteEObject", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
	}

} //EobjectPackageImpl
