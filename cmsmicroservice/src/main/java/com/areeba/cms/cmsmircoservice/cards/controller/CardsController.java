package com.areeba.cms.cmsmircoservice.cards.controller;

import com.areeba.cms.cmsmicroservice.controller.CardsApi;
import com.areeba.cms.cmsmicroservice.type.CardCreateRequest;
import com.areeba.cms.cmsmicroservice.type.CardIdPage;
import com.areeba.cms.cmsmicroservice.type.CardResponse;
import com.areeba.cms.cmsmircoservice.cards.service.CardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class CardsController implements CardsApi {

    private final CardService cardService;

    public CardsController(CardService cardService) {
        this.cardService = cardService;
    }

    @Override
    public ResponseEntity<Void> activateCardById(UUID id) {
        cardService.activateCardService(id);
        return ResponseEntity.noContent().build();

    }

    @Override
    public ResponseEntity<CardResponse> createCard(CardCreateRequest card) {
        return ResponseEntity.ok(cardService.createCardService(card));
    }

    @Override
    public ResponseEntity<Void> deactivateCardById(UUID id) {
        cardService.deactivateCardService(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<CardResponse> getCardById(UUID id) {
        return ResponseEntity.ok(cardService.getCardService(id));
    }

    @Override
    public ResponseEntity<CardIdPage> listCardIds(Integer page, Integer size) {
        return ResponseEntity.ok(cardService.listCardIds(page, size));
    }
}
