package tools.vitruv.testutils.metamodels;

import multicontainment_b.ChildB1;
import multicontainment_b.ChildB2;
import multicontainment_b.Identified;
import multicontainment_b.RootB;
import multicontainment_b.impl.Multicontainment_bFactoryImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import tools.vitruv.testutils.activeannotations.WithGeneratedRandomIds;

@WithGeneratedRandomIds(identifierMetaclass = Identified.class)
@SuppressWarnings("all")
public class RandomIdMulticontainmentBFactory extends Multicontainment_bFactoryImpl {
  public RootB createRootB() {
    final RootB created = super.createRootB();
    EcoreUtil.setID(created, EcoreUtil.generateUUID());
    return created;
  }

  public ChildB1 createChildB1() {
    final ChildB1 created = super.createChildB1();
    EcoreUtil.setID(created, EcoreUtil.generateUUID());
    return created;
  }

  public ChildB2 createChildB2() {
    final ChildB2 created = super.createChildB2();
    EcoreUtil.setID(created, EcoreUtil.generateUUID());
    return created;
  }
}
