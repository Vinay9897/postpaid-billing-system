-- Drop the previously-added enabled column from users
-- This migration removes the `enabled` column introduced earlier.
ALTER TABLE users DROP COLUMN IF EXISTS enabled;
