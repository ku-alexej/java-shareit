package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.Status;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class BookingDto {

    private Long id;

    @NotNull(message = "Booking's start time can't be null")
    @FutureOrPresent(message = "Booking's start time mustn't be in past")
    private LocalDateTime start;

    @NotNull(message = "Booking's end time can't be null")
    @Future(message = "Booking's end time mustn't be in future")
    private LocalDateTime end;

    @NotNull(message = "Booking's itemId can't be null")
    private Long itemId;

    private Long bookerId;

    private Status status;

}