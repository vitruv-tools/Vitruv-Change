package tools.vitruv.testutils.metamodels;

import org.eclipse.emf.ecore.util.EcoreUtil;
import pcm_mockup.Component;
import pcm_mockup.Identified;
import pcm_mockup.PInterface;
import pcm_mockup.PMethod;
import pcm_mockup.Repository;
import pcm_mockup.impl.Pcm_mockupFactoryImpl;
import tools.vitruv.testutils.activeannotations.WithGeneratedRandomIds;

@WithGeneratedRandomIds(identifierMetaclass = Identified.class)
@SuppressWarnings("all")
public class RandomIdPcmMockupFactory extends Pcm_mockupFactoryImpl {
  public Repository createRepository() {
    final Repository created = super.createRepository();
    EcoreUtil.setID(created, EcoreUtil.generateUUID());
    return created;
  }

  public PInterface createPInterface() {
    final PInterface created = super.createPInterface();
    EcoreUtil.setID(created, EcoreUtil.generateUUID());
    return created;
  }

  public Component createComponent() {
    final Component created = super.createComponent();
    EcoreUtil.setID(created, EcoreUtil.generateUUID());
    return created;
  }

  public PMethod createPMethod() {
    final PMethod created = super.createPMethod();
    EcoreUtil.setID(created, EcoreUtil.generateUUID());
    return created;
  }
}
