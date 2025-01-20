package tools.vitruv.testutils.metamodels;

import allElementTypes.Identified;
import allElementTypes.NonRoot;
import allElementTypes.NonRootObjectContainerHelper;
import allElementTypes.Root;
import allElementTypes.impl.AllElementTypesFactoryImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import tools.vitruv.testutils.activeannotations.WithGeneratedRandomIds;

@WithGeneratedRandomIds(identifierMetaclass = Identified.class)
@SuppressWarnings("all")
public class RandomIdAllElementTypesFactory extends AllElementTypesFactoryImpl {
  public Root createRoot() {
    final Root created = super.createRoot();
    EcoreUtil.setID(created, EcoreUtil.generateUUID());
    return created;
  }

  public NonRoot createNonRoot() {
    final NonRoot created = super.createNonRoot();
    EcoreUtil.setID(created, EcoreUtil.generateUUID());
    return created;
  }

  public NonRootObjectContainerHelper createNonRootObjectContainerHelper() {
    final NonRootObjectContainerHelper created = super.createNonRootObjectContainerHelper();
    EcoreUtil.setID(created, EcoreUtil.generateUUID());
    return created;
  }
}
