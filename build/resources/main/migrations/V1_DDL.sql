

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
