-- 안전검사 후 변경(존재하면 변경, 없으면 스킵)

SELECT IF(
               (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
                WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'user' AND COLUMN_NAME = 'providerId') = 1
                   AND
               (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
                WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'user' AND COLUMN_NAME = 'provider_id') = 0,
               'ALTER TABLE `user` CHANGE `providerId` `provider_id` VARCHAR(255) NOT NULL;',
               'SELECT "skip: providerId -> provider_id";'
       ) INTO @sql;
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SELECT IF(
               (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
                WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'user' AND COLUMN_NAME = 'goalScore') = 1
                   AND
               (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
                WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'user' AND COLUMN_NAME = 'goal_score') = 0,
               'ALTER TABLE `user` CHANGE `goalScore` `goal_score` INT;',
               'SELECT "skip: goalScore -> goal_score";'
       ) INTO @sql;
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SELECT IF(
               (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
                WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'user' AND COLUMN_NAME = 'preferredCategories') = 1
                   AND
               (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
                WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'user' AND COLUMN_NAME = 'preferred_categories') = 0,
               'ALTER TABLE `user` CHANGE `preferredCategories` `preferred_categories` VARCHAR(500);',
               'SELECT "skip: preferredCategories -> preferred_categories";'
       ) INTO @sql;
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SELECT IF(
               (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
                WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'user' AND COLUMN_NAME = 'isOnboardingCompleted') = 1
                   AND
               (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
                WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'user' AND COLUMN_NAME = 'is_onboarding_completed') = 0,
               'ALTER TABLE `user` CHANGE `isOnboardingCompleted` `is_onboarding_completed` BOOLEAN NOT NULL DEFAULT FALSE;',
               'SELECT "skip: isOnboardingCompleted -> is_onboarding_completed";'
       ) INTO @sql;
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SELECT IF(
               (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
                WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'book' AND COLUMN_NAME = 'categoryName') = 1
                   AND
               (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
                WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'book' AND COLUMN_NAME = 'category_name') = 0,
               'ALTER TABLE `book` CHANGE `categoryName` `category_name` VARCHAR(255) NOT NULL;',
               'SELECT "skip: categoryName -> category_name";'
       ) INTO @sql;
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SELECT IF(
               (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
                WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'book' AND COLUMN_NAME = 'itemId') = 1
                   AND
               (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
                WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'book' AND COLUMN_NAME = 'item_id') = 0,
               'ALTER TABLE `book` CHANGE `itemId` `item_id` VARCHAR(255);',
               'SELECT "skip: itemId -> item_id";'
       ) INTO @sql;
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SELECT IF(
               (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
                WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'book' AND COLUMN_NAME = 'is_received_from_aladin') = 0,
               'ALTER TABLE `book` ADD COLUMN `is_received_from_aladin` BOOLEAN NOT NULL DEFAULT FALSE;',
               'SELECT "skip: is_received_from_aladin already exists";'
       ) INTO @sql;
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SELECT IF(
               (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
                WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'bookcase' AND COLUMN_NAME = 'enrollAt') = 1
                   AND
               (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
                WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'bookcase' AND COLUMN_NAME = 'enroll_at') = 0,
               'ALTER TABLE `bookcase` CHANGE `enrollAt` `enroll_at` DATETIME;',
               'SELECT "skip: enrollAt -> enroll_at";'
       ) INTO @sql;
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SELECT IF(
               (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
                WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'bookcase' AND COLUMN_NAME = 'startedAt') = 1
                   AND
               (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
                WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'bookcase' AND COLUMN_NAME = 'started_at') = 0,
               'ALTER TABLE `bookcase` CHANGE `startedAt` `started_at` DATETIME;',
               'SELECT "skip: startedAt -> started_at";'
       ) INTO @sql;
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SELECT IF(
               (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
                WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'bookcase' AND COLUMN_NAME = 'finishedAt') = 1
                   AND
               (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
                WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'bookcase' AND COLUMN_NAME = 'finished_at') = 0,
               'ALTER TABLE `bookcase` CHANGE `finishedAt` `finished_at` DATETIME;',
               'SELECT "skip: finishedAt -> finished_at";'
       ) INTO @sql;
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
