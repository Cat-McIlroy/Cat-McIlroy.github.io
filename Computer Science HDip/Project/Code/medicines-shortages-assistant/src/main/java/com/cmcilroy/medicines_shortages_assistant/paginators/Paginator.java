package com.cmcilroy.medicines_shortages_assistant.paginators;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface Paginator<T> {

    Page<T> paginate(List<T> list, Pageable pageable);

}
