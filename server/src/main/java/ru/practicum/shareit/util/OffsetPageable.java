package ru.practicum.shareit.util;

import lombok.Setter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;

@Setter
public class OffsetPageable implements Pageable {

    private int from;
    private int size;
    private Sort sort;

    public OffsetPageable(int from, int size, Sort sort) {
        this.from = from;
        this.size = size;
        this.sort = sort;
        if (sort == null)
            this.sort = Sort.unsorted();
    }

    @Override
    public int getPageNumber() {
        return from / size;
    }

    @Override
    public int getPageSize() {
        return size;
    }

    @Override
    public long getOffset() {
        return from;
    }

    @Override
    @NonNull
    public Sort getSort() {
        return sort;
    }

    @Override
    @NonNull
    public Pageable next() {
        return new OffsetPageable(from + size, size, sort);
    }

    @Override
    @NonNull
    public Pageable previousOrFirst() {
        return new OffsetPageable(Math.max(from - size, 0), size, sort);
    }

    @Override
    @NonNull
    public Pageable first() {
        return new OffsetPageable(0, size, sort);
    }

    @Override
    @NonNull
    public Pageable withPage(int pageNumber) {
        return new OffsetPageable(pageNumber * size, size, sort);
    }

    @Override
    public boolean hasPrevious() {
        return getPageNumber() > 0;
    }
}