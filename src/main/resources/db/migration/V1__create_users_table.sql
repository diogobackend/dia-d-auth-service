CREATE TABLE users (
    user_id UUID PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    email VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    status VARCHAR(30) NOT NULL,

    phone VARCHAR(30),
    address VARCHAR(500),
    cpf VARCHAR(14),
    rg VARCHAR(20),
    login VARCHAR(35) NOT NULL,
    birth_date DATE,

    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,

    CONSTRAINT uk_users_email UNIQUE (email),
    CONSTRAINT uk_users_cpf UNIQUE (cpf),
    CONSTRAINT uk_users_rg UNIQUE (rg),
    CONSTRAINT uk_users_login UNIQUE (login)
);

CREATE INDEX idx_users_status
    ON users (status);