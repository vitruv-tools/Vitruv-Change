package tools.vitruv.testutils.metamodels;

import attribute_to_structure_attr.Attributed;
import attribute_to_structure_attr.Identified;
import attribute_to_structure_attr.ModelA;
import attribute_to_structure_attr.impl.Attribute_to_structure_attrFactoryImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import tools.vitruv.testutils.activeannotations.WithGeneratedRandomIds;

@WithGeneratedRandomIds(identifierMetaclass = Identified.class)
@SuppressWarnings("all")
public class RandomIdAttributeToStructureAttrFactory extends Attribute_to_structure_attrFactoryImpl {
  public ModelA createModelA() {
    final ModelA created = super.createModelA();
    EcoreUtil.setID(created, EcoreUtil.generateUUID());
    return created;
  }

  public Attributed createAttributed() {
    final Attributed created = super.createAttributed();
    EcoreUtil.setID(created, EcoreUtil.generateUUID());
    return created;
  }
}
