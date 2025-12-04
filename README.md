# bse-corporate-announcement-automation


Command to run the vault server in local : 
✅ 1. Create a Vault Configuration File
Create a file called vault-config.hcl with the following content:

storage "file" {
path = "C:/vault/db" # Persistent storage path
}

listener "tcp" {
address = "127.0.0.1:8200"
tls_disable = 1
}

ui = true


This ensures Vault uses file storage at C:/vault/db so data persists across restarts.

✅ 2. Start Vault with This Config
Run:

vault server -config="C:\vault-config.hcl"

✅ 3. Initialize and Unseal Vault
After starting, open a new terminal and run:
set VAULT_ADDR=http://127.0.0.1:8200
vault operator init

This will give you Unseal Keys and a Root Token. Save them securely.

Unseal Vault:
vault operator unseal <key1>
vault operator unseal <key2>
vault operator unseal <key3>

Login:
vault login <root-token>

✅ 4. Enable KV Secrets Engine and Create Path
Enable KV engine at path db:

vault secrets enable -path=db kv

Create two keys (username and password) with value root:

vault kv put db/username value=root
vault kv put db/password value=root

Verify:
vault kv get db/username
vault kv get db/password


For rerunning vault server