create extension if not exists "pgcrypto";

create table if not exists accounts (
    id uuid primary key default gen_random_uuid(),
    status varchar(16) not null,
    balance numeric(19,2) not null,
    created_at timestamptz not null,
    updated_at timestamptz not null
);