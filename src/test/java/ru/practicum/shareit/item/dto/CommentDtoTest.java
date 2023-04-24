package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CommentDtoTest {

    @Autowired
    private JacksonTester<CommentDto> json;

    private final CommentDto commentDto = new CommentDto(1L, "text", "author", LocalDateTime.now());

    @Test
    void commentDto() throws Exception {
        var res = json.write(commentDto);

        assertThat(res).hasJsonPath("$.id");
        assertThat(res).hasJsonPath("$.text");
        assertThat(res).hasJsonPath("$.authorName");
        assertThat(res).hasJsonPath("$.created");

        assertThat(res).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(res).extractingJsonPathStringValue("$.text").isEqualTo("text");
        assertThat(res).extractingJsonPathStringValue("$.authorName").isEqualTo("author");
    }

}