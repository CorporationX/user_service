ALTER TABLE public.users
    ADD COLUMN telegram_token VARCHAR(36) UNIQUE;