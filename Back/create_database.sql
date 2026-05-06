-- Création de la base de données pour la plateforme pédiatrique
CREATE DATABASE IF NOT EXISTS pediatric_platform 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

-- Utilisation de la base de données
USE pediatric_platform;

-- Affichage des informations
SELECT 'Base de données pediatric_platform créée avec succès!' AS message;
