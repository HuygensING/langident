package nl.knaw.huygens.pergamon.nlp.langident;

import com.optimaize.langdetect.LanguageDetector;
import com.optimaize.langdetect.LanguageDetectorBuilder;
import com.optimaize.langdetect.ngram.NgramExtractors;
import com.optimaize.langdetect.profiles.LanguageProfile;
import com.optimaize.langdetect.profiles.LanguageProfileBuilder;
import com.optimaize.langdetect.text.CommonTextObjectFactories;
import com.optimaize.langdetect.text.TextObject;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Adapter for com.optimaize.languagedetector.
 */
public class Optimaize extends LanguageGuesser {
  private LanguageDetector ld;
  private Map<String, LanguageProfile> profiles;

  @Override
  public Set<String> languages() {
    return profiles.keySet();
  }

  @Override
  protected Stream<Prediction> predictStream(CharSequence doc) {
    return ld.getProbabilities(doc).stream()
      .map(detected -> new Prediction(detected.getLocale().getLanguage(), detected.getProbability()));
  }

  @Override
  public LanguageGuesser train(List<CharSequence> docs, List<String> labels) {
    Map<String, StringBuilder> trainingData = new HashMap<>();

    for (int i = 0; i < docs.size(); i++) {
      String label = labels.get(i);
      if (!trainingData.containsKey(label)) {
        trainingData.put(label, new StringBuilder());
      }
      trainingData.get(label).append(docs.get(i));
    }

    final TextObject textObject = CommonTextObjectFactories.forDetectingShortCleanText().create();
    profiles = trainingData.entrySet().stream()
      .map(entry -> {
        String label = entry.getKey();
        StringBuilder data = entry.getValue();
        LanguageProfile profile = new LanguageProfileBuilder(label)
          .ngramExtractor(NgramExtractors.standard())
          .minimalFrequency(5)
          .addText(data.toString())
          .build();
        return Pair.of(label, profile);
      })
      .collect(Collectors.toMap(Pair::getLeft, Pair::getRight));

    ld = LanguageDetectorBuilder.create(NgramExtractors.standard())
      .withProfiles(profiles.values())
      .build();

    return this;
  }
}
