-- H5 / 后台均使用 OAuth2 client_id = 'default' 颁发 token
-- access_token 7 天 (604800 秒)，refresh_token 30 天 (2592000 秒)
UPDATE system_oauth2_client
SET access_token_validity_seconds = 604800,
    refresh_token_validity_seconds = 2592000
WHERE client_id = 'default';
