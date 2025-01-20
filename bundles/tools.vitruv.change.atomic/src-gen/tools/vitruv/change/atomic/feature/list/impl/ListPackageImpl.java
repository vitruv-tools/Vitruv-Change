/**
 */
package tools.vitruv.change.atomic.feature.list.impl;

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

import tools.vitruv.change.atomic.feature.attribute.AttributePackage;

import tools.vitruv.change.atomic.feature.attribute.impl.AttributePackageImpl;

import tools.vitruv.change.atomic.feature.impl.FeaturePackageImpl;

import tools.vitruv.change.atomic.feature.list.InsertInListEChange;
import tools.vitruv.change.atomic.feature.list.ListFactory;
import tools.vitruv.change.atomic.feature.list.ListPackage;
import tools.vitruv.change.atomic.feature.list.RemoveFromListEChange;
import tools.vitruv.change.atomic.feature.list.UpdateSingleListEntryEChange;

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
public class ListPackageImpl extends EPackageImpl implements ListPackage
{
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass updateSingleListEntryEChangeEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass insertInListEChangeEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass removeFromListEChangeEClass = null;

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
	 * @see tools.vitruv.change.atomic.feature.list.ListPackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private ListPackageImpl()
	{
		super(eNS_URI, ListFactory.eINSTANCE);
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
	 * <p>This method is used to initialize {@link ListPackage#eINSTANCE} when that field is accessed.
	 * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static ListPackage init()
	{
		if (isInited) return (ListPackage)EPackage.Registry.INSTANCE.getEPackage(ListPackage.eNS_URI);

		// Obtain or create and register package
		Object registeredListPackage = EPackage.Registry.INSTANCE.get(eNS_URI);
		ListPackageImpl theListPackage = registeredListPackage instanceof ListPackageImpl ? (ListPackageImpl)registeredListPackage : new ListPackageImpl();

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
		registeredPackage = EPackage.Registry.INSTANCE.getEPackage(AttributePackage.eNS_URI);
		AttributePackageImpl theAttributePackage = (AttributePackageImpl)(registeredPackage instanceof AttributePackageImpl ? registeredPackage : AttributePackage.eINSTANCE);
		registeredPackage = EPackage.Registry.INSTANCE.getEPackage(ReferencePackage.eNS_URI);
		ReferencePackageImpl theReferencePackage = (ReferencePackageImpl)(registeredPackage instanceof ReferencePackageImpl ? registeredPackage : ReferencePackage.eINSTANCE);

		// Create package meta-data objects
		theListPackage.createPackageContents();
		theAtomicPackage.createPackageContents();
		theEobjectPackage.createPackageContents();
		theRootPackage.createPackageContents();
		theFeaturePackage.createPackageContents();
		theSinglePackage.createPackageContents();
		theAttributePackage.createPackageContents();
		theReferencePackage.createPackageContents();

		// Initialize created meta-data
		theListPackage.initializePackageContents();
		theAtomicPackage.initializePackageContents();
		theEobjectPackage.initializePackageContents();
		theRootPackage.initializePackageContents();
		theFeaturePackage.initializePackageContents();
		theSinglePackage.initializePackageContents();
		theAttributePackage.initializePackageContents();
		theReferencePackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		theListPackage.freeze();

		// Update the registry and return the package
		EPackage.Registry.INSTANCE.put(ListPackage.eNS_URI, theListPackage);
		return theListPackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getUpdateSingleListEntryEChange()
	{
		return updateSingleListEntryEChangeEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getUpdateSingleListEntryEChange_Index()
	{
		return (EAttribute)updateSingleListEntryEChangeEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getInsertInListEChange()
	{
		return insertInListEChangeEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getRemoveFromListEChange()
	{
		return removeFromListEChangeEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ListFactory getListFactory()
	{
		return (ListFactory)getEFactoryInstance();
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
		updateSingleListEntryEChangeEClass = createEClass(UPDATE_SINGLE_LIST_ENTRY_ECHANGE);
		createEAttribute(updateSingleListEntryEChangeEClass, UPDATE_SINGLE_LIST_ENTRY_ECHANGE__INDEX);

		insertInListEChangeEClass = createEClass(INSERT_IN_LIST_ECHANGE);

		removeFromListEChangeEClass = createEClass(REMOVE_FROM_LIST_ECHANGE);
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
		ETypeParameter updateSingleListEntryEChangeEClass_Element = addETypeParameter(updateSingleListEntryEChangeEClass, "Element");
		ETypeParameter updateSingleListEntryEChangeEClass_Feature = addETypeParameter(updateSingleListEntryEChangeEClass, "Feature");
		ETypeParameter insertInListEChangeEClass_Element = addETypeParameter(insertInListEChangeEClass, "Element");
		ETypeParameter insertInListEChangeEClass_Feature = addETypeParameter(insertInListEChangeEClass, "Feature");
		ETypeParameter insertInListEChangeEClass_Value = addETypeParameter(insertInListEChangeEClass, "Value");
		ETypeParameter removeFromListEChangeEClass_Element = addETypeParameter(removeFromListEChangeEClass, "Element");
		ETypeParameter removeFromListEChangeEClass_Feature = addETypeParameter(removeFromListEChangeEClass, "Feature");
		ETypeParameter removeFromListEChangeEClass_Value = addETypeParameter(removeFromListEChangeEClass, "Value");

		// Set bounds for type parameters
		EGenericType g1 = createEGenericType(ecorePackage.getEJavaObject());
		updateSingleListEntryEChangeEClass_Element.getEBounds().add(g1);
		g1 = createEGenericType(theEcorePackage.getEStructuralFeature());
		updateSingleListEntryEChangeEClass_Feature.getEBounds().add(g1);
		g1 = createEGenericType(theEcorePackage.getEJavaObject());
		insertInListEChangeEClass_Element.getEBounds().add(g1);
		g1 = createEGenericType(theEcorePackage.getEStructuralFeature());
		insertInListEChangeEClass_Feature.getEBounds().add(g1);
		g1 = createEGenericType(ecorePackage.getEJavaObject());
		insertInListEChangeEClass_Value.getEBounds().add(g1);
		g1 = createEGenericType(ecorePackage.getEJavaObject());
		removeFromListEChangeEClass_Element.getEBounds().add(g1);
		g1 = createEGenericType(theEcorePackage.getEStructuralFeature());
		removeFromListEChangeEClass_Feature.getEBounds().add(g1);
		g1 = createEGenericType(ecorePackage.getEJavaObject());
		removeFromListEChangeEClass_Value.getEBounds().add(g1);

		// Add supertypes to classes
		g1 = createEGenericType(theFeaturePackage.getUpdateMultiValuedFeatureEChange());
		EGenericType g2 = createEGenericType(updateSingleListEntryEChangeEClass_Element);
		g1.getETypeArguments().add(g2);
		g2 = createEGenericType(updateSingleListEntryEChangeEClass_Feature);
		g1.getETypeArguments().add(g2);
		updateSingleListEntryEChangeEClass.getEGenericSuperTypes().add(g1);
		g1 = createEGenericType(this.getUpdateSingleListEntryEChange());
		g2 = createEGenericType(insertInListEChangeEClass_Element);
		g1.getETypeArguments().add(g2);
		g2 = createEGenericType(insertInListEChangeEClass_Feature);
		g1.getETypeArguments().add(g2);
		insertInListEChangeEClass.getEGenericSuperTypes().add(g1);
		g1 = createEGenericType(theAtomicPackage.getAdditiveEChange());
		g2 = createEGenericType(insertInListEChangeEClass_Element);
		g1.getETypeArguments().add(g2);
		g2 = createEGenericType(insertInListEChangeEClass_Value);
		g1.getETypeArguments().add(g2);
		insertInListEChangeEClass.getEGenericSuperTypes().add(g1);
		g1 = createEGenericType(this.getUpdateSingleListEntryEChange());
		g2 = createEGenericType(removeFromListEChangeEClass_Element);
		g1.getETypeArguments().add(g2);
		g2 = createEGenericType(removeFromListEChangeEClass_Feature);
		g1.getETypeArguments().add(g2);
		removeFromListEChangeEClass.getEGenericSuperTypes().add(g1);
		g1 = createEGenericType(theAtomicPackage.getSubtractiveEChange());
		g2 = createEGenericType(removeFromListEChangeEClass_Element);
		g1.getETypeArguments().add(g2);
		g2 = createEGenericType(removeFromListEChangeEClass_Value);
		g1.getETypeArguments().add(g2);
		removeFromListEChangeEClass.getEGenericSuperTypes().add(g1);

		// Initialize classes, features, and operations; add parameters
		initEClass(updateSingleListEntryEChangeEClass, UpdateSingleListEntryEChange.class, "UpdateSingleListEntryEChange", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getUpdateSingleListEntryEChange_Index(), theEcorePackage.getEInt(), "index", "0", 1, 1, UpdateSingleListEntryEChange.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(insertInListEChangeEClass, InsertInListEChange.class, "InsertInListEChange", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(removeFromListEChangeEClass, RemoveFromListEChange.class, "RemoveFromListEChange", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
	}

} //ListPackageImpl
