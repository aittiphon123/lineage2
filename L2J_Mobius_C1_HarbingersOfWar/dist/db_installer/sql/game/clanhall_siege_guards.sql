DROP TABLE IF EXISTS `clanhall_siege_guards`;
CREATE TABLE IF NOT EXISTS `clanhall_siege_guards` (
  `clanHallId` tinyint(2) unsigned NOT NULL DEFAULT '0',
  `npcId` smallint(5) unsigned NOT NULL DEFAULT '0',
  `x` mediumint(6) NOT NULL DEFAULT '0',
  `y` mediumint(6) NOT NULL DEFAULT '0',
  `z` mediumint(6) NOT NULL DEFAULT '0',
  `heading` mediumint(6) NOT NULL DEFAULT '0',
  `respawnDelay` mediumint(5) NOT NULL DEFAULT '0',
  `isSiegeBoss` enum('false','true') NOT NULL DEFAULT 'false',
  KEY `clanHallId` (`clanHallId`)
) DEFAULT CHARSET=latin1 COLLATE=latin1_general_ci;

INSERT INTO `clanhall_siege_guards` VALUES
-- Partisan's Hideaway (Fortress of Resistance)
-- Mercury Monster
(21,35369,44545,108867,-2020,0,60,'false'),
(21,35369,44505,108867,-2020,0,60,'false'),
(21,35371,44535,108884,-2020,0,60,'false'),
(21,35371,44515,108884,-2020,0,60,'false'),
(21,35371,44515,108850,-2020,0,60,'false'),
(21,35371,44535,108850,-2020,0,60,'false'),
(21,35370,44565,108867,-2020,0,60,'false'),
(21,35370,44553,108895,-2020,0,60,'false'),
(21,35370,44535,108895,-2020,0,60,'false'),
(21,35370,44497,108895,-2020,0,60,'false'),
(21,35370,44485,108867,-2020,0,60,'false'),
(21,35370,44497,108839,-2020,0,60,'false'),
(21,35370,44525,108827,-2020,0,60,'false'),
(21,35370,44553,108839,-2020,0,60,'false'),
(21,35374,44812,109492,-1705,0,60,'false'),
(21,35373,44788,109492,-1705,0,60,'false'),
(21,35374,45236,108980,-1705,0,60,'false'),
(21,35373,45168,109020,-1705,0,60,'false'),
-- (21,35372,anywhere - total 15,0,60,'false'), TODO: needs support for random spawn by zone.
(21,35382,50343,111282,-1970,0,60,'false'),
(21,35383,43129,108841,-1980,0,60,'false'),
(21,35375,44525,108867,-2020,0,10800,'true');
