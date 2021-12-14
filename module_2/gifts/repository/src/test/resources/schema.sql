CREATE TABLE gift_certificates (
  id BIGINT NOT NULL AUTO_INCREMENT,
  name varchar(50) NOT NULL UNIQUE,
  description varchar(1000) NOT NULL,
  price decimal(7,2) NOT NULL,
  duration INTEGER NOT NULL,
  create_date TIMESTAMP NOT NULL,
  last_update_date TIMESTAMP NOT NULL,
  is_deleted INTEGER NOT NULL DEFAULT 0,
   
   CONSTRAINT pk_certificates PRIMARY KEY (id)
);

CREATE TABLE tags (
  id BIGINT NOT NULL AUTO_INCREMENT,
  name varchar(25) NOT NULL UNIQUE,
  is_deleted INTEGER NOT NULL DEFAULT 0,
  
  CONSTRAINT pk_tags PRIMARY KEY (id)
);

CREATE TABLE tags_certificates (
  certificate_id bigint NOT NULL,
  tag_id bigint NOT NULL,
  
  CONSTRAINT pk_tags_certificates PRIMARY KEY (certificate_id, tag_id),
  
  CONSTRAINT fk_tags_certificates_tag_id FOREIGN KEY (tag_id)
        REFERENCES tags (id) ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT fk_tags_certificates_certificate_id FOREIGN KEY (certificate_id)
        REFERENCES gift_certificates (id) ON DELETE NO ACTION ON UPDATE NO ACTION
)
