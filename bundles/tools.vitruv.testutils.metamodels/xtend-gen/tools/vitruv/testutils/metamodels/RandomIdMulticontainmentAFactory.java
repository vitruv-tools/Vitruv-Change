package tools.vitruv.testutils.metamodels;

import multicontainment_a.ChildA1;
import multicontainment_a.ChildA2;
import multicontainment_a.Identified;
import multicontainment_a.RootA;
import multicontainment_a.impl.Multicontainment_aFactoryImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import tools.vitruv.testutils.activeannotations.WithGeneratedRandomIds;

@WithGeneratedRandomIds(identifierMetaclass = Identified.class)
@SuppressWarnings("all")
public class RandomIdMulticontainmentAFactory extends Multicontainment_aFactoryImpl {
  public RootA createRootA() {
    final RootA created = super.createRootA();
    EcoreUtil.setID(created, EcoreUtil.generateUUID());
    return created;
  }

  public ChildA1 createChildA1() {
    final ChildA1 created = super.createChildA1();
    EcoreUtil.setID(created, EcoreUtil.generateUUID());
    return created;
  }

  public ChildA2 createChildA2() {
    final ChildA2 created = super.createChildA2();
    EcoreUtil.setID(created, EcoreUtil.generateUUID());
    return created;
  }
}
