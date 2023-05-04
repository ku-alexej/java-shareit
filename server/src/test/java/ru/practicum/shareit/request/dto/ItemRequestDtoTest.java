package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;


import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestDtoTest {

    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @Test
    void itemRequestDto() throws Exception {

        LocalDateTime created = LocalDateTime.of(2023, 4, 10, 10, 10, 10);

        ItemRequestDto itemRequestDto = new ItemRequestDto(
                1L,
                "description1",
                null,
                created);

        var res = json.write(itemRequestDto);

        assertThat(res).hasJsonPath("$.id");
        assertThat(res).hasJsonPath("$.description");
        assertThat(res).hasJsonPath("$.requesterId");
        assertThat(res).hasJsonPath("$.created");

        assertThat(res).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(res).extractingJsonPathStringValue("$.description").isEqualTo("description1");
        assertThat(res).extractingJsonPathStringValue("$.requesterId").isNull();
        assertThat(res).extractingJsonPathStringValue("$.created").isEqualTo(created.toString());
    }
}