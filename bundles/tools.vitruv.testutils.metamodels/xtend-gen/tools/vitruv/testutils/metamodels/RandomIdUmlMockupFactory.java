package tools.vitruv.testutils.metamodels;

import org.eclipse.emf.ecore.util.EcoreUtil;
import tools.vitruv.testutils.activeannotations.WithGeneratedRandomIds;
import uml_mockup.Identified;
import uml_mockup.UClass;
import uml_mockup.UInterface;
import uml_mockup.UMethod;
import uml_mockup.UPackage;
import uml_mockup.impl.Uml_mockupFactoryImpl;

@WithGeneratedRandomIds(identifierMetaclass = Identified.class)
@SuppressWarnings("all")
public class RandomIdUmlMockupFactory extends Uml_mockupFactoryImpl {
  public UPackage createUPackage() {
    final UPackage created = super.createUPackage();
    EcoreUtil.setID(created, EcoreUtil.generateUUID());
    return created;
  }

  public UInterface createUInterface() {
    final UInterface created = super.createUInterface();
    EcoreUtil.setID(created, EcoreUtil.generateUUID());
    return created;
  }

  public UClass createUClass() {
    final UClass created = super.createUClass();
    EcoreUtil.setID(created, EcoreUtil.generateUUID());
    return created;
  }

  public UMethod createUMethod() {
    final UMethod created = super.createUMethod();
    EcoreUtil.setID(created, EcoreUtil.generateUUID());
    return created;
  }
}
