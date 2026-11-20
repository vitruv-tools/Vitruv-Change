package tools.vitruv.change.interaction.impl;

import com.google.common.base.Preconditions;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;
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
 * <p>One user interactor serves all change propagations of a virtual model, which may run
 * concurrently on different threads. Listeners are therefore kept in a thread-safe list, and
 * {@link #replaceUserInteractionResultProvider} replaces the result provider only for the calling
 * thread, so that the predefined results of one propagation do not answer the interactions of
 * another.
 *
 * @author Heiko Klare
 */
public class UserInteractorImpl implements InternalUserInteractor {
  private UserInteractionOptions.WindowModality defaultWindowModality = UserInteractionOptions.WindowModality.MODELESS;

  private final List<UserInteractionListener> userInteractionListeners = new CopyOnWriteArrayList<UserInteractionListener>();

  private final InteractionResultProvider interactionResultProvider;

  private final InteractionFactory interactionFactory;

  /** The result provider replacing {@link #interactionResultProvider} on the current thread. */
  private final ThreadLocal<InteractionResultProvider> threadInteractionResultProvider = new ThreadLocal<>();

  /** The interaction factory for {@link #threadInteractionResultProvider}. */
  private final ThreadLocal<InteractionFactory> threadInteractionFactory = new ThreadLocal<>();

  public UserInteractorImpl(final InteractionResultProvider interactionResultProvider) {
    if ((interactionResultProvider == null)) {
      throw new IllegalArgumentException("Interaction result provider must not be null");
    }
    this.interactionResultProvider = interactionResultProvider;
    this.interactionFactory = this.createInteractionFactory(interactionResultProvider);
  }

  public UserInteractorImpl(final InteractionResultProvider interactionResultProvider, final UserInteractionOptions.WindowModality defaultWindowModality) {
    this(interactionResultProvider);
    this.defaultWindowModality = defaultWindowModality;
  }

  private InteractionFactory currentInteractionFactory() {
    InteractionFactory threadFactory = this.threadInteractionFactory.get();
    return threadFactory != null ? threadFactory : this.interactionFactory;
  }

  private InteractionResultProvider currentInteractionResultProvider() {
    InteractionResultProvider threadProvider = this.threadInteractionResultProvider.get();
    return threadProvider != null ? threadProvider : this.interactionResultProvider;
  }

  @Override
  public NotificationInteractionBuilder getNotificationDialogBuilder() {
    return new NotificationInteractionBuilderImpl(this.currentInteractionFactory(), this.userInteractionListeners);
  }

  @Override
  public ConfirmationInteractionBuilder getConfirmationDialogBuilder() {
    return new ConfirmationInteractionBuilderImpl(this.currentInteractionFactory(), this.userInteractionListeners);
  }

  @Override
  public TextInputInteractionBuilder getTextInputDialogBuilder() {
    return new TextInputInteractionBuilderImpl(this.currentInteractionFactory(), this.userInteractionListeners);
  }

  @Override
  public MultipleChoiceSingleSelectionInteractionBuilder getSingleSelectionDialogBuilder() {
    return new MultipleChoiceSingleSelectionInteractionBuilderImpl(this.currentInteractionFactory(), this.userInteractionListeners);
  }

  @Override
  public MultipleChoiceMultiSelectionInteractionBuilder getMultiSelectionDialogBuilder() {
    return new MultipleChoiceMultiSelectionInteractionBuilderImpl(this.currentInteractionFactory(), this.userInteractionListeners);
  }

  @Override
  public void registerUserInputListener(final UserInteractionListener listener) {
    this.userInteractionListeners.add(listener);
  }

  @Override
  public void deregisterUserInputListener(final UserInteractionListener listener) {
    this.userInteractionListeners.remove(listener);
  }

  /**
   * {@inheritDoc}
   *
   * <p>The replacement applies to the calling thread only, until the returned
   * {@link AutoCloseable} is closed on that thread.
   */
  @Override
  public AutoCloseable replaceUserInteractionResultProvider(final Function<InteractionResultProvider, InteractionResultProvider> decoratingInteractionResultProviderProducer) {
    final InteractionResultProvider oldThreadProvider = this.threadInteractionResultProvider.get();
    final InteractionFactory oldThreadFactory = this.threadInteractionFactory.get();
    final InteractionResultProvider oldProvider = Preconditions.<InteractionResultProvider>checkNotNull(this.currentInteractionResultProvider(), "No user interaction result provider is set!");
    final InteractionResultProvider newProvider = decoratingInteractionResultProviderProducer.apply(oldProvider);
    this.threadInteractionResultProvider.set(newProvider);
    this.threadInteractionFactory.set(this.createInteractionFactory(newProvider));
    return () -> {
      if (oldThreadProvider == null) {
        this.threadInteractionResultProvider.remove();
        this.threadInteractionFactory.remove();
      } else {
        this.threadInteractionResultProvider.set(oldThreadProvider);
        this.threadInteractionFactory.set(oldThreadFactory);
      }
    };
  }

  private InteractionFactory createInteractionFactory(final InteractionResultProvider provider) {
    return new InteractionFactoryImpl(provider, this.defaultWindowModality);
  }
}
