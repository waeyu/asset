CREATE TABLE ipt.third_event_notify
(
    event_key character varying(50)  NOT NULL,
    target_id character varying(50)  NOT NULL,
    target_name character varying(200)  ,
    event_time timestamp with time zone NOT NULL,
    event_message character varying(4000)  NOT NULL,
    register_datetime timestamp with time zone NOT NULL,
    CONSTRAINT third_event_notify_pk PRIMARY KEY (event_key,register_datetime)
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE ipt.third_event_notify OWNER to insator;
GRANT ALL ON TABLE ipt.third_event_notify TO insator;