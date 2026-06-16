-- Multi-tenant SaaS Database Schema
CREATE TABLE organizations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT
);

CREATE TABLE tenants (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    domain VARCHAR(255),
    organization_id BIGINT,
    FOREIGN KEY (organization_id) REFERENCES organizations(id)
);

CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    tenant_id BIGINT,
    FOREIGN KEY (tenant_id) REFERENCES tenants(id)
);

-- Insert sample data
INSERT INTO organizations (name, description) VALUES ('Simple Agent Corp', 'Simple Agent is Sample organization for demo');
INSERT INTO tenants (name, domain, organization_id) VALUES ('Simple Agent', 'simpleagent.example.com', 1);
INSERT INTO users (username, email, password, tenant_id) VALUES ('SuperAdmin', 'superadmin@simpleagent.example.com', 'password', 1);

-- Query to verify data insertion
select * from organizations;
select * from tenants;
select * from users;