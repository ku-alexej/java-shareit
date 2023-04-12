package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class AnswerBookingDtoTest {

    @Autowired
    private JacksonTester<AnswerBookingDto> json;

    private final User owner = new User(1L, "owner", "owner@ya.ru");

    private final UserDto userDto = new UserDto(2L, "user", "mail@ya.ru");

    private final ItemDto itemDto = new ItemDto(
            1L,
            "item",
            "desc",
            true,
            owner,
            1L);

    private final AnswerBookingDto answerBookingDto = new AnswerBookingDto(
            1L,
            LocalDateTime.now().plusDays(1),
            LocalDateTime.now().plusDays(2),
            itemDto,
            userDto,
            Status.WAITING);

    @Test
    void AnswerBookingDto() throws Exception {
        var res = json.write(answerBookingDto);

        assertThat(res).hasJsonPath("$.id");
        assertThat(res).hasJsonPath("$.start");
        assertThat(res).hasJsonPath("$.end");
        assertThat(res).hasJsonPath("$.item");
        assertThat(res).hasJsonPath("$.booker");
        assertThat(res).hasJsonPath("$.status");

        assertThat(res).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(res).extractingJsonPathNumberValue("$.item.id").isEqualTo(1);
        assertThat(res).extractingJsonPathNumberValue("$.booker.id").isEqualTo(2);
        assertThat(res).extractingJsonPathStringValue("$.status")
                .isEqualTo(answerBookingDto.getStatus().toString());

    }

}