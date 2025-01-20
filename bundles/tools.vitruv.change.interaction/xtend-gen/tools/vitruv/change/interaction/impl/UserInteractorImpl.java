package tools.vitruv.change.interaction.impl;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import tools.vitruv.change.interaction.InteractionResultProvider;
import tools.vitruv.change.interaction.InternalUserInteractor;
import tools.vitruv.change.interaction.UserInteractionListener;
import tools.vitruv.change.interaction.UserInteractionOptions;
import tools.vitruv.change.interaction.builder.ConfirmationInteractionBuilder;
import tools.vitruv.change.interaction.builder.MultipleChoiceMultiSelectionInteractionBuilder;
import tools.vitruv.change.interaction.builder.MultipleChoiceSingleSelectionInteractionBuilder;
import tools.vitruv.change.interaction.builder.NotificationInteractionBuilder;
import tools.vitruv.change.interaction.builder.TextInputInteractionBuilder;
import tools.vitruv.change.interaction.builder.impl.ConfirmationInteractionBuilderImpl;
import tools.vitruv.change.interaction.builder.impl.MultipleChoiceMultiSelectionInteractionBuilderImpl;
import tools.vitruv.change.interaction.builder.impl.MultipleChoiceSingleSelectionInteractionBuilderImpl;
import tools.vitruv.change.interaction.builder.impl.NotificationInteractionBuilderImpl;
import tools.vitruv.change.interaction.builder.impl.TextInputInteractionBuilderImpl;
import tools.vitruv.change.interaction.types.InteractionFactory;
import tools.vitruv.change.interaction.types.InteractionFactoryImpl;

/**
 * The default implementation of the {@link InternalUserInteractor}.
 * 
 * @author Heiko Klare
 */
@SuppressWarnings("all")
public class UserInteractorImpl implements InternalUserInteractor {
  private UserInteractionOptions.WindowModality defaultWindowModality = UserInteractionOptions.WindowModality.MODELESS;

  private final List<UserInteractionListener> userInteractionListeners = new ArrayList<UserInteractionListener>();

  private InteractionResultProvider interactionResultProvider;

  private InteractionFactory interactionFactory;

  public UserInteractorImpl(final InteractionResultProvider interactionResultProvider) {
    if ((interactionResultProvider == null)) {
      throw new IllegalArgumentException("Interaction result provider must not be null");
    }
    this.interactionResultProvider = interactionResultProvider;
    this.updateInteractionFactory();
  }

  public UserInteractorImpl(final InteractionResultProvider interactionResultProvider, final UserInteractionOptions.WindowModality defaultWindowModality) {
    this(interactionResultProvider);
    this.defaultWindowModality = defaultWindowModality;
  }

  @Override
  public NotificationInteractionBuilder getNotificationDialogBuilder() {
    return new NotificationInteractionBuilderImpl(this.interactionFactory, this.userInteractionListeners);
  }

  @Override
  public ConfirmationInteractionBuilder getConfirmationDialogBuilder() {
    return new ConfirmationInteractionBuilderImpl(this.interactionFactory, this.userInteractionListeners);
  }

  @Override
  public TextInputInteractionBuilder getTextInputDialogBuilder() {
    return new TextInputInteractionBuilderImpl(this.interactionFactory, this.userInteractionListeners);
  }

  @Override
  public MultipleChoiceSingleSelectionInteractionBuilder getSingleSelectionDialogBuilder() {
    return new MultipleChoiceSingleSelectionInteractionBuilderImpl(this.interactionFactory, this.userInteractionListeners);
  }

  @Override
  public MultipleChoiceMultiSelectionInteractionBuilder getMultiSelectionDialogBuilder() {
    return new MultipleChoiceMultiSelectionInteractionBuilderImpl(this.interactionFactory, this.userInteractionListeners);
  }

  @Override
  public void registerUserInputListener(final UserInteractionListener listener) {
    this.userInteractionListeners.add(listener);
  }

  @Override
  public void deregisterUserInputListener(final UserInteractionListener listener) {
    this.userInteractionListeners.remove(listener);
  }

  @Override
  public AutoCloseable replaceUserInteractionResultProvider(final Function1<? super InteractionResultProvider, ? extends InteractionResultProvider> decoratingInteractionResultProviderProducer) {
    final InteractionResultProvider oldProvider = Preconditions.<InteractionResultProvider>checkNotNull(this.interactionResultProvider, "No user interaction result provider is set!");
    this.interactionResultProvider = decoratingInteractionResultProviderProducer.apply(this.interactionResultProvider);
    this.updateInteractionFactory();
    final AutoCloseable _function = () -> {
      this.interactionResultProvider = oldProvider;
      this.updateInteractionFactory();
    };
    return _function;
  }

  private InteractionFactory updateInteractionFactory() {
    InteractionFactoryImpl _interactionFactoryImpl = new InteractionFactoryImpl(this.interactionResultProvider, this.defaultWindowModality);
    return this.interactionFactory = _interactionFactoryImpl;
  }
}
