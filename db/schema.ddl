--  This file belongs to the ShortUrl project, the latest version of which
--  can be found at https://github.com/jacleland/ShortUrl.
--
--  Copyright (c) 2020, James A. Cleland <jcleland at jamescleland dot com>
--
--  This program is free software: you can redistribute it and/or modify
--  it under the terms of the GNU General Public License as published by
--  the Free Software Foundation, either version 3 of the License, or
--  (at your option) any later version.
--
--  This program is distributed in the hope that it will be useful,
--  but WITHOUT ANY WARRANTY; without even the implied warranty of
--  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
--  GNU General Public License for more details.
--
--  You should have received a copy of the GNU General Public License
--  along with this program.  If not, see <http://www.gnu.org/licenses/>.

-- Select database
USE ShortUrl;

-- Drop existing table first
DROP TABLE IF EXISTS ShortMap;

-- Create table objects
CREATE TABLE ShortMap (
			id					INT							AUTO_INCREMENT PRIMARY KEY,
			createTime	TIMESTAMP 			DEFAULT CURRENT_TIMESTAMP,
			token				VARCHAR(64) 		NOT NULL,
			url 				VARCHAR(10240) 	NOT NULL
);

-- Primary key index
ALTER TABLE ShortMap ADD INDEX (id);

-- Timestamp index for query on duplicate keys/order by most recent
ALTER TABLE ShortMap ADD INDEX (createTime);

-- Index for short URL token
ALTER TABLE ShortMap ADD INDEX (token);


