package ru.practicum.shareit.utility;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.errorhandler.model.Violation;
import ru.practicum.shareit.exceptions.ValidationException;

public class PageCalc {

    public static Pageable getPageable(Integer from, Integer size) {
        if (from == null) {
            return Pageable.unpaged();
        } else if (from < 0) {
            throw new ValidationException(new Violation("from", "Некорректное значение"));
        } else {
            return PageRequest.of(from / size, size);
        }
    }

}
