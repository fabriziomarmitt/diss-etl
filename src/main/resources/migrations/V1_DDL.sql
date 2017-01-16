
CREATE TABLE tmp_cpf
(
  cpf bigint NOT NULL,
  CONSTRAINT pk PRIMARY KEY (cpf)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE tmp_cpf
  OWNER TO postgres;

CREATE INDEX idx_only_tmp_cpf
    ON tmp_cpf ( cpf );

CREATE TABLE "PESSOA"
(
  "CD_CPF" numeric(11,0) NOT NULL,
  "CD_PESSOA" jsonb NOT NULL,
  "CD_JORNAL" integer NOT NULL,
  "NM_PESSOA" text NOT NULL,
  "SEXO" character(1),
  "DT_NASC" date,
  "ID_ESTADO_CIVIL" character varying(10),
  "ID_EMAIL" character varying(100),
  "CEP" character(8),
  "UF" character(2),
  "CIDADE" character varying(50),
  "BAIRRO" character varying(50),
  "TIPO_LOGRADOURO" character varying(50),
  "LOGRADOURO" character varying(50),
  "NUMERO" character varying(10),
  "COMPLEMENTO" character varying(10),
  "DT_INCLUSAO" date,
  "DT_ALTERACAO" date,
  "QTD_ASS" bigint NOT NULL DEFAULT 0,
  "QTD_ASS_ATIVAS" bigint NOT NULL DEFAULT 0,
  "TOTAL_PAGO" money NOT NULL DEFAULT 0,
  "FREQUENCIA" bigint NOT NULL DEFAULT 0,
  "TOTAL_PAGO_3" money NOT NULL DEFAULT 0,
  "FREQUENCIA_3" bigint NOT NULL DEFAULT 0,
  "TOTAL_PAGO_12" money NOT NULL DEFAULT 0,
  "FREQUENCIA_12" bigint NOT NULL DEFAULT 0,
  "RECENCIA" bigint NOT NULL DEFAULT 0,
  "LONGEVIDADE" bigint NOT NULL DEFAULT 0,
  CONSTRAINT pk_pessoa PRIMARY KEY ("CD_CPF")
)
WITH (
  OIDS=FALSE
);
ALTER TABLE "PESSOA"
  OWNER TO postgres;

CREATE INDEX idx_only_pessoa
    ON "PESSOA" ( "CD_CPF" );

CREATE INDEX idx_only_pessoa_cep
    ON "PESSOA" ( "CEP" );