package com.areeba.cms.cmsmircoservice.type;

import com.areeba.cms.cmsmicroservice.type.CardStatus;
import com.areeba.cms.cmsmircoservice.utils.AttributeEncryptor;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "cards")
@Getter
@Setter
public class Card {

    @Id
    @GeneratedValue
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Account account;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CardStatus status = CardStatus.INACTIVE;

    @Column(nullable = false)
    private LocalDate expiry;

    @Convert(converter = AttributeEncryptor.class)
    @Column(nullable = false, unique = true, length = 512)
    private String cardNumber;
}
