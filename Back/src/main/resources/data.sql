-- Créer un utilisateur par défaut avec MERGE pour H2
MERGE INTO users (id, account_non_expired, account_non_locked, address, bio, created_at, credentials_non_expired, email, enabled, first_name, last_name, password, phone_number, role, updated_at, username)
KEY(username)
VALUES (1, true, true, NULL, 'Default user', CURRENT_TIMESTAMP, true, 'admin@pediatric.com', true, 'Admin', 'User', '$2a$10$xGJ9Q7K5X8Y3Z5W6K7N8O9P0Q1R2S3T4U5V6W7X8Y9Z0A1B2C3D4E5F', NULL, 'ADMIN', CURRENT_TIMESTAMP, 'admin');

-- Insérer des posts de test pour la première page (5 posts)
MERGE INTO posts (id, subject, content, created_at, date_post, is_anonymous, is_deleted, is_public, like_count, picture, post_type, status, title, updated_at, view_count, author_id)
KEY(id)
VALUES (1, 'Bienvenue sur le Forum Pédiatrique', 'Bienvenue sur notre forum pédiatrique! Ce forum est dédié aux professionnels de santé pour partager leurs connaissances et expériences concernant la santé des enfants.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false, false, true, 5, NULL, 'FORUM', 'Approved', 'Bienvenue', CURRENT_TIMESTAMP, 100, 1);

MERGE INTO posts (id, subject, content, created_at, date_post, is_anonymous, is_deleted, is_public, like_count, picture, post_type, status, title, updated_at, view_count, author_id)
KEY(id)
VALUES (2, 'Infections Respiratoires chez les Enfants', 'Les infections respiratoires sont très courantes chez les enfants. Partagez vos expériences et conseils pour la prise en charge.', CURRENT_TIMESTAMP, DATEADD('DAY', -1, CURRENT_TIMESTAMP), false, false, true, 3, NULL, 'FORUM', 'Approved', 'Infections Respiratoires', CURRENT_TIMESTAMP, 50, 1);

MERGE INTO posts (id, subject, content, created_at, date_post, is_anonymous, is_deleted, is_public, like_count, picture, post_type, status, title, updated_at, view_count, author_id)
KEY(id)
VALUES (3, 'Calendrier Vaccinal 2024', 'La vaccination est un sujet crucial en pédiatrie. Discutons des derniers calendriers vaccinals.', CURRENT_TIMESTAMP, DATEADD('DAY', -2, CURRENT_TIMESTAMP), false, false, true, 8, NULL, 'MEDICAL_UPDATE', 'Approved', 'Vaccination 2024', CURRENT_TIMESTAMP, 75, 1);

MERGE INTO posts (id, subject, content, created_at, date_post, is_anonymous, is_deleted, is_public, like_count, picture, post_type, status, title, updated_at, view_count, author_id)
KEY(id)
VALUES (4, 'Troubles du Sommeil Infantile', 'Les troubles du sommeil chez les enfants sont fréquents. Échangeons sur les stratégies pour améliorer la qualité du sommeil.', CURRENT_TIMESTAMP, DATEADD('DAY', -3, CURRENT_TIMESTAMP), false, false, true, 2, NULL, 'FORUM', 'Approved', 'Sommeil Enfant', CURRENT_TIMESTAMP, 30, 1);

MERGE INTO posts (id, subject, content, created_at, date_post, is_anonymous, is_deleted, is_public, like_count, picture, post_type, status, title, updated_at, view_count, author_id)
KEY(id)
VALUES (5, 'Nutrition et Développement', 'La nutrition infantile est fondamentale pour un développement sain. Partageons des conseils nutritionnels.', CURRENT_TIMESTAMP, DATEADD('DAY', -4, CURRENT_TIMESTAMP), false, false, true, 6, NULL, 'FOLLOW_UP', 'Approved', 'Nutrition', CURRENT_TIMESTAMP, 45, 1);

-- Insérer 5 autres posts pour la deuxième page
MERGE INTO posts (id, subject, content, created_at, date_post, is_anonymous, is_deleted, is_public, like_count, picture, post_type, status, title, updated_at, view_count, author_id)
KEY(id)
VALUES (6, 'Allergies Alimentaires', 'Les allergies alimentaires sont de plus en plus courantes. Comment diagnostiquer et gérer ces allergies?', CURRENT_TIMESTAMP, DATEADD('DAY', -5, CURRENT_TIMESTAMP), false, false, true, 4, NULL, 'FORUM', 'Approved', 'Allergies', CURRENT_TIMESTAMP, 60, 1);

MERGE INTO posts (id, subject, content, created_at, date_post, is_anonymous, is_deleted, is_public, like_count, picture, post_type, status, title, updated_at, view_count, author_id)
KEY(id)
VALUES (7, 'Développement Psychomoteur', 'Le développement psychomoteur est un indicateur important de la santé de l''enfant.', CURRENT_TIMESTAMP, DATEADD('DAY', -6, CURRENT_TIMESTAMP), false, false, true, 7, NULL, 'FORUM', 'Approved', 'Développement', CURRENT_TIMESTAMP, 40, 1);

MERGE INTO posts (id, subject, content, created_at, date_post, is_anonymous, is_deleted, is_public, like_count, picture, post_type, status, title, updated_at, view_count, author_id)
KEY(id)
VALUES (8, 'Suivi des Maladies Chroniques', 'Les maladies chroniques chez les enfants nécessitent un suivi régulier.', CURRENT_TIMESTAMP, DATEADD('DAY', -7, CURRENT_TIMESTAMP), false, false, true, 1, NULL, 'MEDICAL_UPDATE', 'Approved', 'Maladies Chroniques', CURRENT_TIMESTAMP, 25, 1);

MERGE INTO posts (id, subject, content, created_at, date_post, is_anonymous, is_deleted, is_public, like_count, picture, post_type, status, title, updated_at, view_count, author_id)
KEY(id)
VALUES (9, 'Santé Mentale Infantile', 'La santé mentale des enfants est aussi importante que leur santé physique.', CURRENT_TIMESTAMP, DATEADD('DAY', -8, CURRENT_TIMESTAMP), false, false, true, 9, NULL, 'FORUM', 'Approved', 'Santé Mentale', CURRENT_TIMESTAMP, 55, 1);

MERGE INTO posts (id, subject, content, created_at, date_post, is_anonymous, is_deleted, is_public, like_count, picture, post_type, status, title, updated_at, view_count, author_id)
KEY(id)
VALUES (10, 'Protocoles d''Urgence Pédiatrique', 'Les urgences pédiatriques nécessitent une prise en charge spécifique.', CURRENT_TIMESTAMP, DATEADD('DAY', -9, CURRENT_TIMESTAMP), false, false, true, 3, NULL, 'ANNOUNCEMENT', 'Approved', 'Urgences', CURRENT_TIMESTAMP, 80, 1);