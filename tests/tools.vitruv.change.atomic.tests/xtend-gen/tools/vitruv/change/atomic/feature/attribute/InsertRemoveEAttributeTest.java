package tools.vitruv.change.atomic.feature.attribute;

import allElementTypes.AllElementTypesPackage;
import allElementTypes.Root;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.xtend.lib.annotations.AccessorType;
import org.eclipse.xtend.lib.annotations.Accessors;
import org.eclipse.xtext.xbase.lib.Pure;
import org.junit.jupiter.api.BeforeEach;
import tools.vitruv.change.atomic.EChangeTest;

/**
 * Abstract class which is used by insert and remove attribute test classes.
 */
@SuppressWarnings("all")
public abstract class InsertRemoveEAttributeTest extends EChangeTest {
  @Accessors(AccessorType.PROTECTED_GETTER)
  private Root affectedEObject;

  @Accessors(AccessorType.PROTECTED_GETTER)
  private EAttribute affectedFeature;

  @Accessors(AccessorType.PROTECTED_GETTER)
  private EList<Integer> attributeContent;

  protected static final Integer NEW_VALUE = Integer.valueOf(111);

  protected static final Integer NEW_VALUE_2 = Integer.valueOf(222);

  protected static final Integer NEW_VALUE_3 = Integer.valueOf(333);

  @BeforeEach
  public void beforeTest() {
    this.affectedEObject = this.getRootObject();
    this.affectedFeature = AllElementTypesPackage.Literals.ROOT__MULTI_VALUED_EATTRIBUTE;
    Object _eGet = this.affectedEObject.eGet(this.affectedFeature);
    this.attributeContent = ((EList<Integer>) _eGet);
  }

  @Pure
  protected Root getAffectedEObject() {
    return this.affectedEObject;
  }

  @Pure
  protected EAttribute getAffectedFeature() {
    return this.affectedFeature;
  }

  @Pure
  protected EList<Integer> getAttributeContent() {
    return this.attributeContent;
  }
}
