package tools.vitruv.change.testutils.printing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.BiConsumer;

public final class TestMessages {
  public static String a(final String string) {
    String _switchResult = null;
    String _lowerCase = string.substring(0, 1).toLowerCase();
    if (_lowerCase != null) {
      switch (_lowerCase) {
        case "a":
        case "e":
        case "i":
        case "o":
          _switchResult = "an ";
          break;
        default:
          _switchResult = "a ";
          break;
      }
    } else {
      _switchResult = "a ";
    }
    return (_switchResult + string);
  }

  public static String plural(final String string) {
    String _xifexpression = null;
    boolean _endsWith = string.endsWith("s");
    if (_endsWith) {
      _xifexpression = (string + "es");
    } else {
      _xifexpression = (string + "s");
    }
    return _xifexpression;
  }

  public static String plural(final Collection<?> reference, final String string) {
    String _xifexpression = null;
    int _size = reference.size();
    boolean _notEquals = (_size != 1);
    if (_notEquals) {
      _xifexpression = TestMessages.plural(string);
    } else {
      _xifexpression = string;
    }
    return _xifexpression;
  }

  public static <T extends Object> StringBuilder joinSemantic(final StringBuilder builder,
      final Collection<? extends T> collection, final String word, final Consumer<T> mapper) {
    return TestMessages.<T>joinSemantic(builder, collection, word, ",", mapper);
  }

  public static <T extends Object> StringBuilder joinSemantic(final StringBuilder builder,
      final Collection<? extends T> collection, final String word, final String separator, final Consumer<T> mapper) {
    {
      int i = 0;
      Iterator<? extends T> iterator = collection.iterator();
      boolean _hasNext = iterator.hasNext();
      boolean _while = _hasNext;
      while (_while) {
        {
          final T element = iterator.next();
          if ((i > 0)) {
            builder.append(separator).append(" ");
            int _size = collection.size();
            int _minus = (_size - 1);
            boolean _equals = (i == _minus);
            if (_equals) {
              builder.append(word).append(" ");
            }
          }
          mapper.accept(element);
        }
        int _i = i;
        i = (_i + 1);
        boolean _hasNext_1 = iterator.hasNext();
        _while = _hasNext_1;
      }
    }
    return builder;
  }

  public static <T extends Object> String joinSemantic(final Collection<? extends T> collection, final String word,
      final String separator, final BiConsumer<StringBuilder, ? super T> mapper) {
    String _xblockexpression = null;
    {
      final StringBuilder result = new StringBuilder();
      final Consumer<T> _function = (T it) -> {
        mapper.accept(result, it);
      };
      TestMessages.<T>joinSemantic(result, collection, word, separator, _function);
      _xblockexpression = result.toString();
    }
    return _xblockexpression;
  }

  public static <T extends Object> String joinSemantic(final Iterable<? extends T> iterable, final String word,
      final String separator, final BiConsumer<StringBuilder, ? super T> mapper) {
    return TestMessages.<T>joinSemantic(
        new ArrayList<T>(com.google.common.collect.Lists.newArrayList(iterable)),
        word, separator, mapper);
  }

  private TestMessages() {

  }
}
