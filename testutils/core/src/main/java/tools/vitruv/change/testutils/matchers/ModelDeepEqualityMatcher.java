package tools.vitruv.change.testutils.matchers;

import com.google.common.base.Preconditions;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import edu.kit.ipd.sdq.commons.util.java.lang.IterableUtil;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.Monitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.compare.AttributeChange;
import org.eclipse.emf.compare.Comparison;
import org.eclipse.emf.compare.Diff;
import org.eclipse.emf.compare.DifferenceKind;
import org.eclipse.emf.compare.EMFCompare;
import org.eclipse.emf.compare.Match;
import org.eclipse.emf.compare.ReferenceChange;
import org.eclipse.emf.compare.diff.DefaultDiffEngine;
import org.eclipse.emf.compare.diff.DiffBuilder;
import org.eclipse.emf.compare.diff.FeatureFilter;
import org.eclipse.emf.compare.match.DefaultComparisonFactory;
import org.eclipse.emf.compare.match.DefaultEqualityHelperFactory;
import org.eclipse.emf.compare.match.DefaultMatchEngine;
import org.eclipse.emf.compare.match.IMatchEngine;
import org.eclipse.emf.compare.match.eobject.EqualityHelperExtensionProviderDescriptorRegistryImpl;
import org.eclipse.emf.compare.match.eobject.IEObjectMatcher;
import org.eclipse.emf.compare.match.eobject.WeightProvider;
import org.eclipse.emf.compare.match.eobject.WeightProviderDescriptorRegistryImpl;
import org.eclipse.emf.compare.match.impl.MatchEngineFactoryRegistryImpl;
import org.eclipse.emf.compare.match.resource.StrategyResourceMatcher;
import org.eclipse.emf.compare.postprocessor.BasicPostProcessorDescriptorImpl;
import org.eclipse.emf.compare.postprocessor.IPostProcessor;
import org.eclipse.emf.compare.postprocessor.PostProcessorDescriptorRegistryImpl;
import org.eclipse.emf.compare.scope.DefaultComparisonScope;
import org.eclipse.emf.compare.scope.IComparisonScope;
import org.eclipse.emf.compare.utils.EqualityHelper;
import org.eclipse.emf.compare.utils.IEqualityHelper;
import org.eclipse.emf.compare.utils.UseIdentifiers;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import java.util.function.Function;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.BiConsumer;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import tools.vitruv.change.testutils.printing.DefaultPrintIdProvider;
import tools.vitruv.change.testutils.printing.ModelPrinter;
import tools.vitruv.change.testutils.printing.ModelPrinting;
import tools.vitruv.change.testutils.printing.PrintIdProvider;
import tools.vitruv.change.testutils.printing.PrintMode;
import tools.vitruv.change.testutils.printing.PrintResult;
import tools.vitruv.change.testutils.printing.PrintResultExtension;
import tools.vitruv.change.testutils.printing.PrintTarget;
import tools.vitruv.change.testutils.printing.TestMessages;

class ModelDeepEqualityMatcher<O extends EObject> extends TypeSafeMatcher<O> {
  private static class FixOrderingFeatureFilter extends FeatureFilter {
    @Override
    public boolean checkForOrderingChanges(final EStructuralFeature feature) {
      return (feature.isMany() && feature.isOrdered());
    }
  }

  private static class EmfCompareEqualityFeatureFilter extends ModelDeepEqualityMatcher.FixOrderingFeatureFilter {
    private final EqualityFeatureFilter featureFilter;

    @Override
    public Iterator<EAttribute> getAttributesToCheck(final Match match) {
      return Iterators.filter(super.getAttributesToCheck(match), it -> this.featureFilter.includeFeature(it));
    }

    @Override
    public Iterator<EReference> getReferencesToCheck(final Match match) {
      return Iterators.filter(super.getReferencesToCheck(match), it -> this.featureFilter.includeFeature(it));
    }

    public EmfCompareEqualityFeatureFilter(final EqualityFeatureFilter featureFilter) {
      super();
      this.featureFilter = featureFilter;
    }
  }

  /**
   * It is central for tests that we match both roots together, otherwise there
   * might be no differences at all.
   * The request ‘compare these two objects’ has to be translated to EMF Compare
   * by matching those two objects.
   */
  private static class MatchRoots implements IPostProcessor {
    private final EObject leftRoot;

    private final EObject rightRoot;

    @Override
    public void postComparison(final Comparison comparison, final Monitor monitor) {
      // Intentionally empty: matching the two roots only requires the postMatch hook,
      // so this lifecycle callback of IPostProcessor is deliberately a no-op.
    }

    @Override
    public void postConflicts(final Comparison comparison, final Monitor monitor) {
      // Intentionally empty: matching the two roots only requires the postMatch hook,
      // so this lifecycle callback of IPostProcessor is deliberately a no-op.
    }

    @Override
    public void postDiff(final Comparison comparison, final Monitor monitor) {
      // Intentionally empty: matching the two roots only requires the postMatch hook,
      // so this lifecycle callback of IPostProcessor is deliberately a no-op.
    }

    @Override
    public void postEquivalences(final Comparison comparison, final Monitor monitor) {
      // Intentionally empty: matching the two roots only requires the postMatch hook,
      // so this lifecycle callback of IPostProcessor is deliberately a no-op.
    }

    @Override
    public void postMatch(final Comparison comparison, final Monitor monitor) {
      this.ensureMatched(comparison, this.leftRoot, this.rightRoot);
    }

    private void ensureMatched(final Comparison comparison, final EObject left, final EObject right) {
      this.ensureMatched(comparison, left, right, comparison.getMatch(left), comparison.getMatch(right));
    }

    private void ensureMatched(final Comparison comparison, final EObject left, final EObject right,
        final Match leftMatch, final Match rightMatch) {
      boolean _notEquals = (!Objects.equals(leftMatch, rightMatch));
      if (_notEquals) {
        this.combineMatches(comparison, left, right, leftMatch, rightMatch);
      }
    }

    @SuppressWarnings("unchecked")
    private void combineMatches(final Comparison comparison, final EObject left, final EObject right,
        final Match leftMatch, final Match rightMatch) {
      EObject _right = leftMatch.getRight();
      boolean _tripleEquals = (_right == null);
      Preconditions.checkState(_tripleEquals,
          "%s should be matched with %s, but is already matched with %s!", left, right, leftMatch.getRight());
      EObject _left = rightMatch.getLeft();
      boolean _tripleEquals_1 = (_left == null);
      Preconditions.checkState(_tripleEquals_1,
          "%s should be matched with %s, but is already matched with %s!", right, left, rightMatch.getLeft());
      EList<Match> _submatches = rightMatch.getSubmatches();
      EList<Match> _submatches_1 = leftMatch.getSubmatches();
      Iterables.<Match>addAll(_submatches, _submatches_1);
      Object _eGet = leftMatch.eContainer().eGet(leftMatch.eContainingFeature());
      final Collection<EObject> leftContainer = ((Collection<EObject>) _eGet);
      leftContainer.remove(leftMatch);
      rightMatch.setLeft(left);
    }

    @Override
    public void postRequirements(final Comparison comparison, final Monitor monitor) {
      // Intentionally empty: matching the two roots only requires the postMatch hook,
      // so this lifecycle callback of IPostProcessor is deliberately a no-op.
    }

    public MatchRoots(final EObject leftRoot, final EObject rightRoot) {
      super();
      this.leftRoot = leftRoot;
      this.rightRoot = rightRoot;
    }
  }

  private static class IgnoredFeaturesPrinter implements ModelPrinter {
    private final EqualityFeatureFilter featureFilter;

    @Override
    public ModelPrinter withSubPrinter(final ModelPrinter subPrinter) {
      return this;
    }

    @Override
    public PrintResult printFeatureValue(final PrintTarget target, final PrintIdProvider idProvider,
        final EObject object, final EStructuralFeature feature, final Object value) {
      return this.printIfIgnored(target, feature);
    }

    @Override
    public PrintResult printFeatureValueSet(final PrintTarget target, final PrintIdProvider idProvider,
        final EObject object, final EStructuralFeature feature, final Collection<?> values) {
      return this.printIfIgnored(target, feature);
    }

    @Override
    public PrintResult printFeatureValueList(final PrintTarget target, final PrintIdProvider idProvider,
        final EObject object, final EStructuralFeature feature, final Collection<?> values) {
      return this.printIfIgnored(target, feature);
    }

    private PrintResult printIfIgnored(final PrintTarget target, final EStructuralFeature feature) {
      PrintResult _xifexpression = null;
      boolean _includeFeature = this.featureFilter.includeFeature(feature);
      boolean _not = (!_includeFeature);
      if (_not) {
        _xifexpression = target.print("…");
      } else {
        _xifexpression = PrintResult.NOT_RESPONSIBLE;
      }
      return _xifexpression;
    }

    public IgnoredFeaturesPrinter(final EqualityFeatureFilter featureFilter) {
      super();
      this.featureFilter = featureFilter;
    }
  }

  private static class ComparisonAwarePrintIdProvider implements PrintIdProvider {
    private final DefaultPrintIdProvider delegate = new DefaultPrintIdProvider();

    private Comparison comparison;

    @Override
    public String getFallbackId(final Object object) {
      String _switchResult = null;
      boolean _matched = false;
      if (object instanceof EObject) {
        _matched = true;
        _switchResult = this.delegate.getFallbackId(this.<EObject>replaceWithRight(((EObject) object)));
      }
      if (!_matched) {
        _switchResult = this.delegate.getFallbackId(object);
      }
      return _switchResult;
    }

    @SuppressWarnings("unchecked")
    public <T extends EObject> T replaceWithRight(final T object) {
      T _xblockexpression = null;
      {
        Match _match = null;
        if (this.comparison != null) {
          _match = this.comparison.getMatch(object);
        }
        final Match match = _match;
        T _xifexpression = null;
        if (((match != null)
            && (!Iterables.any(match.getAllDifferences(), it -> (it instanceof AttributeChange))))) {
          T _elvis = null;
          EObject _right = match.getRight();
          if (((T) _right) != null) {
            _elvis = ((T) _right);
          } else {
            _elvis = object;
          }
          _xifexpression = _elvis;
        } else {
          _xifexpression = object;
        }
        _xblockexpression = _xifexpression;
      }
      return _xblockexpression;
    }
  }

  private static class ComparisonPrinter {
    private static final PrintMode DIFFERENCE_PRINT_MODE = PrintMode.multiLineIfAtLeast(1).withSeparator("");

    private final PrintIdProvider idProvider;

    private final Comparison comparison;

    private final FeatureFilter featureFilter;
    private final ModelPrinter modelPrinter;

    private final Set<Diff> seenDiffs = new HashSet<Diff>();

    private final Set<Match> seenMatches = new HashSet<Match>();

    private PrintResult printDifferences(final PrintTarget target, final EObject root) {
      final BiFunction<PrintTarget, Map.Entry<String, Diff>, PrintResult> _function = (PrintTarget subTarget,
          Map.Entry<String, Diff> difference) -> {
        return this.printDifference(subTarget, difference.getKey(), difference.getValue());
      };
      return target.<Map.Entry<String, Diff>>printIterableElements(this.getDifferencesWithContext(root),
          ModelDeepEqualityMatcher.ComparisonPrinter.DIFFERENCE_PRINT_MODE, _function);
    }

    private Iterable<Map.Entry<String, Diff>> getDifferencesWithContext(final EObject object) {
      final Iterable<Map.Entry<String, Diff>> viaContainment = this.getDifferencesWithContext(object, "", true);
      this.seenMatches.clear();
      final Iterable<Map.Entry<String, Diff>> viaNonContainment = this.getDifferencesWithContext(object, "", false);
      return Iterables.<Map.Entry<String, Diff>>concat(viaContainment, viaNonContainment);
    }

    private Iterable<Map.Entry<String, Diff>> getDifferencesWithContext(final EObject object, final String context,
        final boolean containment) {
      Iterable<Map.Entry<String, Diff>> _xblockexpression = null;
      {
        if ((object == null)) {
          return List.of();
        }
        final Match thisMatch = this.comparison.getMatch(object);
        if (((thisMatch == null) || (!this.seenMatches.add(thisMatch)))) {
          return List.of();
        }
        Iterable<Map.Entry<String, Diff>> _map = () -> thisMatch.getDifferences().stream()
            .filter(this.seenDiffs::add).<Map.Entry<String, Diff>>map(
                difference -> new AbstractMap.SimpleEntry<>(context, difference))
            .iterator();
        List<Map.Entry<String, Diff>> _flatMapFixed = IterableUtil.<EReference, Map.Entry<String, Diff>>flatMapFixed(
            () -> Iterators.filter(this.featureFilter.getReferencesToCheck(thisMatch),
                it -> it.isContainment() == containment),
            reference -> {
              Iterable<Map.Entry<String, Diff>> _elvis = null;
              EObject _left = thisMatch.getLeft();
              Iterable<Map.Entry<String, Diff>> _referenceDifferencesWithContext = null;
              if (_left != null) {
                _referenceDifferencesWithContext = this.getReferenceDifferencesWithContext(_left, context, reference,
                    containment);
              }
              if (_referenceDifferencesWithContext != null) {
                _elvis = _referenceDifferencesWithContext;
              } else {
                _elvis = List.of();
              }
              Iterable<Map.Entry<String, Diff>> _elvis_1 = null;
              EObject _right = thisMatch.getRight();
              Iterable<Map.Entry<String, Diff>> _referenceDifferencesWithContext_1 = null;
              if (_right != null) {
                _referenceDifferencesWithContext_1 = this.getReferenceDifferencesWithContext(_right, context,
                    reference, containment);
              }
              if (_referenceDifferencesWithContext_1 != null) {
                _elvis_1 = _referenceDifferencesWithContext_1;
              } else {
                _elvis_1 = List.of();
              }
              return Iterables.<Map.Entry<String, Diff>>concat(_elvis, _elvis_1);
            });
        _xblockexpression = Iterables.<Map.Entry<String, Diff>>concat(_map, _flatMapFixed);
      }
      return _xblockexpression;
    }

    @SuppressWarnings("unchecked")
    private Iterable<Map.Entry<String, Diff>> getReferenceDifferencesWithContext(final EObject object,
        final String context,
        final EReference reference, final boolean containment) {
      Iterable<Map.Entry<String, Diff>> _xblockexpression = null;
      {
        final String referenceContext = context + "." + reference.getName();
        Iterable<Map.Entry<String, Diff>> _xifexpression = null;
        boolean _isMany = reference.isMany();
        if (_isMany) {
          Object _eGet = object.eGet(reference);
          _xifexpression = IterableUtil.flatMapFixedIndexed(((Iterable<? extends EObject>) _eGet), (index, element) -> {
            String _xifexpression_1 = null;
            boolean _isOrdered = reference.isOrdered();
            if (_isOrdered) {
              _xifexpression_1 = "[" + index + "]";
            } else {
              _xifexpression_1 = "{" + index + "}";
            }
            final String elementIndicator = _xifexpression_1;
            return this.getDifferencesWithContext(element, (referenceContext + elementIndicator), containment);
          });
        } else {
          Object _eGet_1 = object.eGet(reference);
          _xifexpression = this.getDifferencesWithContext(((EObject) _eGet_1), referenceContext, containment);
        }
        _xblockexpression = _xifexpression;
      }
      return _xblockexpression;
    }

    private PrintResult printDifference(final PrintTarget target, final String context,
        final Diff difference) {
      PrintResult _print = target.print("• ");
      PrintResult _switchResult = null;
      boolean _matched = false;
      if (difference instanceof AttributeChange) {
        _matched = true;
        _switchResult = this.printFeatureDifference(target, ((AttributeChange) difference).getAttribute(), context,
            difference, ((AttributeChange) difference).getValue());
      }
      if (!_matched) {
        if (difference instanceof ReferenceChange) {
          _matched = true;
          _switchResult = this.printFeatureDifference(target, ((ReferenceChange) difference).getReference(), context,
              difference, ((ReferenceChange) difference).getValue());
        }
      }
      if (!_matched) {
        _switchResult = this.modelPrinter.printObject(target, this.idProvider, difference);
      }
      return PrintResultExtension.operator_plus(_print, _switchResult);
    }

    private PrintResult printFeatureDifference(final PrintTarget target, final EStructuralFeature feature,
        final String context, final Diff difference, final Object value) {
      PrintResult _print = target.print(context);
      PrintResult _xifexpression = null;
      EObject _left = difference.getMatch().getLeft();
      boolean _tripleNotEquals = (_left != null);
      if (_tripleNotEquals) {
        PrintResult _print_1 = target.print(" (");
        PrintResult _printObjectShortened = this.modelPrinter.printObjectShortened(target, this.idProvider,
            difference.getMatch().getLeft());
        PrintResult _plus = PrintResultExtension.operator_plus(_print_1, _printObjectShortened);
        PrintResult _print_2 = target.print(")");
        _xifexpression = PrintResultExtension.operator_plus(_plus, _print_2);
      } else {
        _xifexpression = PrintResult.PRINTED_NO_OUTPUT;
      }
      PrintResult _plus_1 = PrintResultExtension.operator_plus(_print, _xifexpression);
      PrintResult _print_3 = target.print(".");
      PrintResult _plus_2 = PrintResultExtension.operator_plus(_plus_1, _print_3);
      PrintResult _print_4 = target.print(feature.getName());
      PrintResult _plus_3 = PrintResultExtension.operator_plus(_plus_2, _print_4);
      PrintResult _print_5 = target.print(" ");
      PrintResult _plus_4 = PrintResultExtension.operator_plus(_plus_3, _print_5);
      PrintResult _print_6 = target.print(this.getVerb(difference.getKind()));
      PrintResult _plus_5 = PrintResultExtension.operator_plus(_plus_4, _print_6);
      PrintResult _print_7 = target.print(": ");
      PrintResult _plus_6 = PrintResultExtension.operator_plus(_plus_5, _print_7);
      EObject _elvis = null;
      EObject _left_1 = difference.getMatch().getLeft();
      if (_left_1 != null) {
        _elvis = _left_1;
      } else {
        EObject _right = difference.getMatch().getRight();
        _elvis = _right;
      }
      PrintResult _printFeatureValue = this.modelPrinter.printFeatureValue(target, this.idProvider, _elvis, feature,
          value);
      return PrintResultExtension.operator_plus(_plus_6, _printFeatureValue);
    }

    private String getVerb(final DifferenceKind kind) {
      String _switchResult = null;
      if (kind != null) {
        switch (kind) {
          case ADD:
            _switchResult = "contained the unexpected value";
            break;
          case DELETE:
            _switchResult = "was missing the value";
            break;
          case CHANGE:
            _switchResult = "had the wrong value";
            break;
          case MOVE:
            _switchResult = "had a different position for";
            break;
          default:
            break;
        }
      }
      return _switchResult;
    }

    public ComparisonPrinter(final PrintIdProvider idProvider, final Comparison comparison,
        final FeatureFilter featureFilter, final ModelPrinter modelPrinter) {
      super();
      this.idProvider = idProvider;
      this.comparison = comparison;
      this.featureFilter = featureFilter;
      this.modelPrinter = modelPrinter;
    }
  }

  private static class EqualityStrategyEqualityHelper extends EqualityHelper {
    private final EqualityStrategy equalityStrategy;

    private EqualityStrategyEqualityHelper(final LoadingCache<EObject, URI> uriCache,
        final EqualityStrategy equalityStrategy) {
      super(uriCache);
      this.equalityStrategy = equalityStrategy;
    }

    @Override
    public boolean matchingEObjects(final EObject object1, final EObject object2) {
      boolean _switchResult = false;
      EqualityStrategy.Result _compare = this.equalityStrategy.compare(object1, object2);
      if (_compare != null) {
        switch (_compare) {
          case EQUAL:
            _switchResult = true;
            break;
          case UNEQUAL:
            _switchResult = false;
            break;
          case UNKNOWN:
            _switchResult = super.matchingEObjects(object1, object2);
            break;
          default:
            break;
        }
      }
      return _switchResult;
    }
  }

  private static class FilteredFeaturesAwareWeightProvider implements WeightProvider {
    private final WeightProvider delegate;
    private final EqualityFeatureFilter featureFilter;

    @Override
    public int getWeight(final EStructuralFeature attribute) {
      int _xifexpression = (int) 0;
      boolean _includeFeature = this.featureFilter.includeFeature(attribute);
      if (_includeFeature) {
        _xifexpression = this.delegate.getWeight(attribute);
      } else {
        _xifexpression = 0;
      }
      return _xifexpression;
    }

    public FilteredFeaturesAwareWeightProvider(final WeightProvider delegate,
        final EqualityFeatureFilter featureFilter) {
      super();
      this.delegate = delegate;
      this.featureFilter = featureFilter;
    }

    public int getContainingFeatureWeight(final EObject arg0) {
      return this.delegate.getContainingFeatureWeight(arg0);
    }

    public int getParentWeight(final EObject arg0) {
      return this.delegate.getParentWeight(arg0);
    }
  }

  private static class ParametersAwareMatchEngineFactory implements IMatchEngine.Factory {
    private final UseIdentifiers useIdentifiers;

    private final EqualityStrategy equalityStrategy;

    private final EqualityFeatureFilter featureFilter;
    private int ranking = Integer.MAX_VALUE;

    @Override
    public IMatchEngine getMatchEngine() {
      final DefaultComparisonFactory comparisonFactory = new DefaultComparisonFactory(
          new DefaultEqualityHelperFactory() {
            @Override
            public IEqualityHelper createEqualityHelper() {
              LoadingCache<EObject, URI> _createDefaultCache = EqualityHelper
                  .createDefaultCache(this.getCacheBuilder());
              return new ModelDeepEqualityMatcher.EqualityStrategyEqualityHelper(_createDefaultCache,
                  ParametersAwareMatchEngineFactory.this.equalityStrategy);
            }
          });
      final IEObjectMatcher eObjectMatcher = DefaultMatchEngine.createDefaultEObjectMatcher(
          this.useIdentifiers,
          this.parameterAwareWeightProviderDescriptorRegistry(),
          EqualityHelperExtensionProviderDescriptorRegistryImpl.createStandaloneInstance());
      StrategyResourceMatcher _strategyResourceMatcher = new StrategyResourceMatcher();
      return new DefaultMatchEngine(eObjectMatcher, _strategyResourceMatcher, comparisonFactory);
    }

    @Override
    public boolean isMatchEngineFactoryFor(final IComparisonScope scope) {
      return true;
    }

    private WeightProviderDescriptorRegistryImpl parameterAwareWeightProviderDescriptorRegistry() {
      final WeightProviderDescriptorRegistryImpl resultRegistry = new WeightProviderDescriptorRegistryImpl();
      final WeightProvider.Descriptor.Registry sourceRegistry = WeightProviderDescriptorRegistryImpl
          .createStandaloneInstance();
      Collection<WeightProvider.Descriptor> _descriptors = sourceRegistry.getDescriptors();
      for (final WeightProvider.Descriptor descriptor : _descriptors) {
        String _name = descriptor.getWeightProvider().getClass().getName();
        WeightProvider _weightProvider = descriptor.getWeightProvider();
        ModelDeepEqualityMatcher.FilteredFeaturesAwareWeightProvider _filteredFeaturesAwareWeightProvider = new ModelDeepEqualityMatcher.FilteredFeaturesAwareWeightProvider(
            _weightProvider, this.featureFilter);
        int _ranking = descriptor.getRanking();
        Pattern _nsURI = descriptor.getNsURI();
        ModelDeepEqualityMatcher.WeightProviderDescriptorImpl _weightProviderDescriptorImpl = new ModelDeepEqualityMatcher.WeightProviderDescriptorImpl(
            _filteredFeaturesAwareWeightProvider, _ranking, _nsURI);
        resultRegistry.put(_name, _weightProviderDescriptorImpl);
      }
      return resultRegistry;
    }

    public ParametersAwareMatchEngineFactory(final UseIdentifiers useIdentifiers,
        final EqualityStrategy equalityStrategy, final EqualityFeatureFilter featureFilter) {
      super();
      this.useIdentifiers = useIdentifiers;
      this.equalityStrategy = equalityStrategy;
      this.featureFilter = featureFilter;
    }

    @Override
    public int getRanking() {
      return this.ranking;
    }

    public void setRanking(final int ranking) {
      this.ranking = ranking;
    }
  }

  private static class WeightProviderDescriptorImpl implements WeightProvider.Descriptor {
    private final WeightProvider weightProvider;

    private final int ranking;

    private final Pattern nsURI;

    public WeightProviderDescriptorImpl(final WeightProvider weightProvider, final int ranking, final Pattern nsURI) {
      super();
      this.weightProvider = weightProvider;
      this.ranking = ranking;
      this.nsURI = nsURI;
    }

    @Override
    public WeightProvider getWeightProvider() {
      return this.weightProvider;
    }

    @Override
    public int getRanking() {
      return this.ranking;
    }

    @Override
    public Pattern getNsURI() {
      return this.nsURI;
    }
  }

  private final O expectedObject;

  private final List<? extends ModelDeepEqualityOption> options;

  private final ModelDeepEqualityMatcher.ComparisonAwarePrintIdProvider idProvider = new ModelDeepEqualityMatcher.ComparisonAwarePrintIdProvider();

  private final ModelPrinter descriptionPrinter;

  private final EqualityFeatureFilter featureFilter;

  private final FeatureFilter emfCompareFeatureFilter;

  private Comparison comparison;

  ModelDeepEqualityMatcher(final O expectedEObject, final List<? extends ModelDeepEqualityOption> options) {
    this.expectedObject = expectedEObject;
    this.options = options;
    final List<EqualityFeatureFilter> featureFilters = Lists.newArrayList(
        Iterables.<EqualityFeatureFilter>filter(options, EqualityFeatureFilter.class));
    MultiEqualityFeatureFilter _multiEqualityFeatureFilter = new MultiEqualityFeatureFilter(featureFilters);
    this.featureFilter = _multiEqualityFeatureFilter;
    ModelDeepEqualityMatcher.IgnoredFeaturesPrinter _ignoredFeaturesPrinter = new ModelDeepEqualityMatcher.IgnoredFeaturesPrinter(
        this.featureFilter);
    this.descriptionPrinter = _ignoredFeaturesPrinter;
    ModelDeepEqualityMatcher.FixOrderingFeatureFilter _xifexpression = null;
    boolean _isEmpty = featureFilters.isEmpty();
    if (_isEmpty) {
      _xifexpression = new ModelDeepEqualityMatcher.FixOrderingFeatureFilter();
    } else {
      _xifexpression = new ModelDeepEqualityMatcher.EmfCompareEqualityFeatureFilter(this.featureFilter);
    }
    this.emfCompareFeatureFilter = _xifexpression;
  }

  @Override
  public boolean matchesSafely(final EObject item) {
    EMFCompare _buildEmfCompare = this.buildEmfCompare(item);
    DefaultComparisonScope _defaultComparisonScope = new DefaultComparisonScope(item, this.expectedObject, null);
    this.comparison = _buildEmfCompare.compare(_defaultComparisonScope);
    return this.comparison.getDifferences().isEmpty();
  }

  @Override
  public void describeTo(final Description description) {
    try {
      this.describeTo(ModelPrinting.prepend(this.descriptionPrinter), description);
    } catch (RuntimeException | Error _e) {
      throw _e;
    } catch (Throwable _e) {
      throw new RuntimeException(_e);
    }
  }

  private void describeTo(final AutoCloseable printerChange, final Description description)
      throws Exception {
    try (AutoCloseable r_printerChange = printerChange) {
      _describeTo_with_safe_resources(r_printerChange, description);
    }
  }

  @Override
  public void describeMismatchSafely(final EObject item, final Description mismatchDescription) {
    try {
      this.describeMismatchSafely(ModelPrinting.prepend(this.descriptionPrinter), item, mismatchDescription);
    } catch (RuntimeException | Error _e) {
      throw _e;
    } catch (Throwable _e) {
      throw new RuntimeException(_e);
    }
  }

  private void describeMismatchSafely(final AutoCloseable printerChange, final EObject item,
      final Description mismatchDescription) throws Exception {
    try (AutoCloseable r_printerChange = printerChange) {
      _describeMismatchSafely_with_safe_resources(r_printerChange, item, mismatchDescription);
    }
  }

  private EMFCompare buildEmfCompare(final EObject item) {
    EMFCompare.Builder _builder = EMFCompare.builder();
    final Consumer<EMFCompare.Builder> _function = (EMFCompare.Builder it) -> {
      IMatchEngine.Factory.Registry _createStandaloneInstance = MatchEngineFactoryRegistryImpl
          .createStandaloneInstance();
      final Consumer<IMatchEngine.Factory.Registry> _function_1 = (IMatchEngine.Factory.Registry it_1) -> {
        List<EqualityStrategy> _list = Lists.newArrayList(
            Iterables.<EqualityStrategy>filter(this.options, EqualityStrategy.class));
        MultiEqualityStrategy _multiEqualityStrategy = new MultiEqualityStrategy(_list);
        ModelDeepEqualityMatcher.ParametersAwareMatchEngineFactory _parametersAwareMatchEngineFactory = new ModelDeepEqualityMatcher.ParametersAwareMatchEngineFactory(
            UseIdentifiers.NEVER, _multiEqualityStrategy,
            this.featureFilter);
        it_1.add(_parametersAwareMatchEngineFactory);
      };
      _function_1.accept(_createStandaloneInstance);
      it.setMatchEngineFactoryRegistry(_createStandaloneInstance);
      DiffBuilder _diffBuilder = new DiffBuilder();
      it.setDiffEngine(new DefaultDiffEngine(_diffBuilder) {
        @Override
        public FeatureFilter createFeatureFilter() {
          return ModelDeepEqualityMatcher.this.emfCompareFeatureFilter;
        }
      });
      PostProcessorDescriptorRegistryImpl<Object> _postProcessorDescriptorRegistryImpl = new PostProcessorDescriptorRegistryImpl<Object>();
      final Consumer<PostProcessorDescriptorRegistryImpl<Object>> _function_2 = (
          PostProcessorDescriptorRegistryImpl<Object> it_1) -> {
        String _name = ModelDeepEqualityMatcher.MatchRoots.class.getName();
        ModelDeepEqualityMatcher.MatchRoots _matchRoots = new ModelDeepEqualityMatcher.MatchRoots(item,
            this.expectedObject);
        Pattern _compile = Pattern.compile(".*");
        BasicPostProcessorDescriptorImpl _basicPostProcessorDescriptorImpl = new BasicPostProcessorDescriptorImpl(
            _matchRoots, _compile, null);
        it_1.put(_name, _basicPostProcessorDescriptorImpl);
      };
      _function_2.accept(_postProcessorDescriptorRegistryImpl);
      it.setPostProcessorRegistry(_postProcessorDescriptorRegistryImpl);
    };
    _function.accept(_builder);
    return _builder.build();
  }

  private void _describeTo_with_safe_resources(final AutoCloseable printerChange, final Description description) {
    ModelPrinting.appendModelValue(
        description.appendText(TestMessages.a(this.expectedObject.eClass().getName())).appendText(" deeply equal to "),
        this.expectedObject, this.idProvider);
  }

  private void _describeMismatchSafely_with_safe_resources(final AutoCloseable printerChange, final EObject item,
      final Description mismatchDescription) {
    this.comparison.getMatch(this.expectedObject);
    mismatchDescription.appendText("found the following differences:");
    this.idProvider.comparison = this.comparison;
    final Function<PrintTarget, PrintResult> _function = (PrintTarget target) -> {
      ModelPrinter _printer = ModelPrinting.getPrinter();
      return new ModelDeepEqualityMatcher.ComparisonPrinter(this.idProvider, this.comparison,
          this.emfCompareFeatureFilter, _printer).printDifferences(target, this.expectedObject);
    };
    ModelPrinting.appendPrintResult(mismatchDescription, _function);
    ModelPrinting.appendModelValue(mismatchDescription.appendText("    for object "), item, this.idProvider);
    boolean _isEmpty = this.options.isEmpty();
    boolean _not = (!_isEmpty);
    if (_not) {
      final BiConsumer<StringBuilder, ModelDeepEqualityOption> _function_1 = (StringBuilder target,
          ModelDeepEqualityOption it) -> {
        it.describeTo(target);
      };
      mismatchDescription.appendText(System.lineSeparator()).appendText(System.lineSeparator()).appendText(
          "    (the comparison ")
          .appendText(TestMessages.<ModelDeepEqualityOption>joinSemantic(this.options, "and", ";", _function_1))
          .appendText(")");
    }
  }
}
