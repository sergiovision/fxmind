--  Fill Predefined Jobs
INSERT INTO `JOBS` VALUES
(7, 'com.fxmind.manager.jobs.ForexFactoryNewsJob', 'SYSTEM', 'ForexFactoryNewsJob', '0 0 9 ? * MON-FRI *', 'Parse ForexFactory.com news every day', '', '2015-08-06 00:00:00', '2015-08-25 22:21:44', '', false);


INSERT INTO `SETTINGS` (NAME, VALUE, RELOADABLE) VALUES
  ('DB.version','1', 1);
