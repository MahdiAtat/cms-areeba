package com.areeba.cms.cmsmircoservice.transactions.service;

import com.areeba.cms.cmsmicroservice.type.AccountCardIdsResponse;
import com.areeba.cms.cmsmicroservice.type.CardCreateRequest;
import com.areeba.cms.cmsmicroservice.type.CardIdPage;
import com.areeba.cms.cmsmicroservice.type.CardResponse;

import java.util.UUID;

public interface CardService {

    CardResponse createCardService(CardCreateRequest req);

    void activateCardService(UUID id);

    void deactivateCardService(UUID id);

    CardResponse getCardService(UUID id);

    CardIdPage listCardIds(int page, int size);

    AccountCardIdsResponse listCardIdsByAccount(UUID accountId, int page, int size);

}
