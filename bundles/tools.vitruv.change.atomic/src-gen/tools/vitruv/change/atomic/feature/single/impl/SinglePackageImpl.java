/**
 */
package tools.vitruv.change.atomic.feature.single.impl;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EGenericType;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.ETypeParameter;
import org.eclipse.emf.ecore.EcorePackage;

import org.eclipse.emf.ecore.impl.EPackageImpl;

import tools.vitruv.change.atomic.AtomicPackage;

import tools.vitruv.change.atomic.eobject.EobjectPackage;

import tools.vitruv.change.atomic.eobject.impl.EobjectPackageImpl;

import tools.vitruv.change.atomic.feature.FeaturePackage;

import tools.vitruv.change.atomic.feature.attribute.AttributePackage;

import tools.vitruv.change.atomic.feature.attribute.impl.AttributePackageImpl;

import tools.vitruv.change.atomic.feature.impl.FeaturePackageImpl;

import tools.vitruv.change.atomic.feature.list.ListPackage;

import tools.vitruv.change.atomic.feature.list.impl.ListPackageImpl;

import tools.vitruv.change.atomic.feature.reference.ReferencePackage;

import tools.vitruv.change.atomic.feature.reference.impl.ReferencePackageImpl;

import tools.vitruv.change.atomic.feature.single.ReplaceSingleValuedFeatureEChange;
import tools.vitruv.change.atomic.feature.single.SingleFactory;
import tools.vitruv.change.atomic.feature.single.SinglePackage;

import tools.vitruv.change.atomic.impl.AtomicPackageImpl;

import tools.vitruv.change.atomic.root.RootPackage;

import tools.vitruv.change.atomic.root.impl.RootPackageImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class SinglePackageImpl extends EPackageImpl implements SinglePackage
{
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass replaceSingleValuedFeatureEChangeEClass = null;

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
	 * @see tools.vitruv.change.atomic.feature.single.SinglePackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private SinglePackageImpl()
	{
		super(eNS_URI, SingleFactory.eINSTANCE);
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
	 * <p>This method is used to initialize {@link SinglePackage#eINSTANCE} when that field is accessed.
	 * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static SinglePackage init()
	{
		if (isInited) return (SinglePackage)EPackage.Registry.INSTANCE.getEPackage(SinglePackage.eNS_URI);

		// Obtain or create and register package
		Object registeredSinglePackage = EPackage.Registry.INSTANCE.get(eNS_URI);
		SinglePackageImpl theSinglePackage = registeredSinglePackage instanceof SinglePackageImpl ? (SinglePackageImpl)registeredSinglePackage : new SinglePackageImpl();

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
		registeredPackage = EPackage.Registry.INSTANCE.getEPackage(ListPackage.eNS_URI);
		ListPackageImpl theListPackage = (ListPackageImpl)(registeredPackage instanceof ListPackageImpl ? registeredPackage : ListPackage.eINSTANCE);
		registeredPackage = EPackage.Registry.INSTANCE.getEPackage(AttributePackage.eNS_URI);
		AttributePackageImpl theAttributePackage = (AttributePackageImpl)(registeredPackage instanceof AttributePackageImpl ? registeredPackage : AttributePackage.eINSTANCE);
		registeredPackage = EPackage.Registry.INSTANCE.getEPackage(ReferencePackage.eNS_URI);
		ReferencePackageImpl theReferencePackage = (ReferencePackageImpl)(registeredPackage instanceof ReferencePackageImpl ? registeredPackage : ReferencePackage.eINSTANCE);

		// Create package meta-data objects
		theSinglePackage.createPackageContents();
		theAtomicPackage.createPackageContents();
		theEobjectPackage.createPackageContents();
		theRootPackage.createPackageContents();
		theFeaturePackage.createPackageContents();
		theListPackage.createPackageContents();
		theAttributePackage.createPackageContents();
		theReferencePackage.createPackageContents();

		// Initialize created meta-data
		theSinglePackage.initializePackageContents();
		theAtomicPackage.initializePackageContents();
		theEobjectPackage.initializePackageContents();
		theRootPackage.initializePackageContents();
		theFeaturePackage.initializePackageContents();
		theListPackage.initializePackageContents();
		theAttributePackage.initializePackageContents();
		theReferencePackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		theSinglePackage.freeze();

		// Update the registry and return the package
		EPackage.Registry.INSTANCE.put(SinglePackage.eNS_URI, theSinglePackage);
		return theSinglePackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getReplaceSingleValuedFeatureEChange()
	{
		return replaceSingleValuedFeatureEChangeEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getReplaceSingleValuedFeatureEChange_IsUnset()
	{
		return (EAttribute)replaceSingleValuedFeatureEChangeEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EOperation getReplaceSingleValuedFeatureEChange__IsFromNonDefaultValue()
	{
		return replaceSingleValuedFeatureEChangeEClass.getEOperations().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EOperation getReplaceSingleValuedFeatureEChange__IsToNonDefaultValue()
	{
		return replaceSingleValuedFeatureEChangeEClass.getEOperations().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public SingleFactory getSingleFactory()
	{
		return (SingleFactory)getEFactoryInstance();
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
		replaceSingleValuedFeatureEChangeEClass = createEClass(REPLACE_SINGLE_VALUED_FEATURE_ECHANGE);
		createEAttribute(replaceSingleValuedFeatureEChangeEClass, REPLACE_SINGLE_VALUED_FEATURE_ECHANGE__IS_UNSET);
		createEOperation(replaceSingleValuedFeatureEChangeEClass, REPLACE_SINGLE_VALUED_FEATURE_ECHANGE___IS_FROM_NON_DEFAULT_VALUE);
		createEOperation(replaceSingleValuedFeatureEChangeEClass, REPLACE_SINGLE_VALUED_FEATURE_ECHANGE___IS_TO_NON_DEFAULT_VALUE);
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
		EcorePackage theEcorePackage = (EcorePackage)EPackage.Registry.INSTANCE.getEPackage(EcorePackage.eNS_URI);
		FeaturePackage theFeaturePackage = (FeaturePackage)EPackage.Registry.INSTANCE.getEPackage(FeaturePackage.eNS_URI);
		AtomicPackage theAtomicPackage = (AtomicPackage)EPackage.Registry.INSTANCE.getEPackage(AtomicPackage.eNS_URI);

		// Create type parameters
		ETypeParameter replaceSingleValuedFeatureEChangeEClass_Element = addETypeParameter(replaceSingleValuedFeatureEChangeEClass, "Element");
		ETypeParameter replaceSingleValuedFeatureEChangeEClass_Feature = addETypeParameter(replaceSingleValuedFeatureEChangeEClass, "Feature");
		ETypeParameter replaceSingleValuedFeatureEChangeEClass_Value = addETypeParameter(replaceSingleValuedFeatureEChangeEClass, "Value");

		// Set bounds for type parameters
		EGenericType g1 = createEGenericType(theEcorePackage.getEJavaObject());
		replaceSingleValuedFeatureEChangeEClass_Element.getEBounds().add(g1);
		g1 = createEGenericType(theEcorePackage.getEStructuralFeature());
		replaceSingleValuedFeatureEChangeEClass_Feature.getEBounds().add(g1);
		g1 = createEGenericType(ecorePackage.getEJavaObject());
		replaceSingleValuedFeatureEChangeEClass_Value.getEBounds().add(g1);

		// Add supertypes to classes
		g1 = createEGenericType(theFeaturePackage.getUpdateSingleValuedFeatureEChange());
		EGenericType g2 = createEGenericType(replaceSingleValuedFeatureEChangeEClass_Element);
		g1.getETypeArguments().add(g2);
		g2 = createEGenericType(replaceSingleValuedFeatureEChangeEClass_Feature);
		g1.getETypeArguments().add(g2);
		replaceSingleValuedFeatureEChangeEClass.getEGenericSuperTypes().add(g1);
		g1 = createEGenericType(theAtomicPackage.getAdditiveEChange());
		g2 = createEGenericType(replaceSingleValuedFeatureEChangeEClass_Element);
		g1.getETypeArguments().add(g2);
		g2 = createEGenericType(replaceSingleValuedFeatureEChangeEClass_Value);
		g1.getETypeArguments().add(g2);
		replaceSingleValuedFeatureEChangeEClass.getEGenericSuperTypes().add(g1);
		g1 = createEGenericType(theAtomicPackage.getSubtractiveEChange());
		g2 = createEGenericType(replaceSingleValuedFeatureEChangeEClass_Element);
		g1.getETypeArguments().add(g2);
		g2 = createEGenericType(replaceSingleValuedFeatureEChangeEClass_Value);
		g1.getETypeArguments().add(g2);
		replaceSingleValuedFeatureEChangeEClass.getEGenericSuperTypes().add(g1);

		// Initialize classes, features, and operations; add parameters
		initEClass(replaceSingleValuedFeatureEChangeEClass, ReplaceSingleValuedFeatureEChange.class, "ReplaceSingleValuedFeatureEChange", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getReplaceSingleValuedFeatureEChange_IsUnset(), theEcorePackage.getEBoolean(), "isUnset", null, 0, 1, ReplaceSingleValuedFeatureEChange.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEOperation(getReplaceSingleValuedFeatureEChange__IsFromNonDefaultValue(), theEcorePackage.getEBoolean(), "isFromNonDefaultValue", 0, 1, !IS_UNIQUE, IS_ORDERED);

		initEOperation(getReplaceSingleValuedFeatureEChange__IsToNonDefaultValue(), theEcorePackage.getEBoolean(), "isToNonDefaultValue", 0, 1, !IS_UNIQUE, IS_ORDERED);
	}

} //SinglePackageImpl
