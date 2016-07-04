package nl.knaw.huygens.pergamon.nlp.langident;

import org.knallgrau.utils.textcat.TextCategorizer;

import java.util.*;
import java.util.stream.Stream;

import static java.util.Collections.unmodifiableSet;

/**
 * Language guesser based on the JTCL library (http://textcat.sourceforge.net/).
 */
public class JTCLLanguageGuesser extends LanguageGuesser {
  private final TextCategorizer tc = new TextCategorizer();

  // Translates JTCL's human-readable language names to ISO codes.
  private static final Map<String, String> languageCode = new HashMap<>();

  static {
    languageCode.put("albanian", "sq");
    languageCode.put("danish", "da");
    languageCode.put("dutch", "nl");
    languageCode.put("finnish", "fi");
    languageCode.put("french", "fr");
    languageCode.put("english", "en");
    languageCode.put("german", "de");
    languageCode.put("italian", "it");
    languageCode.put("norwegian", "no");
    languageCode.put("polish", "pl");
    languageCode.put("slovakian", "sk");
    languageCode.put("slovenian", "sl");
    languageCode.put("spanish", "es");
  }

  private static final Set<String> codes = unmodifiableSet(new HashSet<>(languageCode.values()));

  @Override
  public Set<String> languages() {
    return codes;
  }

  @Override
  protected Stream<Prediction> predictStream(CharSequence doc) {
    return tc.getCategoryDistances(doc.toString()).entrySet().stream()
      .map(entry -> {
        String code = languageCode.getOrDefault(entry.getKey(), entry.getKey());
        return new Prediction(code, entry.getValue());
      });
  }

  /**
   * Throws an UnsupportedOperationException: JTCL uses its built-in profiles,
   * not training data.
   *
   * @param docs
   * @param labels
   * @return this
   */
  @Override
  public LanguageGuesser train(List<CharSequence> docs, List<String> labels) {
    throw new UnsupportedOperationException("JTCL is not re-trainable");
  }
}
