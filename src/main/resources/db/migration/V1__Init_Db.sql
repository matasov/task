CREATE SEQUENCE IF NOT EXISTS tasks_id_seq INCREMENT 1 MINVALUE 1 MAXVALUE 2147483647 START 1 CACHE 1;

CREATE TABLE IF NOT EXISTS "public"."tasks" (
    "id" integer DEFAULT nextval('tasks_id_seq') NOT NULL,
    "input" character varying(1000) NOT NULL,
    "template" character varying(1000) NOT NULL,
    "progress" integer DEFAULT '0' NOT NULL,
    "status" character varying(10) DEFAULT 'IDLE' NOT NULL,
    "best_match_index" integer,
    "best_match_typos" integer,    
    CONSTRAINT "tasks_pkey" PRIMARY KEY ("id")
) WITH (oids = false);