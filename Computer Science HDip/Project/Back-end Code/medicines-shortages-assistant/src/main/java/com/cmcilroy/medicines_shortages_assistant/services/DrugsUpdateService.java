package com.cmcilroy.medicines_shortages_assistant.services;

public interface DrugsUpdateService {

    void downloadAuthorisedList();

    void downloadInterchangeablesList();

    void downloadWithdrawnList();

    void initialUpdate();

    void scheduledUpdate();

}
