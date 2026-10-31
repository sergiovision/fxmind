-- Reference data and default runtime configuration.

INSERT INTO currency (NAME, ENABLED) VALUES
    ('EUR', TRUE),
    ('USD', TRUE),
    ('JPY', TRUE),
    ('GBP', TRUE),
    ('CHF', TRUE),
    ('AUD', TRUE),
    ('CAD', TRUE),
    ('NZD', TRUE);

INSERT INTO symbol (NAME, BASE_CURRENCY, QUOTE_CURRENCY, DISABLED, TRACK_SENTIMENT) VALUES
    ('EUR/USD', 'EUR', 'USD', FALSE, TRUE),
    ('GBP/USD', 'GBP', 'USD', FALSE, TRUE),
    ('USD/JPY', 'USD', 'JPY', FALSE, TRUE),
    ('USD/CHF', 'USD', 'CHF', FALSE, TRUE),
    ('USD/CAD', 'USD', 'CAD', FALSE, TRUE),
    ('AUD/USD', 'AUD', 'USD', FALSE, TRUE),
    ('NZD/USD', 'NZD', 'USD', FALSE, TRUE),
    ('EUR/JPY', 'EUR', 'JPY', FALSE, TRUE),
    ('EUR/GBP', 'EUR', 'GBP', FALSE, TRUE),
    ('EUR/CHF', 'EUR', 'CHF', FALSE, TRUE),
    ('EUR/CAD', 'EUR', 'CAD', FALSE, TRUE),
    ('EUR/AUD', 'EUR', 'AUD', FALSE, TRUE),
    ('EUR/NZD', 'EUR', 'NZD', FALSE, TRUE),
    ('GBP/JPY', 'GBP', 'JPY', FALSE, TRUE),
    ('GBP/CHF', 'GBP', 'CHF', FALSE, TRUE),
    ('GBP/CAD', 'GBP', 'CAD', FALSE, TRUE),
    ('GBP/AUD', 'GBP', 'AUD', FALSE, TRUE),
    ('GBP/NZD', 'GBP', 'NZD', FALSE, TRUE),
    ('CHF/JPY', 'CHF', 'JPY', FALSE, TRUE),
    ('CAD/JPY', 'CAD', 'JPY', FALSE, TRUE),
    ('CAD/CHF', 'CAD', 'CHF', FALSE, TRUE),
    ('AUD/JPY', 'AUD', 'JPY', FALSE, TRUE),
    ('AUD/CHF', 'AUD', 'CHF', FALSE, TRUE),
    ('AUD/CAD', 'AUD', 'CAD', FALSE, TRUE),
    ('AUD/NZD', 'AUD', 'NZD', FALSE, TRUE),
    ('NZD/JPY', 'NZD', 'JPY', FALSE, TRUE),
    ('NZD/CHF', 'NZD', 'CHF', FALSE, TRUE),
    ('NZD/CAD', 'NZD', 'CAD', FALSE, TRUE);

INSERT INTO site (NAME, DESCRIPTION) VALUES
    ('ForexFactory', 'https://www.forexfactory.com - economic calendar'),
    ('eToro', 'https://www.etoro.com - OpenBook instrument sentiment'),
    ('Exness', 'https://www.exness.com - economic calendar'),
    ('MyFXBook', 'https://www.myfxbook.com - community outlook'),
    ('OANDA', 'https://www.oanda.com - open position ratios');

-- Quartz cron syntax: sec min hour day-of-month month day-of-week [year]
INSERT INTO jobs (GRP, NAME, HANDLER, CRON, DESCRIPTION, ENABLED) VALUES
    ('News', 'ForexFactoryNews', 'forexfactory', '0 0 */4 ? * MON-FRI', 'Import the ForexFactory weekly calendar', TRUE),
    ('News', 'ExnessNews', 'exness', '0 30 6 ? * MON-FRI', 'Import the Exness economic calendar', FALSE),
    ('Positions Ratio', 'MyFXBookRatio', 'myfxbook', '0 */15 * ? * MON-FRI', 'Sample MyFXBook community outlook', TRUE),
    ('Positions Ratio', 'OandaRatio', 'oanda', '0 */20 * ? * MON-FRI', 'Sample OANDA open position ratios', TRUE),
    ('Positions Ratio', 'EToroRatio', 'etoro', '0 */30 * ? * MON-FRI', 'Sample eToro OpenBook sentiment', FALSE);

INSERT INTO settings (NAME, SETTING_VALUE, DEFAULT_VALUE, DESCRIPTION) VALUES
    ('db.version', '2', '2', 'Reference-data revision applied by Flyway'),
    ('scheduler.sync-interval-seconds', '60', '60', 'How often job definitions are re-synchronised with Quartz (applied on restart)');
