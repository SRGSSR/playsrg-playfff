package ch.srgssr.playfff.utils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SeoTests {
    /*
        Tests that convert a string to a seo name
        Based on Play SRG web application (from 'to-seo-string.test.ts')
     */

    @Test
    public void shouldLowercaseGivenString() {
        assertThat(Seo.nameFromTitle("ABCDEFGhi")).isEqualTo("abcdefghi");
    }

    @Test
    public void shouldNotChangeALowercaseCharactersAndNumbers() {
        assertThat(Seo.nameFromTitle("abcd12345")).isEqualTo("abcd12345");
    }

    @Test
    public void shouldReplaceRemoveWhitespaceFromSeperatingDashes() {
        assertThat(Seo.nameFromTitle("test - content")).isEqualTo("test-content");
    }

    @Test
    public void shouldReplaceNewlinesAndTabsAndSpacesWithDashes() {
        assertThat(Seo.nameFromTitle("\ntest\tcontent ")).isEqualTo("-test-content-");
    }

    @Test
    public void shouldReplaceDotsWithADash() {
        assertThat(Seo.nameFromTitle(".")).isEqualTo("-");
    }

    @Test
    public void shouldReplaceLongDashesWithNormalOnes() {
        assertThat(Seo.nameFromTitle("–")).isEqualTo("-");
    }

    @Test
    public void shouldReplaceUtf8NonBreakingSpacesWithADash() {
        assertThat(Seo.nameFromTitle("\\xa0")).isEqualTo("-");
    }

    @Test
    public void shouldReplaceAUmlautWithAe() {
        assertThat(Seo.nameFromTitle("ä")).isEqualTo("ae");
    }

    @Test
    public void shouldReplaceOUmlautWithOe() {
        assertThat(Seo.nameFromTitle("ö")).isEqualTo("oe");
    }

    @Test
    public void shouldReplaceUUmlautWithUe() {
        assertThat(Seo.nameFromTitle("ü")).isEqualTo("ue");
    }

    @Test
    public void shouldReplaceOWithAccentsWithNormalOnes() {
        assertThat(Seo.nameFromTitle("óòô")).isEqualTo("ooo");
    }

    @Test
    public void shouldReplaceEWithAccentsWithNormalOnes() {
        assertThat(Seo.nameFromTitle("éèê")).isEqualTo("eee");
    }

    @Test
    public void shouldReplaceAWithAccentsWithNormalOnes() {
        assertThat(Seo.nameFromTitle("áàâ")).isEqualTo("aaa");
    }

    @Test
    public void shouldReplaceUWithAccentsWithNormalOnes() {
        assertThat(Seo.nameFromTitle("úùû")).isEqualTo("uuu");
    }

    @Test
    public void shouldRemoveAllSpecialCharactersOtherThanDashAndUnderline() {
        assertThat(Seo.nameFromTitle("_-!§$%&/()=?`@[]{}\\#*,:+°^@|<>µ³²'\"")).isEqualTo("_-");
    }
}
