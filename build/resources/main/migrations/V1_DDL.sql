

CREATE TABLE tmp_cpf
(
  cpf bigint NOT NULL,
  CONSTRAINT pk PRIMARY KEY (cpf)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE tmp_cpf
  OWNER TO postgres


-- Table: public."PESSOA"

-- DROP TABLE public."PESSOA";

CREATE TABLE public."PESSOA"
(
    "CD_CPF" numeric(11),
    "CD_PESSOA" jsonb NOT NULL,
    "CD_JORNAL" integer NOT NULL,
    "NM_PESSOA" text COLLATE pg_catalog."default" NOT NULL,
    "SEXO" character(1) COLLATE pg_catalog."default",
    "DT_NASC" date,
    "ID_ESTADO_CIVIL" character varying(10) COLLATE pg_catalog."default",
    "ID_EMAIL" character varying(100) COLLATE pg_catalog."default",
    "CEP" character(8) COLLATE pg_catalog."default",
    "UF" character(2) COLLATE pg_catalog."default" NOT NULL,
    "CIDADE" character varying(50) COLLATE pg_catalog."default" NOT NULL,
    "BAIRRO" character varying(50) COLLATE pg_catalog."default" NOT NULL,
    "TIPO_LOGRADOURO" character varying(50) COLLATE pg_catalog."default" NOT NULL,
    "LOGRADOURO" character varying(50) COLLATE pg_catalog."default" NOT NULL,
    "NUMERO" character varying(10) COLLATE pg_catalog."default",
    "COMPLEMENTO" character varying(10) COLLATE pg_catalog."default",
    "DT_INCLUSAO" date,
    "DT_ALTERACAO" date,
    CONSTRAINT pk_pessoa PRIMARY KEY ("CD_CPF")
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public."PESSOA"
    OWNER to postgres;