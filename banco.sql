CREATE DATABASE IF NOT EXISTS vcRiquinhoDB;

USE vcRiquinhoDB;

CREATE TABLE clients (
	id VARCHAR(10) PRIMARY KEY,
    nome VARCHAR(50),
    email VARCHAR(60),
    senha VARCHAR(30)
);

CREATE TABLE corporate_client (
	client_id VARCHAR(10),
	cnpj VARCHAR(14),
    CONSTRAINT fk_corporate_client
		FOREIGN KEY (client_id)
        REFERENCES clients(id)
        ON DELETE CASCADE
);

CREATE TABLE individual_client (
	client_id VARCHAR(10),
	cpf VARCHAR(11),
    CONSTRAINT fk_individual_client
		FOREIGN KEY (client_id)
        REFERENCES clients(id)
        ON DELETE CASCADE
);

CREATE TABLE accounts (
	id VARCHAR(10) PRIMARY KEY,
    balance DECIMAL(15,2) NOT NULL,
    client_id VARCHAR(10),
    CONSTRAINT fk_client_account
		FOREIGN KEY (client_id)
        REFERENCES clients(id)
        ON DELETE CASCADE
);

CREATE TABLE investment_product (
	id VARCHAR(10) PRIMARY KEY,
    investment_name VARCHAR(20),
    investment_description VARCHAR (100),
    monthlyYield DECIMAL(15,2)
);

CREATE TABLE fixed_income_product (
	investment_id VARCHAR(10),
    gracePeriodDays INT,
	CONSTRAINT fk_investment_fixed
		FOREIGN KEY (investment_id)
        REFERENCES investment_product(id)
        ON DELETE CASCADE
);

CREATE TABLE variable_income_product (
	investment_id VARCHAR(10),
    	CONSTRAINT fk_investment_variable
		FOREIGN KEY (investment_id)
        REFERENCES investment_product(id)
        ON DELETE CASCADE
);


CREATE TABLE auto_investment_account (
	account_id VARCHAR(10),
    investment_product_id VARCHAR(10),
    PRIMARY KEY (account_id, investment_product_id),
    
    CONSTRAINT fk_auto_account_account
		FOREIGN KEY (account_id)
        REFERENCES accounts(id)
        ON DELETE CASCADE,
	
    CONSTRAINT fk_auto_account_investment
		FOREIGN KEY (investment_product_id)
        REFERENCES investment_product(id)
);

CREATE TABLE cdi_account (
	account_id VARCHAR(10),
	CONSTRAINT fk_cdi_account_account
		FOREIGN KEY (account_id)
        REFERENCES accounts(id)
        ON DELETE CASCADE
);

/*
-------------------------------------------------------------------

	INSERCAO DE DADOS PARA TESTE

---------------------------------------------------------------------
*/
-- 1. Inserir Clientes Base
INSERT INTO clients (id, nome, email, senha) VALUES 
('C001', 'Ana Silva', 'ana.silva@email.com', 'senha123'),
('C002', 'Bruno Souza', 'bruno.souza@email.com', 'senha456'),
('C003', 'Empresa Tech Ltda', 'contato@techltda.com', 'empresa789');

-- 2. Inserir Clientes Pessoa Física e Jurídica
INSERT INTO individual_client (client_id, cpf) VALUES 
('C001', '12345678901'),
('C002', '98765432109');

INSERT INTO corporate_client (client_id, cnpj) VALUES 
('C003', '12345678000199');

-- 3. Inserir Contas Bancárias (Tipos diferentes: Corrente, CDI e Auto-Investimento)
-- ACC01 será apenas Conta Corrente (padrão)
-- ACC02 será Conta CDI
-- ACC03 será Conta de Investimento Automático
INSERT INTO accounts (id, balance, client_id) VALUES 
('ACC01', 2500.00, 'C001'),
('ACC02', 15000.50, 'C002'),
('ACC03', 50000.00, 'C003');

-- 4. Registrar a Conta CDI (ACC02)
INSERT INTO cdi_account (account_id) VALUES 
('ACC02');

-- 5. Inserir Produtos de Investimento (Geral, Renda Fixa e Variável)
INSERT INTO investment_product (id, investment_name, investment_description, monthlyYield) VALUES 
('INV001', 'CDB Fácil', 'CDB com liquidez diária', 0.85),
('INV002', 'Ações PETR4', 'Ações ordinárias Petrobras', 0.00);

INSERT INTO fixed_income_product (investment_id, gracePeriodDays) VALUES 
('INV001', 0);

INSERT INTO variable_income_product (investment_id) VALUES 
('INV002');

-- 6. VINCULAR APENAS CONTAS DE INVESTIMENTO AUTOMÁTICO AOS PRODUTOS
-- Nota: Para respeitar sua regra, apenas a ACC03 está recebendo o vínculo de auto-investimento.
INSERT INTO auto_investment_account (account_id, investment_product_id) VALUES 
('ACC03', 'INV001'),
('ACC03', 'INV002');

/*
-----------------------------------------------------------------

	PROCEDURES 

----------------------------------------------------------------------

*/
DELIMITER //
CREATE PROCEDURE CADASTRO_CLIENTE (
    IN p_name VARCHAR(50),
    IN p_email VARCHAR(60),
    IN p_senha VARCHAR(30),
    IN p_tipo VARCHAR(2),
    IN p_documento VARCHAR(14)
)
BEGIN
    DECLARE v_novo_id VARCHAR(10);
    DECLARE v_ultimo_numero INT;
    
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
    END;
    
    START TRANSACTION;
    
    -- 1. Descobre o número do último ID cadastrado (ex: extrai o número de 'C005' -> 5)
    -- Se a tabela estiver vazia, define o número inicial como 0
    SELECT COALESCE(MAX(CAST(SUBSTRING(id, 2) AS UNSIGNED)), 0) INTO v_ultimo_numero 
    FROM clients;
    
    -- 2. Gera o novo ID formatado (ex: 'C001', 'C002', etc.)
    SET v_novo_id = CONCAT('C', LPAD(v_ultimo_numero + 1, 3, '0'));
    
    -- 3. Insere na tabela pai usando o ID gerado automaticamente
    INSERT INTO clients (id, nome, email, senha)
    VALUES (v_novo_id, p_name, p_email, p_senha);
    
    -- 4. Insere na tabela filha correspondente
    IF p_tipo = 'PF' THEN 
        INSERT INTO individual_client(client_id, cpf)
        VALUES (v_novo_id, p_documento);
        
    ELSEIF p_tipo = 'PJ' THEN
        INSERT INTO corporate_client(client_id, cnpj)
        VALUES (v_novo_id, p_documento);
    END IF;
    
    COMMIT;
END //
DELIMITER ;


-- procedure de busca de cliente
DELIMITER //
CREATE PROCEDURE BUSCA_CLIENTE (
	IN p_nome VARCHAR(50),
    IN p_senha VARCHAR(30)
)
BEGIN
	SELECT 
		c.id,
        c.nome,
        c.email,
        c.senha,
        ic.cpf,
        cp.cnpj,
        
        -- nessa parte uma nova coluna é criada para mapear qual é o tipo do cliente
        -- a fim de facilitar no instanciamento da classe
        CASE 
			WHEN ic.cpf IS NOT NULL THEN 'PF'
            WHEN cp.cnpj IS NOT NULL THEN 'PJ'
            ELSE 'DESCONHECIDO'
		END AS tipo_cliente
        FROM clients c
        LEFT JOIN individual_client ic ON c.id = ic.client_id
        LEFT JOIN corporate_client cp ON c.id = cp.client_id
        WHERE c.nome = p_nome AND c.senha = p_senha;
END //
DELIMITER ;


-- procedure para buscar contas do cliente
DELIMITER //
CREATE PROCEDURE BUSCAR_CONTAS_DO_CLIENTE (
	IN p_client_id VARCHAR(10)
)
BEGIN
	SELECT
		a.id AS conta_id,
        a.balance,
        CASE
			WHEN MAX(aia.account_id) IS NOT NULL THEN 'AUTO_INVESTIMENTO'
            WHEN MAX(cdi.account_id) IS NOT NULL THEN 'CDI'
            ELSE 'CORRENTE'
		END AS tipo_conta
	FROM accounts a 
    LEFT JOIN cdi_account cdi ON a.id = cdi.account_id
    LEFT JOIN auto_investment_account aia ON a.id = aia.account_id
	WHERE a.client_id = p_client_id
    GROUP BY a.id, a.balance;
END //
DELIMITER ;



DELIMITER //
CREATE PROCEDURE BUSCAR_PRODUTOS_INVESTIMENTO (
    IN p_conta_id VARCHAR(10)
)
BEGIN
    SELECT 
        ip.id,
        ip.investment_name,
        ip.investment_description,
        ip.monthlyYield,
        f.gracePeriodDays,
        CASE
            WHEN v.investment_id IS NOT NULL THEN 'VARIABLE'
            WHEN f.investment_id IS NOT NULL THEN 'FIXED'
            ELSE 'DESCONHECIDO'
        END AS tipo_investimento
    FROM auto_investment_account aia
    -- 1. Primeiro faz o JOIN com a tabela principal de produtos (ip)
    JOIN investment_product ip ON aia.investment_product_id = ip.id
    -- 2. Depois faz os LEFT JOINs usando o alias correto (ip.id)
    LEFT JOIN fixed_income_product f ON ip.id = f.investment_id
    LEFT JOIN variable_income_product v ON ip.id = v.investment_id
    WHERE aia.account_id = p_conta_id;
END //
DELIMITER ;

DELIMITER //
CREATE PROCEDURE ATUALIZAR_CLIENTE (
    IN p_id VARCHAR(10),
    IN p_nome VARCHAR(50),
    IN p_email VARCHAR(60),
    IN p_senha VARCHAR(30)
)
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
    END;
    
    START TRANSACTION;
    
    UPDATE clients 
    SET nome = p_nome, 
        email = p_email, 
        senha = p_senha 
    WHERE id = p_id;
    
    COMMIT;
END //
DELIMITER ;

DELIMITER //
CREATE PROCEDURE ATUALIZAR_CONTA (
    IN p_conta_id VARCHAR(10),
    IN p_balance DECIMAL(15,2)
)
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
    END;
    
    START TRANSACTION;
    
    UPDATE accounts 
    SET balance = p_balance 
    WHERE id = p_conta_id;
    
    COMMIT;
END //
DELIMITER ;

DELIMITER //
CREATE PROCEDURE CADASTRAR_CONTA (
    IN p_client_id VARCHAR(10),
    IN p_tipo_conta VARCHAR(20) -- 'CORRENTE', 'CDI', 'AUTO_INVESTIMENTO'
)
BEGIN
    DECLARE v_novo_id VARCHAR(10);
    DECLARE v_ultimo_numero INT;
    
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
    END;
    
    START TRANSACTION;
    
    -- 1. Descobre o número do último ID de conta cadastrado (extrai o número após 'ACC')
    SELECT COALESCE(MAX(CAST(SUBSTRING(id, 4) AS UNSIGNED)), 0) INTO v_ultimo_numero 
    FROM accounts;
    
    -- 2. Gera o novo ID formatado (ex: 'ACC01', 'ACC02', etc.)
    SET v_novo_id = CONCAT('ACC', LPAD(v_ultimo_numero + 1, 2, '0'));
    
    -- 3. Insere na tabela pai 'accounts' fixando o saldo inicial em 0.00
    INSERT INTO accounts (id, balance, client_id)
    VALUES (v_novo_id, 0.00, p_client_id);
    
    -- 4. Se for CDI, insere o relacionamento na tabela auxiliar
    IF p_tipo_conta = 'CDI' THEN
        INSERT INTO cdi_account (account_id)
        VALUES (v_novo_id);
    END IF;
    
    COMMIT;
END //
DELIMITER ;

DELIMITER //
CREATE PROCEDURE CADASTRO_INVESTMENT_PRODUCT (
    IN p_name VARCHAR(20),
    IN p_description VARCHAR(100),
    IN p_monthly_yield DECIMAL(15,2),
    IN p_tipo VARCHAR(10),           -- 'FIXED' ou 'VARIABLE'
    IN p_grace_period_days INT       -- Usado apenas se for FIXED (pode ser NULL para variável)
)
BEGIN
    DECLARE v_novo_id VARCHAR(10);
    DECLARE v_ultimo_numero INT;
    
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
    END;
    
    START TRANSACTION;
    
    SELECT COALESCE(MAX(CAST(SUBSTRING(id, 4) AS UNSIGNED)), 0) INTO v_ultimo_numero 
    FROM investment_product;
    
    SET v_novo_id = CONCAT('INV', LPAD(v_ultimo_numero + 1, 3, '0'));
    
    INSERT INTO investment_product (id, investment_name, investment_description, monthlyYield)
    VALUES (v_novo_id, p_name, p_description, p_monthly_yield);
    
    IF p_tipo = 'FIXED' THEN
        INSERT INTO fixed_income_product (investment_id, gracePeriodDays)
        VALUES (v_novo_id, p_grace_period_days);
        
    ELSEIF p_tipo = 'VARIABLE' THEN
        INSERT INTO variable_income_product (investment_id)
        VALUES (v_novo_id);
    END IF;
    
    COMMIT;
END //
DELIMITER ;

DELIMITER //
CREATE PROCEDURE LISTAR_INVESTMENT_PRODUCTS ()
BEGIN
    SELECT p.id, p.investment_name, p.investment_description, p.monthlyYield, 
           f.gracePeriodDays, 
           CASE WHEN f.investment_id IS NOT NULL THEN 'FIXED' ELSE 'VARIABLE' END AS tipo 
    FROM investment_product p 
    LEFT JOIN fixed_income_product f ON p.id = f.investment_id 
    LEFT JOIN variable_income_product v ON p.id = v.investment_id;
END //
DELIMITER ;

DELIMITER //

-- Procedure de Update
CREATE PROCEDURE ATUALIZAR_INVESTMENT_PRODUCT (
    IN p_id VARCHAR(10),
    IN p_name VARCHAR(20),
    IN p_description VARCHAR(100),
    IN p_monthly_yield DECIMAL(15,2),
    IN p_tipo VARCHAR(10),
    IN p_grace_period_days INT
)
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
    END;
    
    START TRANSACTION;
    
    -- 1. Atualiza a tabela pai
    UPDATE investment_product 
    SET investment_name = p_name, 
        investment_description = p_description, 
        monthlyYield = p_monthly_yield
    WHERE id = p_id;
    
    -- 2. Atualiza a tabela filha correspondente (caso altere o valor ou período)
    IF p_tipo = 'FIXED' THEN
        UPDATE fixed_income_product 
        SET gracePeriodDays = p_grace_period_days 
        WHERE investment_id = p_id;
    END IF;
    
    COMMIT;
END //

-- Procedure de Delete (Graças ao ON DELETE CASCADE, deletar da pai limpa a filha)
CREATE PROCEDURE DELETAR_INVESTMENT_PRODUCT (
    IN p_id VARCHAR(10)
)
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
    END;
    
    START TRANSACTION;
    DELETE FROM investment_product WHERE id = p_id;
    COMMIT;
END //

DELIMITER ;


	