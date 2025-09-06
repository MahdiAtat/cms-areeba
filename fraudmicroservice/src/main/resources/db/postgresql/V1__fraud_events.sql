create table if not exists fraud_events (
    id uuid primary key default gen_random_uuid(),
    card_id uuid not null,
    amount numeric(19,2) not null,
    event_time timestamptz not null
    );
create index if not exists idx_card_time on fraud_events(card_id, event_time);
