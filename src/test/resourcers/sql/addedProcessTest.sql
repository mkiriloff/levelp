CREATE TABLE public.accounts
(
    id SERIAL PRIMARY KEY,
    balance INTEGER DEFAULT 0,
    firstname VARCHAR(30),
    lastname VARCHAR(30)
);
CREATE UNIQUE INDEX accounts_id_uindex ON public.accounts (id);

INSERT INTO accounts (firstname, lastname) VALUES ('FirstAccount', 'FirstAccount');