INSERT INTO users (user_id, email, password, name, birth, created_at, updated_at)
VALUES ('testuser', 'test@petfit.com', '$2a$10$wZVi7NIXLTjqlTRF3WjKdOuBc5QCsnHRDHqmYCKFv1wuWHHW/VMD.', '테스트유저', '2000-01-01', NOW(), NOW())
ON CONFLICT DO NOTHING;
