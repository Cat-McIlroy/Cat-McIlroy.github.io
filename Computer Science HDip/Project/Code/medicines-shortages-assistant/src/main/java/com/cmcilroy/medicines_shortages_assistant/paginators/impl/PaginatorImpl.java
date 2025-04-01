package com.cmcilroy.medicines_shortages_assistant.paginators.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.cmcilroy.medicines_shortages_assistant.paginators.Paginator;

@Component
public class PaginatorImpl<T> implements Paginator<T> {

    @Override
    public Page<T> paginate(List<T> list, Pageable pageable) {

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), list.size());

            if (start >= list.size()) {
                // if start is beyond the size of the list, adjust start and end
                start = list.size() - pageable.getPageSize();
                end = list.size();
            }
            List<T> pagedResults = list.subList(start, end);

            return new PageImpl<T>(pagedResults, pageable, list.size());
    }

}
