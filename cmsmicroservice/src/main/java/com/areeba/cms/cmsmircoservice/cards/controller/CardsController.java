package com.areeba.cms.cmsmircoservice.cards.controller;

import com.areeba.cms.cmsmicroservice.controller.CardsApi;
import com.areeba.cms.cmsmicroservice.type.CardCreateRequest;
import com.areeba.cms.cmsmicroservice.type.CardIdPage;
import com.areeba.cms.cmsmicroservice.type.CardResponse;
import com.areeba.cms.cmsmircoservice.cards.service.CardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class CardsController implements CardsApi {

    private static final Logger log = LoggerFactory.getLogger(CardsController.class);

    private final CardService cardService;

    public CardsController(CardService cardService) {
        this.cardService = cardService;
    }

    @Override
    public ResponseEntity<Void> activateCardById(UUID id) {
        log.debug("Activate card {}", id);
        cardService.activateCardService(id);
        log.info("Card activated {}", id);
        return ResponseEntity.noContent().build();

    }

    @Override
    public ResponseEntity<CardResponse> createCard(CardCreateRequest card) {
        log.debug("Creating card {}", card);
        CardResponse cardResponse = cardService.createCardService(card);
        log.info("Card created id={}", cardResponse.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(cardResponse);
    }

    @Override
    public ResponseEntity<Void> deactivateCardById(UUID id) {
        log.debug("Deactivate card {}", id);
        cardService.deactivateCardService(id);
        log.info("Card deactivated {}", id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<CardResponse> getCardById(UUID id) {
        log.debug("Fetching card {}", id);
        CardResponse cardResponse = cardService.getCardService(id);
        log.info("Card fetched {}", id);
        return ResponseEntity.ok(cardResponse);
    }

    @Override
    public ResponseEntity<CardIdPage> listCardIds(Integer page, Integer size) {
        log.debug("Listing all cards");
        CardIdPage cardIdPage = cardService.listCardIds(page, size);
        log.info("Cards listed");
        return ResponseEntity.ok(cardIdPage);
    }
}
