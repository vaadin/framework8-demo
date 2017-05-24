CREATE TABLE COMPANY (
  company_id INT PRIMARY KEY,
  company_name VARCHAR(50) NOT NULL
);

CREATE TABLE DEPARTMENT (
  department_id INT PRIMARY KEY,
  company_id INT NOT NULL FOREIGN KEY REFERENCES COMPANY(company_id),
  department_name VARCHAR(50) NOT NULL
);
create table PEOPLE (
  id INT PRIMARY KEY,
  department_id INT NOT NULL FOREIGN KEY REFERENCES DEPARTMENT(department_id),
  first_name VARCHAR(50),
  last_name VARCHAR(50),
  email VARCHAR(50),
  gender VARCHAR(50)
);
