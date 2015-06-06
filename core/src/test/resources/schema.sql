---
-- #region
-- specs-service-core
-- %%
-- Copyright (C) 2013 - 2015 Etilize
-- %%
-- NOTICE: All information contained herein is, and remains the property of ETILIZE.
-- The intellectual and technical concepts contained herein are proprietary to
-- ETILIZE and may be covered by U.S. and Foreign Patents, patents in process, and
-- are protected by trade secret or copyright law. Dissemination of this information
-- or reproduction of this material is strictly forbidden unless prior written
-- permission is obtained from ETILIZE. Access to the source code contained herein
-- is hereby forbidden to anyone except current ETILIZE employees, managers or
-- contractors who have executed Confidentiality and Non-disclosure agreements
-- explicitly covering such access.
-- 
-- The copyright notice above does not evidence any actual or intended publication
-- or disclosure of this source code, which includes information that is confidential
-- and/or proprietary, and is a trade secret, of ETILIZE. ANY REPRODUCTION, MODIFICATION,
-- DISTRIBUTION, PUBLIC PERFORMANCE, OR PUBLIC DISPLAY OF OR THROUGH USE OF THIS
-- SOURCE CODE WITHOUT THE EXPRESS WRITTEN CONSENT OF ETILIZE IS STRICTLY PROHIBITED,
-- AND IN VIOLATION OF APPLICABLE LAWS AND INTERNATIONAL TREATIES. THE RECEIPT
-- OR POSSESSION OF THIS SOURCE CODE AND/OR RELATED INFORMATION DOES NOT CONVEY OR
-- IMPLY ANY RIGHTS TO REPRODUCE, DISCLOSE OR DISTRIBUTE ITS CONTENTS, OR TO
-- MANUFACTURE, USE, OR SELL ANYTHING THAT IT MAY DESCRIBE, IN WHOLE OR IN PART.
-- #endregion
---
-- This script will execute within tests only
-- Named table and column names in upper case because DBUNIT matches tables in upper case

CREATE TABLE IF NOT EXISTS `PRODUCT` (
  `PRODUCTID` INT NOT NULL DEFAULT '0',
  `MANUFACTURERID` INT NOT NULL DEFAULT '0',
  `ISACTIVE` TINYINT NOT NULL DEFAULT '0',
  `MFGPARTNO` VARCHAR(70) NOT NULL DEFAULT '',
  `CATEGORYID` INT NOT NULL DEFAULT '0',
  PRIMARY KEY (`PRODUCTID`)
);

CREATE TABLE IF NOT EXISTS `PRODUCTLANGUAGE` (
  `PRODUCTLANGUAGEID` INT NOT NULL AUTO_INCREMENT,
  `PRODUCTID` INT NOT NULL DEFAULT '0',
  `LANGUAGEID` SMALLINT NOT NULL DEFAULT '0',
  `ISACTIVE` TINYINT NOT NULL DEFAULT '0',
  `PRODUCTSTATUSID` TINYINT NOT NULL DEFAULT '0',
  PRIMARY KEY (`PRODUCTLANGUAGEID`)
);

CREATE TABLE IF NOT EXISTS `PRODUCTPARAMETER` (
  `PRODUCTPARAMETERID` BIGINT NOT NULL,
  `PRODUCTID` INT NOT NULL DEFAULT '0',
  `PARAMETERID` INT NOT NULL DEFAULT '0',
  `SETNUMBER` SMALLINT DEFAULT NULL,
  `EXCEPTIONCODEID` INT DEFAULT NULL,
  `ISACTIVE` TINYINT NOT NULL DEFAULT '0',
  `LANGUAGEID` SMALLINT NOT NULL DEFAULT '0',
  `UNITVALUE` VARCHAR(25) DEFAULT NULL,
  `VALUEID` INT DEFAULT NULL,
  PRIMARY KEY (`PRODUCTPARAMETERID`)
);

CREATE TABLE IF NOT EXISTS `PARAGRAPHPRODUCTPARAMETER` (
  `ID` BIGINT NOT NULL DEFAULT '0',
  `PRODUCTID` INT NOT NULL DEFAULT '0',
  `PARAMETERID` INT NOT NULL DEFAULT '0',
  `VALUE` VARCHAR(4000) DEFAULT NULL,
  `SETNUMBER` SMALLINT DEFAULT '0',
  `EXCEPTIONCODEID` INT DEFAULT NULL,
  `ISACTIVE` TINYINT NOT NULL DEFAULT '0',
  `LANGUAGEID` SMALLINT NOT NULL DEFAULT '0',
  `CATEGORYID` INT DEFAULT NULL,
  PRIMARY KEY (`ID`)
);

CREATE TABLE IF NOT EXISTS `PARAMETERVALUE` (
  `VALUEID` INT NOT NULL,
  `VALUE` VARCHAR(255) DEFAULT NULL,
  `ISACTIVE` INT DEFAULT NULL,
  PRIMARY KEY (`VALUEID`)
);