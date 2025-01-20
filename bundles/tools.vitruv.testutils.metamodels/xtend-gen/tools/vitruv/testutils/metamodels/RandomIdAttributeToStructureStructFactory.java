package tools.vitruv.testutils.metamodels;

import attribute_to_structure_struct_1.FloatContainer;
import attribute_to_structure_struct_1.Identified;
import attribute_to_structure_struct_1.IntContainer;
import attribute_to_structure_struct_1.ModelB;
import attribute_to_structure_struct_1.StrContainer;
import attribute_to_structure_struct_1.Structured;
import attribute_to_structure_struct_1.impl.Attribute_to_structure_struct_1FactoryImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import tools.vitruv.testutils.activeannotations.WithGeneratedRandomIds;

@WithGeneratedRandomIds(identifierMetaclass = Identified.class)
@SuppressWarnings("all")
public class RandomIdAttributeToStructureStructFactory extends Attribute_to_structure_struct_1FactoryImpl {
  public ModelB createModelB() {
    final ModelB created = super.createModelB();
    EcoreUtil.setID(created, EcoreUtil.generateUUID());
    return created;
  }

  public Structured createStructured() {
    final Structured created = super.createStructured();
    EcoreUtil.setID(created, EcoreUtil.generateUUID());
    return created;
  }

  public IntContainer createIntContainer() {
    final IntContainer created = super.createIntContainer();
    EcoreUtil.setID(created, EcoreUtil.generateUUID());
    return created;
  }

  public StrContainer createStrContainer() {
    final StrContainer created = super.createStrContainer();
    EcoreUtil.setID(created, EcoreUtil.generateUUID());
    return created;
  }

  public FloatContainer createFloatContainer() {
    final FloatContainer created = super.createFloatContainer();
    EcoreUtil.setID(created, EcoreUtil.generateUUID());
    return created;
  }
}
