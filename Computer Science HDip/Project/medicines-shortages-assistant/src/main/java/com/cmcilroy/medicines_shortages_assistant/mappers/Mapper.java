package com.cmcilroy.medicines_shortages_assistant.mappers;

public interface Mapper<A,B> {

    B mapTo(A a);

    A mapFrom(B b);

}
