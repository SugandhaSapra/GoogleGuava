package com.core.utils;


import com.google.common.base.Function;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import com.google.common.collect.Ordering;
import com.google.common.collect.Table;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class GuavaCollectionsTest {
    String sample = "Opera refers to a dramatic art form, originating in Europe,"
                    + " in which the emotional content is conveyed to the audience "
                    + "as much through music, both vocal and instrumental, "
                    + "as it is through the lyrics. By contrast, in musical "
                    + "theater an actor's dramatic performance is primary, and "
                    + "the music plays a lesser role. The drama in opera is presented "
                    + "using the primary elements of theater such as scenery, costumes, "
                    + "and acting. However, the words of the opera, or libretto, are sung "
                    + "rather than spoken. The singers are accompanied by a musical ensemble"
                    + " ranging from a small instrumental ensemble to a full symphonic orchestra.";

    @Test
    public void should_count_occurrences_by_multiset() {
        //Initializing Multiset
        Multiset<Character> letters = sample.chars()
                .mapToObj(c -> (char) c)
                .filter(c -> !c.equals(' '))
                .collect(Collectors.toCollection(HashMultiset::create));
        //Checking occurrence
        assertThat(letters.count('a')).isEqualTo(48);

        for (Character letter : Multisets.copyHighestCountFirst(letters).elementSet()) {
            System.out.format("%s appears %d times in the passage...\n", letter, letters.count(letter));
        }
    }

    @Test
    public void should_fetch_all_words_with_given_letter_by_multimap() {
        List<String> words = Arrays.stream(sample.split(" "))
                .collect(Collectors.toList());
        //Initializing Multiset with start letter and words
        Multimap<Character, String> wordWithFirstLetter = Multimaps.index(words, w -> w.charAt(0));
        assertThat(wordWithFirstLetter.get('o')).contains("opera");
        System.out.println("Words starting with o: " + wordWithFirstLetter.get('o'));
    }

    @Test
    public void should_get_max_length_word_of_given_char_with_bimap() {
        //Initializing BiMap with Start Letter and Words with maximum length
        BiMap<Character, String> wordsWithMaxLengthPerChar = Arrays.stream(sample.split(" "))
                .collect(Collectors.toMap(x -> x.charAt(0), x -> x, (a, b) -> {
                    if (a.length() > b.length())
                        return a;
                    else
                        return b;
                }, HashBiMap::create));
        String maxLengthWordWithGivenLetter = wordsWithMaxLengthPerChar.get('a');
        assertThat(maxLengthWordWithGivenLetter).isEqualTo("accompanied");
        assertThat(wordsWithMaxLengthPerChar.inverse().get(maxLengthWordWithGivenLetter)).isEqualTo('a');
        System.out.format("Highest length word with letter %s is %s ",
                wordsWithMaxLengthPerChar.inverse().get(maxLengthWordWithGivenLetter),
                maxLengthWordWithGivenLetter);
    }

    @Test
    public void should_get_based_on_first_char_and_length_with_table() {
        //Initializing Table with Start letter as row,length as col,word as value
        Table<Character, Integer, String> tableWithStartCharAndLength = Arrays.stream(sample.split(" "))
                .map(s -> ImmutableTable.of(s.charAt(0), s.length(), s))
                .flatMap(table -> table.cellSet().stream())
                .collect(ImmutableTable.toImmutableTable(
                        Table.Cell::getRowKey,
                        Table.Cell::getColumnKey,
                        Table.Cell::getValue,
                        (b1, b2) -> b2));//merge function
        assertThat(tableWithStartCharAndLength.row('d')).containsKeys(8,5);
        assertThat(tableWithStartCharAndLength.row('d')).containsValues("dramatic","drama");
        assertThat(tableWithStartCharAndLength.get('O',5)).isEqualTo("Opera");
        System.out.format("Words with length of letter d is %s\n ", tableWithStartCharAndLength.row('d'));
        System.out.format("Words with letter O and length 5 is %s\n ", tableWithStartCharAndLength.get('O',5));

    }

    @Test
    public void should_sort_based_on_ordering() {
        List<String> words = Arrays.stream(sample.split(" "))
                .map(String::toLowerCase)
                .distinct()
                .collect(Collectors.toList());
        //sorting alphabetically
        words.sort(Ordering.natural());
        assertThat(words).startsWith("a");
        assertThat(words).endsWith("words");
        //Custom Sorting
        Ordering<String> customOrdering = Ordering.natural()
                .reverse()
                .onResultOf(getLength())
                .compound(Ordering.natural());
        words.sort(customOrdering);
        assertThat(words).startsWith("instrumental,");
        assertThat(words).endsWith("a");
        System.out.println(words);
    }

    private Function<String,Integer> getLength()
    {
      return   new Function<String, Integer>() {
            public Integer apply(String word) {
                return word.length();
            }
        };
    }
}
