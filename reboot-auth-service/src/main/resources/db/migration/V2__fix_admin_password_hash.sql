-- Corrects the admin password hash seeded in V1 which did not match the documented password.
-- Password: Admin@2024!  BCrypt cost 10
UPDATE internal_users
SET hashed_password = '$2a$10$lusNUszNRYWVXMW6NhyiF.7KeNti8PNTqmYSCSQaMyEqSaQzSpXl2'
WHERE email = 'admin@reboot.local';
