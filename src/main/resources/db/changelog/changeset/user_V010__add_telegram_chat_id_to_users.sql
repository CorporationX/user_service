ALTER TABLE public.users
    ADD COLUMN telegram_chat_id BIGINT;

ALTER TABLE public.users
    ADD CONSTRAINT users_telegram_chat_id_unique UNIQUE (telegram_chat_id);