CREATE TABLE public.accounts
(
    id SERIAL PRIMARY KEY,
    balance INTEGER DEFAULT 0,
    firstname VARCHAR(30),
    lastname VARCHAR(30)
);
CREATE UNIQUE INDEX accounts_id_uindex ON public.accounts (id);

INSERT INTO accounts (firstname, lastname, balance) VALUES ('Account1', 'Account1', 300000);
INSERT INTO accounts (firstname, lastname, balance) VALUES ('Account2', 'Account2', 300000);