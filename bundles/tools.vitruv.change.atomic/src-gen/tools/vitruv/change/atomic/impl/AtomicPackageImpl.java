/**
 */
package tools.vitruv.change.atomic.impl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EGenericType;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.ETypeParameter;
import org.eclipse.emf.ecore.EcorePackage;

import org.eclipse.emf.ecore.impl.EPackageImpl;

import tools.vitruv.change.atomic.AdditiveEChange;
import tools.vitruv.change.atomic.AtomicFactory;
import tools.vitruv.change.atomic.AtomicPackage;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.SubtractiveEChange;

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

import tools.vitruv.change.atomic.feature.single.SinglePackage;

import tools.vitruv.change.atomic.feature.single.impl.SinglePackageImpl;

import tools.vitruv.change.atomic.root.RootPackage;

import tools.vitruv.change.atomic.root.impl.RootPackageImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class AtomicPackageImpl extends EPackageImpl implements AtomicPackage
{
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass eChangeEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass additiveEChangeEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass subtractiveEChangeEClass = null;

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
	 * @see tools.vitruv.change.atomic.AtomicPackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private AtomicPackageImpl()
	{
		super(eNS_URI, AtomicFactory.eINSTANCE);
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
	 * <p>This method is used to initialize {@link AtomicPackage#eINSTANCE} when that field is accessed.
	 * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static AtomicPackage init()
	{
		if (isInited) return (AtomicPackage)EPackage.Registry.INSTANCE.getEPackage(AtomicPackage.eNS_URI);

		// Obtain or create and register package
		Object registeredAtomicPackage = EPackage.Registry.INSTANCE.get(eNS_URI);
		AtomicPackageImpl theAtomicPackage = registeredAtomicPackage instanceof AtomicPackageImpl ? (AtomicPackageImpl)registeredAtomicPackage : new AtomicPackageImpl();

		isInited = true;

		// Initialize simple dependencies
		EcorePackage.eINSTANCE.eClass();

		// Obtain or create and register interdependencies
		Object registeredPackage = EPackage.Registry.INSTANCE.getEPackage(EobjectPackage.eNS_URI);
		EobjectPackageImpl theEobjectPackage = (EobjectPackageImpl)(registeredPackage instanceof EobjectPackageImpl ? registeredPackage : EobjectPackage.eINSTANCE);
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
		theAtomicPackage.createPackageContents();
		theEobjectPackage.createPackageContents();
		theRootPackage.createPackageContents();
		theFeaturePackage.createPackageContents();
		theSinglePackage.createPackageContents();
		theListPackage.createPackageContents();
		theAttributePackage.createPackageContents();
		theReferencePackage.createPackageContents();

		// Initialize created meta-data
		theAtomicPackage.initializePackageContents();
		theEobjectPackage.initializePackageContents();
		theRootPackage.initializePackageContents();
		theFeaturePackage.initializePackageContents();
		theSinglePackage.initializePackageContents();
		theListPackage.initializePackageContents();
		theAttributePackage.initializePackageContents();
		theReferencePackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		theAtomicPackage.freeze();

		// Update the registry and return the package
		EPackage.Registry.INSTANCE.put(AtomicPackage.eNS_URI, theAtomicPackage);
		return theAtomicPackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getEChange()
	{
		return eChangeEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getAdditiveEChange()
	{
		return additiveEChangeEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EOperation getAdditiveEChange__GetNewValue()
	{
		return additiveEChangeEClass.getEOperations().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getSubtractiveEChange()
	{
		return subtractiveEChangeEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EOperation getSubtractiveEChange__GetOldValue()
	{
		return subtractiveEChangeEClass.getEOperations().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public AtomicFactory getAtomicFactory()
	{
		return (AtomicFactory)getEFactoryInstance();
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
		eChangeEClass = createEClass(ECHANGE);

		additiveEChangeEClass = createEClass(ADDITIVE_ECHANGE);
		createEOperation(additiveEChangeEClass, ADDITIVE_ECHANGE___GET_NEW_VALUE);

		subtractiveEChangeEClass = createEClass(SUBTRACTIVE_ECHANGE);
		createEOperation(subtractiveEChangeEClass, SUBTRACTIVE_ECHANGE___GET_OLD_VALUE);
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
		EobjectPackage theEobjectPackage = (EobjectPackage)EPackage.Registry.INSTANCE.getEPackage(EobjectPackage.eNS_URI);
		RootPackage theRootPackage = (RootPackage)EPackage.Registry.INSTANCE.getEPackage(RootPackage.eNS_URI);
		FeaturePackage theFeaturePackage = (FeaturePackage)EPackage.Registry.INSTANCE.getEPackage(FeaturePackage.eNS_URI);

		// Add subpackages
		getESubpackages().add(theEobjectPackage);
		getESubpackages().add(theRootPackage);
		getESubpackages().add(theFeaturePackage);

		// Create type parameters
		ETypeParameter eChangeEClass_Element = addETypeParameter(eChangeEClass, "Element");
		ETypeParameter additiveEChangeEClass_Element = addETypeParameter(additiveEChangeEClass, "Element");
		ETypeParameter additiveEChangeEClass_Value = addETypeParameter(additiveEChangeEClass, "Value");
		ETypeParameter subtractiveEChangeEClass_Element = addETypeParameter(subtractiveEChangeEClass, "Element");
		ETypeParameter subtractiveEChangeEClass_Value = addETypeParameter(subtractiveEChangeEClass, "Value");

		// Set bounds for type parameters
		EGenericType g1 = createEGenericType(ecorePackage.getEJavaObject());
		eChangeEClass_Element.getEBounds().add(g1);
		g1 = createEGenericType(ecorePackage.getEJavaObject());
		additiveEChangeEClass_Element.getEBounds().add(g1);
		g1 = createEGenericType(ecorePackage.getEJavaObject());
		additiveEChangeEClass_Value.getEBounds().add(g1);
		g1 = createEGenericType(ecorePackage.getEJavaObject());
		subtractiveEChangeEClass_Element.getEBounds().add(g1);
		g1 = createEGenericType(ecorePackage.getEJavaObject());
		subtractiveEChangeEClass_Value.getEBounds().add(g1);

		// Add supertypes to classes
		g1 = createEGenericType(this.getEChange());
		EGenericType g2 = createEGenericType(additiveEChangeEClass_Element);
		g1.getETypeArguments().add(g2);
		additiveEChangeEClass.getEGenericSuperTypes().add(g1);
		g1 = createEGenericType(this.getEChange());
		g2 = createEGenericType(subtractiveEChangeEClass_Element);
		g1.getETypeArguments().add(g2);
		subtractiveEChangeEClass.getEGenericSuperTypes().add(g1);

		// Initialize classes, features, and operations; add parameters
		initEClass(eChangeEClass, EChange.class, "EChange", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(additiveEChangeEClass, AdditiveEChange.class, "AdditiveEChange", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		EOperation op = initEOperation(getAdditiveEChange__GetNewValue(), null, "getNewValue", 1, 1, IS_UNIQUE, IS_ORDERED);
		g1 = createEGenericType(additiveEChangeEClass_Value);
		initEOperation(op, g1);

		initEClass(subtractiveEChangeEClass, SubtractiveEChange.class, "SubtractiveEChange", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		op = initEOperation(getSubtractiveEChange__GetOldValue(), null, "getOldValue", 1, 1, IS_UNIQUE, IS_ORDERED);
		g1 = createEGenericType(subtractiveEChangeEClass_Value);
		initEOperation(op, g1);

		// Create resource
		createResource(eNS_URI);
	}

} //AtomicPackageImpl
