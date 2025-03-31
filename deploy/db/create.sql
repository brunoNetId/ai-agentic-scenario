CREATE DATABASE demo;
\c demo;
CREATE USER camel WITH PASSWORD 'camel';
GRANT ALL PRIVILEGES ON DATABASE demo TO camel;

CREATE TABLE promotions (
	id serial NOT NULL,
	type varchar NULL,
	description VARCHAR (255) NOT NULL,
	date DATE NOT NULL DEFAULT CURRENT_DATE,
	quantity NUMERIC(10, 2),
	discount NUMERIC(10, 2),
	active boolean DEFAULT TRUE,
	CONSTRAINT promotions_pk PRIMARY KEY (id)
);

CREATE TABLE customers (
	id serial NOT NULL,
	taxid varchar NOT NULL,
	email varchar NOT NULL,
	name varchar NOT NULL,
	CONSTRAINT customers_pk PRIMARY KEY (taxid)
);


CREATE TABLE invoices (
	id varchar NOT NULL,
	taxid varchar NOT NULL,
	CONSTRAINT invoices_pk PRIMARY KEY (id, taxid)
);

-- ALTER TABLE invoices
-- ADD CONSTRAINT fk_taxid
-- FOREIGN KEY (taxid) REFERENCES customers(taxid);

CREATE TABLE awards (
	id serial NOT NULL,
	customerid varchar NOT NULL,
	promotionid varchar NOT NULL,
	quantity varchar NOT NULL,
	CONSTRAINT awards_pk PRIMARY KEY (id)
);

-- ALTER TABLE awards
-- ADD CONSTRAINT fk_customerid
-- FOREIGN KEY (customerid) REFERENCES customers(taxid);

-- ALTER TABLE awards
-- ADD CONSTRAINT fk_promotionid
-- FOREIGN KEY (promotionid) REFERENCES promotions(id);


INSERT INTO promotions (type, description, quantity, discount) VALUES ('vouchers', 'Wine related products', 1, 40.00);
INSERT INTO promotions (type, description, quantity, discount) VALUES ('loyalty', 'Loyalty points', 10, 0.00);
INSERT INTO promotions (type, description, quantity, discount) VALUES ('two-for-one', 'Buy 1 book, get 1 free', 1, 0.00);

ALTER TABLE promotions OWNER TO camel;
ALTER TABLE customers  OWNER TO camel;
ALTER TABLE awards  OWNER TO camel;
ALTER TABLE invoices  OWNER TO camel;
