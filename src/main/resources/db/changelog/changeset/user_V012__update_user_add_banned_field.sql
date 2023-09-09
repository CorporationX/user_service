ALTER TABLE users
ADD COLUMN if not exists banned BOOLEAN DEFAULT false;
