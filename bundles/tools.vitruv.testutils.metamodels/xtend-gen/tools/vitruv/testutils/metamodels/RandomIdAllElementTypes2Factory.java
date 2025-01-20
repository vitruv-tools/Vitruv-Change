package tools.vitruv.testutils.metamodels;

import allElementTypes2.Identified2;
import allElementTypes2.NonRoot2;
import allElementTypes2.NonRootObjectContainerHelper2;
import allElementTypes2.Root2;
import allElementTypes2.impl.AllElementTypes2FactoryImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import tools.vitruv.testutils.activeannotations.WithGeneratedRandomIds;

@WithGeneratedRandomIds(identifierMetaclass = Identified2.class)
@SuppressWarnings("all")
public class RandomIdAllElementTypes2Factory extends AllElementTypes2FactoryImpl {
  public Root2 createRoot2() {
    final Root2 created = super.createRoot2();
    EcoreUtil.setID(created, EcoreUtil.generateUUID());
    return created;
  }

  public NonRoot2 createNonRoot2() {
    final NonRoot2 created = super.createNonRoot2();
    EcoreUtil.setID(created, EcoreUtil.generateUUID());
    return created;
  }

  public NonRootObjectContainerHelper2 createNonRootObjectContainerHelper2() {
    final NonRootObjectContainerHelper2 created = super.createNonRootObjectContainerHelper2();
    EcoreUtil.setID(created, EcoreUtil.generateUUID());
    return created;
  }
}
