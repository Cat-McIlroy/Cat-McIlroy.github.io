package com.cmcilroy.medicines_shortages_assistant.services;

import com.cmcilroy.medicines_shortages_assistant.domain.entities.DrugEntity;

public interface DrugsUpdateService {

    void downloadAuthorisedList();

    void downloadInterchangeablesList();

    void downloadWithdrawnList();

    void initialUpdate();

    void scheduledUpdate();

    boolean isDifferent(DrugEntity existingRecord, DrugEntity drugEntity);

}
