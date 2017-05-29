CREATE TABLE company (
  company_id BIGINT PRIMARY KEY,
  company_name VARCHAR(50) NOT NULL,
  company_email VARCHAR(80) NOT NULL
);

CREATE TABLE department (
  department_id BIGINT PRIMARY KEY,
  company_id BIGINT NOT NULL FOREIGN KEY REFERENCES COMPANY(company_id),
  department_name VARCHAR(50) NOT NULL
);

CREATE TABLE people (
  id BIGINT PRIMARY KEY,
  department_id BIGINT NOT NULL FOREIGN KEY REFERENCES DEPARTMENT(department_id),
  first_name VARCHAR(50),
  last_name VARCHAR(50),
  email VARCHAR(50),
  gender VARCHAR(50)
);
