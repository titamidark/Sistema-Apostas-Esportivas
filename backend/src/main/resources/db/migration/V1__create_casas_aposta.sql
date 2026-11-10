CREATE TABLE casas_aposta (
                              id BIGINT AUTO_INCREMENT PRIMARY KEY,
                              nome VARCHAR(100) NOT NULL UNIQUE,
                              saldo_atual DECIMAL(12,2) NOT NULL DEFAULT 0.00,
                              ativa BOOLEAN NOT NULL DEFAULT TRUE,
                              created_at DATETIME NOT NULL,
                              updated_at DATETIME NOT NULL
);