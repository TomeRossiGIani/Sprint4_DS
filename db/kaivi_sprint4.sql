-- Kaivi.AI | Sprint 4 | DDL + DML + DQL
-- PostgreSQL / Oracle: trocar UUID por RAW(16) e GENERATED ALWAYS por IDENTITY quando necessário.

CREATE TABLE meeting_session (
    id UUID PRIMARY KEY,
    customer_name VARCHAR(120) NOT NULL,
    started_at TIMESTAMP NOT NULL,
    transcript_text TEXT NOT NULL,
    status VARCHAR(20) NOT NULL CHECK (status IN ('PROCESSED', 'FAILED'))
);

CREATE TABLE participant (
    id UUID PRIMARY KEY,
    session_id UUID NOT NULL REFERENCES meeting_session(id),
    display_name VARCHAR(120) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('SELLER', 'CLIENT'))
);

CREATE TABLE transcript_chunk (
    id UUID PRIMARY KEY,
    session_id UUID NOT NULL REFERENCES meeting_session(id),
    speaker VARCHAR(120),
    started_second INTEGER NOT NULL CHECK (started_second >= 0),
    content TEXT NOT NULL
);

CREATE TABLE insight (
    id UUID PRIMARY KEY,
    session_id UUID NOT NULL REFERENCES meeting_session(id),
    insight_type VARCHAR(30) NOT NULL CHECK (insight_type IN ('SENTIMENT', 'CHURN_RISK', 'UPSELL', 'BUDGET', 'COMPETITOR', 'LISTENING_SCORE')),
    value VARCHAR(200) NOT NULL,
    confidence NUMERIC(4,3) NOT NULL CHECK (confidence BETWEEN 0 AND 1),
    evidence_chunk_id UUID REFERENCES transcript_chunk(id)
);

CREATE TABLE alert (
    id UUID PRIMARY KEY,
    session_id UUID NOT NULL REFERENCES meeting_session(id),
    severity VARCHAR(10) NOT NULL CHECK (severity IN ('LOW', 'MEDIUM', 'HIGH')),
    message VARCHAR(300) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    resolved_at TIMESTAMP
);

CREATE INDEX idx_meeting_customer ON meeting_session(customer_name);
CREATE INDEX idx_insight_session_type ON insight(session_id, insight_type);
CREATE INDEX idx_alert_open ON alert(session_id) WHERE resolved_at IS NULL;

-- Dados sintéticos para a demonstração. Não representam clientes reais.
INSERT INTO meeting_session VALUES ('0c0b8b8f-23ab-4b65-a7f8-47f3c2d77393', 'Clínica Horizonte', CURRENT_TIMESTAMP, 'O preço está alto e talvez não vamos renovar. A SAP ofereceu proposta de R$ 50 mil.', 'PROCESSED');
INSERT INTO participant VALUES ('4a49c97e-5632-46da-94d7-55f69dbe2d78', '0c0b8b8f-23ab-4b65-a7f8-47f3c2d77393', 'Ana', 'SELLER');
INSERT INTO participant VALUES ('66e5d03a-5f80-4208-9e75-3ca0d1de468f', '0c0b8b8f-23ab-4b65-a7f8-47f3c2d77393', 'Carlos', 'CLIENT');
INSERT INTO transcript_chunk VALUES ('8ae6f2d3-5d76-44a9-b420-48b2d7ca4c43', '0c0b8b8f-23ab-4b65-a7f8-47f3c2d77393', 'Carlos', 92, 'O preço está alto e talvez não vamos renovar.');
INSERT INTO insight VALUES ('7889c054-1a52-45b8-b1cd-2e92458575b5', '0c0b8b8f-23ab-4b65-a7f8-47f3c2d77393', 'CHURN_RISK', 'HIGH', 0.880, '8ae6f2d3-5d76-44a9-b420-48b2d7ca4c43');
INSERT INTO alert VALUES ('17eea7b9-6c7a-4fb4-8dc4-28f1b716499f', '0c0b8b8f-23ab-4b65-a7f8-47f3c2d77393', 'HIGH', 'Risco de churn detectado: tratar objeção de preço em até 24 horas.', CURRENT_TIMESTAMP, NULL);

-- DQL 1: histórico de sessões com risco alto e evidência rastreável.
SELECT s.customer_name, s.started_at, i.value AS churn_risk, c.content AS evidence
FROM meeting_session s
JOIN insight i ON i.session_id = s.id AND i.insight_type = 'CHURN_RISK'
LEFT JOIN transcript_chunk c ON c.id = i.evidence_chunk_id
WHERE i.value = 'HIGH'
ORDER BY s.started_at DESC;

-- DQL 2: concorrentes mais citados no período.
SELECT value AS competitor, COUNT(*) AS mentions
FROM insight
WHERE insight_type = 'COMPETITOR'
GROUP BY value
ORDER BY mentions DESC;

-- DQL 3: alertas abertos para o dashboard de carteira.
SELECT a.severity, a.message, s.customer_name, s.started_at
FROM alert a JOIN meeting_session s ON s.id = a.session_id
WHERE a.resolved_at IS NULL
ORDER BY CASE a.severity WHEN 'HIGH' THEN 1 WHEN 'MEDIUM' THEN 2 ELSE 3 END, s.started_at DESC;
