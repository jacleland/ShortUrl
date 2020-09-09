-- Create schema for ShortUrl
--
USE ShortUrl;

DROP TABLE IF EXISTS ShortMap;

CREATE TABLE ShortMap (
			id					INT							AUTO_INCREMENT PRIMARY KEY,
			createTime	TIMESTAMP 			DEFAULT CURRENT_TIMESTAMP,
			token				VARCHAR(64) 		NOT NULL,
			url 				VARCHAR(10240) 	NOT NULL
);

ALTER TABLE ShortMap ADD INDEX (id);
ALTER TABLE ShortMap ADD INDEX (createTime);
ALTER TABLE ShortMap ADD INDEX (token);


