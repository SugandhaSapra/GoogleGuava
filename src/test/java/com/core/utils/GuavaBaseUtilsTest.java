package com.core.utils;

import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class GuavaBaseUtilsTest {

    @Test
    public void should_check_preconditions() {
        String mobileNum = "99999999A";
        String message = "Mobile Number should contain only numbers";
        assertThatThrownBy(
                () -> Preconditions.checkArgument(mobileNum.matches("[0-9]+") && mobileNum.length() == 10, message))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(message).hasNoCause();

        String message2 = "Null Objects should not be passed";
        assertThatThrownBy(() -> Preconditions.checkNotNull(null, message2))
                .isInstanceOf(NullPointerException.class)
                .hasMessage(message2).hasNoCause();

        int[] validStates = {-1, 0, 1};
        int givenState = 10;
        String message1 = "You have entered an invalid state";

        assertThatThrownBy(
                () -> Preconditions.checkState(
                        Arrays.binarySearch(validStates, givenState) > 0, message1))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageStartingWith(message1).hasNoCause();
    }

    @Test
    public void should_get_elapsed_time() throws InterruptedException {
        Stopwatch stopwatch = Stopwatch.createStarted();
        Thread.sleep(1000);
        stopwatch.stop();
        assertThat(stopwatch.elapsed(TimeUnit.MILLISECONDS)).isEqualTo(1000);
    }

    @Test
    public void should_split_strings() throws InterruptedException {
        //Converting String to collections
        String input = "Fruit::Apple  ,,Dessert::Waffle  ,Veggies::Spinach,  Dairy Product::Cheese,,";
        Map<String, String> mapOfClassAndItem = Splitter.on(",")
                .trimResults()
                .omitEmptyStrings()
                .withKeyValueSeparator("::")
                .split(input);
        assertThat(mapOfClassAndItem.get("Fruit")).isEqualTo("Apple");
        assertThat(mapOfClassAndItem.get("Dessert")).isEqualTo("Waffle");
        assertThat(mapOfClassAndItem.get("Veggies")).isEqualTo("Spinach");
        assertThat(mapOfClassAndItem.get("Dairy Product")).isEqualTo("Cheese");

    }

    @Test
    public void should_join_collection_to_string() {
        //String to collection
        ImmutableMap<Object, Object> food = ImmutableMap.builder()
                .put("Fruit", "Apple")
                .put("Dessert", "Waffle")
                .put("Veggies", "Spinach")
                .put("Dairy Product", "Cheese")
                .build();
        String result = Joiner.on(",")
                .withKeyValueSeparator("::")
                .join(food);
        assertThat(result).isEqualTo("Fruit::Apple,Dessert::Waffle,Veggies::Spinach,Dairy Product::Cheese");

    }

    @Test
    public void should_test_char_matcher() {
        String sample = "Opera refers to a dramatic art form, originating in Europe";
        CharMatcher matcher = CharMatcher.anyOf("aeiou");
        int result = matcher.countIn(sample);//Count vowels
        assertThat(result).isEqualTo(20);
        String input = "       hello    world      ";
        String output = CharMatcher.is(' ').trimAndCollapseFrom(input, '-');
        assertThat(output).isEqualTo("hello-world");
    }
}
