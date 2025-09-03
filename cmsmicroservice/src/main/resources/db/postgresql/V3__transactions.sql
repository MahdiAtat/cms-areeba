-- create extension if not exists "pgcrypto";

create table if not exists cards (
    id uuid primary key default gen_random_uuid(),
    account_id uuid not null references accounts(id) on delete cascade,
    status varchar(16) not null,
    expiry date not null,
    card_number varchar(512) not null unique
    );
create index if not exists ix_cards_account on cards(account_id);