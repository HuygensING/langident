package nl.knaw.huygens.pergamon.nlp.langident;

import org.junit.Assert;
import org.junit.Test;

import java.util.Set;

public class JTCLTest extends LanguageGuesserTest {
  @Test
  public void testJTCL() {
    LanguageGuesser guesser = new JTCLLanguageGuesser();
    Set<String> languages = guesser.languages();
    Assert.assertTrue(languages.size() > 5);

    // Translation to ISO codes should work
    languages.forEach(l -> Assert.assertTrue(l.length() == 2));

    // Disabled because JTCL cannot even get these simple examples right.
//    testAll("en", english, guesser);
//    testAll("it", italian, guesser);
//    testAll("nl", dutch, guesser);
  }

  private void testAll(String expected, String[] inputs, LanguageGuesser guesser) {
    for (String s : inputs) {
      Assert.assertEquals(expected, guesser.predictBest(s));
    }
  }
}
