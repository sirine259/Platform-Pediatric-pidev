-- Script pour créer la base de données pediatric_platform
-- Exécuter ce script dans phpMyAdmin

-- Création de la base de données
CREATE DATABASE IF NOT EXISTS pediatric_platform 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

-- Utilisation de la base de données
USE pediatric_platform;

-- Message de confirmation
SELECT 'Base de données pediatric_platform créée avec succès!' AS Resultat;
