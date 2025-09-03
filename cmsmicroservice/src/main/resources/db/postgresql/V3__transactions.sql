create table if not exists transactions (
    id uuid primary key default gen_random_uuid(),
    account_id uuid not null references accounts(id),
    card_id uuid not null references cards(id),
    transaction_amount numeric(19,2) not null,
    transaction_type varchar(1) not null,
    transaction_date timestamptz not null,
    response varchar(16) not null
    );
create index if not exists ix_tx_account_date on transactions(account_id, transaction_date);
create index if not exists ix_tx_card_date on transactions(card_id, transaction_date);