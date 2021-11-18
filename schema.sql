
-- Table: public.user_data

-- DROP TABLE public.user_data;

CREATE TABLE IF NOT EXISTS public.user_data
(
    id character varying(255) COLLATE pg_catalog."default" NOT NULL,
    account_created character varying(255) COLLATE pg_catalog."default",
    account_updated character varying(255) COLLATE pg_catalog."default",
    first_name character varying(255) COLLATE pg_catalog."default",
    last_name character varying(255) COLLATE pg_catalog."default",
    password character varying(255) COLLATE pg_catalog."default",
    username character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT user_data_pkey PRIMARY KEY (id)
    )

    TABLESPACE pg_default;

ALTER TABLE public.user_data
    OWNER to postgres;


-- Table: public.image_data

-- DROP TABLE public.image_data;

CREATE TABLE IF NOT EXISTS public.image_data
(
    id character varying(255) COLLATE pg_catalog."default" NOT NULL,
    file_name character varying(255) COLLATE pg_catalog."default",
    url character varying(255) COLLATE pg_catalog."default",
    upload_date character varying(255) COLLATE pg_catalog."default",
    userid character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT image_data_pkey PRIMARY KEY (id)
    )

    TABLESPACE pg_default;

ALTER TABLE public.image_data
    OWNER to postgres;
